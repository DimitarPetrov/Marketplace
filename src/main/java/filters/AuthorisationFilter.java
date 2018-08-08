package filters;

import data.Permissions;
import data.Requirement;
import data.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebFilter(filterName = "AuthorisationFilter",
urlPatterns = {"/products", "/product/*"})
public class AuthorisationFilter implements Filter {

    List<Requirement> requirements;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute("authenticated");

        String path = request.getRequestURI();
        String method = request.getMethod();
        Set<Permissions> permissionsNeeded = new HashSet<>();
        for(Requirement requirement : requirements){
            if(requirement.match(path,method)){
                permissionsNeeded = requirement.getPermissions();
                break;
            }
        }
        if(user.getPermissions().containsAll(permissionsNeeded)){
            chain.doFilter(req, resp);
        } else {
            response.setStatus(401);
        }
    }

    public void init(FilterConfig config) throws ServletException {
        //TODO: Add permissions schema in json file and parse it!
        requirements = new ArrayList<>();
        requirements.add(new Requirement("/products", "GET", Set.of(Permissions.GET_PRODUCT)));
        requirements.add(new Requirement("/products", "POST", Set.of(Permissions.ADD_PRODUCT)));
        requirements.add(new Requirement("/product/.*", "GET", Set.of(Permissions.GET_PRODUCT)));
        requirements.add(new Requirement("/product/.*", "DELETE", Set.of(Permissions.DELETE_PRODUCT)));
    }

}
