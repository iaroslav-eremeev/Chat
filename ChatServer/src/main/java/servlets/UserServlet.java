package servlets;

import hibernate.DAO;
import model.User;
import util.Encryption;
import util.UnicodeSetup;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/login")
public class UserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UnicodeSetup.setUnicode(req, resp);
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        if (login != null && password != null) {
            try {
                User user = (User) DAO.getObjectByParams(new String[]{"login", "password"}, new Object[]{login, password}, User.class);
                DAO.closeOpenedSession();
                if (user != null) {
                    String hash = Encryption.generateHash();
                    user.setHash(hash);
                    DAO.updateObject(user);
                    Cookie cookie = new Cookie("hash", hash);
                    cookie.setMaxAge(30 * 60);
                    cookie.setPath("/");
                    resp.addCookie(cookie);
                    // Save userId in the second cookie
                    Cookie userIdCookie = new Cookie("userId", String.valueOf(user.getUserId()));
                    userIdCookie.setMaxAge(30 * 60);
                    userIdCookie.setPath("/");
                    resp.addCookie(userIdCookie);
                }
                else{
                    resp.setStatus(400);
                    resp.getWriter().print("Incorrect login or password");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                resp.getWriter().println(e.getMessage());
            }
        }
        else {
            System.out.println("Login or password input is incorrect");
        }
    }
}
