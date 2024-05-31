package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.AnimalNotFoundException;
import ru.pet.nursery.web.exception.UserNotFoundException;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptedServiceTest {
    @Mock
    private AnimalRepo animalRepo;
    @Mock
    private UserService userService;
    @InjectMocks
    private AdoptedService adoptedService;

    private final Faker faker = new Faker();

    List<Animal> animalList = new ArrayList<>();
    List<User> userList = new ArrayList<>();

    @BeforeEach
    void BeforeEach() {
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setTelegramUserId(faker.random().nextLong());
            user.setUserName(faker.harryPotter().character());
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setAddress(faker.harryPotter().location());
            userList.add(user);
        }
        for (int i = 0; i < 5; i++) {
            Animal animal = new Animal();
            animal.setUser(null);
            animal.setAnimalName(faker.harryPotter().character());
            animalList.add(animal);
        }
    }

    @AfterEach
    void AfterEach() {
        animalList.clear();
        userList.clear();
    }

    @Test
    void setAdopterForAnimalPositiveTest() {
        User user = userList.get(faker.random().nextInt(userList.size()));
        Animal animal = new Animal();
        animal.setUser(user);
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        when(userService.getUserById(user.getTelegramUserId())).thenReturn(user);

        Animal actual = adoptedService.setAdopterForAnimal(animal.getId(), user.getTelegramUserId());
        assertThat(actual).isEqualTo(animal);
        assertThat(actual.getTookDate()).isNotNull().isEqualTo(LocalDateTime.now().toLocalDate());
        assertThat(actual.getPetReturnDate()).isNotNull().isEqualTo(LocalDateTime.now().plusDays(14).toLocalDate());
        assertThat(actual.getUser().getTelegramUserId()).isNotNull().isEqualTo(user.getTelegramUserId());
    }

    @Test
    void setAdopterForAnimalNegativeTest_ifAnimalNotDb() {
        User user = userList.get(faker.random().nextInt(userList.size()));
        Animal animal = new Animal();
        animal.setUser(user);
        when(animalRepo.findById(animal.getId())).thenThrow(AnimalNotFoundException.class);
        assertThatThrownBy(() -> adoptedService.setAdopterForAnimal(animal.getId(), user.getTelegramUserId())).isInstanceOf(AnimalNotFoundException.class);
    }

    @Test
    void setAdopterForAnimalNegativeTest_ifUserNotDb() {
        User user = userList.get(faker.random().nextInt(userList.size()));
        Animal animal = new Animal();
        animal.setUser(user);
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(userService.getUserById(user.getTelegramUserId())).thenThrow(UserNotFoundException.class);
        assertThatThrownBy(() -> adoptedService.setAdopterForAnimal(animal.getId(), user.getTelegramUserId())).isInstanceOf(UserNotFoundException.class);
    }


    @Test
    void prolongTrialForNDaysTestPositive() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        User user = userList.get(faker.random().nextInt(userList.size()));
        animal.setUser(user);
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        when(userService.getUserById(user.getTelegramUserId())).thenReturn(user);
        adoptedService.setAdopterForAnimal(animal.getId(), user.getTelegramUserId());
        int days = faker.random().nextInt(1,30);
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        Animal actual = adoptedService.prolongTrialForNDays(animal.getId(), days);
        assertThat(actual).isNotNull().isEqualTo(animal);
        assertThat(actual.getTookDate()).isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        assertThat(actual.getPetReturnDate()).isEqualTo(LocalDateTime.now().plusDays(14+days).truncatedTo(ChronoUnit.DAYS));
    }

    @Test
    void prolongTrialForNDaysTest_ifAdoptedDaysNull() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        User user = userList.get(faker.random().nextInt(userList.size()));
        animal.setUser(user);
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        when(userService.getUserById(user.getTelegramUserId())).thenReturn(user);
        adoptedService.setAdopterForAnimal(animal.getId(), user.getTelegramUserId());
        animal.setPetReturnDate(null);
        int days = faker.random().nextInt(1,30);
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        Animal actual = adoptedService.prolongTrialForNDays(animal.getId(), days);
        assertThat(actual).isNotNull().isEqualTo(animal);
        assertThat(actual.getPetReturnDate()).isEqualTo(LocalDateTime.now().plusDays(days+days).truncatedTo(ChronoUnit.DAYS));
    }

    @Test
    void cancelTrialPositiveTest() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        when(animalRepo.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(animalRepo.save(animal)).thenReturn(animal);
        Animal actual = adoptedService.cancelTrial(animal.getId());
        assertThat(actual).isNotNull().isEqualTo(animal);
        assertThat(actual.getTookDate()).isNull();
        assertThat(actual.getPetReturnDate()).isNull();
    }

    @Test
    void cancelTrialNegativeTest_ifAnimalNotDb() {
        Animal animal = animalList.get(faker.random().nextInt(animalList.size()));
        when(animalRepo.findById(animal.getId())).thenThrow(AnimalNotFoundException.class);
        assertThatThrownBy(() -> adoptedService.cancelTrial(animal.getId()))
                .isInstanceOf(AnimalNotFoundException.class);
    }
}