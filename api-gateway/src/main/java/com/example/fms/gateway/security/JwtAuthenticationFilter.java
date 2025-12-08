package com.example.fms.gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    // Manual constructor - removed @RequiredArgsConstructor
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);  // Tell Spring Gateway which Config class to use
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
        // Empty config class - no properties needed
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                return this.onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtUtil.extractAllClaims(token);

            // Forward user identity to microservices
            return chain.filter(
                    exchange.mutate().request(
                            exchange.getRequest().mutate()
                                    .header("X-USER-EMAIL", claims.getSubject())
                                    .header("X-USER-ROLE", claims.get("role", String.class))
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