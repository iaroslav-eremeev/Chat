package repository;

import model.Message;

import java.util.ArrayList;

public class MessageRepository {

    private ArrayList<Message> messages = new ArrayList<>();

    public MessageRepository() {
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void add(Message message){
        messages.add(message);
    }

    public int getLength(){
        return messages.size();
    }
}
