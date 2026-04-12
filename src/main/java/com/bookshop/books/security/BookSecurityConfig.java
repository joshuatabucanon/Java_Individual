package com.bookshop.books.security;

import static com.bookshop.books.security.BookPermissions.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BookSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/v1/books/**")
                    .hasAuthority(BOOK_READ)

                .requestMatchers(HttpMethod.POST, "/api/v1/books")
                    .hasAuthority(BOOK_CREATE)

                .requestMatchers(HttpMethod.PATCH, "/api/v1/books/**")
                    .hasAuthority(BOOK_UPDATE)

                .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**")
                    .hasAuthority(BOOK_DELETE)

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}