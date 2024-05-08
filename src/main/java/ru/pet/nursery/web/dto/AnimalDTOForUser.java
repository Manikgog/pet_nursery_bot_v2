package ru.pet.nursery.web.dto;

import lombok.Getter;
import lombok.Setter;
import ru.pet.nursery.entity.Nursery;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
public class AnimalDTOForUser {
    private int id;
    private String animalName;
    private String animalType;
    private String gender;
    private LocalDate birthDate;
    private Nursery nursery;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalDTOForUser that = (AnimalDTOForUser) o;
        return id == that.id && Objects.equals(animalName, that.animalName) && Objects.equals(animalType, that.animalType) && Objects.equals(gender, that.gender) && Objects.equals(birthDate, that.birthDate) && Objects.equals(nursery, that.nursery) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, animalName, animalType, gender, birthDate, nursery, description);
    }
}
