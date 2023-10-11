package servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Scanner;

@WebServlet(
        name = "handler",
        urlPatterns = {"/handler"}
)

public class codehandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        String code="";
        code = req.getParameter("code");        //gets code from user
            try{
                FileWriter myWriter = new FileWriter("Main.java");
                myWriter.write(code);   //writes code into java file
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            String command = "cd \"C:\\Users\\Ishaan Thakur\\Computer Science\\BMS\\main_project\" && set path=C:\\Program Files\\Java\\jdk-14.0.2\\bin&&javac Main.java&&java Main";   //command runs java file
            String output="";
            try {
                output = runcode(command);      //gets output from code when ran
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.write(output.getBytes());       //writes output on iframe on question page

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

