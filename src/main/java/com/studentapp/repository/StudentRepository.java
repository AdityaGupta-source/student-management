// Repository layer: database access. Spring Data JPA writes the SQL from the method names.
package com.studentapp.repository;

import com.studentapp.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CRUD operations for Student. findById, findAll, save, deleteById and existsById come free
 * from JpaRepository; we only add email and course lookups (Q2.4).
 */
public interface StudentRepository extends JpaRepository<Student, String> {

    /** Email is unique, so at most one student matches. */
    Optional<Student> findByEmail(String email);

    /** Many students can share a course, so this returns a list. */
    List<Student> findByCourseName(String courseName);

    /** Used to block duplicate emails when adding. */
    boolean existsByEmail(String email);
}
