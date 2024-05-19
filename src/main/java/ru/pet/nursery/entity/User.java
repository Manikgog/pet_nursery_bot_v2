package ru.pet.nursery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pet.nursery.web.exception.UserNotValidException;

import java.util.Objects;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users_table")
public class User {
    @Id
    private Long telegramUserId;
    private String firstName;
    private String lastName;
    private String userName;
    private String phoneNumber;
    private String address;

    public String getUserFullName() {
            return this.firstName + this.lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(telegramUserId, user.telegramUserId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(userName, user.userName) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telegramUserId, firstName, lastName, userName, phoneNumber, address);
    }
}
