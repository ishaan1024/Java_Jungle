package servlet;

import java.sql.*;
import java.util.ArrayList;

public class dbaccess {
    private final String databaseName;
    public dbaccess(String DatabaseName) {
        databaseName = DatabaseName;
    }

    static String contentPath = "src\\main\\project.db";
    static String url = "jdbc:sqlite:" + contentPath + "/";

    public static Connection openConnection() {     //connects to database
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String[] getUserInfo(String email) throws SQLException {      //gets all of user's information
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(String.format("select fname,lname,status,email,userID from Users where email='%s'",email));
        String[] userInfo = new String[5];
        userInfo[0] = set.getString("fname");
        userInfo[1] = set.getString("lname");
        userInfo[2] = set.getString("status");
        userInfo[3] = set.getString("email");
        userInfo[4] = set.getString("userID");
        conn.close();
        return userInfo;
    }
    public static Boolean addUser(String fname,String lname,String status,String email, String password) throws SQLException {      //adds user to database if account is valid
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(String.format("select * from Users where email='%s'",email));
        int counter = 0;
        while(set.next()){
            counter++;
        }
        if(fname.equals("") || lname.equals("") || email.equals("")){   //checks if fields are empty
            conn.close();
            return false;
        }
        if(counter==0){     //checks if email hasn't been used in another account
            stmt.executeUpdate("INSERT INTO Users(fname,lname,status,email,hash) VALUES ('"+fname+"','"+lname+"','"+status+"','"+email+"','"+hash(password)+"');");     //adds user to database
            conn.close();
            return true;
        }
        else{
            conn.close();
            return false;
        }
    }
    public static void setResult(int userID,int questionID,int assignmentID,int marks) throws SQLException {        //adds user's assignment result to database
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(String.format("insert into Attempts(userID,questionID,assignmentID,marks) values (%d,%d,%d,%d);",userID,questionID,assignmentID,marks));       //adds result to database
        conn.close();
    }
    public static int checkIfQuestionComplete(int userId,int questionId,int assignmentId) throws SQLException {         //checks if a question from an assignment has already been attempted
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Attempts where userID=%d and questionID=%d and assignmentID=%d",userId,questionId,assignmentId));      //checks if there is an attempt in the databse
        // if mark is equal to 14032022 then there has been no attempt
        int marks = 14032022;
        if(set.next()){     //gets marks if question has been attempted
             marks = set.getInt("marks");
        }
        conn.close();
        return marks;
    }

    public static boolean checkUser(String email,String password) throws SQLException {     //checks if account exists when logging in
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Users where email='%s' and hash=%d;",email,hash(password)));
        int counter = 0;        //keeps track of whether account has been found
        while (set.next()){
            if(!set.getString("fname").equals("")){
                counter++;
            }
        }
        if(counter>=1){
            conn.close();
            return true;
        }
        else{
            conn.close();
            return false;
        }
    }
    public static String getProblems() throws SQLException {        //gets all questions to be displayed on home page
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Questions;"));
        int count = 0;      //checks how many questions there are
        while(set.next()){
            count++;
        }
        String[][] problems = new String[count][2];
        int i = 0;
        ResultSet set2;
        set2 = stmt.executeQuery(String.format("select * from Questions order by maxscore;"));
        while (set2.next()){        //gets title and maxscore for each questions
            problems[i][0] = set2.getString("title");
            problems[i][1] = set2.getString("maxscore");
            i++;
        }
        String html = "";
        for(int j =0;j<problems.length;j++){        //formats questions into html to be displayed on home page
            html = html + String.format("<div class=\"problem\">\n" +
                    "                <p class=\"ptitle\">%s</p>\n" +
                    "                <p class=\"align\">Max Score: %d</p>\n" +
                    "                <button class=\"button\" type=\"Submit\" name=\"clicked\" value=\"%s\" form=\"solvebutton\" type=\"button\">Solve</button>\n" +
                    "            </div>\n",problems[j][0],Integer.parseInt(problems[j][1]),problems[j][0]);
        }
        conn.close();
        return html;
    }
    public static String getTeacherAssignments(int id) throws SQLException {        //gets assignments to be displayed on a teacher account
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Assignments inner join setWork on Assignments.assignmentID=setWork.assignmentID inner join Classes on setWork.ClassID=Classes.ClassID inner join Users on Classes.teacher=Users.userID where userID=%d",id));
        int count = 0;      //checks how many assignments exist
        while(set.next()){
            count++;
        }
        String[][] assignments = new String[count][4];
        int i = 0;
        ResultSet set2;
        set2 = stmt.executeQuery(String.format("select * from Assignments inner join setWork on Assignments.assignmentID=setWork.assignmentID inner join Classes on setWork.ClassID=Classes.ClassID inner join Users on Classes.teacher=Users.userID where userID=%d",id));
        while (set2.next()){        //gets assignment's information
            assignments[i][0] = set2.getString("title");
            assignments[i][1] = set2.getString("classname");
            assignments[i][2] = set2.getString("duedate");
            assignments[i][3] = set2.getString("assignmentID");
            i++;
        }
        String html = "";
        for(int j =0;j<assignments.length;j++){
            ResultSet set3;
            set3 = stmt.executeQuery(String.format("select * from Questions inner join Allocation on Questions.questionID=Allocation.questionID inner join Assignments on Allocation.assignmentID=Assignments.assignmentID where Assignments.assignmentID = %d;",Integer.parseInt(assignments[j][3])));
            int c = 0;  //checks how many questions there are in each assignment
            while (set3.next()){
                c++;
            }
            String[][] questions = new String[c][3];
            int x = 0;
            ResultSet set4;
            set4 = stmt.executeQuery(String.format("select * from Questions inner join Allocation on Questions.questionID=Allocation.questionID inner join Assignments on Allocation.assignmentID=Assignments.assignmentID where Assignments.assignmentID = %d;",Integer.parseInt(assignments[j][3])));
            while (set4.next()){    //gets question information
                questions[x][0] = set4.getString("title");
                questions[x][1] = set4.getString("maxscore");
                questions[x][2] = set4.getString("questionID");
                x++;
            }
            String shtml="";
            for (int z = 0;z<questions.length;z++){
                ResultSet set5 = stmt.executeQuery(String.format("select * from Attempts where assignmentID=%d and questionID=%d;",Integer.parseInt(assignments[j][3]),Integer.parseInt(questions[z][2])));
                int y = 0;      //checks how many student attempts there have been for each question
                while (set5.next()){
                    y++;
                }
                String[][] scores = new String[y][2];
                int k = 0;
                ResultSet set6 = stmt.executeQuery(String.format("select * from Attempts inner join Users on Attempts.userID=Users.userID where assignmentID=%d and questionID=%d order by fname;",Integer.parseInt(assignments[j][3]),Integer.parseInt(questions[z][2])));
                while (set6.next()){        //gets the user and the marks for each attempt
                    scores[k][0] = set6.getString("userID");
                    scores[k][1] = set6.getString("marks");
                    k++;
                }
                for (int l = 0; l < scores.length; l++) {
                    ResultSet set7 = stmt.executeQuery(String.format("select * from Users where userID=%d;",Integer.parseInt(scores[l][0])));
                    shtml = shtml + String.format("                <p class=\"writing\" style=\"display: none;\" name=\"a%d\">%s - %s %s - %d/%d</p>\n",j,questions[z][0],set7.getString("fname"),set7.getString("lname"),Integer.parseInt(scores[l][1]),Integer.parseInt(questions[z][1]));    //formats student attempts into html
                }
            }
            html = html + String.format("<div class=\"assignment\">\n" +        //formats assignments into html
                    "                <p class=\"title\">%s</p>\n" +
                    "                <p class=\"align\">Class: %s</p>\n" +
                    "                <p class=\"align\">Due: %s</p>\n" +
                    "                <button class=\"button\" type=\"Submit\" onClick=\"showScores(this.id)\" id=\"a%d\" type=\"button\">View Scores</button>\n" +shtml+
                    "            </div>\n",assignments[j][0],assignments[j][1],assignments[j][2],j);
        }
        conn.close();
        return html;
    }
    public static String getStudentAssignments(int id) throws SQLException {        //gets assignments set for student accounts
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Assignments inner join setWork on Assignments.assignmentID=setWork.assignmentID inner join Classes on setWork.ClassID=Classes.ClassID inner join Members on Classes.ClassID=Members.ClassID where userID=%d;",id));
        int count = 0;  //checks how many assignments there are
        while(set.next()){
            count++;
        }
        String[][] assignments = new String[count][4];
        int i = 0;
        ResultSet set2;
        set2 = stmt.executeQuery(String.format("select * from Assignments inner join setWork on Assignments.assignmentID=setWork.assignmentID inner join Classes on setWork.ClassID=Classes.ClassID inner join Members on Classes.ClassID=Members.ClassID where userID=%d;",id));
        while (set2.next()){        //gets assignment's information
            assignments[i][0] = set2.getString("title");
            assignments[i][1] = set2.getString("classname");
            assignments[i][2] = set2.getString("duedate");
            assignments[i][3] = set2.getString("assignmentID");
            i++;
        }
        String html = "";
        for(int j =0;j<assignments.length;j++){
            ResultSet set3;
            set3 = stmt.executeQuery(String.format("select * from Questions inner join Allocation on Questions.questionID=Allocation.questionID inner join Assignments on Allocation.assignmentID=Assignments.assignmentID where Assignments.assignmentID = %d;",Integer.parseInt(assignments[j][3])));
            int c = 0;      //checks how many questions there are for each assignment
            while (set3.next()){
                c++;
            }
            String[][] questions = new String[c][3];
            int x = 0;
            ResultSet set4;
            set4 = stmt.executeQuery(String.format("select * from Questions inner join Allocation on Questions.questionID=Allocation.questionID inner join Assignments on Allocation.assignmentID=Assignments.assignmentID where Assignments.assignmentID = %d;",Integer.parseInt(assignments[j][3])));
            while (set4.next()){    //gets question information from database
                questions[x][0] = set2.getString("title");
                questions[x][1] = set2.getString("maxscore");
                questions[x][2] = set2.getString("questionID");
                x++;
            }
            String qhtml = "";
            for(int z =0;z<questions.length;z++){
                if(dbaccess.checkIfQuestionComplete(id,Integer.parseInt(questions[z][2]),Integer.parseInt(assignments[j][3]))==14032022) {      //checks that question hasn't been attempted already
                    qhtml = qhtml + String.format(  //formats each question for each assignment into html
                            "                <div class=\"problem\" name=\"a%d\" style=\"display: none;\">\n" +
                            "                <form method=\"post\" action=\"problem\" id=\"a%d\">\n" +
                            "                   <p class=\"title\">%s</p>\n" +
                            "                   <p class=\"align\">Max Score: %d</p>\n" +
                            "                   <input type = \"hidden\" value=\"%s\"  name=\"title\">\n" +
                            "                   <input type = \"hidden\" value=\"%s\"  name=\"cname\">\n" +
                            "                   <input type=\"hidden\" value=\"assignment\" name=\"assignment\">"+
                            "                   <button class=\"button\" type=\"Submit\" value=\"%s\" name=\"clicked\"  type=\"button\">Solve</button>\n" +
                            "               </form>\n"+
                            "            </div>\n",j,j, questions[z][0], Integer.parseInt(questions[z][1]), assignments[j][0], assignments[j][1], questions[z][0]);
                }else{  //formats each question so that it cannot be attempted again
                    qhtml = qhtml + String.format("<div class=\"problem\" name=\"a%d\" style=\"display: none;\">\n" +
                            "                <p class=\"title\">%s</p>\n" +
                            "                <p class=\"align\">Max Score: %d</p>\n" +
                            "                <input type = \"hidden\" value=\"%s\" name=\"title\">\n" +
                            "                <input type = \"hidden\" value=\"%s\" name=\"cname\">\n" +
                            "                <p class=\"align\">Score = %d</p>\n" +
                            "            </div>\n",j,questions[z][0], Integer.parseInt(questions[z][1]), assignments[j][0], assignments[j][1],dbaccess.checkIfQuestionComplete(id,Integer.parseInt(questions[z][2]),Integer.parseInt(assignments[j][3])));
                }
            }
            html = html + String.format("<div class=\"assignment\">\n" +    //formats each assignment into html
                    "                <p class=\"title\">%s</p>\n" +
                    "                <p class=\"align\">Class: %s</p>\n" +
                    "                <p class=\"align\">Due: %s</p>\n" +
                    "                <button class=\"button\" id=\"a%d\" onClick=\"showQuestions(this.id)\" type=\"button\">View Questions</button>\n" +
                    "            </div>\n",assignments[j][0],assignments[j][1],assignments[j][2],j);
            html = html+qhtml;
        }
        conn.close();
        return html;
    }
    public static String getStudents() throws SQLException{     //gets all students on database for the teacher to create a class
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Users where status='student';"));
        int count = 0;      //checks how many students there are
        while(set.next()){
            count++;
        }
        String[][] students = new String[count][2];
        int i = 0;
        ResultSet set2;
        set2 = stmt.executeQuery(String.format("select * from Users where status='student' order by fname;"));
        while (set2.next()){        //gets first and last name of each students
            students[i][0] = set2.getString("fname");
            students[i][1] = set2.getString("lname");
            i++;
        }
        String html = "";
        for(int j = 0;j<students.length;j++){
            html = html + String.format("<input type=\"checkbox\" name=\"student\" value=\"%s %s\">\n" +        //formats each student to be a checkbox in html
                    "<label for=\"student\" name=\"student2\"> %s %s</label><br>",students[j][0],students[j][1],students[j][0],students[j][1]);
        }
        conn.close();
        return html;
    }

    public static int getUserID(String fname,String lname) throws SQLException {    //gets a user's id
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Users where fname='%s' and lname='%s';",fname,lname));
        int id = Integer.parseInt(set.getString("userID"));
        conn.close();
        return id;
    }
    public static String[] getQinfo(String title) throws SQLException {     //gets the details of a question
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Questions where title = '%s';",title));
        String[] info = new String[3];
        info[0] = set.getString("description");
        info[1] = set.getString("output");
        info[2] = Integer.toString(set.getInt("maxscore"));
        conn.close();
        return info;
    }
    public static int getQid(String title) throws SQLException {        //gets the id of a question
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Questions where title = '%s';",title));
        int qid = Integer.parseInt(set.getString("questionID"));
        conn.close();
        return qid;
    }
    public static int getAssignmentid(String cname, String title) throws SQLException {     //gets the id of an assignment
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Assignments where classname = '%s' and title='%s';",cname,title));
        int assignId = set.getInt("assignmentID");
        conn.close();
        return assignId;
    }
    public static ArrayList<String> getTests(String title) throws SQLException {        //gets the test cases for a question
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        int id = getQid(title);
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Tests where questionID = %d;",id));
        ArrayList<String> tests = new ArrayList<String>();
        while (set.next()){
            tests.add(set.getString("code"));
        }
        conn.close();
        return tests;
    }
    public static int getClassID(String name) throws SQLException {     //gets the id of a class
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Classes where name='%s'",name));
        int id = Integer.parseInt(set.getString("ClassID"));
        conn.close();
        return id;
    }

    public static void createClass(String className,String[] students,int teacherID) throws SQLException{       //adds a class to the database
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(String.format("insert into Classes(name,teacher) values ('%s',%d);",className,teacherID));   //creates the class
        int classid = dbaccess.getClassID(className);
        for(int i = 0;i<students.length;i++){
            String[] name = students[i].split(" ");
            int userid = dbaccess.getUserID(name[0],name[1]);
            stmt.executeUpdate(String.format("insert into Members(userID,ClassID) values (%d,%d);",userid,classid));        //adds students to class in the database
        }
        conn.close();
    }
    public static String getClasses(int teacherID) throws SQLException{     //gets all of a teacher's classes for creating an assignment
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Classes where teacher=%d;",teacherID));
        int count = 0;      //checks how many classes there are
        while(set.next()){
            count++;
        }
        String[] classes = new String[count];
        int i = 0;
        ResultSet set2;
        set2 = stmt.executeQuery(String.format("select * from Classes where teacher=%d;",teacherID));
        while (set2.next()){        //gets the name of the classes
            classes[i] = set2.getString("name");
            i++;
        }
        String html = "";
        for(int j = 0;j<classes.length;j++){        //formats the classes to be checkboxes in html
            html = html + String.format("<input type=\"checkbox\" name=\"class\" value=\"%s\">\n" +
                    "<label for=\"class\" name=\"class2\"> %s </label><br>",classes[j],classes[j]);
        }
        conn.close();
        return html;
    }
    public static String getProblemnames() throws SQLException {        //gets names of all questions for creating an assignment
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet set;
        set = stmt.executeQuery(String.format("select * from Questions;"));
        int count = 0;      //checks how many questions there are
        while(set.next()){
            count++;
        }
        String[] problems = new String[count];
        int i = 0;
        ResultSet set2;
        set2 = stmt.executeQuery(String.format("select * from Questions;"));
        while (set2.next()){    //gets all the names from database
            problems[i] = set2.getString("title");
            i++;
        }
        String html = "";
        for(int j = 0;j<problems.length;j++){       //formats the questions to be checkboxes in html
            html = html + String.format("<input type=\"checkbox\" name=\"problem\" value=\"%s\">\n" +
                    "<label for=\"problem\" name=\"problem2\"> %s </label><br>",problems[j],problems[j]);
        }
        conn.close();
        return html;
    }
    public static void createAssignment(String cname,String deadline,String title,String problems) throws SQLException {        //adds assignment to database
        problems = problems.replace("'","''");      //this is done so that the questions can be added to the database
        String[] problemarray = problems.split("~");
        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(String.format("insert into Assignments(title,classname,duedate) values ('%s','%s','%s');",title,cname,deadline));    //adds assignment to database
        conn.close();
        Connection conn2 = openConnection();
        Statement stmt2 = conn2.createStatement();
        int classid = dbaccess.getClassID(cname);
        int assignid = dbaccess.getAssignmentid(cname,title);
        stmt2.executeUpdate(String.format("insert into setWork(assignmentID,ClassID) values (%d,%d);",assignid,classid));   //adds the assignment set for the classes to the database
        conn2.close();
        for (int i = 0;i<problemarray.length;i++){
            Connection conn3 = openConnection();
            Statement stmt3 = conn3.createStatement();
            int qid = dbaccess.getQid(problemarray[i]);
            stmt3.executeUpdate(String.format("insert into Allocation(questionID,assignmentID) values (%d,%d);",qid,assignid));     //adds the questions for each assignment to the database
            conn3.close();
        }
    }
    public static int hash(String password){        //hashing algorithm to hash the password
        int key = 0;
        for(int i = 0;i<password.length();i++){
            int val = password.charAt(i);
            key=key+val;
        }
        key = key % 1039;
        return key;
    }

}

