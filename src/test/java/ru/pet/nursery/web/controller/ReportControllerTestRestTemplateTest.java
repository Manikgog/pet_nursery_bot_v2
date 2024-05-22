package ru.pet.nursery.web.controller;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;
import ru.pet.nursery.web.validator.VolunteerValidator;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    ReportService reportService;
    @Autowired
    ReportRepo reportRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    NurseryRepo nurseryRepo;
    @Autowired
    AnimalRepo animalRepo;
    @Autowired
    ReportValidator reportValidator;
    @Autowired
    VolunteerValidator validator;
    private final Faker faker = new Faker();
    private final static int NUMBER_OF_USERS = 5;
    private final static int NUMBER_OF_ANIMALS = 5;
    private final static int MIN_AGE = 1;
    private final static int MAX_AGE = 10;

    @BeforeEach
    public void beforeEach(){
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_USERS; i++){
            userList.add(createUser(i + 10));
        }
        userRepo.saveAll(userList);
        List<Nursery> nurseries = new ArrayList<>();
        nurseries.add(createNursery(false));
        nurseries.add(createNursery(true));

        nurseryRepo.saveAll(nurseries);
        List<Nursery> nurseriesFromDB = nurseryRepo.findAll();
        for(Nursery n : nurseriesFromDB){
            for(int j = 0; j < NUMBER_OF_ANIMALS; j++){
                animalRepo.save(createAnimal(n));
            }
        }
    }

    @AfterEach
    public void afterEach(){
        animalRepo.deleteAll();
        reportRepo.deleteAll();
        userRepo.deleteAll();
        nurseryRepo.deleteAll();
    }

    private Animal createAnimal(Nursery nursery){
        Animal animal = new Animal();
        animal.setAnimalName(faker.name().name());
        Random rnd = new Random();
        animal.setAnimalType(nursery.isForDog() ? AnimalType.DOG : AnimalType.CAT);
        boolean isMale = rnd.nextBoolean();
        animal.setGender(isMale ? Gender.MALE : Gender.FEMALE);
        animal.setNursery(nursery);
        animal.setBirthDate(faker.date().birthdayLocalDate(MIN_AGE, MAX_AGE));
        animal.setPhotoPath(null);
        boolean isAdopted = rnd.nextInt(10) <= 2;
        if(isAdopted){
            User whoNotAdopt = findUserWhoNotAdopt();
            animal.setUser(whoNotAdopt);
            animal.setTookDate(faker.date().past(faker.random().nextInt(5, 15), TimeUnit.DAYS).toLocalDateTime().toLocalDate());
        }else {
            animal.setUser(null);
        }
        animal.setDescription(faker.examplify(animal.getAnimalName()));
        return animal;
    }

    /**
     * Метод для поиска пользователя, который еще не усыновлял
     * животное
     * @return объект класса User
     */
    private User findUserWhoNotAdopt(){
        List<User> userWhoAdopt = animalRepo.findAll()
                .stream()
                .map(Animal::getUser)
                .filter(Objects::nonNull)
                .toList();
        Optional<User> userWhoNotAdopt = userRepo.findAll()
                .stream()
                .filter(Objects::nonNull)
                .filter(u -> !userWhoAdopt.contains(u))
                .findFirst();
        return userWhoNotAdopt.orElse(null);
    }

    /**
     * Метод для получения пользователя который усыновил животное
     * @return пользователь, усыновивший животное
     */
    private List<User> findUserWhoAdopt(){
        // список тех кто усыновлял животное
        return animalRepo.findAll()
                .stream()
                .map(Animal::getUser)
                .filter(Objects::nonNull)
                .toList();
    }

    private Nursery createNursery(boolean forDog){
        Nursery nursery = new Nursery();
        nursery.setNameShelter(faker.funnyName().name());
        nursery.setForDog(forDog);
        nursery.setAddress(faker.address().fullAddress());
        nursery.setPhoneNumber(faker.phoneNumber().phoneNumberNational());
        return nursery;
    }

    private User createUser(long i){
        User user = new User();
        user.setTelegramUserId(i + 10);
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUserName(user.getFirstName() + user.getLastName());
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberNational());
        return user;
    }



    @Test
    public void upload_positiveTest(){

        List<User> adopters = findUserWhoAdopt();
        User adopter = adopters.get(faker.random().nextInt(0, adopters.size() - 1));
        long adopterId = adopter.getTelegramUserId();

        ResponseEntity<Report> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/report/" + adopterId,
                null,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
    }


}
