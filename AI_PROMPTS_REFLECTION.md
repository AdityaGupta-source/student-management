# AI Prompts and Reflections

Draft written from this project's conversation with Claude. Edit the reflections so they match your own experience before submitting.

---

## Prompt 1: Planning from the problem statement

**Prompt I used:** I pasted a long prompt asking Claude to read the PS folder first, and to build a Spring Boot + MySQL Student Profile and Skill Development system. I gave it the layered folder structure, the rule of simple readable code (no Lombok, short methods, comments on streams), a mapping of every PS item (Q1, Q2.1 to 2.8, Q3.1 to 3.6) to a file, and told it to work in stages and wait for my approval.

**What Claude produced:** It read the PDF and gave a summary, a list of doubts and the file list. The doubts were the meaning of "same technical skills" (per skill or identical skill sets) and of "needs improvement in programming". It also noted that it used `double` for scores.

**What I had to correct or verify:** I checked the summary against the PDF myself. I confirmed that the "same skills" reading was per skill, and that "needs improvement" meant programming below 50.

**Reflection:** Making Claude plan first stopped it from guessing. The unclear points in the PS came up before any code was written, which saved rework. I learned that a prompt with a clear structure and rules gives much more usable output than a short request. I still had to decide the unclear parts myself, because only I know what my examiner expects.

---

## Prompt 2: Foundation and the Maven structure

**Prompt I used:** "go ahead, MySQL 8 is installed", then "also there is one more thing that we will need maven structure here".

**What Claude produced:** Stage 1: `pom.xml`, `application.properties` with placeholder database values, `schema.sql` (primary key, foreign key, CHECK constraints), `sample_data.sql` with 10 students, the main class and the `Student` entity. After my second message it added the full Maven layout: `src/test`, `.gitignore`, the Maven wrapper (`mvnw`) and a `StudentTest` with JUnit.

**What I had to correct or verify:** My second message was vague, so Claude had to guess what I meant by "maven structure". I checked that the result was what I wanted. I also have to put my own MySQL username and password into `application.properties`, and run `schema.sql` myself, because Hibernate's `update` mode does not create CHECK constraints.

**Reflection:** A short, unclear instruction gets a guessed answer, so I should say exactly what I want ("add the test folder and mvnw"). It was good that Claude ran `mvn clean compile` and the tests and showed that they pass instead of just saying the code was fine. Reading the entity code line by line also helped me understand encapsulation, `equals` and `hashCode`, which I need for the viva.

---

## Prompt 3: Business logic, REST layer and documents

**Prompt I used:** "next and make everything at once".

**What Claude produced:** The repository, DTOs, enum, exceptions, `StudentServiceImpl` (average, category, stronger subject, merit list with a `Comparator`, same-skills `Map`, the needs-improvement filter, duplicate and score checks), the controller, `GlobalExceptionHandler`, unit tests with a mock repository, `README.md` (with test requests) and `VIVA_NOTES.md`.

**What I had to correct or verify:** Its first attempt to write many files in one shell command failed, and it re-did them with the file tool, so I learned to check that files really exist. The unit tests pass, but they use a mock repository. I still have to run the app against my own MySQL and try the Postman requests, because Claude could not do that without my database password. I also need to be able to explain every method for the viva.

**Reflection:** Doing all stages at once was fast, but I lost the review step between stages, so I must read the code carefully myself. The tests gave me confidence in the business rules, but passing tests do not prove the full app works with MySQL. The most useful part was `VIVA_NOTES.md`, because it shows which collection is used where, and I can practise answering from it.
