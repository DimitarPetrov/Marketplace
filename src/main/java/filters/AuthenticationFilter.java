package filters;

import data.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter",
        urlPatterns = {"/products", "/product/*", "/permissions/*"})
public class AuthenticationFilter implements Filter {
    public void destroy() {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpSession session = request.getSession(false);
        User user;
        if(session != null && (user = (User)session.getAttribute("authenticated")) != null){
            chain.doFilter(req, resp);
        } else {
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Unauthenticated\"}");
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
