// Service layer: ALL business logic lives here (Q2 items 1 to 8).
package com.studentapp.service;

import com.studentapp.dto.SkillGroupResponse;
import com.studentapp.dto.StudentRequest;
import com.studentapp.dto.StudentResponse;
import com.studentapp.dto.UpdateStudentRequest;
import com.studentapp.exception.DuplicateStudentException;
import com.studentapp.exception.StudentNotFoundException;
import com.studentapp.model.Student;
import com.studentapp.repository.StudentRepository;
import com.studentapp.util.PerformanceCategory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Implements the student operations: validation, average, category, stronger subject,
 * search, merit list, same-skill groups and the "needs programming improvement" filter.
 */
@Service
public class StudentServiceImpl implements StudentService {

    private static final double MIN_SCORE = 0;
    private static final double MAX_SCORE = 100;

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ---------------------------------------------------------------- add / update / delete

    /** Adds a student after checking duplicates (Q2.8) and score range (Q2.8). */
    @Override
    public StudentResponse addStudent(StudentRequest request) {
        validateScore(request.getProgrammingScore(), "programmingScore");
        validateScore(request.getMathematicsScore(), "mathematicsScore");
        checkForDuplicates(request);
        Student student = new Student(request.getStudentId().trim(), request.getStudentName().trim(),
                request.getEmail().trim(), request.getCourseName().trim(),
                request.getProgrammingScore(), request.getMathematicsScore(),
                cleanSkills(request.getTechnicalSkills()));
        return toResponse(studentRepository.save(student));
    }

    /** Changes only the two scores and the skill list of an existing student (Q3.4). */
    @Override
    public StudentResponse updateStudent(String studentId, UpdateStudentRequest request) {
        validateScore(request.getProgrammingScore(), "programmingScore");
        validateScore(request.getMathematicsScore(), "mathematicsScore");
        Student student = getStudentOrThrow(studentId);
        student.setProgrammingScore(request.getProgrammingScore());
        student.setMathematicsScore(request.getMathematicsScore());
        student.setTechnicalSkills(cleanSkills(request.getTechnicalSkills()));
        return toResponse(studentRepository.save(student));
    }

    /** Deletes a student; throws 404 if the ID does not exist. */
    @Override
    public void deleteStudent(String studentId) {
        Student student = getStudentOrThrow(studentId);
        studentRepository.delete(student);
    }

    // ---------------------------------------------------------------- search (Q2.4)

    @Override
    public List<StudentResponse> getAllStudents() {
        return toResponseList(studentRepository.findAll());
    }

    @Override
    public StudentResponse findStudentById(String studentId) {
        return toResponse(getStudentOrThrow(studentId));
    }

