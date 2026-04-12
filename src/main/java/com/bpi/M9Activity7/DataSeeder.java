package com.bpi.M9Activity7;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedOnReady() {
        System.out.println("[DataSeeder] START (ApplicationReadyEvent)");
        long before = userRepository.count();
        System.out.println("[DataSeeder] user count before seeding = " + before);
        if (before > 0) {
            System.out.println("[DataSeeder] data already present, skipping.");
            return;
        }
        User dev1 = new User("dev_1", passwordEncoder.encode("dev_1password"), true);
        dev1 = userRepository.save(dev1);
        roleRepository.save(new Role(dev1, "USER"));

        User dev2 = new User("dev_2", passwordEncoder.encode("dev_2password"), true);
        dev2 = userRepository.save(dev2);
        roleRepository.save(new Role(dev2, "USER"));

        User mgr1 = new User("mgr_1", passwordEncoder.encode("mgr_1password"), true);
        mgr1 = userRepository.save(mgr1);
        roleRepository.save(new Role(mgr1, "MANAGER"));

        System.out.println("[DataSeeder] seeding done. user count after = " + userRepository.count());
    }
}
