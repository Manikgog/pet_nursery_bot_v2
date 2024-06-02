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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.repository.ShelterRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShelterControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ShelterRepo shelterRepo;

    private final Faker faker = new Faker();

    private final List<Nursery> nurseryList = new ArrayList<>(10);

    @AfterEach
    public void afterEach() {
    nurseryList.clear();
    shelterRepo.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        for (int i = 0; i < 9; i++) {
            Nursery nursery = new Nursery();
            nursery.setNameShelter(faker.harryPotter().house());
            nursery.setAddress(faker.harryPotter().location());
            nursery.setForDog(faker.random().nextBoolean());
            nursery.setPhoneNumber(String.valueOf(faker.random().nextInt(123456789)));
            shelterRepo.save(nursery);
            nurseryList.add(nursery);
        }
    }

    private String builderUrl(String url) {
        return "http://localhost:%d%s".formatted(port, url);
    }

    @Test
    void putShelterPositiveTest() {
        Nursery nursery = new Nursery();
        nursery.setNameShelter(faker.harryPotter().house());
        nursery.setAddress(faker.harryPotter().location());
        nursery.setForDog(faker.random().nextBoolean());
        nursery.setPhoneNumber(String.valueOf(faker.random().nextInt(123456789)));
        ResponseEntity<Nursery> responseEntity = testRestTemplate.postForEntity(builderUrl("/shelter"),
                nursery,
                Nursery.class
        );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Optional<Nursery> nurseryFromDB = shelterRepo.findById(responseEntity.getBody().getId());
        Assertions.assertThat(nurseryFromDB).isPresent();
        Assertions.assertThat(nurseryFromDB.get())
                .usingRecursiveComparison()
                .isEqualTo(responseEntity.getBody());
    }

