package ru.pet.nursery.web.dto;

import lombok.Getter;
import lombok.Setter;
import ru.pet.nursery.entity.Nursery;

import java.time.LocalDate;

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
}
