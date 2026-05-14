package com.medicalrecords.demo;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    @Test
    void testPasswordMatch() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("secret matches: " + encoder.matches("secret", hash));
        System.out.println("password123 matches: " + encoder.matches("password123", hash));
        System.out.println("admin matches: " + encoder.matches("admin", hash));
        System.out.println("New hash for 'admin123': " + encoder.encode("admin123"));
    }
}
