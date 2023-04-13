package repository;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.util.concurrent.CopyOnWriteArrayList;

public class SSEEmittersRepository {

    //TODO сделать потокобезопасную мапу для фиксации онлайн/офлайн
    private CopyOnWriteArrayList<AsyncContext> list = new CopyOnWriteArrayList<>();

    public void add(AsyncContext asyncContext){
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Finish");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Timeout");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                list.remove(asyncContext);
                System.out.println("Error");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start async");
            }
        });

        list.add(asyncContext);
        System.out.println("After adding emitter " + list);
    }

    public CopyOnWriteArrayList<AsyncContext> getList() {
        return list;
    }

    public void clear() {
        this.list.clear();
    }
}
