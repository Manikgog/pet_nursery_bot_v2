package ru.pet.nursery.web.controller;

import com.pengrad.telegrambot.TelegramBot;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.UserRepo;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepo userRepo;

    private final Faker faker = new Faker();

    private final List<User> userList = new ArrayList<>(10);

    @AfterEach
    public void afterEach() {
        userList.clear();
        userRepo.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        for (int i = 0; i < 9; i++) {
            User user = new User();
            user.setTelegramUserId(faker.random().nextLong(1231231));
            user.setUserName(faker.harryPotter().character());
            user.setFirstName(faker.harryPotter().character());
            user.setLastName(faker.harryPotter().character());
            user.setAddress(faker.harryPotter().location());
            user.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
            userList.add(user);
            userRepo.save(user);
        }
    }

    private String builderUrl(String url) {
        return "http://localhost:%d%s".formatted(port, url);
    }

    @Test
    void postUserPositiveTest() {
        User user = new User();
        Long telegramUserId = faker.random().nextLong(0, 1000000000);
        user.setTelegramUserId(telegramUserId);
        String firstName = faker.name().firstName();
        user.setFirstName(firstName);
        String lastName = faker.name().lastName();
        user.setLastName(lastName);
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational().substring(0, 15));
        ResponseEntity<User> responseEntity = testRestTemplate.postForEntity(builderUrl("/users"),
                user,
                User.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Optional<User> UserFromDB = userRepo.findById(responseEntity.getBody().getTelegramUserId());
        Assertions.assertThat(UserFromDB).isPresent();
        Assertions.assertThat(UserFromDB.get())
                .usingRecursiveComparison()
                .isEqualTo(responseEntity.getBody());
    }

    @Test
    void postUserNegativeTest_byLastNameNull() {
        User user = new User();
        user.setTelegramUserId(faker.random().nextLong(1231231));
        user.setUserName(faker.harryPotter().character());
        user.setFirstName(null);
        user.setLastName(null);
        user.setAddress(faker.harryPotter().location());
        user.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(builderUrl("/users"),
                user,
                String.class);

        String created = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(created).isEqualTo("Необходимо ввести имя/или фамилию");
    }
    @Test
    void postUserNegativeTest_byFirstNameNull() {
        User user = new User();
        user.setTelegramUserId(faker.random().nextLong(1231231));
        user.setUserName(faker.harryPotter().character());
        user.setFirstName(null);
        user.setLastName(null);
        user.setAddress(faker.harryPotter().location());
        user.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(builderUrl("/users"),
                user,
                String.class);

        String created = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(created).isEqualTo("Необходимо ввести имя/или фамилию");
    }

    @Test
    void getUserByIdPositiveTest() {
        User user = userList.get(faker.random().nextInt(userList.size()));
        ResponseEntity<User> getUserFromDb = testRestTemplate.getForEntity(builderUrl("/users/"+user.getTelegramUserId()),
                User.class);
        User get = getUserFromDb.getBody();
        assertThat(getUserFromDb.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get).isNotNull();
        assertThat(get).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    void getUserByIdNegativeTest() {
        int invalidId = -1;
        ResponseEntity<String> getUserFromDb = testRestTemplate.getForEntity(builderUrl("/users/" + invalidId),
                String.class);
        String created = getUserFromDb.getBody();
        assertThat(getUserFromDb.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(created).isEqualTo("Пользователя с таким ID = " + invalidId + " не существует");
    }


    @Test
    void updateUserPositiveTest() {
        User oldUser = userList.get(faker.random().nextInt(userList.size()));
        Long id = oldUser.getTelegramUserId();
        User newUser = new User();
        newUser.setTelegramUserId(id);
        newUser.setFirstName("newName");
        newUser.setPhoneNumber(oldUser.getPhoneNumber()+5);
        HttpEntity<User> entity = new HttpEntity<>(newUser);
        ResponseEntity<User> updateNursery = testRestTemplate.exchange(builderUrl("/users/"+id),
                HttpMethod.PUT,
                entity,
                User.class);
        assertThat(updateNursery.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateNursery.getBody()).isNotNull();
        assertThat(updateNursery.getBody())
                .isEqualTo(newUser);

    }

    @Test
    void updateUserNegativeTest() {
        User newUser = new User();
        newUser.setTelegramUserId(faker.random().nextLong(1231231));
        newUser.setUserName(faker.harryPotter().character());
        newUser.setFirstName(faker.name().firstName());
        newUser.setLastName(faker.name().lastName());
        newUser.setAddress(faker.harryPotter().location());
        newUser.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
        HttpEntity<User> entity = new HttpEntity<>(newUser);
        ResponseEntity<String> updateNursery = testRestTemplate.exchange(builderUrl("/users/-1"),
                HttpMethod.PUT,
                entity,
                String.class);
        assertThat(updateNursery.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateNursery.getBody()).isNotNull();
        assertThat(updateNursery.getBody()).isEqualTo("Пользователя с таким ID = " + -1 + " не существует");
    }

    @Test
    void deleteUserPositiveTest() {
        User user = userList.get(faker.random().nextInt(userList.size()));
        ResponseEntity<User> deleteUser = testRestTemplate.exchange(builderUrl("/users/"+user.getTelegramUserId()),
                HttpMethod.DELETE,
                null,
                User.class);
        assertThat(deleteUser.getBody()).isNotNull();
        assertThat(deleteUser.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getUserFromDb = testRestTemplate.getForEntity(builderUrl("/users/"+user.getTelegramUserId()),
                String.class);
        String created = getUserFromDb.getBody();
        assertThat(getUserFromDb.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(created).isEqualTo("Пользователя с таким ID = " + user.getTelegramUserId() + " не существует");
    }

    @Test
    void deleteUserNegativeTest() {
        User user = new User();
        user.setTelegramUserId(faker.random().nextLong());
        ResponseEntity<String> deleteUser = testRestTemplate.exchange(builderUrl("/users/"+user.getTelegramUserId()),
                HttpMethod.DELETE,
                null,
                String.class);
        String created = deleteUser.getBody();
        assertThat(deleteUser.getBody()).isNotNull();
        assertThat(deleteUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(created).isEqualTo("Пользователя с таким ID = " + user.getTelegramUserId() + " не существует");
    }

    @Test
    void paginationUserFromDb() throws InterruptedException {
        Random rnd = new Random();
        List<User> users = userRepo.findAll()
                .stream()
                .toList();
        int userRepoSize = users.size();
        int page = rnd.nextInt(userRepoSize/2) + 1;
        int size = rnd.nextInt(userRepoSize/4) + 1;
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<User> usersByPage = userRepo.findAll(pageRequest)
                .stream()
                .toList();
        Thread.sleep(500);
        ResponseEntity<List<User>> responseEntity = testRestTemplate
                .exchange(
                        builderUrl("/users?page=" + page + "&size=" + size),
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Thread.sleep(500);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isEqualTo(usersByPage.size());
    }
}