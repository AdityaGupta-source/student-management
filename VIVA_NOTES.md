# Viva Notes

All business logic is in `StudentServiceImpl`. The controller only handles HTTP.

**Note:** the REST API has exactly the 6 endpoints the PS lists in Q3 (plus `/api/docs`). The Q2 features that have no URL of their own (search by email/course, same-skills groups, programming-improvement filter) are implemented as service methods (`findStudentByEmail`, `findStudentsByCourse`, `getSameSkillGroups`, `getNeedsProgrammingImprovement`) and are proved by `StudentServiceImplTest`. Run `mvn test` to show them in the viva.

## Business rules and collections

| Rule | Logic (plain English) | Collection used and why |
|---|---|---|
| **Q2.1 Average** `calculateAverage` | `(programming + mathematics) / 2.0`. The `2.0` forces decimal division. | None. |
| **Q2.2 Category** `assignCategory` | Check from the top: `>= 85` Excellent, `>= 70` Good, `>= 50` Satisfactory, else Needs Improvement. Checking `>=` from the top means no gaps such as 84.995. | Enum `PerformanceCategory`: a fixed set of 4 values, so no typos. |
| **Q2.3 Stronger subject** `findStrongerSubject` | Compare the two scores; return "Programming", "Mathematics" or "Both Equal". | None. |
| **Q2.4 Search** | `findById`, `findByEmail`, `findByCourseName` in `StudentRepository`; Spring Data builds the SQL from the method name. | `Optional` for ID/email (may be empty); `List` for course (many matches). |
| **Q2.5 Merit list** `getMeritList` | Copy all students, sort by average, highest first; ties are ordered by name. | `List` + `Comparator` + `Collections.sort`. A copy is sorted so the repository's list is untouched. |
| **Q2.6 Same skills** `getSameSkillGroups` | For every student and each skill, add the student's name under that skill. Keep only skills with 2 or more names. | `Map<String, List<String>>` (skill -> names): quick lookup by skill. `TreeMap` with `CASE_INSENSITIVE_ORDER` so "java" = "Java". A `Set` per student removes a skill listed twice. |
| **Q2.7 Needs improvement** `getNeedsProgrammingImprovement` | Loop over all students; keep those with programming `< 50` and mathematics `>= 75`. *Assumption:* "needs improvement" = the below-50 band. | `List` of matches. |
| **Q2.8 Validation** | Duplicate: `existsById` / `existsByEmail` then throw `DuplicateStudentException` (409). Scores: `@Min(0) @Max(100)` on the DTO, plus `validateScore` in the service as a safety net (400). | None. |

Other collection uses: `List<String> technicalSkills` in `Student` (stored with `@ElementCollection` in `student_skills`); a `Stream.map` in `toResponseList` to convert entities to responses; `LinkedHashMap` for ordered JSON error bodies.

Note on Q2.6: another reading of "same skills" is students with an *identical set* of skills. I used the per-skill reading (grouping students sharing any skill). If asked, say it can be changed by using the whole sorted skill set as the Map key instead.

## Likely viva questions

1. **Why separate controller, service and repository?** Each has one job: the controller handles HTTP, the service holds the rules, the repository talks to the database. It is easier to read and test.
2. **Why DTOs instead of returning the entity?** The response needs extra calculated fields (average, category, stronger subject) that are not stored, and the request needs validation rules. DTOs keep that separate from the table.
3. **How does the merit list work?** `Collections.sort` with a `Comparator` that compares averages in reverse (`Double.compare(second, first)`). Equal averages fall back to name.
4. **Why a Map for same skills?** Skill is the natural key and the names are the value. `computeIfAbsent` creates the list the first time a skill is seen, so one pass over the students is enough.
5. **Why TreeMap with CASE_INSENSITIVE_ORDER?** So "Java" and "java" count as one skill. It also sorts the skills alphabetically.
6. **How are duplicates prevented?** The service checks `existsById` and `existsByEmail` first (409). The database primary key and unique email are a second safety net.
7. **How are scores validated?** `@Min(0) @Max(100)` with `@Valid` rejects bad requests before the service runs. The service re-checks, and MySQL has `CHECK` constraints.
8. **Why is `studentId` not auto-generated?** The PS wants user-entered IDs like "S101", so there is no `@GeneratedValue`.
9. **Why are `equals` and `hashCode` based on `studentId`?** It is the primary key, so it identifies a student. Both methods use the same field to stay consistent (needed for Sets and Maps).
10. **What is `@ElementCollection`?** It stores a list of simple values (the skills) in a separate table, `student_skills`, linked to the student by `student_id`.
11. **What does `ddl-auto=update` do?** It creates missing tables/columns at startup but never drops data. It does not create CHECK constraints, so `schema.sql` is run first.
12. **Which HTTP status codes do you return?** 201 create, 200 success, 400 bad input, 404 not found, 409 duplicate.
