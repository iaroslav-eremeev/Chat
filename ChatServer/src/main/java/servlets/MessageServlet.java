package servlets;


import hibernate.DAO;
import model.Message;
import model.User;
import org.json.JSONArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/messages", asyncSupported = true)
public class MessageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // if GET request has a parameter "userId"
        if (req.getParameter("userId") != null) {
            int userId = Integer.parseInt(req.getParameter("userId"));
            // return all messages of the user with certain id
            if (userId > 0) {
                User user = (User) DAO.getObjectById(userId, User.class);
                DAO.closeOpenedSession();
                assert user != null;
                resp.setContentType("application/json");
                try {
                    resp.getWriter().write(user.getMessages().toString());
                } catch (Exception e) {
                    resp.setStatus(400);
                    resp.getWriter().println(e.getMessage());
                }
            }
            // return all messages from the database
            else {
                List messages = DAO.getAllObjects(Message.class);
                DAO.closeOpenedSession();
                JSONArray jsonArray = new JSONArray(messages);
                resp.setContentType("application/json");
                try {
                    resp.getWriter().write(jsonArray.toString());
                } catch (Exception e) {
                    resp.setStatus(400);
                    resp.getWriter().println(e.getMessage());
                }
            }
        }
    }
}
