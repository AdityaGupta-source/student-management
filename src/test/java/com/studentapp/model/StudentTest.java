// Test layer: checks the Student entity (Q1). Needs no database.
package com.studentapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Simple unit tests for Student: equals/hashCode by studentId and safe skill copying.
 */
class StudentTest {

    @Test
    void studentsWithSameIdAreEqual() {
        Student first = new Student("S1", "A", "a@x.com", "CSE", 50, 60, null);
        Student second = new Student("S1", "B", "b@x.com", "IT", 70, 80, null);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void settingSkillsCopiesTheList() {
        List<String> original = new ArrayList<>(Arrays.asList("Java", "SQL"));
        Student student = new Student("S1", "A", "a@x.com", "CSE", 50, 60, original);
        original.add("Python");
        assertNotSame(original, student.getTechnicalSkills());
        assertEquals(2, student.getTechnicalSkills().size());
    }

    @Test
    void nullSkillsBecomeEmptyList() {
        Student student = new Student("S1", "A", "a@x.com", "CSE", 50, 60, null);
        assertTrue(student.getTechnicalSkills().isEmpty());
    }
}
