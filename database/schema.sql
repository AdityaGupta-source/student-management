-- Database layer: creates the database and tables.
-- Run this BEFORE starting the app (needs MySQL 8.0.16+ for CHECK constraints).

CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

-- Main table: one row per student. student_id is typed by the user (e.g. S101).
CREATE TABLE IF NOT EXISTS students (
    student_id         VARCHAR(20)  NOT NULL,
    student_name       VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL,
    course_name        VARCHAR(100) NOT NULL,
    programming_score  DOUBLE       NOT NULL,
    mathematics_score  DOUBLE       NOT NULL,
    PRIMARY KEY (student_id),
    UNIQUE KEY uk_students_email (email),
    CONSTRAINT chk_programming_score CHECK (programming_score BETWEEN 0 AND 100),
    CONSTRAINT chk_mathematics_score CHECK (mathematics_score BETWEEN 0 AND 100)
);

-- Skills table: one row per (student, skill). Holds the List<String> technicalSkills.
CREATE TABLE IF NOT EXISTS student_skills (
    student_id VARCHAR(20)  NOT NULL,
    skill      VARCHAR(100) NOT NULL,
    CONSTRAINT fk_skills_student FOREIGN KEY (student_id)
        REFERENCES students (student_id) ON DELETE CASCADE
);
