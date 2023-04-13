package program;

import util.SimpleEventHandler;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SseClientEventSourceApplication {

    public static void main(String[] args)  {
        try(ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        System.out.println("Initialize event source");
                        EventHandler eventHandler = new SimpleEventHandler();
                        String url = "http://localhost:8080/sse/chat-watch";
                        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));

                        try (EventSource eventSource = builder.build()) {
                            eventSource.start();
                            TimeUnit.MINUTES.sleep(1);
                        }
                    }
                } catch (InterruptedException ignored) {}
            });
        }
    }

}
