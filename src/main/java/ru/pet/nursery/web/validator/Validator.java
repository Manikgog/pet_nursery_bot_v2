package ru.pet.nursery.web.validator;

import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.PageNumberException;
import ru.pet.nursery.web.exception.PageSizeException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Validator {
    private final NurseryRepo nurseryRepo;
    public Validator(NurseryRepo nurseryRepo){
        this.nurseryRepo = nurseryRepo;
    }

    /**
     * Метод для проверки валидности полей объекта animalDTO.
     * На валидность проверяются все поля.
     * Текстовые поля проверяются на пустоту.
     * Поле с датой рождения на пустоту и на то чтобы оно не было
     * в будущем.
     * Поле с идентификатором приюта проверяется на наличие такого
     * в базе данных.
     * @param animalDTO - объект класса AnimalDTO
     */
    public void validateAnimalDTO(AnimalDTO animalDTO){
        List<String> messageList = new ArrayList<>();
        messageList.add(validateAnimalName(animalDTO.getAnimalName()));
        if(animalDTO.getAnimalType() == null){
            messageList.add("Поле AnimalType не должно быть равным null");
        }else {
            messageList.add(validateAnimalType(animalDTO.getAnimalType().toString()));
        }
        messageList.add(validateGender(animalDTO.getGender().toString()));
        messageList.add(validateDescription(animalDTO.getDescription()));
        messageList.add(validateAnimalBirthDate(animalDTO.getBirthDate()));
        messageList.add(validateNurseryId(animalDTO));
        StringBuilder resultMessage = new StringBuilder();
        messageList.stream()
                .filter(message -> !message.isEmpty())
                .forEach(message -> resultMessage.append(message).append("\n"));
        if(!resultMessage.isEmpty()){
            throw new IllegalFieldException(resultMessage.toString());
        }
    }

    /**
     * Метод для проверки строки на пустоту
     * @param animalName - строка с именем животного
     * @return строка для добавления в итоговое сообщение об ошибке.
     */
    private String validateAnimalName(String animalName){
        if(animalName == null || animalName.isEmpty() || animalName.isBlank()){
            return "Поле animalName класса AnimalDTO не должно быть пустым";
        }
        return "";
    }

    /**
     * Метод для проверки строки на пустоту
     * @param animalType - строка с типом животного
     * @return строка для добавления в итоговое сообщение об ошибке.
     */
    private String validateAnimalType(String animalType){
        if(animalType == null || animalType.isEmpty() || animalType.isBlank()){
            return "Поле animalType класса AnimalDTO не должно быть пустым";
        }
        return "";
    }

    /**
     * Метод для проверки строки на пустоту
     * @param gender - строка с полом животного
     * @return строка для добавления в итоговое сообщение об ошибке.
     */
    private String validateGender(String gender){
        if(gender == null || gender.isEmpty()){
            return "Поле gender класса AnimalDTO не должно быть пустым";
        }else{
            if(!gender.equals("MALE") && !gender.equals("FEMALE")){
                return "Поле gender класса AnimalDTO должно быть равно MALE или FEMALE";
            }
        }
        return "";
    }

    /**
     * Метод для проверки строки на пустоту
     * @param description - строка с описанием животного
     * @return строка для добавления в итоговое сообщение об ошибке.
     */
    private String validateDescription(String description){
        if(description == null || description.isEmpty()){
            return "Поле description класса AnimalDTO не должно быть пустым";
        }
        return "";
    }

    /**
     * Метод для проверки LocalDate на null и на будущее время
     * @param animalBirthDate - дата рождения животного
     * @return строка для добавления в итоговое сообщение об ошибке.
     */
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

    /**
     * Метод для проверки наличия приюта в базе данных
     * @param animalDTO - объект класса AnimalDTO
     * @return строка для добавления в итоговое сообщение об ошибке.
     */
    private String validateNurseryId(AnimalDTO animalDTO){
        int id = Math.toIntExact(animalDTO.getNurseryId());
        Optional<Nursery> nursery = nurseryRepo.findById((long) id);
        if(nursery.isEmpty()){
            return "Питомника с id = " + id + " нет в нашей базе данных";
        }
        if(!nursery.get().isForDog() && animalDTO.getAnimalType() == AnimalType.DOG){
            return "Питомник в id = " + id + " предназначен для кошек";
        }else if(nursery.get().isForDog() && animalDTO.getAnimalType() == AnimalType.CAT){
            return "Питомник в id = " + id + " предназначен для собак";
        }
        return "";
    }

    /**
     * Метод для проверки pageNumber на, то что pageNumber больше нуля
     * @param pageNumber - целое число
     */
    public void validatePageNumber(Integer pageNumber) {
        if(pageNumber <= 0)
            throw new PageNumberException("Номер страницы должен быть больше нуля");
    }

    /**
     * Метод для проверки pageSize на, то что pageSize больше нуля
     * @param pageSize - целое число
     */
    public void validatePageSize(Integer pageSize) {
        if(pageSize <= 0)
            throw new PageSizeException("Количество страниц должно быть больше нуля");
    }
}
