package filters;

import data.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter",
        urlPatterns = {"/products", "/product/*"})
public class AuthenticationFilter implements Filter {
    public void destroy() {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute("authenticated");
        if(session != null && user != null){
            //System.out.println("Authenticated!");
            chain.doFilter(req, resp);
        } else {
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setStatus(401);
            //System.out.println("UNauthenticated!");
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
