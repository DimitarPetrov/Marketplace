package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Permissions;
import data.User;
import exceptions.UserAlreadyExistsException;
import repositories.UserRepository;
import util.Utilities;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    @EJB(beanName = "UserRepositoryInDatabase")
    private UserRepository repo;

    public RegisterServlet(){}

    public RegisterServlet(UserRepository repo){
        this.repo = repo;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte[] bytes = req.getInputStream().readAllBytes();
        String credentials = new String(bytes);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(credentials).getAsJsonObject();
        String username = jsonObject.get("username").getAsString();
        String password = jsonObject.get("password").getAsString();
        String REpassword = jsonObject.get("repeat_password").getAsString();
        if(!password.equals(REpassword)){
            resp.setStatus(400);
            return;
        }
        User newUser = new User(username, Utilities.encrypt(password));
        newUser.addPermission(Permissions.GET_PRODUCT);
        try{
            repo.addUser(newUser);
            resp.setStatus(200);
        } catch (UserAlreadyExistsException e){
            resp.setStatus(400);
        }
    }
}