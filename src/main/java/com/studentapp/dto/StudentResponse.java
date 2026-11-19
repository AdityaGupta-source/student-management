// DTO layer: JSON output for a student, including the calculated fields.
package com.studentapp.dto;

import java.util.List;

/**
 * What the API returns for a student: stored data plus average, category and stronger subject.
 * The calculated fields are not stored in the database.
 */
public class StudentResponse {

    private String studentId;
    private String studentName;
    private String email;
    private String courseName;
    private double programmingScore;
    private double mathematicsScore;
    private List<String> technicalSkills;
    private double average;
    private String category;
    private String strongerSubject;

    public StudentResponse() {
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(double programmingScore) { this.programmingScore = programmingScore; }

    public double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }

    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStrongerSubject() { return strongerSubject; }
    public void setStrongerSubject(String strongerSubject) { this.strongerSubject = strongerSubject; }
}
