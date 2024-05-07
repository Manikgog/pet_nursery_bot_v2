package ru.pet.nursery;

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
import ru.pet.nursery.configuration.TelegramBotConfiguration;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.service.AnimalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimalControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private TelegramBotConfiguration telegramBotConfiguration;
    @Autowired
    private AnimalRepo animalRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NurseryRepo nurseryRepo;
    @Autowired
    private AnimalService animalService;

    private final Faker faker = new Faker();
    private static int NUMBER_OF_USERS = 5;
    private static int NUMBER_OF_NURSERIES = 2;
    private static int NUMBER_OF_ANIMALS = 10;
    private static int MIN_AGE = 1;
    private static int MAX_AGE = 10;
    @BeforeEach
    public void beforeEach(){
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_USERS; i++){
            userList.add(createUser(i + 10));
        }
        userRepo.saveAll(userList);
        List<Nursery> nurseries = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_NURSERIES; i++){
            nurseries.add(createNursery());
        }
        nurseryRepo.saveAll(nurseries);
        List<Nursery> nurseriesFromDB = nurseryRepo.findAll();
        List<Animal> animals = new ArrayList<>();
        for(Nursery n : nurseriesFromDB){
            for(int j = 0; j < NUMBER_OF_ANIMALS; j++){
                animals.add(createAnimal(n.getId()));
            }
        }
        animalRepo.saveAll(animals);

    }

    @AfterEach
    public void afterEach(){
        animalRepo.deleteAll();
        nurseryRepo.deleteAll();
        userRepo.deleteAll();
    }

    private Animal createAnimal(int nurseryId){
        Animal animal = new Animal();
        animal.setAnimalName(faker.name().name());
        animal.setAnimalType(faker.animal().name());
        animal.setGender(faker.gender().binaryTypes());
        animal.setNurseryId(nurseryId);
        animal.setBirthDate(faker.date().birthdayLocalDate(MIN_AGE, MAX_AGE));
        animal.setPhotoPath(null);
        animal.setWhoTookPet(faker.random().nextInt(1,NUMBER_OF_USERS + 1));
        animal.setTookDate(faker.date().past(faker.random().nextInt(5, 15), TimeUnit.DAYS).toLocalDateTime().toLocalDate());
        animal.setDescription(faker.examplify(animal.getAnimalName()));
        return animal;
    }

    private Nursery createNursery(){
        Nursery nursery = new Nursery();
        nursery.setAddress(faker.address().fullAddress());
        nursery.setPhoneNumber(faker.phoneNumber().phoneNumberNational());
        return nursery;
    }

    private User createUser(int i){
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
    public void putAnimal_positiveTest(){
        for (int i = 0; i < 3; i++) {
            AnimalDTO animalDTO = new AnimalDTO();
            animalDTO.setAnimalName(faker.name().name());
            animalDTO.setAnimalType(faker.animal().name());
            animalDTO.setGender(faker.gender().binaryTypes());
            animalDTO.setNurseryId(nurseryRepo.findById(1).get().getId());
            animalDTO.setBirthDate(faker.date().birthdayLocalDate(MIN_AGE, MAX_AGE));
            animalDTO.setDescription(faker.examplify(animalDTO.getAnimalName()));

            ResponseEntity<Animal> responseEntity = testRestTemplate.postForEntity(
                    "http://localhost:" + port + "/animal",
                    animalDTO,
                    Animal.class
            );
            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            // проверяется наличие загруженного через POST запрос объекта в базе данных
            Optional<Animal> animalFromDB = animalRepo.findById(responseEntity.getBody().getId());
            Assertions.assertThat(animalFromDB).isPresent();
            Assertions.assertThat(animalFromDB.get())
                    .usingRecursiveComparison()
                    .isEqualTo(responseEntity.getBody());
        }
    }
}
