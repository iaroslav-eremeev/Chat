package repository;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SSEEmittersRepository {
    private CopyOnWriteArrayList<AsyncContext> emittersList = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, Integer> onlineUsers = new ConcurrentHashMap<>();

    public void add(AsyncContext asyncContext){
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                emittersList.remove(asyncContext);
                removeOfflineUser(asyncContext.getRequest().getParameter("userId"));
                System.out.println("Finish");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                emittersList.remove(asyncContext);
                removeOfflineUser(asyncContext.getRequest().getParameter("userId"));
                System.out.println("Timeout");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                emittersList.remove(asyncContext);
                removeOfflineUser(asyncContext.getRequest().getParameter("userId"));
                System.out.println("Error");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        emittersList.add(asyncContext);
        addOnlineUser(asyncContext.getRequest().getParameter("userId"));
        System.out.println("After adding emitter " + emittersList);
    }

    public CopyOnWriteArrayList<AsyncContext> getEmittersList() {
        return emittersList;
    }

    private void addOnlineUser(String userId) {
        onlineUsers.compute(userId, (key, value) -> value == null ? 1 : value + 1);
    }

    private void removeOfflineUser(String userId) {
        onlineUsers.computeIfPresent(userId, (key, value) -> value == 1 ? null : value - 1);
    }

    public boolean isOnline(String userId) {
        return onlineUsers.containsKey(userId);
    }

    public void clear() {
        this.emittersList.clear();
    }
}
