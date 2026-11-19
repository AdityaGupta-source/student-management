// Util layer: the four performance bands used for Q2.2.
package com.studentapp.util;

/**
 * Performance category of a student, decided from the average score.
 * Each value carries a readable label for API output.
 */
public enum PerformanceCategory {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    SATISFACTORY("Satisfactory"),
    NEEDS_IMPROVEMENT("Needs Improvement");

    private final String label;

    PerformanceCategory(String label) {
        this.label = label;
    }

    /** Returns the human-friendly name, e.g. "Needs Improvement". */
    public String getLabel() {
        return label;
    }
}
