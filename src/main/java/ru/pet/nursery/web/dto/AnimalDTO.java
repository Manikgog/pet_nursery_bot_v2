package ru.pet.nursery.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AnimalDTO {
    private String animalName;
    private String animalType;
    private String gender;
    private LocalDate birthDate;
    private int nurseryId;
    private String description;

}
