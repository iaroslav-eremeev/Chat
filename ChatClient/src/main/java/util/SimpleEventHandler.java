package util;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;

public class SimpleEventHandler implements EventHandler {

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
}
