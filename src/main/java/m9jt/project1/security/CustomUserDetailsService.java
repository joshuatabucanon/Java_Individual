package m9jt.project1.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import m9jt.project1.security.repository.SecurityUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SecurityUserRepository userRepository;

    public CustomUserDetailsService(SecurityUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(
                user.getRoles().stream()
                    .map(r -> r.getName())
                    .toArray(String[]::new)
            )
            .build();
    }
}