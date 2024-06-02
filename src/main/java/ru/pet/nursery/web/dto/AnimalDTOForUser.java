package ru.pet.nursery.web.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
public class AnimalDTOForUser {
    private Long id;
    private String animalName;
    private AnimalType animalType;
    private Gender gender;
    private LocalDate birthDate;
    private Nursery nursery;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalDTOForUser that = (AnimalDTOForUser) o;
        return id == that.id && Objects.equals(animalName, that.animalName) && animalType == that.animalType && gender == that.gender && Objects.equals(birthDate, that.birthDate) && Objects.equals(nursery, that.nursery) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, animalName, animalType, gender, birthDate, nursery, description);
    }

    private String description;


}
