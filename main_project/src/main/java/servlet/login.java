package servlet;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

@WebServlet(
        name = "login",
        urlPatterns = {"/login"}
)
public class login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = "";
        String password = "";
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context=getServletContext();
        try{
            email = req.getParameter("email");     //get user's email
            password = req.getParameter("pword");  //get user's hashed password
            if(context.getAttribute("loggedin")!=null&& (boolean) context.getAttribute("loggedin")){     //check if user has already logged in
                FileReader file = new FileReader("home.txt");       //text file contains html for home page
                Scanner s = new Scanner(file);
                String userInfo = String.format("%s %s - %s",context.getAttribute("fname"),context.getAttribute("lname"),context.getAttribute("status").toString().toUpperCase());  //formats user's info to display on page
                if(context.getAttribute("status").equals("teacher")){  //checks if user is a teacher
                    String form = "<form method=\"post\" action=\"class\">\n" +         //adds create class button for teachers
                            "                 <button type=\"submit\" class=\"classes\">Create Class</button>\n" +
                            "            </form>";
                    while(s.hasNextLine()) {
                        out.write(s.nextLine().replace("*p*", dbaccess.getProblems()).replace("qa", userInfo).replace("*c*", form).getBytes());         //displays home page
                    }
                }
                else{
                    while(s.hasNextLine()){
                        out.write(s.nextLine().replace("*p*",dbaccess.getProblems()).replace("qa",userInfo).replace("*c*","").getBytes());         //displays home page
                    }
                }
            }
            else {  //if not already logged in:
                Boolean exists = dbaccess.checkUser(email, password); //check if account exists
                if (exists) {
                    context.setAttribute("loggedin", true);
                    FileReader file = new FileReader("home.txt");
                    String[] userinfoarray = dbaccess.getUserInfo(email);
                    context.setAttribute("fname", userinfoarray[0]); //cookies with user's info:
                    context.setAttribute("lname", userinfoarray[1]);
                    context.setAttribute("status", userinfoarray[2]);
                    context.setAttribute("email", userinfoarray[3]);
                    context.setAttribute("userID", userinfoarray[4]);
                    String userInfo = String.format("%s %s - %s", userinfoarray[0], userinfoarray[1], userinfoarray[2].toUpperCase());
                    Scanner s = new Scanner(file);
                    if (context.getAttribute("status").equals("teacher")) {
                        String form = "<form method=\"post\" action=\"class\">\n" +   //adds create class button for teachers
                                "                 <button type=\"submit\" class=\"classes\">Create Class</button>\n" +
                                "            </form>";
                        while (s.hasNextLine()) {
                            out.write(s.nextLine().replace("*p*", dbaccess.getProblems()).replace("qa", userInfo).replace("*c*", form).getBytes());         //displays home page
                        }
                    } else {
                        while (s.hasNextLine()) {
                            out.write(s.nextLine().replace("*p*", dbaccess.getProblems()).replace("qa", userInfo).replace("*c*", "").getBytes());       //displays home page
                        }
                    }
                } else { //if user not logged in and doesn't have an account
                    FileReader file = new FileReader("loginfail.txt");          //file contains login page with error message
                    Scanner s = new Scanner(file);
                    while (s.hasNextLine())
                        out.write(s.nextLine().getBytes());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

