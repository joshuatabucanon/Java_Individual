package m9jt.project1.model;

import static m9jt.project1.util.DbFieldLimits.USER_NAME_MAX;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import m9jt.project1.security.model.RoleEntity;

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
       Security fields
       ========================= */

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    /* =========================
       Constructors
       ========================= */

    public UserEntity() {}

    public UserEntity(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    /* =========================
       Getters
       ========================= */

    public Integer getUserId() { return userId; }
    public String getName() { return name; }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Set<RoleEntity> getRoles() { return roles; }

    /* =========================
       Setters
       ========================= */

    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}