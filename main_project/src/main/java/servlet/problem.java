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
import java.util.Scanner;

@WebServlet(
        name = "problem",
        urlPatterns = {"/problem"}
)

public class problem extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context=getServletContext();
        FileReader file = new FileReader("template.txt");   //file contains html for a question page
        String q_title = req.getParameter("clicked");   //gets the name of the question clicked
        String isAssignment = req.getParameter("assignment");   //checks if question is part of an assignment
        String description = "";
        String output = "";
        if(isAssignment!=null){     //check if question is part of assignment
            String a_title = req.getParameter("title");     //gets name of assignment
            String cname = req.getParameter("cname");       //gets name of class with the assignment
            context.setAttribute("assignment",true);        //sets cookies with assignment information
            context.setAttribute("atitle",a_title);
            context.setAttribute("cname",cname);
        }else{
            context.setAttribute("assignment",false);
        }
        try {
            String sqltitle = q_title.replace("'", "''");       //formats question title so it can be checked in database
            String[] info = dbaccess.getQinfo(sqltitle);        //gets question inormation
            description = info[0];
            output = info[1];
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String userInfo = String.format("%s %s - %s",context.getAttribute("fname"),context.getAttribute("lname"),context.getAttribute("status").toString().toUpperCase());
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
            out.write(s.nextLine().replace("problem_title",q_title).replace("description",description).replace("expected_output",output).replace("qa",userInfo).getBytes());    //displays question page
        }
    }


}
