package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.validator.VolunteerValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceMockTest {
    @Mock
    VolunteerRepo volunteerRepo;
    @Mock
    VolunteerValidator validator;
    @InjectMocks
    VolunteerService volunteerService;
    private final Faker faker = new Faker();

    /**
     * Метод для проверки правильности работы
     * метода upload при валидном входном параметре
     * объекте класса Volunteer
     */
    @Test
    public void upload_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(faker.random().nextInt(0, 1000000000));
            String first_name = faker.name().firstName();
            String last_name = faker.name().lastName();
            volunteer.setName(first_name + " " + last_name);
            String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            volunteer.setPhoneNumber(phone);
            long telegramUserId = new Random().nextInt(1, 1000000000);
            volunteer.setTelegramUserId(telegramUserId);
            volunteer.setActive(false);

            when(volunteerRepo.save(volunteer)).thenReturn(volunteer);
            Assertions.assertEquals(volunteer, volunteerService.upload(volunteer));
        }
    }

    /**
     * Метод для проверки правильности работы
     * метода upload при получении невалидного объекта Volunteer,
     * т.е. имеющего одно и более невалидное поле
     */
    @Test
    public void upload_negativeTest(){
        // проверка при получении объекта с невалидным полем
        Volunteer volunteer = new Volunteer();

        Mockito.doThrow(new IllegalFieldException("")).when(validator).validate(volunteer);

        Assertions.assertThrows(IllegalFieldException.class, () -> volunteerService.upload(volunteer));
    }

    /**
     * Проверка правильности работы метода
     * updateName при получении валидных значений
     * параметров
     */
    @Test
    public void updateName_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            String name = faker.name().name();
            int id = 1;
            String phone = faker.phoneNumber().cellPhone();
            volunteer.setId(id);
            volunteer.setName(name);
            volunteer.setPhoneNumber(phone);
            when(volunteerRepo.findById(anyInt())).thenReturn(Optional.ofNullable(volunteer));
            String newName = faker.name().name();
            when(volunteerRepo.save(volunteer)).thenReturn(volunteer);
            Assertions.assertEquals(newName, volunteerService.updateName(newName, id).getName());
        }
    }

    /**
     * Проверка правильности работы метода updateName
     * при получении невалидного параметра name
     */
    @Test
    public void updateName_negativeTestByName(){
        // проверка при получении невалидного значения имени
        Volunteer volunteer = new Volunteer();
        String name = null;
        int id = 1;
        String phone = faker.phoneNumber().cellPhone();
        volunteer.setId(id);
        volunteer.setName(name);
        volunteer.setPhoneNumber(phone);

        Mockito.doThrow(new IllegalParameterException("")).when(validator).stringValidate(name);
        Assertions.assertThrows(IllegalParameterException.class, () -> volunteerService.updateName(name, id));
    }


    /**
     * Проверка метода updateName при передаче в параметр
     * невалидного id, который не содержиться в базе данных
     */
    @Test
    public void updateName_negativeTestById(){
        // проверка при получении невалидного значения id, которое отсутствует в базе данных
        String name = faker.name().name();
        int id = 0;
        Mockito.doThrow(new EntityNotFoundException((long) id)).when(validator).validateId(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerService.updateName(name, id));
    }


    /**
     * Проверка метода updateStatus при передаче
     * валидного параметра status
     */
    @Test
    public void updateStatus_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            String name = faker.name().name();
            int id = 1;
            String phone = faker.phoneNumber().cellPhone();
            volunteer.setId(id);
            volunteer.setName(name);
            volunteer.setPhoneNumber(phone);
            when(volunteerRepo.findById(anyInt())).thenReturn(Optional.ofNullable(volunteer));
            Boolean newStatus = !volunteer.isActive();
            when(volunteerRepo.save(volunteer)).thenReturn(volunteer);
            Assertions.assertEquals(newStatus, volunteerService.updateStatus(newStatus, id).isActive());
        }
    }

    /**
     * Проверка метода updateStatus при передаче идентификатора,
     * который отсутствует в базе данных
     */
    @Test
    public void updateStatus_negativeTestById(){
        // проверка при получении невалидного значения id, которое отсутствует в базе данных
        Boolean newStatus = true;
        int id = 0;
        Mockito.doThrow(new EntityNotFoundException((long) id)).when(validator).validateId(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerService.updateStatus(newStatus, id));
    }


    /**
     * Проверка метода updatePhone при передаче валидного
     * параметра phone
     */
    @Test
    public void updatePhone_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            String name = faker.name().name();
            int id = 1;
            String phone = faker.phoneNumber().cellPhone();
            volunteer.setId(id);
            volunteer.setName(name);
            volunteer.setPhoneNumber(phone);
            when(volunteerRepo.findById(anyInt())).thenReturn(Optional.ofNullable(volunteer));
            String newPhone = faker.phoneNumber().cellPhone();
            when(volunteerRepo.save(volunteer)).thenReturn(volunteer);
            Assertions.assertEquals(newPhone, volunteerService.updateName(newPhone, id).getName());
        }
    }


    /**
     * Проверка метода updatePhone при получении невалидного значения
     * номера телефона phone
     */
    @Test
    public void updatePhone_negativeTestByPhone(){
        // проверка при получении невалидного значения телефона
        Volunteer volunteer = new Volunteer();
        String name = null;
        int id = 1;
        String phone = "";
        volunteer.setId(id);
        volunteer.setName(name);
        volunteer.setPhoneNumber(phone);

        Mockito.doThrow(new IllegalParameterException("")).when(validator).stringValidate(phone);
        Assertions.assertThrows(IllegalParameterException.class, () -> volunteerService.updatePhone(phone, id));
    }


    /**
     * Проверка метода updatePhone при передаче невалидного
     * идентификатора, который не содержится в базе данных
     */
    @Test
    public void updatePhone_negativeTestById(){
        // проверка при получении невалидного значения id, которое отсутствует в базе данных
        String newPhone = faker.phoneNumber().cellPhone();
        int id = 0;
        Mockito.doThrow(new EntityNotFoundException((long) id)).when(validator).validateId(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerService.updatePhone(newPhone, id));
    }


    /**
     * Проверка метода updateVolunteer при передаче валидных
     * параметров
     */
    @Test
    public void updateVolunteer_positiveTest(){
        for (int i = 0; i < 5; i++) {
            Volunteer volunteer = new Volunteer();
            int id = faker.random().nextInt();
            volunteer.setId(id);
            String first_name = faker.name().firstName();
            String last_name = faker.name().lastName();
            volunteer.setName(first_name + " " + last_name);
            String phone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            volunteer.setPhoneNumber(phone);
            long telegramUserId = new Random().nextInt(1, 1000000000);
            volunteer.setTelegramUserId(telegramUserId);
            volunteer.setActive(false);

            when(volunteerRepo.findById(volunteer.getId())).thenReturn(Optional.ofNullable(volunteer));
            String newName = faker.name().firstName() + " " + faker.name().lastName();
            volunteer.setName(newName);

            Assertions.assertEquals(volunteer, volunteerService.updateVolunteer(id, volunteer));
        }
    }


    /**
     * Проверка метода updateVolunteer при передаче
     * невалидного объекта Volunteer
     */
    @Test
    public void updateVolunteer_negativeTestByVolunteer(){
        Volunteer volunteer = new Volunteer();
        int id = 1;
        Mockito.doThrow(new IllegalFieldException("")).when(validator).validate(volunteer);
        Assertions.assertThrows(IllegalFieldException.class, () -> volunteerService.updateVolunteer(id, volunteer));
    }

    /**
     * Проверка метода updateVolunteer при передаче
     * невалидного id, который не присутствует в базе данных
     */
    @Test
    public void updateVolunteer_negativeTestById(){
        Volunteer volunteer = new Volunteer();
        int id = 0;
        Mockito.doThrow(new EntityNotFoundException((long) id)).when(validator).validateId(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerService.updateVolunteer(id, volunteer));
    }


    /**
     * Проверка метода get при получении валидного id
     */
    @Test
    public void get_positiveTest(){
        Volunteer volunteer = new Volunteer();
        int id = 1;
        volunteer.setId(id);
        when(volunteerRepo.findById(anyInt())).thenReturn(Optional.ofNullable(volunteer));
        Assertions.assertEquals(volunteer, volunteerService.get(id));
    }

    /**
     * Проверка метода get при получении невалидного id
     */
    @Test
    public void get_negativeTest(){
        int id = 0;
        Mockito.doThrow(new EntityNotFoundException((long) id)).when(validator).validateId(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerService.get(id));
    }

    /**
     * Метод для проверки работы метода delete при
     * передаче валидного id
     */
    @Test
    public void delete_positiveTest(){
        Volunteer volunteer = new Volunteer();
        int id = 1;
        volunteer.setId(id);
        when(volunteerRepo.findById(id)).thenReturn(Optional.ofNullable(volunteer));
        Assertions.assertEquals(volunteer, volunteerService.delete(id));
    }


    /**
     * Метод для проверки работы метода delete при
     * передаче невалидного id
     */
    @Test
    public void delete_negativeTest(){
        Volunteer volunteer = new Volunteer();
        int id = 0;
        volunteer.setId(id);
        Mockito.doThrow(new EntityNotFoundException((long) id)).when(validator).validateId(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> volunteerService.delete(id));
    }


    @Test
    public void getAll_Test(){
        List<Volunteer> volunteers = new ArrayList<>();
        when(volunteerRepo.findAll()).thenReturn(volunteers);
        Assertions.assertEquals(volunteers, volunteerService.getAll());
    }


}
