package javamaven.M6Activity6.entity;


import jakarta.persistence.*;

@Entity
@Table(schema = "public", name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            nullable = false,
            columnDefinition = "BIGINT")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "student_id",
        foreignKey = @ForeignKey(name = "courses_student_id_fkey")
        , nullable = false   
    )
    private Student student;

    @Column(name = "course_name",
            length = 50,
            columnDefinition = "VARCHAR(50)")
    private String courseName;

    @Column(name = "grade",
            columnDefinition = "NUMERIC(5,2)")
    private double grade;

    // --- constructors ---
    public Course() { }

    public Course(String courseName, double grade) {
        this.courseName = courseName;
        this.grade = grade;
    }

    // --- getters/setters ---
    public Long getId() { return id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
}
