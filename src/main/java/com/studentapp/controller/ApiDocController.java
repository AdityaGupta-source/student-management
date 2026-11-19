// Controller layer: a simple hand-written API documentation endpoint (GET /api/docs).
package com.studentapp.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Returns a JSON list describing every API endpoint, so the frontend (or a person
 * with a browser) can see how to use the API. No Swagger needed; the list is written by hand.
 */
@RestController
@RequestMapping("/api/docs")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiDocController {

    private static final String BASE = "/api/students";
    private static final String ADD_BODY = "{\"studentId\":\"S111\",\"studentName\":\"Neha Joshi\","
            + "\"email\":\"neha@college.edu\",\"courseName\":\"B.Tech CSE\",\"programmingScore\":80,"
            + "\"mathematicsScore\":70,\"technicalSkills\":[\"Java\",\"SQL\"]}";
    private static final String UPDATE_BODY = "{\"programmingScore\":65,\"mathematicsScore\":80,"
            + "\"technicalSkills\":[\"Python\",\"SQL\"]}";

    /** Lists all endpoints with method, path, description, sample body and status codes. */
    @GetMapping
    public List<Map<String, String>> getApiDocs() {
        List<Map<String, String>> docs = new ArrayList<>();
        docs.add(describe("POST", BASE, "Add a student", ADD_BODY, "201, 400, 409"));
        docs.add(describe("GET", BASE, "Get all students", "-", "200"));
        docs.add(describe("GET", BASE + "/{id}", "Search a student by ID", "-", "200, 404"));
        docs.add(describe("PUT", BASE + "/{id}", "Update scores and skills", UPDATE_BODY, "200, 400, 404"));
        docs.add(describe("DELETE", BASE + "/{id}", "Delete a student", "-", "200, 404"));
        docs.add(describe("GET", BASE + "/merit-list", "Merit list, highest average first", "-", "200"));
        docs.add(describe("GET", "/api/docs", "This documentation", "-", "200"));
        return docs;
    }

    private Map<String, String> describe(String method, String path, String description,
                                         String sampleBody, String statusCodes) {
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("method", method);
        entry.put("path", path);
        entry.put("description", description);
        entry.put("sampleBody", sampleBody);
        entry.put("statusCodes", statusCodes);
        return entry;
    }
}
