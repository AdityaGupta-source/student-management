// Exception layer: thrown when a student ID or email already exists. Mapped to HTTP 409.
package com.studentapp.exception;

/**
 * Thrown when adding a student whose ID (or email) is already in the database.
 */
public class DuplicateStudentException extends RuntimeException {

    public DuplicateStudentException(String message) {
        super(message);
    }
}
