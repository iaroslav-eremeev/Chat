package program;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.EventHandler;
import hibernate.DAO;
import model.User;
import repository.SSEEmittersRepository;
import util.Encryption;
import util.SimpleEventHandler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class UserClientEventSourceApplication {
    private static final String SSE_CHAT_WATCH_URL = "http://localhost:8080/ChatServer/sse/chat-watch";

    public static void main(String[] args) throws InterruptedException, IOException {
        User user = login("iaroslav", "eremeev");
        if (user == null) {
            System.out.println("Login failed");
            return;
        }
        System.out.println("Login successful. User: " + user.getLogin());
        // Start SSE event source
        EventHandler eventHandler = new SimpleEventHandler();
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(SSE_CHAT_WATCH_URL));
        while (true) {
            try (EventSource eventSource = builder.build()) {
                eventSource.start();
                // Wait for SSE events for 1 minute
                TimeUnit.MINUTES.sleep(1);
            }
        }
    }

    private static User login(String username, String password) {
        try {
            User user = (User) DAO.getObjectByParams(new String[]{"login", "password"}, new Object[]{username, password}, User.class);
            DAO.closeOpenedSession();
            if (user != null) {
                String hash = Encryption.generateHash();
                user.setHash(hash);
                DAO.updateObject(user);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
