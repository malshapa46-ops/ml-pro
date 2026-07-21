-- 1. Database එක නිර්මාණය කිරීම සහ තෝරාගැනීම
CREATE DATABASE IF NOT EXISTS student_system;
USE student_system;

-- 2. Login / Sign Up සඳහා Users Table එක
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

-- 3. ශිෂ්‍ය තොරතුරු සඳහා Student Table එක
CREATE TABLE IF NOT EXISTS student (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dob DATE,
    gender VARCHAR(10),
    address TEXT,
    phone VARCHAR(15),
    email VARCHAR(100)
);

-- 4. ගුරුවරුන් සඳහා Teacher Table එක
CREATE TABLE IF NOT EXISTS teacher (
    teacher_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    specialization VARCHAR(100)
);

-- 5. පාඨමාලා / විෂයන් සඳහා Course Table එක
CREATE TABLE IF NOT EXISTS course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    duration VARCHAR(50),
    fee DOUBLE
);

-- 6. ශිෂ්‍යයන් සහ පාඨමාලා සම්බන්ධ කිරීමට Student_Course Table එක
CREATE TABLE IF NOT EXISTS student_course (
    student_id INT,
    course_id INT,
    enroll_date DATE,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
);

-- 7. පැමිණීමේ වාර්තා සඳහා Attendance Table එක
CREATE TABLE IF NOT EXISTS attendance (
    att_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    att_date DATE,
    status TINYINT(1) DEFAULT 0, -- 1 = Present, 0 = Absent
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (student_id, att_date)
);

-- -------------------------------------------------------------
-- මූලික දත්ත ඇතුළත් කිරීම (Default Sample Data)
-- -------------------------------------------------------------

-- Default Admin පරිශීලකයා (Username: admin | Password: 123)
INSERT INTO users (username, password) 
VALUES ('admin', '123')
ON DUPLICATE KEY UPDATE username=username;

-- Sample Course
INSERT INTO course (course_name, duration, fee) 
VALUES ('Java Programming', '6 Months', 25000.00);

-- Sample Teacher
INSERT INTO teacher (name, email, phone, specialization) 
VALUES ('Kamal Perera', 'kamal@gmail.com', '0712345678', 'Computer Science');