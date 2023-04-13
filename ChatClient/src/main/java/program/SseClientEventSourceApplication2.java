package program;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SseClientEventSourceApplication2 {
    public static void main(String[] args) {
        try(ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        System.out.println("Initialize event source");
                        String url = "http://localhost:8080/sse/chat-watch";
                        EventSource.Builder builder = new EventSource.Builder(new EventHandler() {

                            @Override
                            public void onOpen() {
                                System.out.println("onOpen");
                            }

                            @Override
                            public void onClosed() {
                                System.out.println("onClosed");
                            }

                            @Override
                            public void onMessage(String event, MessageEvent messageEvent) {
                                System.out.println(messageEvent.getData());
                            }

                            @Override
                            public void onComment(String comment) {
                                System.out.println("onComment");
                            }

                            @Override
                            public void onError(Throwable t) {
                                System.out.println("onError: " + t);
                            }
                        }, URI.create(url));

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
