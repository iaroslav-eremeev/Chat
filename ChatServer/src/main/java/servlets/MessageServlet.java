package servlets;


import model.Message;
import repository.SSEEmittersRepository;
import service.ChatWatchService;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getHeader("Accept").equals("text/event-stream")) {

            resp.setContentType("text/event-stream");
            resp.setHeader("Connection", "keep-alive");
            resp.setCharacterEncoding("UTF-8");

            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(60000L);
            this.emitters.add(asyncContext);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String userId = req.getParameter("userId");
        String text = req.getParameter("text");

        Message message = new Message(Integer.parseInt(userId), text);
        this.service.addMessage(message);
    }
}
