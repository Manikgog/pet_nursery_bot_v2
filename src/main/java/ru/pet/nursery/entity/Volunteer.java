package ru.pet.nursery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Setter
@Getter
@Table(name="volunteer_table")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "telegram_user_id")
    private long telegramUserId;
    @Column(name = "name", columnDefinition="VARCHAR(50)")
    private String name;
    @Column(name = "phone_number", columnDefinition="VARCHAR(20)")
    private String phoneNumber;
    @Column(name = "is_active")
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer volunteer = (Volunteer) o;
        return id == volunteer.id && isActive == volunteer.isActive && Objects.equals(name, volunteer.name) && Objects.equals(phoneNumber, volunteer.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phoneNumber, isActive);
    }
}
