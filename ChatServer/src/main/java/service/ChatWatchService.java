package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Message;
import repository.SSEEmittersRepository;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class ChatWatchService {
    private SSEEmittersRepository repository;
    private BlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>();
    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Message message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message) + "\n\n");
            writer.flush();
        } catch (Exception ignored) {
        }
    }

    public ChatWatchService(SSEEmittersRepository repository) {
        this.repository = repository;
        this.startMessageReceive();
    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    Message message = messageBlockingQueue.take();
                    System.out.println("Start sending\n" + repository.getEmittersList());
                    for (AsyncContext asyncContext : repository.getEmittersList()) {
                        try {
                            sendMessage(asyncContext.getResponse().getWriter(), message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread is interrupting");
            }
            System.out.println("Thread is interrupted");
        });
    }

    public void addMessage(Message message) {
        this.messageBlockingQueue.add(message);
    }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.repository.clear();
        this.messageBlockingQueue.clear();
    }
}
