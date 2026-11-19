// Exception layer: thrown when a student cannot be found. Mapped to HTTP 404.
package com.studentapp.exception;

/**
 * Thrown when no student matches the given ID, email or course.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }
}
