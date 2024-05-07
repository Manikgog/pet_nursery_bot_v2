package ru.pet.nursery;

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
import ru.pet.nursery.configuration.TelegramBotConfiguration;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.service.AnimalService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimalControllerTestRestTemplateTest {
    @Value("${path.to.animals.folder}")
    private String animalImagesDir;

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

    @Test
    public void uploadPhotoAnimal_positiveTest(){
        String strPath = System.getProperty("user.dir");
        strPath += animalImagesDir + "/1.jpg";
        Path filePath = Path.of(strPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        /*RestTemplate testRestTemplate = new RestTemplate();*/
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/animal/1/photo",
                new HttpEntity<>(fileSystemResource.getFile(), headers), String.class);


        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        responseEntity.getBody();
    }

    @Test
    public void insertHumanWhoTookAnimal_positiveTest(){
        List<Animal> animalsFromDB = animalRepo.findAll();
        Animal animal = animalsFromDB.get(new Random().nextInt(animalsFromDB.size()));

        List<User> usersFromDB = userRepo.findAll();
        User user = usersFromDB.get(new Random().nextInt(usersFromDB.size()));

        ResponseEntity<HttpStatus> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/" + animal.getId() + "/" + user.getTelegramUserId(),
                null,
                HttpStatus.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(HttpStatus.OK);

        Animal animalAfterAdoption = animalRepo.findById(animal.getId()).get();
        Assertions.assertThat(animalAfterAdoption.getWhoTookPet()).isEqualTo(user.getTelegramUserId());
        Assertions.assertThat(animalAfterAdoption.getTookDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void uploadPhotoAnimal_negativeTest(){
        List<Animal> animalsFromDB = animalRepo.findAll();
        Random rnd = new Random();
        List<Integer> animalsIds = animalsFromDB.stream().map(Animal::getId).toList();
        int wrongAnimalId;
        while (true) {
            wrongAnimalId = rnd.nextInt(animalsIds.size() + 10);
            if (!animalsIds.contains(wrongAnimalId))
                break;
        }

        String strPath = System.getProperty("user.dir");
        strPath += animalImagesDir + "/1.jpg";
        Path filePath = Path.of(strPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("1.jpg", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/" + wrongAnimalId + "/photo",
                requestEntity,
                String.class
        );
        //Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + wrongAnimalId + " не найден");
    }

    /**
     * Метод для тестирования метода insertHumanWhoTookAnimal при вводе
     * невалидных идентификаторов животного или человека. Оба случая проверяются
     * по 5 раз
     */
    @Test
    public void insertHumanWhoTookAnimal_negativeTest(){
        List<Animal> animalsFromDB = animalRepo.findAll();
        Random rnd = new Random();
        int size = animalsFromDB.size();
        List<Integer> animalsIds = animalsFromDB.stream().map(a -> a.getId()).toList();
        int wrongAnimalId;
        for (int i = 0; i < 5; i++) {
            while (true) {
                wrongAnimalId = rnd.nextInt(animalsIds.size() + 10);
                if (!animalsIds.contains(wrongAnimalId))
                    break;
            }

            List<User> usersFromDB = userRepo.findAll();
            User user = usersFromDB.get(new Random().nextInt(usersFromDB.size()));

            ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                    "http://localhost:" + port + "/animal/" + wrongAnimalId + "/" + user.getTelegramUserId(),
                    null,
                    String.class
            );

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + wrongAnimalId + " не найден");
        }

        Animal animal = animalsFromDB.get(new Random().nextInt(animalsFromDB.size()));
        List<User> usersFromDB = userRepo.findAll();
        List<Long> usersIds = usersFromDB.stream().map(User::getTelegramUserId).toList();
        long wrongUserId = 0;
        for (int i = 0; i < 5; i++) {
            while (true) {
                wrongAnimalId = (int) rnd.nextLong(usersIds.get(0));
                if (!usersIds.contains(wrongUserId))
                    break;
            }
            ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                    "http://localhost:" + port + "/animal/" + animal.getId() + "/" + wrongUserId,
                    null,
                    String.class
            );

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + wrongUserId + " не найден");
        }
    }

    /**
     * Метод для тестирования метода insertDateOfReturningAnimal для внесения
     * изменений в базу данных при возвращении животного в питомник
     */
    @Test
    public void insertDateOfReturningAnimal_positiveTest(){
        // сначала производится усыновление с внесением изменений в базу данных
        List<Animal> animalsFromDB = animalRepo.findAll();
        Animal animal = animalsFromDB.get(new Random().nextInt(animalsFromDB.size()));

        List<User> usersFromDB = userRepo.findAll();
        User user = usersFromDB.get(new Random().nextInt(usersFromDB.size()));

        ResponseEntity<HttpStatus> prepareResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/" + animal.getId() + "/" + user.getTelegramUserId(),
                null,
                HttpStatus.class
        );
        Assertions.assertThat(prepareResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(prepareResponseEntity.getBody()).isEqualTo(HttpStatus.OK);

        // затем вносятся изменения о возвращении животного через тестируемый метод
        ResponseEntity<HttpStatus> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/" + animal.getId() + "/return",
                null,
                HttpStatus.class
        );
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(HttpStatus.OK);
        // проверяется содержание ячеек с информацией о возвращении животного
        Animal animalAfterReturn = animalRepo.findById(animal.getId()).get();
        // проверяется ячейка с датой возвращения.
        Assertions.assertThat(animalAfterReturn.getPetReturnDate()).isEqualTo(LocalDate.now());
        // проверяется ячейка с идентификатором человека, который забрал животное
        // если животное возвращено, то в этой ячейке ставиться единица (1)
        Assertions.assertThat(animalAfterReturn.getWhoTookPet()).isEqualTo(1);
        // проверяется ячейка с датой когда животное усыновили
        // если животное возвращено, то в этой ячейке проставляется null
        Assertions.assertThat(animalAfterReturn.getTookDate()).isNull();
    }



}
