package m9jt.project1.security.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @Column(length = 20)
    private String name; // ROLE_ADMIN or ROLE_USER

    protected RoleEntity() {}

    public RoleEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}