package servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Permissions;
import data.User;
import exceptions.AlreadyAcquiredPermissionException;
import exceptions.NoSuchUserException;
import exceptions.NotAcquiredPermissionException;
import repositories.UserRepository;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = "/permissions/*")
public class PermissionsServlet extends HttpServlet {

    @EJB(beanName = "UserRepositoryInDatabase")
    private UserRepository repo;

    public PermissionsServlet() {}

    public PermissionsServlet(UserRepository repo){
        this.repo = repo;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("authenticated");
        int id = Integer.parseInt(req.getPathInfo().split("/")[1]);
        try {
            if (id == user.getId()) {
                resp.setStatus(400);
                return;
            }
            byte[] bytes = req.getInputStream().readAllBytes();
            String permissionToAddInJson = new String(bytes);
            String permissionToAdd = new JsonParser().parse(permissionToAddInJson).getAsJsonObject().get("permission").getAsString();

            repo.addPermission(id, Permissions.getPermissionByString(permissionToAdd));
        } catch (AlreadyAcquiredPermissionException | NoSuchUserException | IllegalArgumentException e) {
            resp.setStatus(400);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("authenticated");
        int id = Integer.parseInt(req.getPathInfo().split("/")[1]);
        try {
            if(id == user.getId()){
                resp.setStatus(400);
                return;
            }
            byte[] bytes = req.getInputStream().readAllBytes();
            String permissionToTakeInJson = new String(bytes);
            String permissionToTake = new JsonParser().parse(permissionToTakeInJson).getAsJsonObject().get("permission").getAsString();

            repo.deletePermission(id,Permissions.getPermissionByString(permissionToTake));
        } catch (NotAcquiredPermissionException | NoSuchUserException | IllegalArgumentException e){
            resp.setStatus(400);
        }
    }
}
