package m7group6.project1.model;

import static m7group6.project1.util.DbFieldLimits.*;

import jakarta.persistence.*; // JPA annotations (Jakarta)
import jakarta.validation.constraints.*; // Bean Validation (Jakarta)

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL identity column
    @Column(name = "user_id", updatable = false, nullable = false)
    private Integer userId;

    @NotBlank(message = "Name is required")
    @Size(max = USER_NAME_MAX, message = "Name must be at most " + USER_NAME_MAX + " characters")
    @Column(name = "name", nullable = false, length = USER_NAME_MAX)
    private String name;

    public UserEntity() {     	
    }

    public UserEntity(String name) {
        this.name = (name == null ? null : name.trim());
    }

    @PrePersist
    @PreUpdate
    void normalize() {
        if (name != null) name = name.trim();
    }

    // --- getters/setters ---
    public Integer getUserID() {
        return userId;
    }

    public void setUserID(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null ? null : name.trim());
    }
}