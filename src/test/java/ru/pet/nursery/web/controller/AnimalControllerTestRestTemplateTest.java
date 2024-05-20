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
import org.springframework.data.domain.PageRequest;
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
        User userNoone = new User();
        userNoone.setTelegramUserId(1L);
        userNoone.setUserName("нет");
        userNoone.setAddress("нет");
        userNoone.setFirstName("нет");
        userNoone.setLastName("нет");
        userNoone.setPhoneNumber("нет");
        userNoone.setAddress("нет");
        userList.add(userNoone);
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
        for(Nursery n : nurseriesFromDB){
            for(int j = 0; j < NUMBER_OF_ANIMALS; j++){
                animalRepo.save(createAnimal(Math.toIntExact(n.getId())));
            }
        }
    }

    @AfterEach
    public void afterEach(){
        animalRepo.deleteAll();
        nurseryRepo.deleteAll();
        userRepo.deleteAll();
    }

    private Animal createAnimal(Long nurseryId){
        Animal animal = new Animal();
        animal.setAnimalName(faker.name().name());
        Random rnd = new Random();
        boolean isCat = rnd.nextBoolean();
        animal.setAnimalType(isCat ? AnimalType.CAT : AnimalType.DOG);
        boolean isMale = rnd.nextBoolean();
        animal.setGender(isMale ? Gender.MALE : Gender.FEMALE);
        animal.setNursery(nurseryRepo.findById(nurseryId).get());
        animal.setBirthDate(faker.date().birthdayLocalDate(MIN_AGE, MAX_AGE));
        animal.setPhotoPath(null);
        boolean isAdopted = rnd.nextInt(10) <= 2 ? true : false;
        if(isAdopted){
            User whoNotAdopt = findUserWhoNotAdopt();
            if(whoNotAdopt == null){
                animal.setUser(userRepo.findById(1L).get());
            }else {
                animal.setUser(whoNotAdopt);
            }
            animal.setTookDate(faker.date().past(faker.random().nextInt(5, 15), TimeUnit.DAYS).toLocalDateTime().toLocalDate());
        }else {
            animal.setUser(userRepo.findById(1L).get());
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
        // список тех кто не усыновлял животное
        List<User> userWhoAdopt = animalRepo.findAll()
                .stream()
                .filter(a -> a.getUser().getTelegramUserId() != 1L)
                .map(Animal::getUser)
                .toList();
        Optional<User> userWhoNotAdopt = userRepo.findAll()
                .stream()
                .filter(u -> u.getTelegramUserId() != 1L)
                .filter(u -> !userWhoAdopt.contains(u))
                .findFirst();
        if(userWhoNotAdopt.isEmpty()){
            return null;
        }
        return userWhoNotAdopt.get();
    }

    private Nursery createNursery(){
        Nursery nursery = new Nursery();
        nursery.setAddress(faker.address().fullAddress());
        nursery.setPhoneNumber(faker.phoneNumber().phoneNumberNational());
        return nursery;
    }

    private User createUser(int i){
        User user = new User();
        user.setTelegramUserId((long)i + 10);
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
            Random rnd = new Random();
            boolean isCat = rnd.nextBoolean();
            animalDTO.setAnimalType(isCat ? AnimalType.CAT : AnimalType.DOG);
            boolean isMale = rnd.nextBoolean();
            animalDTO.setGender(isMale ? Gender.MALE : Gender.FEMALE);
            animalDTO.setNurseryId(nurseryRepo.findById(1L).get().getId());
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
        List<Animal> animals = animalRepo.findAll();
        for (int i = 0; i < 5; i++) {
            Animal animal = animals.get(new Random().nextInt(animals.size()));
            String strPath = System.getProperty("user.dir");
            strPath += "\\" + animalImagesDir + "\\1.jpg";
            Path filePath = Path.of(strPath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            FileSystemResource fileSystemResource = new FileSystemResource(strPath);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("animalPhoto", fileSystemResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                    "http://localhost:" + port + "/animal/" + animal.getId() + "/photo",
                    requestEntity,
                    String.class
            );
            List<Animal> animalsFromDB = animalRepo.findAll();
            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    public void insertHumanWhoTookAnimal_positiveTest(){
        for (int i = 0; i < 5; i++) {
            List<Animal> animalsFromDB = animalRepo.findAll();
            Animal animal = animalsFromDB.get(new Random().nextInt(animalsFromDB.size()));
            while (true) {
                if (animal.getUser().getTelegramUserId() == 1)
                    break;
                animal = animalsFromDB.get(new Random().nextInt(animalsFromDB.size()));
            }
            User user = findUserWhoNotAdopt();
            if(user == null)    // если user = null, то свободных усыновителей не осталось
                break;
            ResponseEntity<HttpStatus> responseEntity = testRestTemplate.postForEntity(
                    "http://localhost:" + port + "/animal/" + animal.getId() + "/" + user.getTelegramUserId(),
                    null,
                    HttpStatus.class
            );

            Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(responseEntity.getBody()).isEqualTo(HttpStatus.OK);

            Animal animalAfterAdoption = animalRepo.findById(Math.toIntExact(animal.getId())).get();
            Assertions.assertThat(animalAfterAdoption.getUser().getTelegramUserId()).isEqualTo(user.getTelegramUserId());
            Assertions.assertThat(animalAfterAdoption.getTookDate()).isEqualTo(LocalDate.now());
        }
    }

    /**
     * Метод для проверки метода uploadPhotoAnimal загрузки фотографии с для невалидного
     * идентификатора животного
     */
    @Test
    public void uploadPhotoAnimal_negativeTest(){
        List<Animal> animalsFromDB = animalRepo.findAll();
        Random rnd = new Random();
        List<Long> animalsIds = animalsFromDB.stream().map(Animal::getId).toList();
        int wrongAnimalId;
        while (true) {
            wrongAnimalId = rnd.nextInt(animalsIds.size() + 10);
            if (!animalsIds.contains(wrongAnimalId))
                break;
        }

        String strPath = System.getProperty("user.dir");
        strPath += "\\" + animalImagesDir + "\\1.jpg";
        Path filePath = Path.of(strPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("animalPhoto", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/animal/" + wrongAnimalId + "/photo",
                requestEntity,
                String.class
        );
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
        List<Long> animalsIds = animalsFromDB.stream().map(a -> a.getId()).toList();
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
        Assertions.assertThat(animalAfterReturn.getUser().getTelegramUserId()).isEqualTo(1);
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
        List<Long> animalsIds = animalsFromDB.stream().map(a -> a.getId()).toList();
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
        Long correctAnimalId = getCorrectAnimalId();
        // сначала надо загрузить фотографию для найденного идентификатора животного
        uploadAnimalPhoto(Math.toIntExact(correctAnimalId));

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/animal/{correctAnimalId}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class,
                Map.of("correctAnimalId", correctAnimalId)
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private Long getCorrectAnimalId(){
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
        body.add("animalPhoto", fileSystemResource);
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
            Long animalId = getCorrectAnimalId();
            Animal animalToDelete = animalRepo.findById(Math.toIntExact(animalId)).get();

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
            Assertions.assertThat(Optional.empty()).isEqualTo(animalRepo.findById(Math.toIntExact(animalId)));
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

    /** Метод для тестирования метода getListByPage
     * для получения списка животных, которые находятся
     * в приютах постранично
     * @throws InterruptedException
     */
    @Test
    public void getListByPage_positiveTest() throws InterruptedException {
        Random rnd = new Random();
        List<Animal> animalsInNurseries = animalRepo.findAll()
                .stream()
                .filter(a -> a.getUser().getTelegramUserId() == 1L)
                .toList();
        int animalRepoSize = animalsInNurseries.size();
        int page = rnd.nextInt(animalRepoSize/2) + 1;
        int size = rnd.nextInt(animalRepoSize/4) + 1;
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Animal> animalsByPage = animalRepo.findAll(pageRequest)
                .stream()
                .filter(a -> a.getUser().getTelegramUserId() == 1L)
                .toList();
        Thread.sleep(500);
        ResponseEntity<List<AnimalDTOForUser>> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/animal?page=" + page + "&size=" + size,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<AnimalDTOForUser>>() {
                        }
                );
        Thread.sleep(500);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody().size()).isEqualTo(animalsByPage.size());
    }

    /**
     * Метод для проверки метода getById
     * при передаче валидного идентификатора
     */
    @Test
    public void getById_positiveTest(){
        for (int i = 0; i < NUMBER_OF_ANIMALS/4; i++) {
            // получение валидного идентификатора питомца
            Long correctAnimalId = getCorrectAnimalId();
            Animal animal = animalRepo.findById(Math.toIntExact(correctAnimalId)).get();
            AnimalDTOForUser animalDTOForUser = new AnimalDTOForUser();
            animalDTOForUser.setId(animal.getId());
            animalDTOForUser.setAnimalName(animal.getAnimalName());
            animalDTOForUser.setAnimalType(animal.getAnimalType());
            animalDTOForUser.setGender(animal.getGender());
            animalDTOForUser.setNursery(nurseryRepo.findById(animal.getNursery().getId()).get());
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
