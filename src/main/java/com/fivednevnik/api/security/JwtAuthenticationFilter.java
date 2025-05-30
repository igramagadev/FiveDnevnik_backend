package com.fivednevnik.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Фильтр для аутентификации пользователей по JWT токену
 */
@Component
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
        log.info("Обработка запроса: {} {}", method, requestURI);

        logRequestHeaders(request);
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (!StringUtils.hasText(jwt)) {
                log.debug("JWT токен не найден в запросе");
            } else {
                log.debug("JWT токен найден в запросе. Извлечение имени пользователя");
                String username = jwtService.extractUsername(jwt);
                
                if (!StringUtils.hasText(username)) {
                    log.warn("Имя пользователя не найдено в JWT токене");
                } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    log.debug("Пользователь уже аутентифицирован");
                } else {
                    log.info("Загрузка информации о пользователе: {}", username);
                    
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.debug("Пользователь загружен: {}. Роли: {}", username, userDetails.getAuthorities());
                        
                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("Пользователь {} успешно аутентифицирован", username);
                        } else {
                            log.warn("JWT токен недействителен для пользователя: {}", username);
                        }
                    } catch (Exception e) {
                        log.error("Не удалось загрузить пользователя: {}", username, e);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Не удалось установить аутентификацию пользователя", ex);
        }

        filterChain.doFilter(request, response);

        log.info("Ответ отправлен: {} {} -> {}", method, requestURI, response.getStatus());
    }

    /**
     * Извлечение JWT токена из заголовка запроса
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String headerName = jwtProperties.getHeaderName();
        String bearerToken = request.getHeader(headerName);
        
        if (StringUtils.hasText(bearerToken)) {
            log.debug("Заголовок {} найден: {}", headerName, bearerToken);
            
            if (bearerToken.startsWith(jwtProperties.getTokenPrefix())) {
                return bearerToken.substring(jwtProperties.getTokenPrefix().length());
            } else {
                log.warn("Заголовок {} не содержит префикс {}", headerName, jwtProperties.getTokenPrefix());
            }
        } else {
            log.debug("Заголовок {} не найден", headerName);
        }
        
        return null;
    }
    
    /**
     * Логирование всех заголовков запроса
     */
    private void logRequestHeaders(HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Заголовки запроса:");
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                Collections.list(headerNames).forEach(headerName -> 
                    log.debug("  {} = {}", headerName, request.getHeader(headerName))
                );
            }
        }
    }
} 