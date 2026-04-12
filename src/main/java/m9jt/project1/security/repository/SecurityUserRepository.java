package m9jt.project1.security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import m9jt.project1.model.UserEntity;

public interface SecurityUserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
}