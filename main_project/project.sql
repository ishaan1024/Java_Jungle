CREATE TABLE Users(
    userID INTEGER PRIMARY KEY AUTOINCREMENT,
    fname VARCHAR(30),
    lname VARCHAR(30),
    status VARCHAR(7),
    email VARCHAR(40),
    hash INTEGER
);
CREATE TABLE Attempts(
    userID INTEGER,
    questionID INTEGER,
    assignmentID INTEGER,
    marks INTEGER,
    PRIMARY KEY(userID,questionID,assignmentID),
    FOREIGN KEY(userID) References Users,
    FOREIGN KEY(questionID) References Questions,
    FOREIGN KEY(assignmentID) References Assignments
);
CREATE TABLE Questions(
    questionID INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(50),
    maxscore INTEGER,
    description VARCHAR(1000),
    output VARCHAR(1000)
);
CREATE TABLE Tests(
    TestID INTEGER PRIMARY KEY AUTOINCREMENT,
    questionID INTEGER,
    code VARCHAR(100),
    FOREIGN KEY(questionID) References Questions
);
CREATE TABLE Hints(
    HintID INTEGER PRIMARY KEY AUTOINCREMENT,
    questionID INTEGER,
    hint VARCHAR(1000),
    FOREIGN KEY(questionID) References Questions
);
CREATE TABLE Members(
    userID INTEGER,
    ClassID INTEGER,
    PRIMARY KEY(userID,ClassID),
    FOREIGN KEY(userID) References Users,
    FOREIGN KEY(ClassID) References Classes
);
CREATE TABLE Classes(
    ClassID INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(30),
    teacher INTEGER,
    FOREIGN KEY(teacher) References Users(userID)
);
CREATE TABLE Assignments(
    assignmentID INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(30),
    classname VARCHAR(30),
    duedate DATE,
    FOREIGN KEY(classname) References Classes(name)
);
CREATE TABLE setWork(
    assignmentID INTEGER,
    ClassID INTEGER,
    PRIMARY KEY(assignmentID,ClassID),
    FOREIGN KEY(assignmentID) References Assignments,
    FOREIGN KEY(ClassID) References Classes
);
CREATE TABLE Allocation(
    questionID INTEGER,
    assignmentID INTEGER,
    PRIMARY KEY(questionID,assignmentID),
    FOREIGN KEY(questionID) References Questions,
    FOREIGN KEY(assignmentID) References Assignments

);