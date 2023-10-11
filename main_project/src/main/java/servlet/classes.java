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
        name = "class",
        urlPatterns = {"/class"}
)

public class classes extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context=getServletContext();
        FileReader file = new FileReader("createclass.txt");        //file contains html page for teacher to create class
        Scanner s = new Scanner(file);
        String userInfo = String.format("%s %s - %s",context.getAttribute("fname"),context.getAttribute("lname"),context.getAttribute("status").toString().toUpperCase());
        String form = "";
        try {
            form = dbaccess.getStudents();      //gets all students for form
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (s.hasNextLine()){
            out.write(s.nextLine().replace("qa",userInfo).replace("*s*",form).getBytes());      //displays page
        }
    }
}
