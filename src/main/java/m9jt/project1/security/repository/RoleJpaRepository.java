package m9jt.project1.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import m9jt.project1.security.model.RoleEntity;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, String> {
    @Query(value = """
            SELECT ur.role_name
            FROM user_roles ur
            WHERE ur.user_id = :userId
            """, nativeQuery = true)
    List<String> findRoleNamesByUserId(@Param("userId") int userId);
}