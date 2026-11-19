// Service layer: the list of operations the controller can ask for (no code here).
package com.studentapp.service;

import com.studentapp.dto.SkillGroupResponse;
import com.studentapp.dto.StudentRequest;
import com.studentapp.dto.StudentResponse;
import com.studentapp.dto.UpdateStudentRequest;
import java.util.List;

/**
 * Contract for all student business operations. The controller depends on this interface only.
 */
public interface StudentService {

    StudentResponse addStudent(StudentRequest request);

    List<StudentResponse> getAllStudents();

    StudentResponse findStudentById(String studentId);

    StudentResponse findStudentByEmail(String email);

    List<StudentResponse> findStudentsByCourse(String courseName);

    StudentResponse updateStudent(String studentId, UpdateStudentRequest request);

    void deleteStudent(String studentId);

    List<StudentResponse> getMeritList();

    List<SkillGroupResponse> getSameSkillGroups();

    List<StudentResponse> getNeedsProgrammingImprovement();
}
