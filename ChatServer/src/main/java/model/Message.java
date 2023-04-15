package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "message_id")
    private long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NonNull
    private User user;

    @Column(name = "text")
    @NonNull
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageId == message.messageId && Objects.equals(user, message.user) && Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, user, text);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }
}