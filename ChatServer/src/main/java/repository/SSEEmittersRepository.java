package repository;

import hibernate.DAO;
import model.User;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.Console;
import java.net.http.HttpRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SSEEmittersRepository {
    private ConcurrentHashMap<Long, CopyOnWriteArrayList<AsyncContext>> onlineUsers = new ConcurrentHashMap<>();

    public void add(AsyncContext asyncContext){
        HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
        Cookie[] cookies = request.getCookies();

        Long userId = Long.parseLong(cookies[1].getValue());
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                onlineUsers.get(userId).remove(asyncContext);
                System.out.println("User " + userId + " is offline");
                System.out.println("Complete");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                onlineUsers.get(userId).remove(asyncContext);
                System.out.println("User " + userId + " is offline");
                System.out.println("Timeout");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                onlineUsers.get(userId).remove(asyncContext);
                System.out.println("User " + userId + " is offline");
                System.out.println("Error");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });
        //TODO getOrDefault
        if (onlineUsers.containsKey(userId)) {
            onlineUsers.get(userId).add(asyncContext);
        } else {
            onlineUsers.put(userId, new CopyOnWriteArrayList<>());
            onlineUsers.get(userId).add(asyncContext);
        }
        System.out.println(this.onlineUsers);
        System.out.println("After adding emitter " + this.onlineUsers.get(userId));
        System.out.println("User " + userId + " is online");
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
