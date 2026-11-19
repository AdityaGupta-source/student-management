// DTO layer: JSON input for "update scores and skills" (Q3.4).
package com.studentapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data sent to change a student's two scores and skill list. Other fields stay unchanged.
 */
public class UpdateStudentRequest {

    @NotNull(message = "programmingScore is required")
    @Min(value = 0, message = "programmingScore must be at least 0")
    @Max(value = 100, message = "programmingScore must be at most 100")
    private Double programmingScore;

    @NotNull(message = "mathematicsScore is required")
    @Min(value = 0, message = "mathematicsScore must be at least 0")
    @Max(value = 100, message = "mathematicsScore must be at most 100")
    private Double mathematicsScore;

    private List<String> technicalSkills;

    public UpdateStudentRequest() {
    }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }
}
