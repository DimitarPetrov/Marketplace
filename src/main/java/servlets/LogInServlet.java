package servlets;


import data.Product;
import data.User;
import exceptions.NoSuchUserException;
import repositories.UserRepository;
import util.Utilities;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

@WebServlet(urlPatterns = "/login")
public class LogInServlet extends HttpServlet {

    @EJB(beanName = "UserRepositoryInDatabase")
    private UserRepository repo;

    public LogInServlet(){}

    public LogInServlet(UserRepository repo){
        this.repo = repo;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User attribute = (User)session.getAttribute("authenticated");
        if(attribute != null){
           resp.setStatus(200);
        } else {
            String credentials = req.getHeader("Authorization").split(" ")[1];
            byte[] bytes = Base64.getDecoder().decode(credentials);
            String plainTextCredentials = new String(bytes);
            String username = plainTextCredentials.split(":")[0];
            String password = Utilities.encrypt(plainTextCredentials.split(":")[1]);
            try {
                User user = repo.getUserByName(username);
                if(user.getPassword().equals(password)) {
                    session.setAttribute("authenticated", user);
                    session.setMaxInactiveInterval(10 * 60);
                    resp.setStatus(200);
                } else {
                    resp.setStatus(400);
                }
            } catch (NoSuchUserException e){
                resp.setStatus(400);
            }
        }

    }
}
