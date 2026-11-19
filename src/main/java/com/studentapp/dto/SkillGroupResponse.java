// DTO layer: JSON output for the "same skills" feature (Q2.6).
package com.studentapp.dto;

import java.util.List;

/**
 * One skill together with the names of all students who have it.
 */
public class SkillGroupResponse {

    private String skill;
    private int studentCount;
    private List<String> studentNames;

    public SkillGroupResponse() {
    }

    public SkillGroupResponse(String skill, List<String> studentNames) {
        this.skill = skill;
        this.studentNames = studentNames;
        this.studentCount = studentNames.size();
    }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public List<String> getStudentNames() { return studentNames; }
    public void setStudentNames(List<String> studentNames) { this.studentNames = studentNames; }
}
