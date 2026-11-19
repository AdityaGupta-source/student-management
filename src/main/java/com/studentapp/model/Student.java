// Model layer: the Student entity (Q1). Maps to the "students" and "student_skills" tables.
package com.studentapp.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Holds one student's profile, scores and technical skills.
 * Fields are private (encapsulation); access is only through getters and setters.
 */
@Entity
@Table(name = "students")
public class Student {

    // Entered by the user (e.g. "S101"), so no @GeneratedValue.
    @Id
    @Column(name = "student_id", length = 20)
    private String studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "programming_score", nullable = false)
    private double programmingScore;

    @Column(name = "mathematics_score", nullable = false)
    private double mathematicsScore;

    // Stored in a separate table; EAGER so skills are always loaded with the student.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill", nullable = false)
    private List<String> technicalSkills = new ArrayList<>();

    /** Required by JPA, which creates objects through this constructor. */
    public Student() {
    }

    /** Creates a student with all fields filled in. */
    public Student(String studentId, String studentName, String email, String courseName,
                   double programmingScore, double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        setTechnicalSkills(technicalSkills);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    /** Copies the list so the student never shares a list with the caller; null becomes empty. */
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = new ArrayList<>();
        if (technicalSkills != null) {
            this.technicalSkills.addAll(technicalSkills);
        }
    }

    /** Readable text for logs and debugging. */
    @Override
    public String toString() {
        return "Student{studentId='" + studentId + "', studentName='" + studentName
                + "', email='" + email + "', courseName='" + courseName
                + "', programmingScore=" + programmingScore
                + ", mathematicsScore=" + mathematicsScore
                + ", technicalSkills=" + technicalSkills + "}";
    }

    /** Two students are equal when their studentId is the same. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Student)) {
            return false;
        }
        Student that = (Student) other;
        return Objects.equals(studentId, that.studentId);
    }

    /** Uses the same field as equals() so the two stay consistent. */
    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}
