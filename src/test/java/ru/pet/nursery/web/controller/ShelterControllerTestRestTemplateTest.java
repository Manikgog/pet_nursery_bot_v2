package ru.pet.nursery.web.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.repository.ShelterRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
                Nursery.class);
        Nursery created = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(responseEntity.getBody()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(nursery);
        assertThat(created.getId()).isNotNull();

        Optional<Nursery> fromDb = shelterRepo.findById(created.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .isEqualTo(created);
    }

//    new Nursery(3L, "Содружество", "г. Томск ул. Кирпчная 1", "8-987-765-43-21", true),
    @Test
    void putShelterNegativeTest() {
        Nursery nursery = new Nursery(null, null, null,
                String.valueOf(faker.random().nextInt(123123123)),faker.random().nextBoolean());
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
        Nursery newNursery = new Nursery(null, faker.harryPotter().house(), faker.harryPotter().location(),
                String.valueOf(faker.random().nextInt(123123123)),faker.random().nextBoolean());
        HttpEntity<Nursery> entity = new HttpEntity<>(newNursery);
        ResponseEntity<Nursery> updateNursery = testRestTemplate.exchange(builderUrl("/shelter"+newNursery.getId()),
                HttpMethod.PUT,
                entity,
                Nursery.class);
        assertThat(updateNursery.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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


        ResponseEntity<Nursery> getNurseryFromDb = testRestTemplate.getForEntity(builderUrl("/shelter"+nursery.getId()),
                Nursery.class);

        assertThat(getNurseryFromDb.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getNursery() {

    }

    @Test
    void getShelterForKindOfAnimals() {
    }
}