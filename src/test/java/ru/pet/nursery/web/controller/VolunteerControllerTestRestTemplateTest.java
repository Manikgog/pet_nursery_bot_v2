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
import ru.pet.nursery.entity.User;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.repository.VolunteerRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VolunteerControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private VolunteerRepo volunteerRepo;
    private final Faker faker = new Faker();

    private final int NUMBER_OF_USER = 10;
    private final int NUMBER_OF_VOLUNTEER = NUMBER_OF_USER/3;
    private List<User> users = new ArrayList<>();
    private List<Volunteer> volunteers = new ArrayList<>();
    @BeforeEach
    public void beforeEach(){
        for (int i = 0; i < NUMBER_OF_USER; i++) {
            users.add(createUser());
        }
        userRepo.saveAll(users);

        for (int i = 0; i < NUMBER_OF_VOLUNTEER; i++) {
            volunteers.add(createVolunteer(i));
        }
        volunteerRepo.saveAll(volunteers);
    }

    private User createUser(){
        User user = new User();
        user.setTelegramUserId(faker.random().nextInt(0, 1000000000));
        String firstName = faker.name().firstName();
        user.setFirstName(firstName);
        String lastName = faker.name().lastName();
        user.setLastName(lastName);
        user.setUserName(firstName + " " + lastName);
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational().substring(0, 15));
        return user;
    }

    private Volunteer createVolunteer(int i){
        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(users.get(i).getUserName());
        volunteer.setPhoneNumber(users.get(i).getPhoneNumber());
        volunteer.setTelegramUserId(users.get(i).getTelegramUserId());
        volunteer.setActive(false);
        return volunteer;
    }

    @AfterEach
    public void afterEach(){
        users.clear();
        volunteers.clear();
        userRepo.deleteAll();
        volunteerRepo.deleteAll();
    }

    /**
     * Проверка работы метода загрузки объекта волонтёра в базу данных
     * при валидных полях объекта волонтёра и наличии в базе данных пользователей
     * telegramUserId такого же как и в объекте волонтёра
     */
    @Test
    public void upload_positiveTest(){
        User user = new User();
        int telegramUserId = faker.random().nextInt(0, 1000000000);
        user.setTelegramUserId(telegramUserId);
        String firstName = faker.name().firstName();
        user.setFirstName(firstName);
        String lastName = faker.name().lastName();
        user.setLastName(lastName);
        user.setUserName(firstName + " " + lastName);
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational().substring(0, 15));
        userRepo.save(user);

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setTelegramUserId(telegramUserId);
        volunteer.setActive(false);

        ResponseEntity<Volunteer> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                Volunteer.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        // проверяется наличие загруженного через POST запрос объекта в базе данных
        Optional<Volunteer> volunteerFromDB = volunteerRepo.findById(responseEntity.getBody().getId());
        Assertions.assertThat(volunteerFromDB).isPresent();
        Assertions.assertThat(volunteerFromDB.get())
                .usingRecursiveComparison()
                .isEqualTo(responseEntity.getBody());
    }

}
