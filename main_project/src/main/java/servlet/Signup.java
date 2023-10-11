package servlet;

import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.Scanner;

@WebServlet(
        name = "registration",
        urlPatterns = {"/signup"}
)
public class Signup extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context=getServletContext();
        String fname = "";
        String lname = "";
        String email = "";
        String pword = "";
        String status = "";
        try {
            fname = req.getParameter("fname");  //get info from html form:
            lname = req.getParameter("lname");
            email = req.getParameter("email");
            pword = req.getParameter("pword");
            status = req.getParameter("button_clicked");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServletOutputStream out = resp.getOutputStream();
        try {
            Boolean success = dbaccess.addUser(fname,lname,status,email,pword);  //add user to the database
            if(success){        //checks if user was added to database
                req.setAttribute("loggedin", true);  //user is logged in
                String[] userinfoarray = dbaccess.getUserInfo(email);
                context.setAttribute("fname",userinfoarray[0]);   //cookies with user's info:
                context.setAttribute("lname",userinfoarray[1]);
                context.setAttribute("status",userinfoarray[2]);
                context.setAttribute("email",userinfoarray[3]);
                context.setAttribute("userID",userinfoarray[4]);
                String userInfo = String.format("%s %s - %s",userinfoarray[0],userinfoarray[1],userinfoarray[2].toUpperCase());     //formats user's info to display on page
                FileReader file = new FileReader("home.txt");       //text file contains home page
                Scanner s = new Scanner(file);
                if(context.getAttribute("status").equals("teacher")){      //if the user is a teacher they can add classes
                    String form = "<form method=\"post\" action=\"class\">\n" +     //adds create class button for teacher
                            "                 <button type=\"submit\" class=\"classes\">Create Class</button>\n" +
                            "            </form>";
                    while(s.hasNextLine()) {
                        out.write(s.nextLine().replace("*p*", dbaccess.getProblems()).replace("qa", userInfo).replace("*c*", form).getBytes());     //displays home page
                    }
                }
                else{
                    while(s.hasNextLine()){
                        out.write(s.nextLine().replace("*p*",dbaccess.getProblems()).replace("qa",userInfo).replace("*c*","").getBytes());      //displays home page
                    }
                }
            }
            else{       //if user doesn't have an account
                FileReader file = new FileReader("signupfail");   //file contains login page with sign up error
                Scanner s = new Scanner(file);
                while(s.hasNextLine())
                    out.write(s.nextLine().getBytes());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        out.flush();
        out.close();

    }
}