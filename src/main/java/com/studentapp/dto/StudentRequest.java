// DTO layer: JSON input for "add student". Validation annotations reject bad input early.
package com.studentapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data sent by the client to add a student. Scores must be between 0 and 100.
 */
public class StudentRequest {

    @NotBlank(message = "studentId is required")
    private String studentId;

    @NotBlank(message = "studentName is required")
    private String studentName;

    @NotBlank(message = "email is required")
    @Email(message = "email is not valid")
    private String email;

    @NotBlank(message = "courseName is required")
    private String courseName;

    @NotNull(message = "programmingScore is required")
    @Min(value = 0, message = "programmingScore must be at least 0")
    @Max(value = 100, message = "programmingScore must be at most 100")
    private Double programmingScore;

    @NotNull(message = "mathematicsScore is required")
    @Min(value = 0, message = "mathematicsScore must be at least 0")
    @Max(value = 100, message = "mathematicsScore must be at most 100")
    private Double mathematicsScore;

    private List<String> technicalSkills;

    public StudentRequest() {
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }
}
