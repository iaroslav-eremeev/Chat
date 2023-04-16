package repository;

import hibernate.DAO;
import model.User;

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
                String login = asyncContext.getRequest().getParameter("login");
                // Use DAO to get Object User by parameter login
                User user = (User) DAO.getObjectByParam("login", login, User.class);
                String userId = String.valueOf(user.getUserId());
                removeOfflineUser(userId);
                if (isOnline(userId)) {
                    System.out.println(asyncContext.getRequest().getParameter("login") + " is online");
                } else {
                    System.out.println(asyncContext.getRequest().getParameter("login") + " is offline");
                }
                DAO.closeOpenedSession();
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                emittersList.remove(asyncContext);
                System.out.println("Timeout");
                String login = asyncContext.getRequest().getParameter("login");
                // Use DAO to get Object User by parameter login
                User user = (User) DAO.getObjectByParam("login", login, User.class);
                String userId = String.valueOf(user.getUserId());
                removeOfflineUser(userId);
                if (isOnline(userId)) {
                    System.out.println(asyncContext.getRequest().getParameter("login") + " is online");
                } else {
                    System.out.println(asyncContext.getRequest().getParameter("login") + " is offline");
                }
                DAO.closeOpenedSession();
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                removeOfflineUser(asyncContext.getRequest().getParameter("userId"));
                emittersList.remove(asyncContext);
                System.out.println("Error");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        emittersList.add(asyncContext);
        String login = asyncContext.getRequest().getParameter("login");
        // Use DAO to get Object User by parameter login
        User user = (User) DAO.getObjectByParam("login", login, User.class);
        String userId = String.valueOf(user.getUserId());
        addOnlineUser(userId);
        System.out.println("After adding emitter " + emittersList);
        if (isOnline(userId)) {
            System.out.println(asyncContext.getRequest().getParameter("login") + " is online");
        } else {
            System.out.println(asyncContext.getRequest().getParameter("login") + " is offline");
        }
        DAO.closeOpenedSession();
    }

    public CopyOnWriteArrayList<AsyncContext> getEmittersList() {
        return emittersList;
    }

    private void addOnlineUser(String userId) {
        if (userId != null) {
            onlineUsers.compute(userId, (key, value) -> value == null ? 1 : value + 1);
        }
    }

    private void removeOfflineUser(String userId) {
        if (userId != null) {
            onlineUsers.computeIfPresent(userId, (key, value) -> value == 1 ? null : value - 1);
        }
    }

    public boolean isOnline(String userId) {
        if (userId == null) {
            return false;
        }
        return onlineUsers.containsKey(userId);
    }

    public void clear() {
        this.emittersList.clear();
    }
}
