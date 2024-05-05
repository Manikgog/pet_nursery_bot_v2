package ru.pet.nursery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private String animalName;
    private String animalType;
    private String gender;
    private LocalDate birthDate;
    private String photoPath;
    private long whoTookPet;
    private LocalDate tookDate;
    private int nurseryId;
    private String description;
    private LocalDate petReturnDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id &&
                whoTookPet == animal.whoTookPet &&
                nurseryId == animal.nurseryId &&
                Objects.equals(animalName, animal.animalName) &&
                Objects.equals(animalType, animal.animalType) &&
                Objects.equals(gender, animal.gender) &&
                Objects.equals(birthDate, animal.birthDate) &&
                Objects.equals(photoPath, animal.photoPath) &&
                Objects.equals(tookDate, animal.tookDate) &&
                Objects.equals(description, animal.description) &&
                Objects.equals(petReturnDate, animal.petReturnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, animalName, animalType, gender, birthDate, photoPath, whoTookPet, tookDate, nurseryId, description, petReturnDate);
    }
}
