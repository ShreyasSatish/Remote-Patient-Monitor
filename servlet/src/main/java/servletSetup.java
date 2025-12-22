import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(urlPatterns={"/patients","/doctors"},loadOnStartup = 1)
public class servletSetup extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String test = req.getServletPath();
        if (test.equals("/doctors")){
            resp.setContentType("text/html");
            resp.getWriter().write("Hello, doctors!");
        }

        if (test.equals("/patients")){
            resp.setContentType("text/html");
            resp.getWriter().write("Hello, patient!");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read the bytes of the body – ie the message
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        resp.setContentType("text/html");
        resp.getWriter().write("Thank you client! "+reqBody);

    }
}
