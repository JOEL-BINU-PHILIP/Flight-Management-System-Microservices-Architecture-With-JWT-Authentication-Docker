package com.example.fms. gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework. http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        log.info("Processing request for path: {}", path);

        // Allow public endpoints
        if (path.startsWith("/auth/") || path.startsWith("/actuator/")) {
            log.info("Allowing public path: {}", path);
            return chain.filter(exchange);
        }

        // Extract token
        String token = extractToken(exchange);

        if (token == null) {
            log.warn("No JWT token found in request");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (! jwtUtil.isTokenValid(token)) {
            log.warn("Invalid JWT token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract claims and add headers
        Claims claims = jwtUtil.extractAllClaims(token);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        log.info("JWT validated for user: {} with role: {}", email, role);

        // Set authentication in security context
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

        // Add headers for downstream services
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(r -> r
                        .header("X-USER-EMAIL", email)
                        .header("X-USER-ROLE", role)
                ).build();

        log.info("Added headers - X-USER-EMAIL: {}, X-USER-ROLE: {}", email, role);

        return chain.filter(modifiedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }

    private String extractToken(ServerWebExchange exchange) {
        // Check cookie first
        HttpCookie jwtCookie = exchange.getRequest().getCookies().getFirst("jwt");
        if (jwtCookie != null && ! jwtCookie.getValue().isEmpty()) {
            log.debug("JWT token found in cookie");
            return jwtCookie.getValue();
        }

        // Check Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("JWT token found in Authorization header");
            return authHeader.substring(7);
        }

        log.debug("No JWT token found");
        return null;
    }
}