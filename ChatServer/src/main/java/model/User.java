package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "login")
    @NonNull
    private String login;

    @Column(name = "password")
    @NonNull
    private String password;

    @Column(name = "name")
    @NonNull
    private String name;

    private String hash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && login.equals(user.login) && password.equals(user.password) && name.equals(user.name) && Objects.equals(hash, user.hash) && Objects.equals(messages, user.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, login, password, name, hash, messages);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", messages=" + messages.size() +
                '}';
    }
}
