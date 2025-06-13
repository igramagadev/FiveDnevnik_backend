package com.fivednevnik.api.util;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LoggingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String requestBody = getContent(requestWrapper.getContentAsByteArray());
            String responseBody = getContent(responseWrapper.getContentAsByteArray());


            responseWrapper.copyBodyToResponse();
        }
    }
    
    private String getContent(byte[] contentAsByteArray) {
        if (contentAsByteArray.length == 0) {
            return "";
        }
        return new String(contentAsByteArray, StandardCharsets.UTF_8);
    }
    
    private String getHeadersAsString(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining(", "));
    }
    
    private String getResponseHeadersAsString(HttpServletResponse response) {
        return response.getHeaderNames()
                .stream()
                .map(headerName -> headerName + ": " + response.getHeader(headerName))
                .collect(Collectors.joining(", "));
    }
} 
