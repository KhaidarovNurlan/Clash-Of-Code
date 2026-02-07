package com.server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        String token = header.substring(7);
        try {
            if (!jwtUtil.validateToken(token)) {
                logger.warn("JWT token is invalid or expired");
                chain.doFilter(req, res);
                return;
            }
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            if (!StringUtils.hasText(username)) {
                logger.warn("JWT token missing username");
                chain.doFilter(req, res);
                return;
            }
            if (!StringUtils.hasText(role)) {
                logger.warn("JWT token missing role, assigning default ROLE_USER");
                role = "USER";
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            logger.warn("Failed to parse JWT token: {}", e.getMessage());
        }
        chain.doFilter(req, res);
    }
}
