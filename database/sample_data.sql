-- Database layer: 10 sample students. Run after schema.sql.
USE student_db;

INSERT INTO students VALUES
('S101', 'Aarav Sharma',  'aarav@college.edu', 'B.Tech CSE',    92, 88),
('S102', 'Diya Verma',    'diya@college.edu',  'B.Tech CSE',    78, 81),
('S103', 'Rohan Mehta',   'rohan@college.edu', 'B.Tech IT',     45, 80),  -- needs programming help, maths >= 75
('S104', 'Sneha Iyer',    'sneha@college.edu', 'B.Tech CSE-AI', 60, 55),
('S105', 'Karan Singh',   'karan@college.edu', 'B.Tech IT',     35, 40),
('S106', 'Meera Nair',    'meera@college.edu', 'B.Tech CSE-AI', 85, 85),  -- both equal
('S107', 'Vikram Rao',    'vikram@college.edu','B.Tech CSE',    30, 90),  -- needs programming help, maths >= 75
('S108', 'Ananya Gupta',  'ananya@college.edu','B.Tech IT',     72, 66),
('S109', 'Ishaan Patel',  'ishaan@college.edu','B.Tech CSE',    55, 70),
('S110', 'Pooja Reddy',   'pooja@college.edu', 'B.Tech CSE-AI', 95, 91);

INSERT INTO student_skills (student_id, skill) VALUES
('S101', 'Java'), ('S101', 'Spring Boot'), ('S101', 'SQL'),
('S102', 'Java'), ('S102', 'Python'),
('S103', 'Python'), ('S103', 'SQL'),
('S104', 'Python'), ('S104', 'Machine Learning'),
('S105', 'HTML'), ('S105', 'CSS'),
('S106', 'Machine Learning'), ('S106', 'Python'),
('S107', 'C++'), ('S107', 'SQL'),
('S108', 'HTML'), ('S108', 'JavaScript'),
('S109', 'Java'),
('S110', 'Machine Learning'), ('S110', 'Java'), ('S110', 'Python');
