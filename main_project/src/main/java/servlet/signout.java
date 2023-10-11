package servlet;

import javax.servlet.RequestDispatcher;
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
        name = "signout",
        urlPatterns = {"/"}
)

public class signout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        context.setAttribute("loggedin",false); //resets all cookies
        context.setAttribute("fname", null);
        context.setAttribute("lname", null);
        context.setAttribute("status", null);
        context.setAttribute("email", null);
        context.setAttribute("userID", null);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("index.jsp");    //goes back to login/sign-up page
        requestDispatcher.forward(req, resp);
    }
}
