package m8jt.project1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import m8jt.project1.model.UserEntity;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
}