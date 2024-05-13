package ru.pet.nursery.web.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;

import java.time.LocalDate;
import java.time.Period;

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

    @Override
    public String toString() {
        return "AnimalDTO{" +
                "animalName='" + animalName + '\'' +
                ", animalType='" + animalType + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", nurseryId=" + nurseryId +
                ", description='" + description + '\'' +
                '}';
    }

    public String toTelegramString(){
        return "Кличка: " + animalName +
                "\nВид: " + animalType +
                "\nВозраст: " + birthDateToAge() +
                "\nОписание: " + description;
    }

    public String birthDateToAge(){
        int years = Period.between(birthDate, LocalDate.now()).getYears();
        int months = Period.between(birthDate, LocalDate.now()).getMonths();
        return years + " г. " + months + " мес.";
    }
}
