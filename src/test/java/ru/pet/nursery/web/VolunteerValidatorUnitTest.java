package ru.pet.nursery.web;

import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.validator.VolunteerValidator;
import java.util.Optional;
import java.util.Random;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VolunteerValidatorUnitTest {
    @Mock
    UserRepo userRepo;
    @Mock
    VolunteerRepo volunteerRepo;
    @InjectMocks
    VolunteerValidator validator;
    private final Faker faker = new Faker();

    /**
     * Тестирование метода validate класса VolunteerValidator
     * при валидных исходных данных
     */
    @Test
    public void validateVolunteer_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1);
            String first_name = faker.name().firstName();
            String last_name = faker.name().lastName();
            volunteer.setName(first_name + " " + last_name);
            String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            volunteer.setPhoneNumber(phone);
            long telegramUserId = new Random().nextInt(1, 1000000000);
            volunteer.setTelegramUserId(telegramUserId);
            volunteer.setActive(false);

            User user = new User();
            user.setUserName(first_name + " " + last_name);
            user.setFirstName(first_name);
            user.setLastName(last_name);
            user.setTelegramUserId(telegramUserId);
            user.setPhoneNumber(phone);
            user.setAddress(faker.address().fullAddress());

            when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
            validator.validate(volunteer);
        }
    }

    @Test
    public void validateVolunteerName_negativeTest(){
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1);
        volunteer.setName(null);
        String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
        volunteer.setPhoneNumber(phone);
        long telegramUserId = new Random().nextInt(1, 1000000000);
        volunteer.setTelegramUserId(telegramUserId);
        volunteer.setActive(false);

        User user = new User();
        user.setUserName(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setTelegramUserId(telegramUserId);
        user.setPhoneNumber(phone);
        user.setAddress(faker.address().fullAddress());

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setName("");

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setName(" ");

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));
    }

    /**
     * Метод для проверки правильности работы метода
     * validateVolunteerPhone при невалидном телефонном номере
     */
    @Test
    public void validateVolunteerPhone_negativeTest(){
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1);
        String first_name = faker.name().firstName();
        String last_name = faker.name().lastName();
        volunteer.setName(first_name + " " + last_name);
        String phone = null;                                // подставляем null вместо номера телефона
        volunteer.setPhoneNumber(phone);
        long telegramUserId = new Random().nextInt(1, 1000000000);
        volunteer.setTelegramUserId(telegramUserId);
        volunteer.setActive(false);

        User user = new User();
        user.setUserName(first_name + " " + last_name);
        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setTelegramUserId(telegramUserId);
        user.setPhoneNumber(phone);
        user.setAddress(faker.address().fullAddress());

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setPhoneNumber("8-654-654-9874");                     // подставляем номер без + в начале

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setPhoneNumber("8-654-654-987");                      // подставляем номер короче на одну цифру

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setPhoneNumber("   ");                                // подставляем номер из одних пробелов

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));
    }

    /**
     * Метод для проверки правильности работы метода validateVolunteer при
     * передаче невалидного telegramUserId или ненайденного в базе данных
     * пользователей нашего бота
     */
    @Test
    public void validateVolunteerTelegramUserId_negativeTest(){
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1);
        String first_name = faker.name().firstName();
        String last_name = faker.name().lastName();
        volunteer.setName(first_name + " " + last_name);
        String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
        volunteer.setPhoneNumber(phone);
        long telegramUserId = new Random().nextInt(1, 1000000000);
        volunteer.setTelegramUserId(telegramUserId);
        volunteer.setActive(false);

        User user = new User();
        user.setUserName(first_name + " " + last_name);
        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setTelegramUserId(telegramUserId + 1);
        user.setPhoneNumber(phone);
        user.setAddress(faker.address().fullAddress());

        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());        // при поиске в базе данных по идентификатору пользователя Telegram вернулся пустой Optional
                                                                                    // т.е. пользователь не зарегистрирован в нашей базе данных
        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setTelegramUserId(0);                                             // подстановка в качестве telegramUserId невалидного числа

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

        volunteer.setTelegramUserId(-1);                                             // подстановка в качестве telegramUserId невалидного числа

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validate(volunteer));

    }


    /**
     * Метод для проверки правильности работы
     * метода для проверки наличия идентификатора
     * в базе данных в таблице волонтеров
     * при передаче валидного идентификатора
     */
    @Test
    public void validateId_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(faker.random().nextInt());
            String first_name = faker.name().firstName();
            String last_name = faker.name().lastName();
            volunteer.setName(first_name + " " + last_name);
            String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            volunteer.setPhoneNumber(phone);
            long telegramUserId = new Random().nextInt(1, 1000000000);
            volunteer.setTelegramUserId(telegramUserId);
            volunteer.setActive(false);

            when(volunteerRepo.findById(volunteer.getId())).thenReturn(Optional.of(volunteer));
            validator.validateId(volunteer.getId());
        }
    }

    /**
     * Метод для проверки правильности работы
     * метода для проверки наличия идентификатора
     * в базе данных в таблице волонтеров
     * при передаче невалидного идентификатора
     */
    @Test
    public void validateId_negativeTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(faker.random().nextInt());
            String first_name = faker.name().firstName();
            String last_name = faker.name().lastName();
            volunteer.setName(first_name + " " + last_name);
            String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            volunteer.setPhoneNumber(phone);
            long telegramUserId = new Random().nextInt(1, 1000000000);
            volunteer.setTelegramUserId(telegramUserId);
            volunteer.setActive(false);

            when(volunteerRepo.findById(volunteer.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFoundException.class, () -> validator.validateId(volunteer.getId()));
        }
    }


    /**
     * Метод для проверки правильности работы
     * метода проверки переданной строки на null,
     * пустоту или пробелы при передаче валидной
     * строки
     */
    @Test
    public void stringValidate_positiveTest(){
        for (int i = 0; i < 5; i++) {
            String string = faker.name().toString();
            validator.stringValidate(string);
        }
    }

    /**
     * Метод для проверки правильности работы
     * метода проверки переданной строки на null,
     * пустоту или пробелы при передаче невалидной
     * строки
     */
    @Test
    public void stringValidate_negativeTest(){

        Assertions.assertThrows(IllegalParameterException.class, () -> validator.stringValidate(null));

        String emptyString = "";
        Assertions.assertThrows(IllegalParameterException.class, () -> validator.stringValidate(emptyString));

        String spaceString = "        ";
        Assertions.assertThrows(IllegalParameterException.class, () -> validator.stringValidate(spaceString));

    }


    /**
     * Метод для проверки метода для проверки
     * телефонного номера на соответствие формату
     * при валидном входном параметре
     */
    @Test
    public void phoneValidate_positiveTest(){
        for (int i = 0; i < 5; i++) {
            String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            validator.phoneValidate(phone);
        }
    }


    /**
     * Метод для тестирования метода для проверки
     * телефонного номера на соответствие формату
     * при невалидном входном параметре
     */
    @Test
    public void phoneValidate_negativeTest(){
        for (int i = 0; i < 5; i++) {
            String phone = faker.phoneNumber().cellPhone();
            Assertions.assertThrows(IllegalParameterException.class, () -> validator.phoneValidate(phone));
        }
    }
}
