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
        name = "results",
        urlPatterns = {"/results"}
)
public class codeMarker extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context=getServletContext();
        String java = "";
        String title = "";
        try {
            java = req.getParameter("final");       //gets code submitted by user
            title = req.getParameter("header").replace("'", "''");  //gets question name
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            FileWriter myWriter = new FileWriter("Main.java");
            myWriter.write(java);       //adds code to java file
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        String command = "cd \"C:\\Users\\Ishaan Thakur\\Computer Science\\BMS\\main_project\" && set path=C:\\Program Files\\Java\\jdk-14.0.2\\bin&&javac Main.java&&java Main";       //command runs java file
        String user_output="";
        try {
            user_output = runcode(command); //gets output when code is ran
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> tests = new ArrayList<String>();
        try {
            tests = dbaccess.getTests(title);   //gets test cases for question
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int marks = 0;
        for(int i = 0;i<tests.size();i++){
            if(java.toUpperCase().contains(tests.get(i).toUpperCase())){    //checks if test case is met
                marks=marks+1;
            }
        }
        String ex_output="";
        try {
            ex_output = dbaccess.getQinfo(title)[1];        //gets expected output for question
            if (user_output.equals(ex_output)){    //checks if user's output matches expected output
                marks=marks+1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FileReader File = new FileReader("resulttemplate");     //file contains the html for the result page
        Scanner s = new Scanner(File);
        String maxscore="";
        try {
            maxscore = dbaccess.getQinfo(title)[2];     //gets the max score for the question
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String percent = Double.toString((double)marks/Integer.parseInt(maxscore)*100);     //calculates percentage
        if((Boolean) context.getAttribute("assignment")){   //checks if questions was done through assignment
            String idString = (String) context.getAttribute("userID");  //gets user's id
            int userid = Integer.parseInt(idString);
            int qid = 0;
            int aid = 0;
            try {
                qid = dbaccess.getQid(title);       //gets question id
                String cname = (String) context.getAttribute("cname");      //gets the students class name
                String atitle = (String) context.getAttribute("atitle") ;   //gets the name of the assignment
                aid = dbaccess.getAssignmentid(cname,atitle);       //gets the id of the assignment
                dbaccess.setResult(userid,qid,aid,marks);       //adds result to database
                context.setAttribute("cname",null);     //resets assignment cookies
                context.setAttribute("atitle",null);
                context.setAttribute("assignment",false);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        while (s.hasNextLine()){
            out.write(s.nextLine().replace("$",Integer.toString(marks)).replace("~",maxscore).replace("@",percent).replace("expected_output",ex_output).replace("user_output",user_output).getBytes());     //displays result page
        }


    }
    public String runcode(String command) throws IOException, InterruptedException{        //runs code
        ProcessBuilder builder=new ProcessBuilder();
        builder.command("cmd.exe","/c",command);        //runs command
        Process process=builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));    //gets normal output
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));   //gets error output
        String readline;
        String readline2;
        String output="";
        int counter = 0;
        while((readline=reader.readLine())!=null){  //formats normal output
            if(counter==0){
                output = output+readline;
            }else {
                output = output + "\n" + readline;
            }
            counter = counter + 1;
        }
        while((readline2=reader2.readLine())!=null){    //formats error output
            output=output+readline2+"\n";
        }
        return output;
    }

}
