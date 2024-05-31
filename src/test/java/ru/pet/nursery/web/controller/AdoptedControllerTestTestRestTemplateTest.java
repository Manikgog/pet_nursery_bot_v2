package ru.pet.nursery.web.controller;

import com.pengrad.telegrambot.TelegramBot;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdoptedControllerTestTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private AnimalRepo animalRepo;
    @Autowired
    private UserRepo userRepo;

    private final Faker faker = new Faker();

    List<Animal> animalList = new ArrayList<>();
    List<User> userList = new ArrayList<>();

    @BeforeEach
    void BeforeEach() {
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setTelegramUserId(faker.random().nextLong());
            user.setUserName(faker.harryPotter().character());
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setAddress(faker.harryPotter().location());
            userList.add(user);
            userRepo.save(user);
        }
        for (int i = 0; i < 5; i++) {
            Animal animal = new Animal();
            animal.setUser(null);
            animal.setAnimalName(faker.harryPotter().character());
            animalList.add(animal);
            animalRepo.save(animal);
        }
    }

    @AfterEach
    void AfterEach() {
        animalRepo.deleteAll();
        userRepo.deleteAll();
        animalList.clear();
        userList.clear();
    }

    private String builderUrl(String url) {
        return "http://localhost:%d%s".formatted(port, url);
    }

    @Test
    void setAdopterForAnimalPositiveTest() {
        int rnd = faker.random().nextInt(animalList.size());
        Animal animal = animalList.get(rnd);
        User user = userList.get(faker.random().nextInt(userList.size()));

        ResponseEntity<Animal> responseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/setAdopterForAnimal?animalId=" + animal.getId() + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, Animal.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Optional<Animal> animalFromDb = animalRepo.findById(animal.getId());
        assertThat(animalFromDb.get()).usingRecursiveComparison().ignoringFields("petReturnDate", "tookDate", "user").isEqualTo(animal);
        assertThat(animalFromDb.get().getUser()).usingRecursiveComparison().ignoringFields("petReturnDate", "tookDate").isEqualTo(user);
        assertThat(animalFromDb.get().getTookDate()).isEqualTo(LocalDate.now());
        assertThat(animalFromDb.get().getPetReturnDate()).isEqualTo(LocalDate.now().plusDays(14));
    }

    @Test
    void setAdopterForAnimalNegativeTest_ifAnimalNotFound() {
        User user = userList.get(faker.random().nextInt(userList.size()));
        long animalId = -1L;

        ResponseEntity<String> responseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/setAdopterForAnimal?animalId=" + animalId + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String animalFromDb = responseEntity.getBody();
        assertThat(animalFromDb).isEqualTo("Питомца с таким ID = " + animalId + " нет в БД");
    }

    @Test
    void setAdopterForAnimalNegativeTest_ifUserNotFound() {
        int rnd = faker.random().nextInt(animalList.size());
        Animal animal = animalList.get(rnd);
        long userId = -1L;

        ResponseEntity<String> responseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/setAdopterForAnimal?animalId=" + animal.getId() + "&adopterId=" + userId),
                        HttpMethod.PUT, null, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String animalFromDb = responseEntity.getBody();
        assertThat(animalFromDb).isEqualTo("Пользователя с таким ID = " + userId + " не существует");
    }

    @Test
    void prolongTrialForNDaysPositiveTest() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        User user = userList.get(faker.random().nextInt(animalList.size()));
        ResponseEntity<Animal> responseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/setAdopterForAnimal?animalId=" + animal.getId() + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, Animal.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        int prolongDays = 5;
        ResponseEntity<Animal> prolongDaysResponseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/prolongTrialForNDays?animalId="+animal.getId()+"&days="+prolongDays),
                HttpMethod.PUT,
                null,
                Animal.class);
        assertThat(prolongDaysResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(prolongDaysResponseEntity.getBody()).isNotNull();
        Optional<Animal> animalFromDb = animalRepo.findById(animal.getId());
        assertThat(animalFromDb.get().getTookDate()).isEqualTo(LocalDate.now());
        assertThat(animalFromDb.get().getPetReturnDate()).isEqualTo(LocalDate.now().plusDays(14+prolongDays));
    }

    @Test
    void prolongTrialForNDaysPositiveTest_ifPetReturnDateIsNull() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        animal.setPetReturnDate(null);
        User user = userList.get(faker.random().nextInt(animalList.size()));
        ResponseEntity<Animal> responseEntitySetAdopter = testRestTemplate.
                exchange(builderUrl("/adopters/setAdopterForAnimal?animalId=" + animal.getId() + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, Animal.class);
        assertThat(responseEntitySetAdopter.getStatusCode()).isEqualTo(HttpStatus.OK);
        int prolongDays = 5;
        ResponseEntity<Animal> prolongDaysResponseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/prolongTrialForNDays?animalId="+animal.getId()+"&days="+prolongDays),
                        HttpMethod.PUT,
                        null,
                        Animal.class);
        assertThat(prolongDaysResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(prolongDaysResponseEntity.getBody()).isNotNull();
        Optional<Animal> animalFromDb = animalRepo.findById(animal.getId());
        assertThat(animalFromDb.get().getTookDate()).isEqualTo(LocalDate.now());
        assertThat(animalFromDb.get().getPetReturnDate()).isEqualTo(LocalDate.now().plusDays(14+prolongDays));
    }

    @Test
    void prolongTrialForNDaysNegativeTest_ifAnimalNotFound() {
        long animalId = -1;
        User user = userList.get(faker.random().nextInt(animalList.size()));
        ResponseEntity<String> responseEntitySetAdopter = testRestTemplate.
                exchange(builderUrl("/adopters?animalId=" + animalId + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, String.class);
        assertThat(responseEntitySetAdopter.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        int prolongDays = 5;
        ResponseEntity<String> prolongDaysResponseEntity = testRestTemplate.
                exchange(builderUrl("/adopters/prolongTrialForNDays?animalId="+animalId+"&days="+prolongDays),
                        HttpMethod.PUT,
                        null,
                        String.class);
        assertThat(prolongDaysResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String message = prolongDaysResponseEntity.getBody();
        assertThat(message).isEqualTo("Питомца с таким ID = " + animalId + " нет в БД");
    }
    @Test
    void cancelTrialPositiveTest() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        User user = userList.get(faker.random().nextInt(animalList.size()));
        ResponseEntity<Animal> responseEntitySetAdopter = testRestTemplate.
                exchange(builderUrl("/adopters/setAdopterForAnimal?animalId=" + animal.getId() + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, Animal.class);
        assertThat(responseEntitySetAdopter.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Animal> animalResponseEntity = testRestTemplate.exchange(builderUrl("/adopters/cancelTrial?animalId="+animal.getId()),
                HttpMethod.PUT,
                null,
                Animal.class);
        Optional<Animal> animalFromDb = animalRepo.findById(animal.getId());
        assertThat(animalResponseEntity.getBody()).isNotNull();
        assertThat(animalResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Animal animalResponse = animalResponseEntity.getBody();
        assertThat(animalResponse.getTookDate()).isNull();
        assertThat(animalResponse.getPetReturnDate()).isNull();
        assertThat(animalResponse.getUser()).isNull();
        assertThat(animalResponse).isEqualTo(animalFromDb.get());
    }

    @Test
    void cancelTrialNegativeTest_ifAnimalNotFound() {
        long animalId = -1;
        User user = userList.get(faker.random().nextInt(animalList.size()));
        ResponseEntity<String> responseEntitySetAdopter = testRestTemplate.
                exchange(builderUrl("/adopters?animalId=" + animalId + "&adopterId=" + user.getTelegramUserId()),
                        HttpMethod.PUT, null, String.class);
        assertThat(responseEntitySetAdopter.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ResponseEntity<String> animalResponseEntity = testRestTemplate.exchange(builderUrl("/adopters/cancelTrial?animalId="+animalId),
                HttpMethod.PUT,
                null,
                String.class);
        assertThat(animalResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        String message = animalResponseEntity.getBody();
        assertThat(message).isEqualTo("Питомца с таким ID = " + animalId + " нет в БД");
    }
}