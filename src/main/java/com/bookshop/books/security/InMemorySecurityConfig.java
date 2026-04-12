package com.bookshop.books.security;

import static com.bookshop.books.security.BookRoles.ADMIN_AUTHORITIES;
import static com.bookshop.books.security.BookRoles.USER_AUTHORITIES;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class InMemorySecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsManager(PasswordEncoder encoder) {

        UserDetails user = User.builder()
            .username("user")
            .password(encoder.encode("user1_password"))
            .authorities(USER_AUTHORITIES)   
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(encoder.encode("admin1_password"))
            .authorities(ADMIN_AUTHORITIES) 
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}