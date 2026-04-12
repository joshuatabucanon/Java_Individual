package bpi.com.M9Activity5;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity        // <-- Enable @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // unchanged: your in-memory users dev_1, dev_2, mgr_1
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails dev1 = User.withUsername("dev_1")
                .password(passwordEncoder().encode("dev_1password"))
                .roles("USER")
                .build();
        UserDetails dev2 = User.withUsername("dev_2")
                .password(passwordEncoder().encode("dev_2password"))
                .roles("USER")
                .build();
        UserDetails mgr1 = User.withUsername("mgr_1")
                .password(passwordEncoder().encode("mgr_1password"))
                .roles("MANAGER")
                .build();
        return new InMemoryUserDetailsManager(dev1, dev2, mgr1);
    }

    /**
     * URL-based authorization (retained), with /profile/** allowed for authenticated users
     * so that method-level security can evaluate @PreAuthorize expressions.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/home").hasAnyRole("USER", "MANAGER")
                .requestMatchers("/dashboard").hasRole("USER")
                .requestMatchers("/reports").hasRole("MANAGER")
                .requestMatchers("/profile/**").authenticated()  // let method security decide details
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}