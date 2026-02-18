package javamaven.M6Activity2.entity;

import jakarta.persistence.*;

@Entity
@Table(schema = "public", name = "students",
       uniqueConstraints = {
           @UniqueConstraint(name = "students_email_key", columnNames = "email")
       })
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            nullable = false,
            columnDefinition = "INTEGER")
    private Integer id;

    @Column(name = "name",
            nullable = false,
            length = 50,
            columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "age",
            nullable = true,
            columnDefinition = "INTEGER")
    private Integer age;

    @Column(name = "email",
            nullable = true,
            length = 100,
            unique = true,
            columnDefinition = "VARCHAR(100)")
    private String email;

    // --- Constructors ---
    public Student() {}

    public Student(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public Integer getId() { 
    	return id; 
    }
    
    public void setId(Integer id) { 
    	this.id = id; 
    }

    public String getName() { 
    	return name; 
    }
    
    public void setName(String name) { 
    	this.name = name; 
    }

    public Integer getAge() { 
    	return age; 
    }
    
    public void setAge(Integer age) { 
    	this.age = age; 
    }

    public String getEmail() { 
    	return email; 
    }
    
    public void setEmail(String email) { 
    	this.email = email; 
    }

}