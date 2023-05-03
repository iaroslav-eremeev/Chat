package servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hibernate.DAO;
import model.Message;
import model.User;
import org.hibernate.annotations.CurrentTimestamp;
import repository.SSEEmittersRepository;
import service.ChatWatchService;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(value = "/sse/chat-watch", asyncSupported = true)
public class ChatWatchServlet extends HttpServlet {
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

            // Send first SSE event
            /*PrintWriter writer = resp.getWriter();
            writer.write("event: Start\n");
            writer.write("data: Launching chat watcher\n");
            writer.write("\n\n");
            writer.flush();*/
        }

        if (req.getParameter("checkOnlineUsers") != null){
            ArrayList<User> onlineUsers = this.emitters.getOnlineUsers();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                resp.getWriter().write(objectMapper.writeValueAsString(onlineUsers));
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
        User user = (User) DAO.getObjectById(Long.parseLong(userId), User.class);
        assert user != null;
        Message message = new Message(user, text);
        this.service.addMessage(message);
        DAO.addObject(message);
        DAO.closeOpenedSession();
    }
}

