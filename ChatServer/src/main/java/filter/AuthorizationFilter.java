package filter;

import hibernate.DAO;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(request.getRequestURI().endsWith("index.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("hash")) {
                    value = cookie.getValue();
                }
            }
        }

        System.out.println(request.getRequestURI());

        // Request/Redirect URL to Login Servlet
        String loginURI = request.getContextPath() + "/login";
        String registerURI = request.getContextPath() + "/registration";
        // If the session was previously created
        boolean loginRequest = request.getRequestURI().contains(loginURI);
        boolean registerRequest = request.getRequestURI().contains(registerURI);
        // If the request came from the login page or the session is not empty, we give the go-ahead to proceed further
        if (request.getRequestURI().endsWith("js") || loginRequest || registerRequest
                || value != null && DAO.getObjectByParam("hash", value, User.class) != null) {
            // Check if the request is asynchronous
            if (request.isAsyncStarted()) {
                // Continue processing the request asynchronously
                AsyncContext asyncContext = request.getAsyncContext();
                asyncContext.start(() -> {
                    try {
                        filterChain.doFilter(request, response);
                        asyncContext.complete();
                    } catch (IOException | ServletException e) {
                        asyncContext.complete();
                        throw new RuntimeException(e);
                    }
                });
            } else {
                // Continue processing the request synchronously
                filterChain.doFilter(request, response);
            }
        // If not redirect to login page
        } else {
            response.sendRedirect(loginURI + ".html");
        }
    }

    @Override
    public void destroy() {

    }
}
