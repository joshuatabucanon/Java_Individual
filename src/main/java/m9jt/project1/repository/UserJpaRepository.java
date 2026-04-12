package m9jt.project1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import m9jt.project1.model.UserEntity;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
	Optional<UserEntity> findByUsername(String username);
}