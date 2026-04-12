package m9jt.project1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import m9jt.project1.model.BookEntity;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, Integer> {
    List<BookEntity> findByIsAvailableTrue();
}