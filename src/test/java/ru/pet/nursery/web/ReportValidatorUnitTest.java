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
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.ReportIsExistException;
import ru.pet.nursery.web.validator.ReportValidator;
import java.time.LocalDate;
import java.util.Optional;
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

        when(userRepo.findById(adopterId)).thenReturn(Optional.of(user));
        when(animalRepo.findByUser(user)).thenReturn(animal);
        reportValidator.validate(adopterId);
    }

    /**
     * Проверка работы метода при отсутствии пользователя в базе данных
     */
    @Test
    public void validate_negativeTestByNotValidId(){
        long adopterId = -2;
        Assertions.assertThrows(IllegalFieldException.class, () -> reportValidator.validate(adopterId));
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

        when(userRepo.findById(adopterId)).thenReturn(Optional.of(user));
        Assertions.assertThrows(IllegalParameterException.class, () -> reportValidator.validate(adopterId));
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

        Report report = new Report();

        when(userRepo.findById(adopterId)).thenReturn(Optional.of(user));
        when(animalRepo.findByUser(user)).thenReturn(animal);
        when(reportRepo.findByUserAndReportDate(user, LocalDate.now())).thenReturn(report);
        Assertions.assertThrows(ReportIsExistException.class, () -> reportValidator.validate(adopterId));
    }


    @Test
    public void validateDate_Test(){
        Assertions.assertThrows(IllegalFieldException.class, () -> reportValidator.validateDate(null));

        reportValidator.validateDate(LocalDate.now());
    }


    @Test
    public void validateIsAdopter_Test(){
        // при отсутствии пользователя в базе данных
        long userId = 2;
        Assertions.assertThrows(EntityNotFoundException.class, () -> reportValidator.validateIsAdopter(userId));

        // если пользователь не является усыновителем
        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(animalRepo.findByUser(new User())).thenReturn(null);
        Assertions.assertThrows(IllegalParameterException.class, () -> reportValidator.validateIsAdopter(userId));

        // если все в норме
        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(animalRepo.findByUser(new User())).thenReturn(new Animal());
        reportValidator.validateIsAdopter(userId);
    }


}
