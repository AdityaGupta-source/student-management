# Student Profile and Skill Development Management System

Spring Boot + MySQL project for CS336B (Object Oriented Programming using Java).
Layers: `controller` -> `service` -> `repository` -> MySQL. All business logic is in `StudentServiceImpl`.

## Setup

**Requirements:** Java 17+, Maven (or use the included `mvnw`), MySQL 8.0.16+.

1. Create the database and tables (the `CHECK` constraints need this script):
   ```
   mysql -u <user> -p < database/schema.sql
   mysql -u <user> -p < database/sample_data.sql
   ```
   (Or open both files in MySQL Workbench and run them, `schema.sql` first.)
2. Set your MySQL username and password. Either edit the placeholders in `src/main/resources/application.properties`, or (recommended, keeps the password out of git) create `src/main/resources/application-local.properties` containing just the two lines `spring.datasource.username=...` and `spring.datasource.password=...`. The URL and defaults are in `application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/student_db?...
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```
3. Run:
   ```
   mvn spring-boot:run        (or ./mvnw spring-boot:run)
   ```
   The backend starts on `http://localhost:8080` (set by `server.port` in `application.properties`).
4. Run the unit tests (no database needed): `mvn test`
5. Start the frontend (plain HTML/CSS/JS in `frontend/`) on port 3000:
   ```
   cd frontend
   python -m http.server 3000
   ```
   Open `http://localhost:3000`. It calls the backend on port 8080 (change `API_BASE` at the top of `frontend/app.js` if needed). CORS allows only `http://localhost:3000`.

**API documentation endpoint (extra, not in the PS):** `GET http://localhost:8080/api/docs` returns every endpoint with its method, path, sample body and status codes. The frontend shows it in the "API Docs" tab.

## API list (base path `/api/students`)

| # | Feature | Method and URL | Success | Errors |
|---|---|---|---|---|
| Q3.1 | Add a student | `POST /api/students` | 201 | 400 bad input, 409 duplicate |
| Q3.2 | All students | `GET /api/students` | 200 | |
| Q3.3 | Search by ID | `GET /api/students/{id}` | 200 | 404 |
| Q3.4 | Update scores and skills | `PUT /api/students/{id}` | 200 | 400, 404 |
| Q3.5 | Delete | `DELETE /api/students/{id}` | 200 | 404 |
| Q3.6 | Merit list | `GET /api/students/merit-list` | 200 | |

Every student in a response includes `average`, `category` and `strongerSubject`.

## Assumptions

- **Needs improvement in programming (Q2.7):** programming score **< 50** (the "Needs Improvement" band) **and** mathematics score **>= 75**.
- **Same technical skills (Q2.6):** students are grouped **per skill**; only skills shared by 2 or more students are shown. Skills are compared ignoring case ("java" = "Java").
- **Category bands (Q2.2):** `>= 85` Excellent, `>= 70` Good, `>= 50` Satisfactory, otherwise Needs Improvement.
- **Update (PUT)** changes only the two scores and the skills list.
- **Duplicates:** both the student ID and the email must be unique (409 otherwise).
- Average, category and stronger subject are calculated on the fly and not stored.
- **Q2.4 (email/course search), Q2.6 and Q2.7** are implemented in `StudentServiceImpl` and covered by unit tests, but have no REST endpoint, because Q3 lists exactly 6 APIs. Search by ID is `GET /api/students/{id}`.

## Test requests (curl; the same bodies work in Postman)

Use these after loading `sample_data.sql`. On Windows PowerShell use `curl.exe` and put JSON in a file (`-d @file.json`) to avoid quote problems.

**1. Add a student: expect 201**
```
curl -i -X POST http://localhost:8080/api/students -H "Content-Type: application/json" -d '{
  "studentId":"S111","studentName":"Neha Joshi","email":"neha@college.edu","courseName":"B.Tech CSE",
  "programmingScore":80,"mathematicsScore":70,"technicalSkills":["Java","SQL"]}'
```
Expected: average 75.0, category "Good", strongerSubject "Programming".

**2. Duplicate ID: expect 409**  (send request 1 again)
```
{"status":409,"error":"Conflict","message":"Student ID already exists: S111", ...}
```

**3. Score of 105: expect 400**
```
curl -i -X POST http://localhost:8080/api/students -H "Content-Type: application/json" -d '{
  "studentId":"S112","studentName":"Test","email":"t@college.edu","courseName":"B.Tech IT",
  "programmingScore":105,"mathematicsScore":70,"technicalSkills":[]}'
```
Expected: `"message":"Validation failed","fieldErrors":{"programmingScore":"programmingScore must be at most 100"}`

**4. Missing student: expect 404**
```
curl -i http://localhost:8080/api/students/S999
```
Expected: `"message":"Student not found with ID: S999"`

**5. Get all (200):** `curl http://localhost:8080/api/students`
**6. Get by ID (200):** `curl http://localhost:8080/api/students/S101` -> average 90.0, Excellent, Programming
**7. Update (200):**
```
curl -X PUT http://localhost:8080/api/students/S103 -H "Content-Type: application/json" -d '{
  "programmingScore":65,"mathematicsScore":80,"technicalSkills":["Python","SQL","Java"]}'
```
Expected: average 72.5, "Good", strongerSubject "Mathematics". 

**8. Update with bad score (400):** same body with `"mathematicsScore":-5`.
**9. Delete (200):** `curl -X DELETE http://localhost:8080/api/students/S111`; a second delete returns 404.
**10. Merit list (200):** `curl http://localhost:8080/api/students/merit-list` -> first is S110 Pooja Reddy (93.0), then S101 Aarav Sharma (90.0).
**11. Bad JSON / missing body (400):** `curl -i -X POST http://localhost:8080/api/students -H "Content-Type: application/json" -d "{"`

## Project structure

```
src/main/java/com/studentapp/
  controller/  StudentController            HTTP only
  service/     StudentService (interface), StudentServiceImpl (all logic)
  repository/  StudentRepository            Spring Data JPA
  model/       Student                      entity (Q1)
  dto/         StudentRequest, UpdateStudentRequest, StudentResponse, SkillGroupResponse
  exception/   DuplicateStudentException, StudentNotFoundException, GlobalExceptionHandler
  util/        PerformanceCategory          enum
database/      schema.sql, sample_data.sql
src/test/      StudentTest, StudentServiceImplTest   (no database needed)
```
