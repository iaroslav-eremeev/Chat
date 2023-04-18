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
        if (login == null || password == null || name == null) {
            resp.setStatus(400);
            resp.getWriter().println("Incorrect input");
            return;
        }
        if (DAO.getObjectByParam("login", login, User.class) != null) {
            resp.setStatus(400);
            resp.getWriter().println("User with this login already exists");
            return;
        }
        DAO.closeOpenedSession();
        if (DAO.getObjectByParam("name", name, User.class) != null) {
            resp.setStatus(400);
            resp.getWriter().println("User with this name already exists");

            return;
        }
        DAO.closeOpenedSession();
        try {
            User user = new User(login, password, name);
            DAO.addObject(user);
            resp.getWriter().write(new ObjectMapper().writeValueAsString(user));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.getWriter().println(e.getMessage());
        }
    }
}