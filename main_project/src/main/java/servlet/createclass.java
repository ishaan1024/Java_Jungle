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
        name = "createclass",
        urlPatterns = {"/createclass"}
)

public class createclass extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletContext();
        String classname = req.getParameter("cname");       //gets name of class
        String nameString = req.getParameter("newClass");       //gets names of students to be added to class
        String[] names = nameString.split(",");     //gets individual names
        String teacher = (String) context.getAttribute("userID");   //gets teacher's user id
        int teacherID = Integer.parseInt(teacher);
        try {
            dbaccess.createClass(classname,names,teacherID);    //adds class to database
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
