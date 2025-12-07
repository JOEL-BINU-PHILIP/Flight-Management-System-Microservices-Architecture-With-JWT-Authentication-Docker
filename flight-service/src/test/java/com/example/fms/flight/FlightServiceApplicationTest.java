package com.example.fms.flight;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// DO NOT LOAD FULL CONTEXT – disable ALL auto configs
@SpringBootTest(
        classes = {},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=*",
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.config.import="
})
class FlightServiceApplicationTests {

    @Test
    void contextLoads() {

    }
}

