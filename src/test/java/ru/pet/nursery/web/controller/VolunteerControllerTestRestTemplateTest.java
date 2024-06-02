package ru.pet.nursery.web.controller;

import com.pengrad.telegrambot.TelegramBot;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.repository.VolunteerRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VolunteerControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private VolunteerRepo volunteerRepo;
    private final Faker faker = new Faker();
    private final int NUMBER_OF_USERS = 10;
    private final int NUMBER_OF_VOLUNTEERS = NUMBER_OF_USERS / 3;
    private final List<User> users = new ArrayList<>();
    private final List<Volunteer> volunteers = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            users.add(createUser());
        }
        userRepo.saveAll(users);

        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            volunteers.add(createVolunteer(i));
        }
        volunteerRepo.saveAll(volunteers);
    }

    private User createUser() {
        User user = new User();
        user.setTelegramUserId(faker.random().nextLong(0, 1000000000));
        String firstName = faker.name().firstName();
        user.setFirstName(firstName);
        String lastName = faker.name().lastName();
        user.setLastName(lastName);
        user.setUserName(firstName + " " + lastName);
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(faker.phoneNumber().phoneNumberInternational().substring(0, 15));
        return user;
    }

    private Volunteer createVolunteer(int i) {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(users.get(i).getUserName());
        volunteer.setPhoneNumber(users.get(i).getPhoneNumber());
        volunteer.setTelegramUserId(users.get(i).getTelegramUserId());
        volunteer.setActive(false);
        return volunteer;
    }

    @AfterEach
    public void afterEach() {
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
    public void upload_positiveTest() {
        User user = new User();
        long telegramUserId = faker.random().nextInt(0, 1000000000);
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

    /**
     * Проверка работы метода при получении
     * невалидного объекта Volunteer c полем равным null
     */
    @Test
    public void upload_negativeTestByNameNull() {
        // Получение объекта класса Volunteer с полем имени равным null
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setTelegramUserId(user.getTelegramUserId());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Поле name не должно быть пустым или состоять из одних пробелов\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * имени, состоящего из пустой строки
     */
    @Test
    public void upload_negativeTestByNameEmpty() {
        // Получение объекта класса Volunteer с полем имени равным пустой строке
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName("");
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setTelegramUserId(user.getTelegramUserId());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Поле name не должно быть пустым или состоять из одних пробелов\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * имени, состоящего из строки из одних пробелов
     */
    @Test
    public void upload_negativeTestByNameSpaces() {
        // Получение объекта класса Volunteer с полем имени из одних пробелов
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName("    ");
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setTelegramUserId(user.getTelegramUserId());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Поле name не должно быть пустым или состоять из одних пробелов\n");
    }

    /**
     * Проверка работы метода при передаче невалидного
     * номера телефона равного null
     */
    @Test
    public void upload_negativeTestByPhoneNull() {
        // Получение объекта класса Volunteer с полем phoneNumber равным null
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());

        volunteer.setTelegramUserId(user.getTelegramUserId());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Поле phoneNumber не должно быть равен null, быть пустым или состоять из одних пробелов\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * номера телефона, состоящего из пустой строки
     */
    @Test
    public void upload_negativeTestByPhoneEmpty() {
        // Получение объекта класса Volunteer с полем phoneNumber из одних пробелов
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());
        String phone = "";
        volunteer.setPhoneNumber(phone);
        volunteer.setTelegramUserId(user.getTelegramUserId());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(
                "Поле phoneNumber не должно быть равен null, быть пустым или состоять из одних пробелов\n" +
                        "Телефон " + phone + " не соответствует формату: +7-654-654-6565 или +1 546 879 2121 или +8/214/541/5475\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * номера телефона, состоящего из одних пробелов
     */
    @Test
    public void upload_negativeTestByPhoneSpaces() {
        // Получение объекта класса Volunteer с полем phoneNumber из одних пробелов
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());
        String phone = "     ";
        volunteer.setPhoneNumber(phone);
        volunteer.setTelegramUserId(user.getTelegramUserId());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(
                "Поле phoneNumber не должно быть равен null, быть пустым или состоять из одних пробелов\n" +
                        "Телефон " + phone + " не соответствует формату: +7-654-654-6565 или +1 546 879 2121 или +8/214/541/5475\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * telegramUserId равного null
     */
    @Test
    public void upload_negativeTestByTelegramUserIdNull() {
        // Получение объекта класса Volunteer с полем telegramUserId равным null
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(
                "Поле telegramUserId должно быть больше 0\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * telegramUserId меньшего нуля
     */
    @Test
    public void upload_negativeTestByTelegramUserIdLessThenNull() {
        // Получение объекта класса Volunteer с полем telegramUserId равным null
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());
        volunteer.setTelegramUserId(-1);
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(
                "Поле telegramUserId должно быть больше 0\n");
    }


    /**
     * Проверка работы метода при передаче невалидного
     * telegramUserId, который отсутствует в базе данных
     */
    @Test
    public void upload_negativeTestByTelegramUserIdIsNotInDB() {
        // Получение объекта класса Volunteer с полем telegramUserId равным null
        User user = users.get(faker.random().nextInt(0, NUMBER_OF_USERS - 1));
        // нахождение telegramUserId, который отсутствует в базе данных
        int telegramUserId = 1;
        while (true) {
            int finalTelegramUserId = telegramUserId;
            if (users.stream()
                    .map(User::getTelegramUserId)
                    .filter(t -> t == finalTelegramUserId)
                    .findFirst()
                    .isEmpty()) {
                break;
            }
            telegramUserId = faker.random().nextInt(0, 100);
        }

        Volunteer volunteer = new Volunteer();
        volunteer.setId(0);
        volunteer.setName(user.getUserName());
        volunteer.setTelegramUserId(telegramUserId);
        volunteer.setPhoneNumber(user.getPhoneNumber());
        volunteer.setActive(false);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/volunteer",
                volunteer,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(
                "Идентификатор пользователя " + telegramUserId + " отсутствует в базе данных. " +
                        "Необходимо зайти в наш бот тогда ваш идентификатор добавиться в базу данных.\n");
    }

    /**
     * Проверка правильности работы метода updateName
     * при валидных входных параметрах
     */
    @Test
    public void updateName_positiveTest() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
            int id = volunteer.getId();
            String name = faker.name().firstName();
            ResponseEntity<Volunteer> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}/name?name={newName}",
                    HttpMethod.PUT,
                    HttpEntity.EMPTY,
                    Volunteer.class,
                    Map.of("id", id, "newName", name));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo(name);
            Assertions.assertThat(volunteerRepo.findById(id).get().getName()).isEqualTo(name);
        }
    }


    /**
     * Проверка правильности работы метода при
     * невалидном id
     */
    @Test
    public void updateName_negativeTestByNotValidId() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            int id = getNotValidId(volunteersFromDB);
            String name = faker.name().firstName();
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}/name?name={newName}",
                    HttpMethod.PUT,
                    HttpEntity.EMPTY,
                    String.class,
                    Map.of("id", id, "newName", name));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + id + " не найден");
        }
    }


    /**
     * Проверка правильности работы метода при
     * невалидном name
     */
    @Test
    public void updateName_negativeTestByNotValidName() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();

        Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
        int id = volunteer.getId();
        String name = "";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/{id}/name?name={newName}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class,
                Map.of("id", id, "newName", name));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");

        name = "   ";
        responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/{id}/name?name={newName}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class,
                Map.of("id", id, "newName", name));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");
    }

    /**
     * Проверка работы метода при получении
     * валидного значения статуса
     */
    @Test
    public void putStatus_positiveTest() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
            int id = volunteer.getId();
            Boolean status = true;
            ResponseEntity<Volunteer> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}/{status}",
                    HttpMethod.PUT,
                    HttpEntity.EMPTY,
                    Volunteer.class,
                    Map.of("id", id, "status", status));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody().isActive()).isEqualTo(status);
            Assertions.assertThat(volunteerRepo.findById(id).get().isActive()).isEqualTo(status);
        }
    }


    /**
     * Проверка правильности работы метода при
     * невалидном id
     */
    @Test
    public void putStatus_negativeTestByNotValidId() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            int id = getNotValidId(volunteersFromDB);
            Boolean status = false;
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}/{status}",
                    HttpMethod.PUT,
                    HttpEntity.EMPTY,
                    String.class,
                    Map.of("id", id, "status", status));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + id + " не найден");

        }
    }


    /**
     * Проверка метода при получении валидных параметров
     * идентификатора и телефонного номера
     */
    @Test
    public void putPhone_positiveTest() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
            int id = volunteer.getId();
            String newPhone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            ResponseEntity<Volunteer> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}/phone?phone={phone}",
                    HttpMethod.PUT,
                    HttpEntity.EMPTY,
                    Volunteer.class,
                    Map.of("id", id, "phone", newPhone));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody().getPhoneNumber()).isEqualTo(newPhone);
            Assertions.assertThat(volunteerRepo.findById(id).get().getPhoneNumber()).isEqualTo(newPhone);
        }
    }


    /**
     * Проверка правильности работы метода при
     * невалидном id
     */
    @Test
    public void putPhone_negativeTestByNotValidId() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            int id = getNotValidId(volunteersFromDB);
            String newPhone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/updatePhone?id={id}&phone={phone}",
                    HttpMethod.PUT,
                    HttpEntity.EMPTY,
                    String.class,
                    Map.of("id", id, "phone", newPhone));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + id + " не найден");
        }
    }

    /**
     * Проверка работы метода при получении
     * невалидного значения phoneNumber
     */
    @Test
    public void putPhone_negativeTestByNotValidPhone() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();

        Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
        int id = volunteer.getId();
        String newPhone = faker.phoneNumber().cellPhone();
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/updatePhone?id={id}&phone={newPhone}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class,
                Map.of("id", id, "newPhone", newPhone));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(
                "Телефон " + newPhone + " не соответствует формату: +7-654-654-6565 или +1 546 879 2121 или +8/214/541/5475");

        newPhone = "   ";
        responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/updatePhone?id={id}&phone={newPhone}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class,
                Map.of("id", id, "newPhone", newPhone));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");


        newPhone = "";
        responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/updatePhone?id={id}&phone={newPhone}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class,
                Map.of("id", id, "newPhone", newPhone));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");
    }

    /**
     * Проверка работы метода при валидных полях
     * передаваемого в метод объекта
     */
    @Test
    public void put_positiveTest() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
            int id = volunteer.getId();
            String newPhone = faker.phoneNumber().phoneNumberInternational().substring(0, 15);
            String newName = faker.name().name();
            volunteer.setPhoneNumber(newPhone);
            volunteer.setName(newName);
            ResponseEntity<Volunteer> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(volunteer),
                    Volunteer.class,
                    Map.of("id", id));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();

            Assertions.assertThat(volunteerRepo.findById(id).get())
                    .usingRecursiveComparison()
                    .isEqualTo(responseEntity.getBody());
        }
    }


    /**
     * Проверка работы метода при невалидном id
     */
    @Test
    public void put_negativeTestByNotValidId() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
            int id = getNotValidId(volunteersFromDB);
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(volunteer),
                    String.class,
                    Map.of("id", id));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + id + " не найден");
        }
    }


    /**
     * Проверка метода при получении валидного id
     */
    @Test
    public void get_positiveTest() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
            int id = volunteer.getId();

            ResponseEntity<Volunteer> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    Volunteer.class,
                    Map.of("id", id));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody())
                    .usingRecursiveComparison()
                    .isEqualTo(volunteer);
            Assertions.assertThat(volunteerRepo.findById(id).get())
                    .usingRecursiveComparison()
                    .isEqualTo(responseEntity.getBody());
        }
    }


    /**
     * Проверка метода при получении невалидного
     * идентификатора волонтёра
     */
    @Test
    public void get_negativeTestByNotValidId() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            int id = getNotValidId(volunteersFromDB);
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    String.class,
                    Map.of("id", id));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + id + " не найден");
        }
    }


    /**
     * Метод для получения невалидного идентификатора волонтёра
     *
     * @param volunteers - список волонтёров
     * @return невалидный id волонтера
     */
    private int getNotValidId(List<Volunteer> volunteers) {
        int id = faker.random().nextInt(1, 1000000000);
        while (true) {
            int finalId = id;
            if (volunteers.stream()
                    .map(Volunteer::getId)
                    .filter(t -> t == finalId)
                    .findFirst()
                    .isEmpty()) {
                break;
            }
            id = faker.random().nextInt(1, 1000000000);
        }
        return id;
    }


    /**
     * Проверка метода при получении валидного
     * идентификатора волонтёра
     */
    @Test
    public void delete_positiveTest() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();

        Volunteer volunteer = volunteersFromDB.get(faker.random().nextInt(1, volunteersFromDB.size() - 1));
        int id = volunteer.getId();

        ResponseEntity<Volunteer> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Volunteer.class,
                Map.of("id", id));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .isEqualTo(volunteer);
        Assertions.assertThat(volunteerRepo.findById(id).isEmpty()).isEqualTo(true);

    }

    /**
     * Проверка метода при получении невалидного
     * идентификатора волонтёра
     */
    @Test
    public void delete_negativeTestByNotValidId() {
        List<Volunteer> volunteersFromDB = volunteerRepo.findAll();
        for (int i = 0; i < NUMBER_OF_VOLUNTEERS; i++) {
            int id = getNotValidId(volunteersFromDB);
            ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                    "http://localhost:" + port + "/volunteer/{id}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    String.class,
                    Map.of("id", id));

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isNotNull();
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + id + " не найден");
        }
    }


    /**
     * Проверка метода получения всего списка
     * волонтёров
     */
    @Test
    public void getAll_Test() {

        ResponseEntity<List<Volunteer>> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/volunteer",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                }
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(volunteerRepo.findAll());

    }

}
