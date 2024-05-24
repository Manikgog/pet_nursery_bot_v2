package ru.pet.nursery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users_table")
public class User {
    @Id
    @Column(name = "telegram_user_id")
    private Long telegramUserId;
    @Column(name = "first_name", columnDefinition="VARCHAR(50)")
    private String firstName;
    @Column(name = "last_name", columnDefinition="VARCHAR(50)")
    private String lastName;
    @Column(name = "user_name", columnDefinition="VARCHAR(50)")
    private String userName;
    @Column(name = "phone_number", columnDefinition="VARCHAR(20)")
    private String phoneNumber;
    @Column(name = "address", columnDefinition="TEXT")
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