    @Override
    public StudentResponse findStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new StudentNotFoundException("No student with email: " + email));
        return toResponse(student);
    }

    @Override
    public List<StudentResponse> findStudentsByCourse(String courseName) {
        List<Student> students = studentRepository.findByCourseName(courseName);
        if (students.isEmpty()) {
            throw new StudentNotFoundException("No students found in course: " + courseName);
        }
        return toResponseList(students);
    }

    // ---------------------------------------------------------------- merit list (Q2.5)

    /** Sorts a copy of all students by average, highest first. */
    @Override
    public List<StudentResponse> getMeritList() {
        List<Student> students = new ArrayList<>(studentRepository.findAll());
        Collections.sort(students, meritComparator());
        return toResponseList(students);
    }

    /** Higher average comes first; equal averages are ordered by name so output is stable. */
    private Comparator<Student> meritComparator() {
        return (first, second) -> {
            int byAverage = Double.compare(calculateAverage(second), calculateAverage(first));
            if (byAverage != 0) {
                return byAverage;
            }
            return first.getStudentName().compareToIgnoreCase(second.getStudentName());
        };
    }

    // ---------------------------------------------------------------- same skills (Q2.6)

    /**
     * Groups students by skill. We use a Map (skill -> student names) because a Map lets us
     * look up a skill and append a name in one step. A TreeMap with CASE_INSENSITIVE_ORDER
     * treats "java" and "Java" as the same key. Only skills shared by 2+ students are returned.
     */
    @Override
    public List<SkillGroupResponse> getSameSkillGroups() {
        Map<String, List<String>> skillToNames = buildSkillToNamesMap(studentRepository.findAll());
        List<SkillGroupResponse> groups = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : skillToNames.entrySet()) {
            if (entry.getValue().size() >= 2) {
                groups.add(new SkillGroupResponse(entry.getKey(), entry.getValue()));
            }
        }
        return groups;
    }

    private Map<String, List<String>> buildSkillToNamesMap(List<Student> students) {
        Map<String, List<String>> skillToNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Student student : students) {
            for (String skill : distinctSkills(student)) {
                skillToNames.computeIfAbsent(skill, key -> new ArrayList<>())
                        .add(student.getStudentName());
            }
        }
        return skillToNames;
    }

    /** A Set removes repeats, so a student who lists "Java" twice is counted once. */
    private Set<String> distinctSkills(Student student) {
        Set<String> skills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        skills.addAll(student.getTechnicalSkills());
        return skills;
    }

    // ---------------------------------------------------------------- needs improvement (Q2.7)

    /**
     * ASSUMPTION: "needs improvement in programming" means programmingScore below 50
     * (the Needs Improvement band), and mathematics must be at least 75.
     */
    @Override
    public List<StudentResponse> getNeedsProgrammingImprovement() {
        List<Student> matches = new ArrayList<>();
        for (Student student : studentRepository.findAll()) {
            if (student.getProgrammingScore() < 50 && student.getMathematicsScore() >= 75) {
                matches.add(student);
            }
        }
        return toResponseList(matches);
    }

    // ---------------------------------------------------------------- calculations (Q2.1 - Q2.3)

    /** Q2.1: average of the two scores. */
    private double calculateAverage(Student student) {
        return (student.getProgrammingScore() + student.getMathematicsScore()) / 2.0;
    }

    /** Q2.2: maps an average to one of the four bands. */
    private PerformanceCategory assignCategory(double average) {
        if (average >= 85) {
            return PerformanceCategory.EXCELLENT;
        }
        if (average >= 70) {
            return PerformanceCategory.GOOD;
        }
        if (average >= 50) {
            return PerformanceCategory.SATISFACTORY;
        }
        return PerformanceCategory.NEEDS_IMPROVEMENT;
    }

    /** Q2.3: compares the two scores. */
    private String findStrongerSubject(Student student) {
        if (student.getProgrammingScore() > student.getMathematicsScore()) {
            return "Programming";
        }
        if (student.getMathematicsScore() > student.getProgrammingScore()) {
            return "Mathematics";
        }
        return "Both Equal";
    }

    // ---------------------------------------------------------------- validation (Q2.8)

    /** Safety net in case the request DTO annotations were bypassed. */
    private void validateScore(Double score, String fieldName) {
        if (score == null || score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and 100");
        }
    }

    private void checkForDuplicates(StudentRequest request) {
        if (studentRepository.existsById(request.getStudentId().trim())) {
            throw new DuplicateStudentException("Student ID already exists: " + request.getStudentId());
        }
        if (studentRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateStudentException("Email already exists: " + request.getEmail());
        }
    }

    // ---------------------------------------------------------------- helpers

    private Student getStudentOrThrow(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
    }

    /** Trims skills and drops empty ones so the database stays clean. */
    private List<String> cleanSkills(List<String> skills) {
        List<String> cleaned = new ArrayList<>();
        if (skills == null) {
            return cleaned;
        }
        for (String skill : skills) {
            if (skill != null && !skill.trim().isEmpty()) {
                cleaned.add(skill.trim());
            }
        }
        return cleaned;
    }

    private StudentResponse toResponse(Student student) {
        double average = calculateAverage(student);
        StudentResponse response = new StudentResponse();
        response.setStudentId(student.getStudentId());
        response.setStudentName(student.getStudentName());
        response.setEmail(student.getEmail());
        response.setCourseName(student.getCourseName());
        response.setProgrammingScore(student.getProgrammingScore());
        response.setMathematicsScore(student.getMathematicsScore());
        response.setTechnicalSkills(new ArrayList<>(student.getTechnicalSkills()));
        response.setAverage(average);
        response.setCategory(assignCategory(average).getLabel());
        response.setStrongerSubject(findStrongerSubject(student));
        return response;
    }

    private List<StudentResponse> toResponseList(List<Student> students) {
        // Stream: converts each Student into a StudentResponse, keeping the same order.
        return students.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
