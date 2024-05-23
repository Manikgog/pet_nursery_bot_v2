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
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;
import ru.pet.nursery.web.validator.VolunteerValidator;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportControllerTestRestTemplateTest {
    @Value("${path.to.animals.folder}")
    private String animalImagesDir;
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
        long notAdopterId = user.getTelegramUserId();

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
        newReport.setReportDate(LocalDate.now());
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
        newReport.setReportDate(LocalDate.now());
        newReport.setUser(adopter);
        // создаётся сущность запроса с фотографией
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\" + animalImagesDir + "\\1.jpg";
        }else{
            strPath += "/" + animalImagesDir + "/1.jpg";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(strPath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("animalPhoto", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/report/" + adopter.getTelegramUserId() + "/photo",
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


}
