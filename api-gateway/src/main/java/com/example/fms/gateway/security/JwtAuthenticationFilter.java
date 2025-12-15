package com.example.fms.gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework. http.HttpHeaders;
import org.springframework. http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core. publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter. Config> {

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
            // 1 ALLOW PUBLIC ENDPOINTS
            // ================================
            if (path.startsWith("/auth/login") ||
                    path.startsWith("/auth/register") ||
                    path.startsWith("/auth/validate") ||
                    path.startsWith("/auth/logout") ||
                    path.startsWith("/actuator")) {

                return chain.filter(exchange);
            }

            // ================================
            // 2️ EXTRACT TOKEN (Cookie First, Then Header)
            // ================================
            String token = extractToken(exchange);

            if (token == null) {
                return onError(exchange, "Missing or Invalid Authorization", HttpStatus.UNAUTHORIZED);
            }

            // ================================
            // 3️ VALIDATE TOKEN
            // ================================
            if (!jwtUtil.isTokenValid(token)) {
                return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
            }

            // ================================
            // 4️⃣ EXTRACT CLAIMS & FORWARD
            // ================================
            Claims claims = jwtUtil.extractAllClaims(token);
            String email = claims.getSubject();
            String role = claims.get("role", String. class);

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

    /**
     * Extract JWT token from Cookie (priority) or Authorization header (fallback)
     */
    private String extractToken(ServerWebExchange exchange) {

        // Check HTTP-only cookie
        HttpCookie jwtCookie = exchange.getRequest().getCookies().getFirst("jwt");
        if (jwtCookie != null && ! jwtCookie.getValue().isEmpty()) {
            return jwtCookie.getValue();
        }

        //Priority 2: Check Authorization header
        String authHeader = exchange. getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus code) {
        exchange.getResponse().setStatusCode(code);
        return exchange.getResponse().setComplete();
    }
}