//    new Nursery(3L, "Содружество", "г. Томск ул. Кирпчная 1", "8-987-765-43-21", true),
    @Test
    void putShelterNegativeTest() {
        Nursery nursery = new Nursery(null, null, null,
                String.valueOf(faker.random().nextInt(123123123)),faker.random().nextBoolean(), null);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(builderUrl("/shelter"),
                nursery,
                String.class);

        String created = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(created).isEqualTo("Необходимо передать адрес приюта и/или контактные данные");
    }


    @Test
    void getShelterByIdPositiveTest() {
        Nursery nursery = nurseryList.get(3);

        ResponseEntity<Nursery> getNurseryFromDb = testRestTemplate.getForEntity(builderUrl("/shelter/"+nursery.getId()),
                Nursery.class);

        Nursery get = getNurseryFromDb.getBody();

        assertThat(getNurseryFromDb.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get).isNotNull();
        assertThat(get).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(nursery);
    }

    @Test
    void getShelterByIdNegativeTest() {
        int invalidId = -1;
        ResponseEntity<String> getUserFromDb = testRestTemplate.getForEntity(builderUrl("/shelter/"+invalidId),
                String.class);
        String created = getUserFromDb.getBody();
        assertThat(getUserFromDb.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(created).isEqualTo("Приют с id = " + invalidId + " не найден");
    }

    @Test
    void updateShelterPositiveTest() {
        Nursery oldNursery = nurseryList.get(faker.random().nextInt(nurseryList.size()));
        Long id = oldNursery.getId();
        Nursery newNursery = new Nursery();
        newNursery.setId(id);
        newNursery.setNameShelter("newName");
        newNursery.setPhoneNumber(oldNursery.getPhoneNumber()+5);
        HttpEntity<Nursery> entity = new HttpEntity<>(newNursery);
        ResponseEntity<Nursery> updateNursery = testRestTemplate.exchange(builderUrl("/shelter/"+id),
                HttpMethod.PUT,
                entity,
                Nursery.class);
        assertThat(updateNursery.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateNursery.getBody()).isNotNull();
        assertThat(updateNursery.getBody().getId()).isNotNull();
        assertThat(updateNursery.getBody())
                .isEqualTo(newNursery);

    }

    @Test
    void updateShelterNegativeTest() {
        Nursery newNursery = new Nursery(11L, "Какой то приют", "г. Н...2", "Номер телефона прежний2", true, null);
        HttpEntity<Nursery> entity = new HttpEntity<>(newNursery);
        ResponseEntity<String> updateNursery = testRestTemplate.exchange(builderUrl("/shelter/-1"),
                HttpMethod.PUT,
                entity,
                String.class);
        assertThat(updateNursery.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateNursery.getBody()).isNotNull();
        assertThat(updateNursery.getBody()).isEqualTo("Приют с id = " + -1 + " не найден");
    }

    @Test
    void deleteShelterPositiveTest() {
        Nursery nursery = nurseryList.get(faker.random().nextInt(nurseryList.size()));
        ResponseEntity<Nursery> deleteFaculty = testRestTemplate.exchange(builderUrl("/shelter/"+nursery.getId()),
                HttpMethod.DELETE,
                null,
                Nursery.class);
        assertThat(deleteFaculty.getBody()).isNotNull();
        assertThat(deleteFaculty.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getNurseryFromDb = testRestTemplate.getForEntity(builderUrl("/shelter/"+nursery.getId()),
                String.class);
        String created = getNurseryFromDb.getBody();
        assertThat(getNurseryFromDb.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(created).isEqualTo("Приют с id = " + nursery.getId() + " не найден");
    }

    @Test
    void deleteShelterNegativeTest() {
        Nursery nursery = new Nursery();
        nursery.setId(faker.random().nextLong());
        ResponseEntity<String> deleteShelter = testRestTemplate.exchange(builderUrl("/shelter/"+nursery.getId()),
                HttpMethod.DELETE,
                null,
                String.class);
        String created = deleteShelter.getBody();
        assertThat(deleteShelter.getBody()).isNotNull();
        assertThat(deleteShelter.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(created).isEqualTo("Приют с nurseryId = " + nursery.getId() + " не найден");
    }

    @Test
    void getNurseryPositiveTest() throws InterruptedException {
        Random rnd = new Random();
        List<Nursery> Nurseries = shelterRepo.findAll()
                .stream()
                .toList();
        int animalRepoSize = Nurseries.size();
        int page = rnd.nextInt(animalRepoSize/2) + 1;
        int size = rnd.nextInt(animalRepoSize/4) + 1;
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Nursery> sheltersByPage = shelterRepo.findAll(pageRequest)
                .stream()
                .toList();
        Thread.sleep(500);
        ResponseEntity<List<Nursery>> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/shelter?page=" + page + "&size=" + size,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<Nursery>>() {
                        }
                );
        Thread.sleep(500);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody().size()).isEqualTo(sheltersByPage.size());
    }

    @Test
    void getShelterForKindOfAnimals() throws InterruptedException {
        Random rnd = new Random();
        boolean kindOfAnimal = rnd.nextBoolean();
        List<Nursery> nurseries = shelterRepo.findAll()
                .stream()
                .filter(pet -> pet.isForDog() == kindOfAnimal)
                .toList();
        int nurseryRepoSize = nurseries.size();
        int page = rnd.nextInt(nurseryRepoSize/2) + 1;
        int size = rnd.nextInt(nurseryRepoSize/2) + 1;
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Nursery> nurseriesKindOfAnimal = shelterRepo.findAll(pageRequest)
                .stream()
                .filter(pet -> pet.isForDog() == kindOfAnimal)
                .toList();
        Thread.sleep(500);
        ResponseEntity<List<Nursery>> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/shelter?kindOfAnimal="+kindOfAnimal+"&page=" + page + "&size=" + size,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        }
                );
        Thread.sleep(500);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody().size()).isEqualTo(nurseriesKindOfAnimal.size());
    }


    @Test
    public void updateShelterMap_Test(){
        List<Nursery> nurseries = shelterRepo.findAll();
        long id = nurseries.get(faker.random().nextInt(0, nurseries.size())).getId();

        String mapLink = faker.examplify("https://yandex.ru/maps/org/inucobo/117827125363/?ll=71.441434%2C51.166809");

        ResponseEntity<Nursery> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/shelter/" + id + "?link=" + mapLink,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        Nursery.class
                );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody().getMapLink()).isEqualTo(mapLink);

        Nursery nurseryFromDB = shelterRepo.findById(responseEntity.getBody().getId()).get();

        Assertions.assertThat(nurseryFromDB.getMapLink()).isEqualTo(mapLink);
    }


    @Test
    public void updateShelterMap_negativeTestByEmptyLink(){
        List<Nursery> nurseries = shelterRepo.findAll();
        long id = nurseries.get(faker.random().nextInt(0, nurseries.size())).getId();

        String mapLink = "";

        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/shelter/" + id + "?link=" + mapLink,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        String.class
                );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка ссылки не должна быть пустой");

        mapLink = "    ";

        responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/shelter/" + id + "?link=" + mapLink,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        String.class
                );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Строка ссылки не должна быть пустой");
    }


    @Test
    public void updateShelterMap_negativeTestByNotValidId(){
        List<Long> nurseriesIds = shelterRepo.findAll().stream().map(Nursery::getId).toList();
        long id = faker.random().nextLong();
        while(nurseriesIds.contains(id)){
            id = faker.random().nextLong();
        }

        String mapLink = faker.examplify("https://yandex.ru/maps/org/inucobo/117827125363/?ll=71.441434%2C51.166809");

        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange(
                        "http://localhost:" + port + "/shelter/" + id + "?link=" + mapLink,
                        HttpMethod.PUT,
                        HttpEntity.EMPTY,
                        String.class
                );

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Приют с id = " + id + " не найден");

    }


}