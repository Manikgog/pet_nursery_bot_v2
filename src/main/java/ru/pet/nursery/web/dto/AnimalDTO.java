package ru.pet.nursery.web.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class AnimalDTO {
    private String animalName;

    @Enumerated(EnumType.STRING)
    private AnimalType animalType;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthDate;
    private int nurseryId;
    private String description;

}
