package servlets;


import hibernate.DAO;
import model.Message;
import model.User;
import repository.SSEEmittersRepository;
import service.ChatWatchService;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/sse/chat-watch", asyncSupported = true)
public class MessageServlet extends HttpServlet {
    private SSEEmittersRepository emitters = new SSEEmittersRepository();
    private ChatWatchService service;

    @Override
    public void init() {
        this.service = new ChatWatchService(this.emitters);
    }

    @Override
    public void destroy() {
        this.service.stop();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getHeader("Accept").equals("text/event-stream")) {
            resp.setContentType("text/event-stream");
            resp.setHeader("Connection", "keep-alive");
            resp.setCharacterEncoding("UTF-8");

            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(60000L);
            this.emitters.add(asyncContext);
        }
        // else if GET request has a parameter "userId" then return all messages of this user
        else if (req.getParameter("userId") != null){
            String userId = req.getParameter("userId");
            User user = (User) DAO.getObjectById(Integer.parseInt(userId), User.class);
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String userId = req.getParameter("userId");
        String text = req.getParameter("text");
        User user = (User) DAO.getObjectById(Integer.parseInt(userId), User.class);
        DAO.closeOpenedSession();
        assert user != null;
        Message message = new Message(user, text);
        DAO.addObject(message);
        this.service.addMessage(message);
    }
}
