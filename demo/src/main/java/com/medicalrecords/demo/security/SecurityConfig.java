package com.medicalrecords.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers("/login").permitAll()

                        // Patient accessible pages
                        .requestMatchers("/visits/my").hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                        .requestMatchers("/diagnoses").hasAnyRole("PATIENT", "ADMIN", "DOCTOR")

                        // ADMIN/DOCTOR only pages
                        .requestMatchers("/doctors/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/patients/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/sick-leaves/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/reports/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/visits/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/diagnoses/**").hasAnyRole("ADMIN", "DOCTOR")

                        // Home
                        .requestMatchers("/").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
