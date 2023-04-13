package model;

import repository.MessageRepository;

import java.util.Objects;

public class Message {
    private int messageId;
    private int userId;
    private String text;

    public Message() {
    }

    public Message(int userId, String text, MessageRepository messageRepository) {
        // Get the next message id from the database
        this.messageId = messageRepository.getLength() + 1;
        this.userId = userId;
        this.text = text;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageId == message.messageId && userId == message.userId && Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, userId, text);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                '}';
    }
}
