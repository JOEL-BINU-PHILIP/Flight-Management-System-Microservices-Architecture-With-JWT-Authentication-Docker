package com.example.fms.booking.config;

import feign. RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j. Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.isAuthenticated()) {
                    String email = authentication.getName();
                    String role = authentication.getAuthorities().stream()
                            .findFirst()
                            .map(auth -> auth.getAuthority())
                            .orElse(null);

                    log.info("Feign adding headers - Email: {}, Role: {}", email, role);

                    template.header("X-USER-EMAIL", email);
                    template.header("X-USER-ROLE", role);
                } else {
                    log.warn("No authentication found in SecurityContext for Feign call");
                }
            }
        };
    }
}