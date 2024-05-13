package ru.pet.nursery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name="animal_table")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 50)
    private String animalName;

    @Enumerated(EnumType.STRING)
    private AnimalType animalType;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    @Column(columnDefinition="TEXT")
    private String photoPath;

    @ManyToOne
    @JoinColumn(name = "telegramUserId")
    private User user;

    private LocalDate tookDate;

    @ManyToOne
    @JoinColumn(name = "nursery_id")
    private Nursery nursery;

    @Column(columnDefinition="TEXT")
    private String description;
    private LocalDate petReturnDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id && Objects.equals(animalName, animal.animalName) && animalType == animal.animalType && gender == animal.gender && Objects.equals(birthDate, animal.birthDate) && Objects.equals(photoPath, animal.photoPath) && Objects.equals(user, animal.user) && Objects.equals(tookDate, animal.tookDate) && Objects.equals(nursery, animal.nursery) && Objects.equals(description, animal.description) && Objects.equals(petReturnDate, animal.petReturnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, animalName, animalType, gender, birthDate, photoPath, user, tookDate, nursery, description, petReturnDate);
    }
}
