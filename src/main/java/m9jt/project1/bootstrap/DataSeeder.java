package m9jt.project1.bootstrap;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import m9jt.project1.model.UserEntity;
import m9jt.project1.repository.UserJpaRepository;
import m9jt.project1.security.model.RoleEntity;
import m9jt.project1.security.repository.RoleJpaRepository;

@Component
public class DataSeeder {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserJpaRepository userRepository,
            RoleJpaRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedUsers() {

        RoleEntity adminRole = roleRepository.findById("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

        RoleEntity userRole = roleRepository.findById("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));

        // --------------------------------------------------
        // Admin
        // --------------------------------------------------
        createUserIfNotExists(
                "Administrator",
                "admin",
                "adminpassword",
                adminRole
        );

        // --------------------------------------------------
        // Users
        // --------------------------------------------------
        createUserIfNotExists(
                "User",
                "user",
                "password",
                userRole
        );

        createUserIfNotExists(
                "User One",
                "user1",
                "password",
                userRole
        );
    }

    private void createUserIfNotExists(
            String name,
            String username,
            String rawPassword,
            RoleEntity role) {

        Optional<UserEntity> existing =
                userRepository.findByUsername(username);

        if (existing.isPresent()) {
            return; // safe re-run
        }

        UserEntity user = new UserEntity(
                name,
                username,
                passwordEncoder.encode(rawPassword)
        );

        user.getRoles().add(role);

        userRepository.save(user);
    }
}