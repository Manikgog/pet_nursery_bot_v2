package ru.pet.nursery.web.validator;

import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.exception.IllegalFieldException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Validator {
    private final NurseryRepo nurseryRepo;
    public Validator(NurseryRepo nurseryRepo){
        this.nurseryRepo = nurseryRepo;
    }
    public void validateAnimalDTO(AnimalDTO animalDTO){
        List<String> messageList = new ArrayList<>();
        messageList.add(validateAnimalName(animalDTO.getAnimalName()));
        messageList.add(validateAnimalType(animalDTO.getAnimalType().toString()));
        messageList.add(validateGender(animalDTO.getGender().toString()));
        messageList.add(validateDescription(animalDTO.getDescription()));
        messageList.add(validateAnimalBirthDate(animalDTO.getBirthDate()));
        messageList.add(validateNurseryId(animalDTO.getNurseryId()));
        StringBuilder resultMessage = new StringBuilder();
        messageList.stream()
                .filter(message -> !message.isEmpty())
                .forEach(message -> resultMessage.append(message).append("\n"));
        if(resultMessage.isEmpty()){
            throw new IllegalFieldException(resultMessage.toString());
        }
    }

    private String validateAnimalName(String animalName){
        if(animalName == null || animalName.isEmpty()){
            return "Поле animalName класса AnimalDTO не должно быть пустым";
        }
        return "";
    }

    private String validateAnimalType(String animalType){
        if(animalType == null || animalType.isEmpty()){
            return "Поле animalType класса AnimalDTO не должно быть пустым";
        }
        return "";
    }

    private String validateGender(String gender){
        if(gender == null || gender.isEmpty()){
            return "Поле gender класса AnimalDTO не должно быть пустым";
        }else{
            if(!gender.equals("male") && !gender.equals("female")){
                return "Поле gender класса AnimalDTO должно быть равно male или female";
            }
        }
        return "";
    }

    private String validateDescription(String description){
        if(description == null || description.isEmpty()){
            return "Поле description класса AnimalDTO не должно быть пустым";
        }
        return "";
    }

    private String validateAnimalBirthDate(LocalDate animalBirthDate) {
        if (animalBirthDate == null) {
            return "Поле birthDate класса AnimalDTO не должно быть пустым";
        } else {
            if (animalBirthDate.isAfter(LocalDate.now())) {
                return "Дата рождения не может быть в будущем";
            }
        }
        return "";
    }

    private String validateNurseryId(int id){
        if(nurseryRepo.findById(id).isEmpty()){
            return "Питомника с id = " + id + " нет в нашей базе данных";
        }
        return "";
    }
}
