// Test layer: checks the business rules in StudentServiceImpl using a fake (mock) repository.
package com.studentapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentapp.dto.SkillGroupResponse;
import com.studentapp.dto.StudentRequest;
import com.studentapp.dto.StudentResponse;
import com.studentapp.exception.DuplicateStudentException;
import com.studentapp.exception.StudentNotFoundException;
import com.studentapp.model.Student;
import com.studentapp.repository.StudentRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests average, category bands, stronger subject, merit order, same skills,
 * needs-improvement filter, duplicate check and score validation.
 */
class StudentServiceImplTest {

    private StudentRepository repository;
    private StudentServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(StudentRepository.class);
        service = new StudentServiceImpl(repository);
    }

    private Student student(String id, String name, double prog, double math, String... skills) {
        return new Student(id, name, id + "@x.com", "CSE", prog, math, Arrays.asList(skills));
    }

    private StudentRequest request(String id, Double prog, Double math) {
        StudentRequest request = new StudentRequest();
        request.setStudentId(id);
        request.setStudentName("Test");
        request.setEmail(id + "@x.com");
        request.setCourseName("CSE");
        request.setProgrammingScore(prog);
        request.setMathematicsScore(math);
        return request;
    }

    @Test
    void averageCategoryAndStrongerSubject() {
        when(repository.findById("S1")).thenReturn(java.util.Optional.of(student("S1", "A", 90, 80)));
        StudentResponse response = service.findStudentById("S1");
        assertEquals(85.0, response.getAverage());
        assertEquals("Excellent", response.getCategory());
        assertEquals("Programming", response.getStrongerSubject());
    }

    @Test
    void categoryBoundaries() {
        when(repository.findAll()).thenReturn(Arrays.asList(
                student("A", "A", 70, 70), student("B", "B", 50, 50),
                student("C", "C", 49, 50), student("D", "D", 60, 60)));
        List<StudentResponse> all = service.getAllStudents();
        assertEquals("Good", all.get(0).getCategory());
        assertEquals("Satisfactory", all.get(1).getCategory());
        assertEquals("Needs Improvement", all.get(2).getCategory());
        assertEquals("Both Equal", all.get(3).getStrongerSubject());
    }

    @Test
    void meritListIsSortedHighestAverageFirst() {
        when(repository.findAll()).thenReturn(Arrays.asList(
                student("A", "Low", 40, 40), student("B", "High", 90, 90), student("C", "Mid", 60, 70)));
        List<StudentResponse> merit = service.getMeritList();
        assertEquals("High", merit.get(0).getStudentName());
        assertEquals("Mid", merit.get(1).getStudentName());
        assertEquals("Low", merit.get(2).getStudentName());
    }

    @Test
    void sameSkillsReturnsOnlySharedSkillsIgnoringCase() {
        when(repository.findAll()).thenReturn(Arrays.asList(
                student("A", "Amy", 50, 50, "Java", "SQL"),
                student("B", "Bob", 50, 50, "java", "Python")));
        List<SkillGroupResponse> groups = service.getSameSkillGroups();
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).getStudentCount());
    }

    @Test
    void needsProgrammingImprovementFilter() {
        when(repository.findAll()).thenReturn(Arrays.asList(
                student("A", "Match", 45, 80), student("B", "LowMath", 45, 74),
                student("C", "GoodProg", 50, 90)));
        List<StudentResponse> result = service.getNeedsProgrammingImprovement();
        assertEquals(1, result.size());
        assertEquals("Match", result.get(0).getStudentName());
    }

    @Test
    void duplicateIdIsRejected() {
        when(repository.existsById("S1")).thenReturn(true);
        assertThrows(DuplicateStudentException.class, () -> service.addStudent(request("S1", 50.0, 50.0)));
        verify(repository, never()).save(any());
    }

    @Test
    void scoreAbove100IsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.addStudent(request("S1", 105.0, 50.0)));
    }

    @Test
    void missingStudentThrowsNotFound() {
        when(repository.findById("S9")).thenReturn(java.util.Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> service.findStudentById("S9"));
    }
}
