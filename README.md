# Student Profile and Skill Development Management System

A small web application that stores student records (details, two subject scores and technical skills),
works out each student's **average, performance category and stronger subject**, and lets you manage everything
through a **REST API** (Spring Boot + MySQL) and a **simple web page** (HTML, CSS, JavaScript).

Made for the course **CS336B: Object Oriented Programming using Java** (Mid Semester Exam II).

---

## Table of contents
1. [What the project does](#1-what-the-project-does)
2. [How it works (architecture)](#2-how-it-works-architecture)
3. [Technologies and dependencies](#3-technologies-and-dependencies)
4. [Before you start](#4-before-you-start)
5. [Run the backend](#5-run-the-backend)
6. [Run the frontend](#6-run-the-frontend)
7. [Using the web page](#7-using-the-web-page)
8. [API reference](#8-api-reference)
9. [API documentation endpoint (`/api/docs`)](#9-api-documentation-endpoint-apidocs)
10. [Business rules](#10-business-rules)
11. [Java collections used](#11-java-collections-used)
12. [Database](#12-database)
13. [Testing](#13-testing)
14. [Project structure](#14-project-structure)
15. [Assumptions](#15-assumptions)
16. [Troubleshooting](#16-troubleshooting)

---

## 1. What the project does

| You can... | How |
|---|---|
| Add a student | `POST /api/students` or the "Add Student" tab |
| See all students | `GET /api/students` or the "All Students" tab |
| Find a student by ID | `GET /api/students/{id}` or the search box |
| Change a student's scores and skills | `PUT /api/students/{id}` or the **Edit** button |
| Delete a student | `DELETE /api/students/{id}` or the **Delete** button |
| See the merit list (best average first) | `GET /api/students/merit-list` or the "Merit List" tab |
| Read the API documentation | `GET /api/docs` or the "API Docs" tab |

Every student in a response also comes with three **calculated** values (they are not stored in the database):

```json
{
  "studentId": "S101",
  "studentName": "Aarav Sharma",
  "email": "aarav@college.edu",
  "courseName": "B.Tech CSE",
  "programmingScore": 92.0,
  "mathematicsScore": 88.0,
  "technicalSkills": ["Java", "Spring Boot", "SQL"],
  "average": 90.0,
  "category": "Excellent",
  "strongerSubject": "Programming"
}
```

---

## 2. How it works (architecture)

The backend is split into layers. Each layer has one job, so the code is easy to read and explain.

```
 Browser (frontend, port 3000)          Postman / curl
          |                                    |
          +------------- HTTP / JSON ----------+
                            |
                  +---------v----------+
                  |    Controller      |   receives the request, returns the right status code
                  +---------+----------+   (StudentController, ApiDocController)
                            |
                  +---------v----------+
                  |      Service       |   ALL business logic: average, category, merit list,
                  +---------+----------+   duplicate and score checks (StudentServiceImpl)
                            |
                  +---------v----------+
                  |    Repository      |   talks to the database (Spring Data JPA)
                  +---------+----------+
                            |
                  +---------v----------+
                  |   MySQL database   |   tables: students, student_skills
                  +--------------------+
```

- **Model** (`Student`) is the Java class that matches the database table.
- **DTOs** (`StudentRequest`, `UpdateStudentRequest`, `StudentResponse`) are the JSON shapes that go in and out of the API.
- **Exceptions** (`StudentNotFoundException`, `DuplicateStudentException`) are turned into clean JSON errors by `GlobalExceptionHandler`.

---

## 3. Technologies and dependencies

**Tools you need installed**

| Tool | Version | Used for |
|---|---|---|
| Java (JDK) | 17 or newer | running the backend |
| MySQL | 8.0.16 or newer | the database (older versions ignore `CHECK` rules) |
| Maven | 3.9+ (optional) | building; the project also includes `mvnw`, so Maven is not required |
| Python | 3.x | only to serve the frontend files (any static server works) |
| Postman or curl | any | testing the API (optional) |

**Libraries (the dependencies in `pom.xml`).** Maven downloads these automatically.

| Dependency | Why it is there |
|---|---|
| `spring-boot-starter-web` | builds the REST controllers and runs the built-in Tomcat web server |
| `spring-boot-starter-data-jpa` | saves and loads `Student` objects from MySQL without writing SQL (uses Hibernate) |
| `spring-boot-starter-validation` | checks input with `@NotBlank`, `@Email`, `@Min(0)`, `@Max(100)` |
| `mysql-connector-j` | the MySQL driver that connects Java to the database |
| `spring-boot-starter-test` | JUnit 5 and Mockito for the unit tests (used only when testing) |

Spring Boot version: **3.3.5**. Java version: **17**. No Lombok, no security, no Swagger.

---

## 4. Before you start

### Step 1: Create the database and tables
Open a terminal in the project folder and run the two SQL files (replace `root` with your MySQL user):

```
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample_data.sql
```

No `mysql` command? Open both files in **MySQL Workbench** and run them, `schema.sql` first.
This creates the database `student_db`, the two tables and 10 sample students.

> You must run `schema.sql` yourself. The app creates missing tables on its own, but it cannot create the
> database itself or the `CHECK` rules on the scores.

### Step 2: Tell the app your MySQL username and password
The settings are in `src/main/resources/application.properties`. It holds placeholders on purpose, so passwords
are never uploaded to GitHub. Choose one way:

**Option A (recommended):** create a new file `src/main/resources/application-local.properties` with your real values.
This file is ignored by git.
```properties
spring.datasource.username=root
spring.datasource.password=your_real_password
```

**Option B:** edit the two placeholder lines directly in `application.properties`
(`your_mysql_username` and `your_mysql_password`). Do not commit your real password.

---

## 5. Run the backend

Open a terminal in the project folder and run one of these:

```
mvn spring-boot:run
```
or, without installing Maven:
```
./mvnw spring-boot:run          (Mac / Linux / Git Bash)
mvnw.cmd spring-boot:run        (Windows Command Prompt)
```

Wait for the line `Started StudentManagementApplication`. The backend is now running at **http://localhost:8080**.

Quick check: open http://localhost:8080/api/students in a browser. You should see the 10 sample students as JSON.

To stop it, press `Ctrl + C` in that terminal.

---

## 6. Run the frontend

The frontend is plain HTML, CSS and JavaScript in the `frontend/` folder. It needs no installation, only a
tiny web server. Open a **second** terminal (leave the backend running) and run:

```
cd frontend
python -m http.server 3000
```

Then open **http://localhost:3000** in your browser.

- The top-right badge should say **"Backend connected (port 8080)"** in green.
- If it says "Backend offline", the backend is not running (see section 16).

Do not double-click `index.html`. Opening it as a file makes the browser block the API calls (CORS).
Always use `http://localhost:3000`.

Want a different backend port? Change `server.port` in `application.properties` **and** `API_BASE` at the top of `frontend/app.js`.

**Summary of ports**

| Part | Address |
|---|---|
| Backend (Spring Boot) | http://localhost:8080 |
| Frontend (web page) | http://localhost:3000 |
| MySQL | localhost:3306 |

---

## 7. Using the web page

| Tab / control | What it does |
|---|---|
| **Summary cards** | Total students, class average and top performer |
| **All Students** | Table of every student with score bars, category badge, stronger subject and skills. Click a column heading to sort. |
| **Search box** | Type a student ID (for example `S101`) and press Enter |
| **Merit List** | Students ranked by average, highest first (gold, silver and bronze for the top three) |
| **+ Add Student** | Form to add a student; skills are typed with commas, for example `Java, SQL` |
| **Edit** (on each row) | Opens a window to change the two scores and the skills |
| **Delete** (on each row) | Asks for confirmation, then deletes the student |
| **API Docs** | Shows every API endpoint, loaded live from `/api/docs` |

Errors from the server (duplicate ID, score above 100, student not found) appear as red pop-up messages.
The page follows your system's light or dark mode.

---

## 8. API reference

Base URL: `http://localhost:8080`. Send JSON bodies with the header `Content-Type: application/json`
(Postman adds it when you choose **Body, raw, JSON**).

| # | Feature | Method and URL | Body | Success | Errors |
|---|---|---|---|---|---|
| 1 | Add a student | `POST /api/students` | student JSON | **201** | 400, 409 |
| 2 | Get all students | `GET /api/students` | none | **200** | |
| 3 | Search by ID | `GET /api/students/{id}` | none | **200** | 404 |
| 4 | Update scores and skills | `PUT /api/students/{id}` | scores + skills | **200** | 400, 404 |
| 5 | Delete a student | `DELETE /api/students/{id}` | none | **200** | 404 |
| 6 | Merit list | `GET /api/students/merit-list` | none | **200** | |
| + | API documentation | `GET /api/docs` | none | **200** | |

**Add a student**
```http
POST /api/students
{
  "studentId": "S201",
  "studentName": "Riya Kapoor",
  "email": "riya@college.edu",
  "courseName": "B.Tech CSE",
  "programmingScore": 95,
  "mathematicsScore": 90,
  "technicalSkills": ["Java", "Python", "SQL"]
}
```
Response (**201 Created**): the saved student with `"average": 92.5`, `"category": "Excellent"`, `"strongerSubject": "Programming"`.

**Update scores and skills** (name, email and course stay unchanged)
```http
PUT /api/students/S201
{
  "programmingScore": 60,
  "mathematicsScore": 75,
  "technicalSkills": ["Java", "Spring Boot"]
}
```
Response (**200 OK**): average 67.5, "Satisfactory", stronger subject "Mathematics".

**Delete**
```http
DELETE /api/students/S201
```
Response (**200 OK**): `{ "message": "Student S201 deleted" }`

**Error responses** always look the same:
```json
{
  "timestamp": "2026-11-19T10:44:50.239",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": { "programmingScore": "programmingScore must be at most 100" }
}
```

| Status | When it happens |
|---|---|
| 201 | A student was created |
| 200 | Success |
| 400 | Bad input: score outside 0 to 100, missing field, bad email, invalid JSON |
| 404 | No student with that ID |
| 409 | The student ID or email already exists |

---

## 9. API documentation endpoint (`/api/docs`)

**What are API docs?** They are a list that explains how to use an API: which URLs exist, which method to use
(GET, POST, PUT, DELETE), what to send, and what comes back. Without them, other people have to guess.

This project has its own small documentation endpoint, written by hand (no extra library):

```
GET http://localhost:8080/api/docs
```

For every endpoint it returns the `method`, `path`, `description`, a `sampleBody` and the possible `statusCodes`:

```json
[
  {
    "method": "POST",
    "path": "/api/students",
    "description": "Add a student",
    "sampleBody": "{\"studentId\":\"S111\", ...}",
    "statusCodes": "201, 400, 409"
  }
]
```

The web page shows the same data in its **API Docs** tab. The code is in `controller/ApiDocController.java`.

---

## 10. Business rules

All rules are in `service/StudentServiceImpl.java`, one short method each.

| Rule | How it works |
|---|---|
| **Average** | `(programming + mathematics) / 2.0` |
| **Category** | average **85 or more**: Excellent; **70 to 84.99**: Good; **50 to 69.99**: Satisfactory; **below 50**: Needs Improvement |
| **Stronger subject** | Compares the two scores: "Programming", "Mathematics" or "Both Equal" |
| **Search** | By ID, email or course (repository methods `findById`, `findByEmail`, `findByCourseName`) |
| **Merit list** | All students sorted by average, highest first (ties are ordered by name) |
| **Same technical skills** | Groups student names under each skill and keeps skills shared by 2 or more students |
| **Needs programming improvement** | Programming score below 50 **and** mathematics score 75 or more |
| **No duplicates** | A new student is rejected (409) if the ID or the email already exists |
| **Score validation** | Scores must be between 0 and 100: checked by `@Min`/`@Max` on the request, again in the service, and by `CHECK` rules in MySQL |

> The search by email or course, the same-skills groups and the programming-improvement list are implemented in
> the service layer and proved by the unit tests. They have no URL of their own, because the exam asks for exactly
> six REST APIs.

---

## 11. Java collections used

| Collection | Where | Why |
|---|---|---|
| `List<String>` | `Student.technicalSkills` | a student has any number of skills, in order |
| `List<Student>` + `Comparator` + `Collections.sort` | merit list | sorts a copy of all students by average (high to low) |
| `Map<String, List<String>>` (a `TreeMap`) | same-skills groups | skill is the key, the list of student names is the value |
| `Set<String>` (a `TreeSet`) | same-skills groups | removes a skill a student typed twice |
| `Optional<Student>` | repository search | the answer may be empty when nothing is found |
| `enum PerformanceCategory` | category | a fixed set of four values |
| `LinkedHashMap` | error responses | keeps the JSON fields in a readable order |

The `TreeMap` uses `String.CASE_INSENSITIVE_ORDER`, so "java" and "Java" count as the same skill.

---

## 12. Database

Two tables in the database `student_db`:

```
students                             student_skills
------------------------             ------------------
student_id   (primary key)  <------  student_id   (foreign key, deleted with the student)
student_name                         skill
email        (unique)
course_name
programming_score  (0 to 100)
mathematics_score  (0 to 100)
```

`student_skills` exists because the `Student` class holds a **list** of skills, and a database column cannot
hold a list. Each skill gets its own row, linked to the student by `student_id`.

**Sample data** (`database/sample_data.sql`) has 10 students that cover every case:

| ID | Name | Prog | Math | Average | Category |
|---|---|---|---|---|---|
| S101 | Aarav Sharma | 92 | 88 | 90.0 | Excellent |
| S102 | Diya Verma | 78 | 81 | 79.5 | Good |
| S103 | Rohan Mehta | 45 | 80 | 62.5 | Satisfactory (needs programming help) |
| S104 | Sneha Iyer | 60 | 55 | 57.5 | Satisfactory |
| S105 | Karan Singh | 35 | 40 | 37.5 | Needs Improvement |
| S106 | Meera Nair | 85 | 85 | 85.0 | Excellent (both equal) |
| S107 | Vikram Rao | 30 | 90 | 60.0 | Satisfactory (needs programming help) |
| S108 | Ananya Gupta | 72 | 66 | 69.0 | Satisfactory |
| S109 | Ishaan Patel | 55 | 70 | 62.5 | Satisfactory |
| S110 | Pooja Reddy | 95 | 91 | 93.0 | Excellent |

---

## 13. Testing

### Unit tests (no database needed)
```
mvn test
```
There are 11 tests. They use a fake repository, so they check the business rules on their own: average, category
boundaries, stronger subject, merit order, same skills (ignoring case), the programming-improvement filter,
duplicate ID, score above 100 and a missing student.

### Postman collection
The file `postman/Student_Management.postman_collection.json` contains 14 ready-made requests with status checks.
- **Postman app:** click **Import**, choose the file, then **Run collection**.
- **Postman CLI:** with the backend running,
  ```
  postman collection run postman/Student_Management.postman_collection.json
  ```

The run adds and deletes a test student `S111` and changes student `S103`. To restore S103 afterwards:
```
PUT /api/students/S103
{ "programmingScore": 45, "mathematicsScore": 80, "technicalSkills": ["Python", "SQL"] }
```

### Quick curl checks
```
curl http://localhost:8080/api/students
curl http://localhost:8080/api/students/S101
curl http://localhost:8080/api/students/merit-list
curl -i http://localhost:8080/api/students/S999          # 404 not found
```
On Windows PowerShell use `curl.exe` instead of `curl`.

### Try the error cases
| Test | Expected |
|---|---|
| POST with `"programmingScore": 105` | 400, message says the score must be at most 100 |
| POST with `"studentId": "S101"` (already exists) | 409 |
| POST with an email that already exists | 409 |
| POST with a missing name or a bad email | 400 |
| GET or PUT or DELETE on `/api/students/S999` | 404 |

---

## 14. Project structure

```
student-management/
├── pom.xml                         Maven build file and dependencies
├── mvnw, mvnw.cmd, .mvn/           Maven wrapper (run without installing Maven)
├── README.md                       This file
├── VIVA_NOTES.md                   Logic and collections explained, viva questions
├── AI_PROMPTS_REFLECTION.md        AI prompts and reflections
├── database/
│   ├── schema.sql                  Creates the database and tables
│   └── sample_data.sql             10 sample students
├── frontend/
│   ├── index.html                  The page
│   ├── style.css                   The design (light and dark)
│   └── app.js                      Calls the API and draws the results
├── postman/
│   └── Student_Management.postman_collection.json
└── src/
    ├── main/java/com/studentapp/
    │   ├── StudentManagementApplication.java     Starts the app
    │   ├── controller/   StudentController, ApiDocController
    │   ├── service/      StudentService (interface), StudentServiceImpl (all logic)
    │   ├── repository/   StudentRepository
    │   ├── model/        Student (the entity)
    │   ├── dto/          StudentRequest, UpdateStudentRequest, StudentResponse, SkillGroupResponse
    │   ├── exception/    StudentNotFoundException, DuplicateStudentException, GlobalExceptionHandler
    │   └── util/         PerformanceCategory (enum)
    ├── main/resources/application.properties     Database and port settings
    └── test/java/com/studentapp/                 Unit tests
```

---

## 15. Assumptions

- **Needs improvement in programming:** programming score **below 50** (the "Needs Improvement" band) **and** mathematics **75 or more**.
- **Same technical skills:** students are grouped **per skill** (not by identical skill lists). Skills are compared
  ignoring upper and lower case and extra spaces.
- **Category limits:** 85, 70 and 50 are the lowest averages for Excellent, Good and Satisfactory.
- **Update (PUT)** changes only the two scores and the skill list.
- **Email** must also be unique, as well as the student ID.
- **Calculated values** (average, category, stronger subject) are worked out each time and are not stored.
- **Scores** may have decimals (for example `84.5`).

---

## 16. Troubleshooting

| Problem | Cause and fix |
|---|---|
| `Unknown database 'student_db'` | `schema.sql` has not been run. See [section 4](#4-before-you-start). |
| `Access denied for user` | Wrong username or password. Check `application-local.properties`. |
| `Communications link failure` | MySQL is not running. Start the MySQL service. |
| `Port 8080 was already in use` | Another program uses 8080. Stop it, or change `server.port`. |
| Page says **"Backend offline"** | Start the backend first (section 5), then refresh the page. |
| Page is blank or shows CORS errors | Open it at `http://localhost:3000`, not by double-clicking `index.html`. |
| `409 Conflict` when adding | That student ID or email already exists. Use new ones. |
| Page looks old after an update | Hard refresh with `Ctrl + F5`. |
| `mvn` is not recognised | Use `./mvnw spring-boot:run` (or `mvnw.cmd`) instead. |
