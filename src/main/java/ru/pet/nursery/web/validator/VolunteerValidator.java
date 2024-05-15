package ru.pet.nursery.web.validator;

import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VolunteerValidator {
    private final UserRepo userRepo;
    private final VolunteerRepo volunteerRepo;

    public VolunteerValidator(UserRepo userRepo,
                              VolunteerRepo volunteerRepo) {
        this.userRepo = userRepo;
        this.volunteerRepo = volunteerRepo;
    }

    private final static String regexPhone = "(\\+\\d(?:\\-|\\ |/)\\d\\d\\d(?:\\-|\\ |/)\\d\\d\\d(?:\\-|\\ |/)\\d\\d\\d\\d)";

    public void validate(Volunteer volunteer){
        List<String> messageList = new ArrayList<>();
        messageList.add(validateName(volunteer.getName()));
        messageList.add(validatePhone(volunteer.getPhoneNumber()));
        messageList.add(validatePhoneNumber(volunteer.getPhoneNumber()));
        messageList.add(validateTelegramUserId(volunteer.getTelegramUserId()));
        messageList.add(validateTelegamUserIdInDataBase(volunteer.getTelegramUserId()));
        StringBuilder resultMessage = new StringBuilder();
        messageList.stream()
                .filter(message -> !message.isEmpty())
                .forEach(message -> resultMessage.append(message).append("\n"));
        if(!resultMessage.isEmpty()){
            throw new IllegalFieldException(resultMessage.toString());
        }
    }

    /**
     * Метод для проверки строки на пустоту или пробелы
     * @param str - входная строка
     */
    public String validateName(String str){
        if(str == null || str.isEmpty() || str.isBlank()){
             return "Поле name не должно быть пустым или состоять из одних пробелов";
        }
        return "";
    }

    /**
     * Метод для проверки поля telegramUserId на null и положительность
     * @param telegramUserId - идентификатор пользователя Telegram
     * @return строка
     */
    private String validateTelegramUserId(Long telegramUserId){
        if(telegramUserId == null){
            return "Поле telegramUserId должно быть не null";
        }
        if(telegramUserId <= 0){
            return "Поле telegramUserId должно быть больше 0";
        }
        return "";
    }

    /**
     * Метод для проверки строки на пустоту или пробелы
     * @param str - входная строка
     */
    private String validatePhone(String str){
        if(str == null || str.isEmpty() || str.isBlank()){
            return "Поле phoneNumber не должно быть равен null, быть пустым или состоять из одних пробелов";
        }
        return "";
    }

    /**
     * Метод для проверки телефонного номера на соответствие формату
     * @param phone - строка с номером телефона
     * @return строка
     */
    public String validatePhoneNumber(String phone){
        if(phone == null) return "";
        if(!phone.matches(regexPhone)){
            return "Телефон " + phone + " не соответствует формату: +7-654-654-6565 или +1 546 879 2121 или +8/214/541/5475";
        }
        return "";
    }

    /**
     * Метод для проверки наличия идентификатора пользователя Telegram в базе данных
     * @param telegramUserId - идентификатор переданных в поле объекта Volunteer
     * @return строка
     */
    public String validateTelegamUserIdInDataBase(Long telegramUserId){
        if(userRepo.findById(telegramUserId).isEmpty()){
            return "Идентификатор пользователя " + telegramUserId + " отсутствует в базе данных. " +
                    "Необходимо зайти в наш бот тогда ваш идентификатор добавиться в базу данных.";
        }
        return "";
    }

    public void validateId(int id){
        if(volunteerRepo.findById(id).isEmpty()){
            throw new EntityNotFoundException((long)id);
        }
    }
}
