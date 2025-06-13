package com.fivednevnik.api.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (!StringUtils.hasText(jwt)) {
                log.debug("JWT токен не удалось извлечь из запроса");
            } else {
                
                try {
                    String username = jwtService.extractUsername(jwt);
                    
                    if (!StringUtils.hasText(username)) {
                        log.warn("Имя пользователя не найдено");
                    } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        log.debug("Пользователь уже аутентифицирован");
                    } else {
                        
                        try {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            
                            try {
                                if (jwtService.isTokenValid(jwt, userDetails)) {
                                    UsernamePasswordAuthenticationToken authentication = 
                                            new UsernamePasswordAuthenticationToken(
                                                userDetails, null, userDetails.getAuthorities());
                                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                    
                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                } else {
                                    log.warn("JWT токен недействителен для пользователя: {}", username);
                                }
                            } catch (io.jsonwebtoken.security.WeakKeyException e) {

                                UsernamePasswordAuthenticationToken authentication = 
                                        new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                
                                SecurityContextHolder.getContext().setAuthentication(authentication);

                                String newToken = jwtService.generateToken(userDetails);
                                response.setHeader("X-New-JWT-Token", newToken);
                                response.setHeader("Access-Control-Expose-Headers", "X-New-JWT-Token");
                            } catch (Exception e) {
                                log.error("Ошибка при проверке токена для пользователя {}: {}", username, e.getMessage());
                            }
                        } catch (Exception e) {
                            log.error("Не удалось загрузить пользователя {}: {}", username, e.getMessage());
                        }
                    }
                } catch (io.jsonwebtoken.security.SignatureException e) {
                    clearAuthenticationAndSetResponseError(response, "Недействительная подпись JWT", HttpStatus.UNAUTHORIZED);
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    clearAuthenticationAndSetResponseError(response, "Срок действия JWT токена истек", HttpStatus.UNAUTHORIZED);
                } catch (io.jsonwebtoken.MalformedJwtException e) {
                    clearAuthenticationAndSetResponseError(response, "Неверный формат JWT токена", HttpStatus.UNAUTHORIZED);
                } catch (io.jsonwebtoken.security.WeakKeyException e) {
                    String responseMessage = "Проблема с ключом JWT. Требуется повторный вход.";

                    if (e.getMessage().contains("HS512")) {
                        responseMessage += " Старый токен использует алгоритм HS512, но система перешла на HS256.";
                    }
                    
                    clearAuthenticationAndSetResponseError(response, responseMessage, HttpStatus.UNAUTHORIZED);
                } catch (Exception e) {
                    clearAuthenticationAndSetResponseError(response, "Ошибка при обработке JWT токена", HttpStatus.UNAUTHORIZED);
                }
            }
        } catch (Exception ex) {
        }

        filterChain.doFilter(request, response);

        int status = response.getStatus();
        if (status == 403) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "неизвестный пользователь";

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames != null && headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.warn("  {} = {}", headerName, headerValue);
            }

            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                log.warn("  {} = {}", entry.getKey(), Arrays.toString(entry.getValue()));
            }

            if (auth != null) {
                Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            }
        }

    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String headerName = jwtProperties.getHeaderName();
        String bearerToken = null;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if (headerName.equalsIgnoreCase(header)) {
                bearerToken = request.getHeader(header);
                break;
            }
        }
        
        if (StringUtils.hasText(bearerToken)) {
            
            String tokenPrefix = jwtProperties.getTokenPrefix().trim();
            
            if (bearerToken.startsWith(tokenPrefix)) {
                String token = bearerToken.substring(tokenPrefix.length());
                return token;
            } else if (bearerToken.startsWith("Bearer")) {
                String token = bearerToken.substring("Bearer".length()).trim();
                return token;
            }
        }
        
        return null;
    }



    private void clearAuthenticationAndSetResponseError(HttpServletResponse response, String message, HttpStatus status) {
        SecurityContextHolder.clearContext();
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = String.format("{\"error\":\"%s\",\"code\":\"%s\"}", 
                                      message.replace("\"", "\\\""), 
                                      status.name());
            response.getWriter().write(json);
            response.setHeader("X-JWT-Error", "true");
            response.setHeader("Access-Control-Expose-Headers", "X-JWT-Error");
        } catch (IOException e) {
            log.error("Ошибка при записи ответа", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        boolean isExcludedEndpoint = requestURI.contains("/auth/") || 
                                requestURI.contains("/api/auth/") ||
                                requestURI.contains("/swagger-ui/") ||
                                requestURI.contains("/v3/api-docs/") ||
                                requestURI.contains("/h2-console/") ||
                                requestURI.equals("/ping");
        
        if (isExcludedEndpoint) {
            return true;
        }
        
        return false;
    }
} 
