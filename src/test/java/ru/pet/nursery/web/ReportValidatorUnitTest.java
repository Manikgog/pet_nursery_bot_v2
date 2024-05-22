package ru.pet.nursery.web;

import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.ReportIsExistException;
import ru.pet.nursery.web.validator.ReportValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.web.Constants.NURSERY_1;

@ExtendWith(MockitoExtension.class)
public class ReportValidatorUnitTest {
    @Mock
    UserRepo userRepo;
    @Mock
    ReportRepo reportRepo;
    @Mock
    AnimalRepo animalRepo;
    @InjectMocks
    ReportValidator reportValidator;
    private final Faker faker = new Faker();

    @Test
    public void validate_positiveTest(){
        long adopterId = 2;
        User user = new User();
        user.setTelegramUserId(adopterId);
        user.setUserName(faker.name().firstName());
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        Animal animal = new Animal();
        animal.setAnimalName(faker.name().name());
        Random rnd = new Random();
        animal.setAnimalType(AnimalType.CAT);
        boolean isMale = rnd.nextBoolean();
        animal.setGender(isMale ? Gender.MALE : Gender.FEMALE);
        animal.setNursery(NURSERY_1);
        animal.setBirthDate(faker.date().birthdayLocalDate());
        animal.setPhotoPath(null);
        List<Animal> animals = new ArrayList<>();
        animals.add(animal);
        when(animalRepo.findByUser(user)).thenReturn(animals);
        reportValidator.validate(user);
    }

    /**
     * Проверка работы метода при отсутствии пользователя в базе данных
     */
    @Test
    public void validate_negativeTestByNotValidId(){
        Assertions.assertThrows(IllegalParameterException.class, () -> reportValidator.validate(new User()));
    }

    /**
     * Проверка работы метода при условии, что User не является усыновителем (is not adopter)
     */
    @Test
    public void validate_negativeTestByUserIsNotAdopter(){
        long adopterId = 2;

        User user = new User();
        user.setTelegramUserId(adopterId);
        user.setUserName(faker.name().firstName());
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        Assertions.assertThrows(IllegalParameterException.class, () -> reportValidator.validate(user));
    }

    /**
     * Проверка работы метода при условии, что отчёт на сегодня уже раннее создан
     */
    @Test
    public void validate_negativeTestByReportIsAlreadyInDB(){
        long adopterId = 2;

        User user = new User();
        user.setTelegramUserId(adopterId);
        user.setUserName(faker.name().firstName());
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        Animal animal = new Animal();
        animal.setAnimalName(faker.name().name());
        Random rnd = new Random();
        animal.setAnimalType(AnimalType.CAT);
        boolean isMale = rnd.nextBoolean();
        animal.setGender(isMale ? Gender.MALE : Gender.FEMALE);
        animal.setNursery(NURSERY_1);
        animal.setBirthDate(faker.date().birthdayLocalDate());
        animal.setPhotoPath(null);
        List<Animal> animals = new ArrayList<>();
        animals.add(animal);
        Report report = new Report();

        when(animalRepo.findByUser(user)).thenReturn(animals);
        when(reportRepo.findByUserAndReportDate(user, LocalDate.now())).thenReturn(report);
        Assertions.assertThrows(ReportIsExistException.class, () -> reportValidator.validate(user));
    }


    @Test
    public void validateDate_Test(){
        Assertions.assertThrows(IllegalFieldException.class, () -> reportValidator.validateDate(null));

        reportValidator.validateDate(LocalDate.now());
    }


    @Test
    public void validateIsAdopter_Test(){
        User user = new User();
        // если пользователь не является усыновителем
        when(animalRepo.findByUser(user)).thenReturn(new ArrayList<>());
        Assertions.assertThrows(IllegalParameterException.class, () -> reportValidator.validateIsAdopter(user));

        // если все в норме
        when(animalRepo.findByUser(user)).thenReturn(List.of(new Animal()));
        reportValidator.validateIsAdopter(user);
    }


    @Test
    public void isReportInDataBase_Test(){
        User user = new User();
        when(reportRepo.findByUserAndReportDate(user, LocalDate.now())).thenReturn(null);
        Assertions.assertFalse(reportValidator.isReportInDataBase(user));

        Report report = new Report();
        when(reportRepo.findByUserAndReportDate(user, LocalDate.now())).thenReturn(report);
        Assertions.assertTrue(reportValidator.isReportInDataBase(user));
    }



}
