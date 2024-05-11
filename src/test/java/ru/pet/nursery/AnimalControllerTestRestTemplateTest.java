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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.pet.nursery.configuration.TelegramBotConfiguration;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.dto.AnimalDTOForUser;
import ru.pet.nursery.web.service.AnimalService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.pet.nursery.Constants.*;

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
    private final static int NUMBER_OF_USERS = 5;
    private final static int NUMBER_OF_NURSERIES = 2;
    private final static int NUMBER_OF_ANIMALS = 10;
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
        Random rnd = new Random();
        boolean isCat = rnd.nextBoolean();
        animal.setAnimalType(isCat ? AnimalType.CAT : AnimalType.DOG);
        boolean isMale = rnd.nextBoolean();
        animal.setGender(isMale ? Gender.MALE : Gender.FEMALE);
        animal.setNursery(isCat ? NURSERY_1 : NURSERY_2);
        animal.setBirthDate(faker.date().birthdayLocalDate(MIN_AGE, MAX_AGE));
        animal.setPhotoPath(null);
        animal.setUser(USER);
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
        strPath += "\\" + animalImagesDir + "\\1.jpg";
        Path filePath = Path.of(strPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(strPath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/1/photo",
                requestEntity,
                String.class
        );
        List<Animal> animalsFromDB = animalRepo.findAll();

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
    @Test
    public void getAnimalPhoto_negativeTest(){
        // получение идентификатора, которого нет в таблице animal_table
        int wrongAnimalId = getWrongAnimalId();

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/animal/{wrongAnimalId}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class,
                Map.of("wrongAnimalId", wrongAnimalId));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + wrongAnimalId + " не найден");

    }

    /**
     * Метод для получения невалидного идентификатора животного
     * @return
     */
    private int getWrongAnimalId(){
        List<Animal> animalsFromDB = animalRepo.findAll();
        Random rnd = new Random();
        int size = animalsFromDB.size();
        List<Integer> animalsIds = animalsFromDB.stream().map(a -> a.getId()).toList();
        int wrongAnimalId;
        while (true) {
            wrongAnimalId = rnd.nextInt(animalsIds.size() + 10);
            if (!animalsIds.contains(wrongAnimalId))
                break;
        }
        return wrongAnimalId;
    }

    @Test
    public void getAnimalPhoto_positiveTest(){
        // получение идентификатора из таблицы animal_table
        int correctAnimalId = getCorrectAnimalId();
        // сначала надо загрузить фотографию для найденного идентификатора животного
        uploadAnimalPhoto(correctAnimalId);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/animal/{correctAnimalId}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class,
                Map.of("correctAnimalId", correctAnimalId)
        );

        animalRepo.findById(correctAnimalId).stream().forEach(a -> System.out.println(a.getPhotoPath()));

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);


    }

    private int getCorrectAnimalId(){
        List<Animal> animalsFromDB = animalRepo.findAll();
        Random rnd = new Random();
        return animalsFromDB.get(rnd.nextInt(animalsFromDB.size())).getId();
    }

    private void uploadAnimalPhoto(int animalId){
        String strPath = System.getProperty("user.dir");
        strPath += "\\" + animalImagesDir + "\\1.jpg";
        Path filePath = Path.of(strPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/" + animalId + "/photo",
                requestEntity,
                String.class
        );
    }

    /**
     * Метод для проверки правильности работы метода deleteAnimal
     * при получении правильного запроса
     */
    @Test
    public void deleteAnimal_positiveTest(){
        for (int i = 0; i < 5; i++) {
            // получение валидного идентификатора животного
            int animalId = getCorrectAnimalId();
            Animal animalToDelete = animalRepo.findById(animalId).get();

            ResponseEntity<Animal> responseEntity = testRestTemplate
                    .exchange(
                            "http://localhost:" + port + "/animal/{animalId}/delete",
                            HttpMethod.DELETE,
                            HttpEntity.EMPTY,
                            Animal.class,
                            Map.of("animalId", animalId)
                    );

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isEqualTo(animalToDelete);
            Assertions.assertThat(Optional.empty()).isEqualTo(animalRepo.findById(animalId));
        }
    }

    /**
     * Метод для проверки метода deleteAnimal
     * при невалидном запросе
     */
    @Test
    public void deleteAnimal_negativeTest(){
        for (int i = 0; i < NUMBER_OF_ANIMALS * NUMBER_OF_NURSERIES/4; i++) {
            // получение невалидного идентификатора животного
            int wrongAnimalId = getWrongAnimalId();
            ResponseEntity<String> responseEntity = testRestTemplate
                    .exchange(
                            "http://localhost:" + port + "/animal/{animalId}/delete",
                            HttpMethod.DELETE,
                            HttpEntity.EMPTY,
                            String.class,
                            Map.of("animalId", wrongAnimalId)
                    );
            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void getListByPage_positiveTest(){
        Random rnd = new Random();
        int animalRepoSize = animalRepo.findAll().size();
        int page = 2;   //rnd.nextInt(animalRepoSize/2) + 1;
        int size = 2; //rnd.nextInt(animalRepoSize/4) + 1;
        int offset = (page - 1) * size;
        int upLevel = offset + size;
        int resultListSize = upLevel - offset;
        if(offset > animalRepoSize){
            resultListSize = 0;
        }

        ResponseEntity<List<AnimalDTOForUser>> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/animal?page=" + page + "&size=" + size,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<AnimalDTOForUser>>() {
                        }
                );

        /*ResponseEntity<List<Animal>> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/animal/all",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<Animal>>() {
                        }
                );*/

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(resultListSize);
    }

    /**
     * Метод для проверки метода getById
     * при передаче валидного идентификатора
     */
    @Test
    public void getById_positiveTest(){
        for (int i = 0; i < NUMBER_OF_ANIMALS/4; i++) {
            // получение валидного идентификатора питомца
            int correctAnimalId = getCorrectAnimalId();
            Animal animal = animalRepo.findById(correctAnimalId).get();
            AnimalDTOForUser animalDTOForUser = new AnimalDTOForUser();
            animalDTOForUser.setId(animal.getId());
            animalDTOForUser.setAnimalName(animal.getAnimalName());
            animalDTOForUser.setAnimalType(animal.getAnimalType());
            animalDTOForUser.setGender(animal.getGender());
            animalDTOForUser.setNursery(nurseryRepo.findById(animal.getNurseryId()).get());
            animalDTOForUser.setBirthDate(animal.getBirthDate());
            animalDTOForUser.setDescription(animal.getDescription());
            ResponseEntity<AnimalDTOForUser> responseEntity =
                    testRestTemplate
                            .getForEntity(
                                    "http://localhost:" + port + "/animal/" + correctAnimalId,
                                    AnimalDTOForUser.class
                            );
            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody())
                    .isEqualTo(animalDTOForUser);
        }
    }

    /**
     * Метод для проверки метода getById
     * при получении невалидного идентификатора
     */
    @Test
    public void getById_negativeTest(){
        for (int i = 0; i < NUMBER_OF_ANIMALS/4;  i++) {
            // получение невалидного идентификатора
            int wrongAnimalId = getWrongAnimalId();
            ResponseEntity<String> responseEntity =
                    testRestTemplate
                            .getForEntity(
                                    "http://localhost:" + port + "/animal/" + wrongAnimalId,
                                    String.class
                            );
            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(responseEntity.getBody()).isEqualTo("Ресурс с id = " + wrongAnimalId + " не найден");
        }
    }

}
