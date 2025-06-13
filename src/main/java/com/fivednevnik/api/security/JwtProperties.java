package com.fivednevnik.api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;


@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret = "2ydgr7d8FD5f7mDYzWXXqwerty123456789ASDFGHJKL";

    private long expirationMs = 86400000;

    private String tokenPrefix = "Bearer ";

    private String headerName = "Authorization";

    @Value("${app.jwt.algorithm:HS256}")
    private String algorithm;

    @Value("${app.jwt.token-version:1}")
    private Integer tokenVersion;
}
