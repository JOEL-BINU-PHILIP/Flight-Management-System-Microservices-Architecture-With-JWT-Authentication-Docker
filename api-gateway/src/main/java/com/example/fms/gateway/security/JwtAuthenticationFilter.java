package com.example.fms.gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getPath().value();

            // ================================
            // 1️⃣ ALLOW PUBLIC ENDPOINTS
            // ================================
            if (path.startsWith("/auth/login") ||
                    path.startsWith("/auth/register") ||
                    path.startsWith("/auth/validate") ||
                    path.startsWith("/auth/logout") ||
                    path.startsWith("/actuator")) {

                return chain.filter(exchange);
            }

            // ================================
            // 2️⃣ CHECK AUTH HEADER
            // ================================
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
            }

            // ================================
            // 3️⃣ EXTRACT CLAIMS
            // ================================
            Claims claims = jwtUtil.extractAllClaims(token);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            // ================================
            // 4️⃣ FORWARD USER DETAILS
            // ================================
            return chain.filter(
                    exchange.mutate().request(
                            exchange.getRequest().mutate()
                                    .header("X-USER-EMAIL", email)
                                    .header("X-USER-ROLE", role)
                                    .build()
                    ).build()
            );
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus code) {
        exchange.getResponse().setStatusCode(code);
        return exchange.getResponse().setComplete();
    }
}