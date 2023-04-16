package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import hibernate.DAO;
import model.User;
import util.UnicodeSetup;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UnicodeSetup.setUnicode(req, resp);
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        if (login == null) {
            resp.setStatus(400);
            resp.getWriter().println("Incorrect login");
            return;
        }
        if (password == null) {
            resp.setStatus(400);
            resp.getWriter().println("Incorrect password");
            return;
        }
        if (name == null) {
            resp.setStatus(400);
            resp.getWriter().println("Incorrect name");
            return;
        }
        try {
            User user = new User(login, password, name);
            DAO.addObject(user);
            resp.getWriter().write(new ObjectMapper().writeValueAsString(user));
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            resp.getWriter().println("User with this login already exists");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.getWriter().println(e.getMessage());
        }
    }
}