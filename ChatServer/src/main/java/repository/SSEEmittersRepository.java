package repository;

import hibernate.DAO;
import model.User;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class SSEEmittersRepository {
    private ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> onlineUsers = new ConcurrentHashMap<>();

    public void add(AsyncContext asyncContext) {
        HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId")) {
                    try {
                        long userId = Long.parseLong(cookie.getValue());
                        asyncContext.addListener(new AsyncListener() {
                            @Override
                            public void onComplete(AsyncEvent asyncEvent) {
                                if (onlineUsers.get(userId) != null){
                                    onlineUsers.get(userId).remove(asyncContext);
                                }
                                System.out.println("User " + userId + " is offline");
                                System.out.println("Complete");
                            }

                            @Override
                            public void onTimeout(AsyncEvent asyncEvent) {
                                if (onlineUsers.get(userId) != null){
                                    onlineUsers.get(userId).remove(asyncContext);
                                }
                                System.out.println("User " + userId + " is offline");
                                System.out.println("Timeout");
                            }

                            @Override
                            public void onError(AsyncEvent asyncEvent) {
                                if (onlineUsers.get(userId) != null){
                                    onlineUsers.get(userId).remove(asyncContext);
                                }
                                System.out.println("User " + userId + " is offline");
                                System.out.println("Error");
                            }

                            @Override
                            public void onStartAsync(AsyncEvent asyncEvent) {
                                System.out.println("Start async");
                            }
                        });
                        CopyOnWriteArrayList<AsyncContext> list = onlineUsers.getOrDefault(userId, new CopyOnWriteArrayList<>());
                        list.add(asyncContext);
                        onlineUsers.put(userId, list);
                        System.out.println(this.onlineUsers);
                        System.out.println("After adding emitter " + this.onlineUsers.get(userId));
                        System.out.println("User " + userId + " is online");
                    } catch (NumberFormatException ignored) {
                    }
                    break;
                }
            }
        }
    }

    public ArrayList<User> getOnlineUsers() {
        ArrayList<User> onlineUsers = new ArrayList<>();
        for (Long userId : this.onlineUsers.keySet()) {
            if (this.onlineUsers.containsKey(userId) && !this.onlineUsers.get(userId).isEmpty()) {
                User user = (User) DAO.getObjectById(userId, User.class);
                if (user != null) {
                    onlineUsers.add(user);
                }
            }
        }
        return onlineUsers;
    }

    public CopyOnWriteArrayList<AsyncContext> getEmittersList() {
        CopyOnWriteArrayList<AsyncContext> emittersList = new CopyOnWriteArrayList<>();
        // Add all the values from the map to the list
        for (CopyOnWriteArrayList<AsyncContext> list : onlineUsers.values()) {
            emittersList.addAll(list);
        }
        return emittersList;
    }

    public void clear() {
        this.onlineUsers.clear();
    }
}
