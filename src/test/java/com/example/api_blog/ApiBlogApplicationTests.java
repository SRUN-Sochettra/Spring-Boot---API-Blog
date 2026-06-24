package com.example.api_blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "DB_PASSWORD=test",
    "JWT_SECRET=testsecret123456789012345678901234567890",
    "PINATA_JWT=test",
    "PINATA_API_KEY=test",
    "PINATA_SECRET_KEY=test"
})
class ApiBlogApplicationTests {

    @Test
    void contextLoads() {
    }

}
