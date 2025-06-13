package com.fivednevnik.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private Key signingKey;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails);
    }

    public String generateToken(UserDetails userDetails) {
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());
        

        SignatureAlgorithm signatureAlgorithm = getSignatureAlgorithm();
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", authorities)
                .claim("version", jwtProperties.getTokenVersion())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), signatureAlgorithm)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        
        Integer tokenVersion = extractClaim(token, claims -> claims.get("version", Integer.class));
        boolean isCurrentVersion = tokenVersion != null && tokenVersion == jwtProperties.getTokenVersion();
        
        if (!isCurrentVersion) {
            return false;
        }
        
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.security.WeakKeyException e) {
            try {

                String[] parts = token.split("\\.");
                if (parts.length != 3) {
                    throw new io.jsonwebtoken.MalformedJwtException("JWT должен состоять из 3 частей");
                }

                try {
                    String header = new String(java.util.Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);

                    String alg = "HS256";
                    if (header.contains("alg")) {
                        if (header.contains("HS512")) {
                            alg = "HS512";
                        } else if (header.contains("HS384")) {
                            alg = "HS384";
                        }
                    }

                    return Jwts.parserBuilder()
                            .setSigningKey(getSigningKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
                } catch (IllegalArgumentException ex) {
                    throw new io.jsonwebtoken.MalformedJwtException("Некорректный JWT заголовок: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private Key getSigningKey() {
        if (signingKey != null) {
            return signingKey;
        }

        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);

        SignatureAlgorithm algorithm = getSignatureAlgorithm();
        
        try {
            signingKey = Keys.hmacShaKeyFor(keyBytes);
            return signingKey;
        } catch (Exception e) {
            signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            return signingKey;
        }
    }

    private SignatureAlgorithm getSignatureAlgorithm() {
        try {
            return SignatureAlgorithm.forName(jwtProperties.getAlgorithm().toUpperCase());
        } catch (Exception e) {
            return SignatureAlgorithm.HS256;
        }
    }
} 
