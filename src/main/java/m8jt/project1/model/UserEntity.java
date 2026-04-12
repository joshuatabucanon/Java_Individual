package m8jt.project1.model;

import static m8jt.project1.util.DbFieldLimits.USER_NAME_MAX;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Integer userId;

    @NotBlank
    @Size(max = USER_NAME_MAX)
    @Column(name = "name", nullable = false, length = USER_NAME_MAX)
    private String name;

    /* =========================
       Constructors
       ========================= */

    // Required by JPA
    public UserEntity() {
    }

    // Application constructor
    public UserEntity(String name) {
        this.name = name;
    }

    /* =========================
       Lifecycle Hooks
       ========================= */

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (name != null) {
            name = name.trim();
        }
    }

    /* =========================
       Getters / Setters
       ========================= */

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}