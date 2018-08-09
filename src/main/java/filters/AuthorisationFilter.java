package filters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Permissions;
import data.Requirement;
import data.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@WebFilter(filterName = "AuthorisationFilter",
urlPatterns = {"/products", "/product/*", "/permissions/*"})
public class AuthorisationFilter implements Filter {

    List<Requirement> requirements;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("authenticated");

        String path = request.getRequestURI();
        String method = request.getMethod();
        Set<Permissions> permissionsNeeded = new HashSet<>();
        boolean matched = false;
        for (Requirement requirement : requirements) {
            if (requirement.match(path, method)) {
                permissionsNeeded = requirement.getPermissions();
                matched = true;
                break;
            }
        }
        if (matched && user.getPermissions().containsAll(permissionsNeeded)) {
            chain.doFilter(req, resp);
        } else {
            response.setStatus(401);
        }
    }

    public void init(FilterConfig config) throws ServletException {
        requirements = new ArrayList<>();
        parseRequirementsFromConfigFile();
    }

    private void parseRequirementsFromConfigFile() {
        //TODO: Why tomEE can't find file! Manually pasted in it's directory!
        try (BufferedReader br = new BufferedReader(new FileReader("permissionsSchema.json"))) {
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(br).getAsJsonArray();
            Iterator<JsonElement> it = array.iterator();
            while (it.hasNext()) {
                JsonObject object = it.next().getAsJsonObject();
                String path = object.get("path").getAsString();
                String method = object.get("method").getAsString();
                JsonArray req_permissions = object.get("req_permissions").getAsJsonArray();
                Requirement requirement = new Requirement(path, method, parsePermissionsNeededArrayFromJSON(req_permissions));
                this.requirements.add(requirement);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    private Set<Permissions> parsePermissionsNeededArrayFromJSON(JsonArray req_permissions) {
        Set<Permissions> permissionsSet = new HashSet<>();
        Iterator<JsonElement> PermissionsIterator = req_permissions.iterator();
        while (PermissionsIterator.hasNext()) {
            permissionsSet.add(Permissions.getPermissionByString(PermissionsIterator.next().getAsString()));
        }
        return permissionsSet;
    }
}