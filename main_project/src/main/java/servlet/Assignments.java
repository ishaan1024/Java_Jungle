package servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

@WebServlet(
        name = "assignments",
        urlPatterns = {"/assignments"}
)

public class Assignments extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletContext();
        if (context.getAttribute("status").equals("teacher")) {     //check if user is a teacher
            FileReader file = new FileReader("teacherAssignments.txt");     //file contains html for assignments page for teacher
            int teacherID = Integer.parseInt((String) context.getAttribute("userID"));      //gets teacher's user id from cookie
            String classhtml = "";
            String studenthtml = "";
            String problemhtml = "";
            String assignmenthtml = "";
            String assignment = req.getParameter("deadline");       //gets all classes for assignments to be created if assignment was created
            try {
                classhtml = dbaccess.getClasses(teacherID);     //gets html for classes
                studenthtml = dbaccess.getStudents();       //gets html for all students
                problemhtml = dbaccess.getProblemnames();      //gets html for all questions
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Scanner s = new Scanner(file);
            String userInfo = String.format("%s %s - %s", context.getAttribute("fname"), context.getAttribute("lname"), context.getAttribute("status").toString().toUpperCase());
            if (assignment != null) {       //checks if assignment was created
                String classnames = assignment.substring(0, assignment.length() - 1);
                String[] classes = classnames.split("~");       //gets all the individual classes
                String date = req.getParameter("date");
                String title = req.getParameter("title");
                String problems = req.getParameter("problems").substring(0, req.getParameter("problems").length() - 1);
                try {
                    for (int i = 0; i < classes.length; i++) {
                        dbaccess.createAssignment(classes[i], date, title, problems);       //creates assignments for all classes
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                assignmenthtml = dbaccess.getTeacherAssignments(teacherID);     //gets all existing assignments for teacher
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while (s.hasNextLine()) {
                out.write(s.nextLine().replace("qa", userInfo).replace("*a*", assignmenthtml).replace("*c*", classhtml).replace("*s*", studenthtml).replace("*p*", problemhtml).getBytes());        //displays teacher's assignments page
            }
        }
        else{
            FileReader file = new FileReader("studentAssignments");     //file contains html for student's assignments page
            Scanner s = new Scanner(file);
            String userInfo = String.format("%s %s - %s", context.getAttribute("fname"), context.getAttribute("lname"), context.getAttribute("status").toString().toUpperCase());
            String assignmenthtml = "";
            try {
                int id = Integer.parseInt((String)context.getAttribute("userID"));
                assignmenthtml = dbaccess.getStudentAssignments(id);        //gets all assignments set for student
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while (s.hasNextLine()) {
                out.write(s.nextLine().replace("qa", userInfo).replace("*a*", assignmenthtml).getBytes());      //displays page
            }
        }
    }
}