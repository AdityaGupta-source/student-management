// Controller layer: handles HTTP only. Calls the service and returns the right status code.
package com.studentapp.controller;

import com.studentapp.dto.StudentRequest;
import com.studentapp.dto.StudentResponse;
import com.studentapp.dto.UpdateStudentRequest;
import com.studentapp.service.StudentService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints under /api/students. Contains no business logic.
 * Errors (404, 400, 409) are turned into JSON by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000") // lets the frontend on port 3000 call this API
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** Q3.1: add a student (201 Created). */
    @PostMapping
    public ResponseEntity<StudentResponse> addStudent(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.addStudent(request));
    }

    /** Q3.2: list all students. */
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    /** Q3.6: merit list, highest average first. */
    @GetMapping("/merit-list")
    public ResponseEntity<List<StudentResponse>> getMeritList() {
        return ResponseEntity.ok(studentService.getMeritList());
    }

    /** Q3.3: search by student ID. */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> findStudentById(@PathVariable("id") String id) {
        return ResponseEntity.ok(studentService.findStudentById(id));
    }

    /** Q3.4: update scores and skills. */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable("id") String id,
                                                         @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    /** Q3.5: delete a student. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable("id") String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Student " + id + " deleted"));
    }
}
