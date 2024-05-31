package ru.pet.nursery.web.controller;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.ShelterRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;
import ru.pet.nursery.web.validator.VolunteerValidator;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportControllerTestRestTemplateTest {
    @Value("${path.to.animals.folder}")
    private String ANIMAL_IMAGES;
    @Value("${path.to.report_photo.folder}")
    private String REPORT_PHOTO;
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
    ShelterRepo shelterRepo;
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

        shelterRepo.saveAll(nurseries);
        List<Nursery> nurseriesFromDB = shelterRepo.findAll();
        for(Nursery n : nurseriesFromDB){
            boolean isAdopted = false;
            for(int j = 0; j < NUMBER_OF_ANIMALS; j++){
                if(j%2 == 0){
                    isAdopted = true;
                }
                animalRepo.save(createAnimal(n, isAdopted));
                isAdopted = false;
            }
        }
    }

    @AfterEach
    public void afterEach(){
        animalRepo.deleteAll();
        reportRepo.deleteAll();
        userRepo.deleteAll();
        shelterRepo.deleteAll();
    }

    private Animal createAnimal(Nursery nursery, boolean isAdopted){
        Animal animal = new Animal();
        animal.setAnimalName(faker.name().name());
        Random rnd = new Random();
        animal.setAnimalType(nursery.isForDog() ? AnimalType.DOG : AnimalType.CAT);
        boolean isMale = rnd.nextBoolean();
        animal.setGender(isMale ? Gender.MALE : Gender.FEMALE);
        animal.setNursery(nursery);
        animal.setBirthDate(faker.date().birthdayLocalDate(MIN_AGE, MAX_AGE));
        animal.setPhotoPath(null);
        if(isAdopted){
            User whoNotAdopt = findUserWhoNotAdopt();
            animal.setUser(whoNotAdopt);
            animal.setTookDate(faker.date().birthdayLocalDate(1, 10));
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
    private List<User> findUsersWhoAdopt(){
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
        List<User> adopters = findUsersWhoAdopt();
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

    /**
     * Проверка работы метода при передаче идентификатора пользователя,
     * которого нет в базе данных
     */
    @Test
    public void upload_negativeTestByNotValidId(){
        List<User> adopters = findUsersWhoAdopt();
        // находим невалидный adopterId
        List<Long> ids = adopters
                .stream()
                .map(User::getTelegramUserId)
                .toList();
        long adopterId = 1;
        while (ids.contains(adopterId)){
            adopterId = faker.random().nextInt(0, 100);
        }
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/report/" + adopterId,
                null,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Идентификатор пользователя " + adopterId + " отсутствует в базе данных");
    }


    @Test
    public void upload_negativeTestByUserNotAdopter(){
        User user = findUserWhoNotAdopt();
        User newUser = new User();
        long notAdopterId;
        List<Long> usersId = userRepo.findAll()
                .stream()
                .map(User::getTelegramUserId)
                .toList();
        long newId = 1;
        while(usersId.contains(newId)){
            newId = faker.random().nextInt(0, 100);
        }
        if(user == null){
            newUser.setUserName(faker.name().firstName());
            newUser.setLastName(faker.name().lastName());
            newUser.setFirstName(faker.name().firstName());
            newUser.setTelegramUserId(newId);
            newUser.setAddress(faker.address().fullAddress());
            notAdopterId = newUser.getTelegramUserId();
            userRepo.save(newUser);
        }else {
            notAdopterId = user.getTelegramUserId();
        }
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/report/" + notAdopterId,
                null,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Пользователь с id = " + notAdopterId + " не усыновлял питомца");
    }



    @Test
    public void delete_positiveTest(){
        List<User> adopters = findUsersWhoAdopt();
        User adopter = adopters.get(faker.random().nextInt(0, adopters.size() - 1));
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(adopter);
        Report reportFromDB = reportRepo.save(newReport);
        long reportId = reportFromDB.getId();
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }



    @Test
    public void delete_negativeTestByNotValidReportId(){
        long reportId = 0;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }



    @Test
    public void putPhoto_positiveTest(){
        // создаётся отчёт в базе данных
        List<User> adopters = findUsersWhoAdopt();
        User adopter = adopters.get(faker.random().nextInt(0, adopters.size() - 1));
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(adopter);
        Report report = reportRepo.save(newReport);
        // создаётся сущность запроса с фотографией
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getRequestEntity();

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + report.getId() + "/photo",
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        String photoPath = reportRepo.findById(report.getId()).get().getPathToPhoto();
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\" + REPORT_PHOTO + "\\";
        }else{
            strPath += "/" + REPORT_PHOTO + "/";
        }
        Assertions.assertThat(photoPath).isEqualTo(strPath + report.getId() + ".jpg");
    }


    public HttpEntity<MultiValueMap<String, Object>> getRequestEntity(){
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\" + ANIMAL_IMAGES + "\\1.jpg";
        }else{
            strPath += "/" + ANIMAL_IMAGES + "/1.jpg";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(strPath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("animalPhoto", fileSystemResource);
        return new HttpEntity<>(body, headers);
    }


    @Test
    public void getAnimalPhoto_negativeTest(){
        // получение идентификатора, которого нет в таблице animal_table
        List<Long> reportIds = reportRepo.findAll().stream().map(Report::getId).toList();
        long wrongReportId = 1;
        while(reportIds.contains(wrongReportId)){
            wrongReportId = faker.random().nextInt(1, 100);
        }
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/{wrongReportId}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class,
                Map.of("wrongReportId", wrongReportId));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + wrongReportId + " не найден");

    }


    @Test
    public void getAnimalPhoto_positiveTest() throws IOException {
        // получение пользователя, который усыновил питомца
        User user = findUsersWhoAdopt().stream().findFirst().get();
        // создание отчёта
        Report newReport = new Report();
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        long correctReportId = reportFromDB.getId();
        // сначала надо загрузить фотографию для найденного идентификатора животного
        updatePhoto(correctReportId);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/{correctReportId}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class,
                Map.of("correctReportId", correctReportId)
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    public void updatePhoto(long id) {
        Report reportFromDB = reportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\" + ANIMAL_IMAGES + "\\1.jpg";
        }else{
            strPath += "/" + ANIMAL_IMAGES + "/1.jpg";
        }

        Path filePath = Path.of(strPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("animalPhoto", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/{id}/photo",
                HttpMethod.PUT,
                requestEntity,
                Report.class,
                Map.of("id", id)
        );
    }

    @Test
    public void putPhoto_negativeTestByNotValidAdopterId(){
        Report newReport = new Report();
        newReport.setId(0);
        // создаётся сущность запроса с фотографией
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getRequestEntity();

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + newReport.getId() + "/photo",
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + newReport.getId() + " не найден");
    }


    @Test
    public void putDiet_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        String diet = "Диета";
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/diet?diet=" + diet,
                HttpMethod.PUT,
                new HttpEntity<>(diet),
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }


    @Test
    public void putDiet_negativeTestByNotValidReportId(){
        Report newReport = new Report();
        newReport.setId(0);
        String diet = "Диета";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + newReport.getId() + "/diet?diet=" + diet,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + newReport.getId() + " не найден");
    }


    @Test
    public void putDiet_negativeTestByEmptyDietString(){
        User user = findUserWhoNotAdopt();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        String diet = "";

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/diet?diet=" + diet,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");
    }


    @Test
    public void putHealth_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        String health = "Здоровье";
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/health?health=" + health,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }

    @Test
    public void putHealth_negativeTestByNotValidReportId(){
        long reportId = 0;
        String health = "Диета";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/health?health=" + health,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }

    @Test
    public void putHealth_negativeTestByEmptyHealthString() {
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        String health = "";

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/health?health=" + health,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");
    }


    @Test
    public void putBehaviour_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        String behaviour = "Здоровье";
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/behaviour?behaviour=" + behaviour,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }

    @Test
    public void putBehaviour_negativeTestByNotValidReportId(){
        long reportId = 0;
        String behaviour = "Поведение";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/behaviour?behaviour=" + behaviour,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }

    @Test
    public void putBehaviour_negativeTestByEmptyBehaviourString() {
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        String behaviour = "";

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/behaviour?behaviour=" + behaviour,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка не должна быть пустой");
    }


    @Test
    public void putIsAllItemsAccepted_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        boolean acceptAll = true;
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/acceptAll?acceptAll=" + acceptAll,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }

    @Test
    public void putIsAllItemsAccepted_negativeTestByNotValidReportId(){
        long reportId = 0;
        boolean acceptAll = true;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/acceptAll?acceptAll=" + acceptAll,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }

    @Test
    public void putIsFotoAccepted_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        boolean acceptPhoto = true;
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/acceptPhoto?acceptPhoto=" + acceptPhoto,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }

    @Test
    public void putIsFotoAccepted_negativeTestByNotValidReportId(){
        long reportId = 0;
        boolean acceptPhoto = true;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/acceptPhoto?acceptPhoto=" + acceptPhoto,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }
    @Test
    public void putIsDietAccepted_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        boolean acceptDiet = true;
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/acceptDiet?acceptDiet=" + acceptDiet,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }


    @Test
    public void putIsDietAccepted_negativeTestByNotValidReportId(){
        long reportId = 0;
        boolean acceptDiet = true;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/acceptDiet?acceptDiet=" + acceptDiet,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }

    @Test
    public void putIsHealthAccepted_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        boolean acceptHealth = true;
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/acceptHealth?acceptHealth=" + acceptHealth,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }


    @Test
    public void putIsHealthAccepted_negativeTestByNotValidReportId(){
        long reportId = 0;
        boolean acceptHealth = true;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/acceptHealth?acceptHealth=" + acceptHealth,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }

    @Test
    public void putIsBehaviourAccepted_positiveTest(){
        User user = findUsersWhoAdopt().stream().findFirst().get();
        Report newReport = new Report();
        newReport.setId(0);
        newReport.setReportDate(LocalDate.now().atStartOfDay());
        newReport.setUser(user);
        Report reportFromDB = reportRepo.save(newReport);
        boolean acceptBehaviour = true;
        ResponseEntity<Report> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportFromDB.getId() + "/acceptBehaviour?acceptBehaviour=" + acceptBehaviour,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Report.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        reportFromDB = reportRepo.findByUserAndReportDate(user, LocalDate.now().atStartOfDay());
        Assertions.assertThat(responseEntity.getBody()).usingRecursiveComparison().isEqualTo(reportFromDB);
    }

    @Test
    public void putIsBehaviourAccepted_negativeTestByNotValidReportId(){
        long reportId = 0;
        boolean acceptBehaviour = true;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + reportId + "/acceptBehaviour?acceptBehaviour=" + acceptBehaviour,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + reportId + " не найден");
    }


    @Test
    public void getListOfReportByDate_positiveTest(){
        List<User> adopters = findUsersWhoAdopt();
        List<Report> reports = new ArrayList<>();
        for(User user : adopters){
            Report newReport = new Report();
            newReport.setId(0);
            newReport.setReportDate(LocalDate.now().atStartOfDay());
            newReport.setUser(user);
            reports.add(reportRepo.save(newReport));
        }

        ResponseEntity<List<Report>> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/report/date?date=" + LocalDate.now(),
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
                .isEqualTo(reports);
    }

}
