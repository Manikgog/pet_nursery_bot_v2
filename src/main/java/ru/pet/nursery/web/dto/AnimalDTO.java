package ru.pet.nursery.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class AnimalDTO {
    private String animalName;
    private String animalType;
    private String gender;
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
