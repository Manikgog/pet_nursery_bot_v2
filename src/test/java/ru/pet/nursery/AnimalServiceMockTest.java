package ru.pet.nursery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.service.AnimalService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.Constants.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceMockTest {

    @Mock
    AnimalRepo animalRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    NurseryRepo nurseryRepo;
    @InjectMocks
    AnimalService animalService;

    @Test
    public void uploadAnimal_positiveTest(){
        when(animalRepo.save(VASKA)).thenReturn(VASKA_FROM_DB);
        when(nurseryRepo.findById(1L)).thenReturn(Optional.ofNullable(NURSERY_1));
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(USER));
        ResponseEntity<Animal> actualResult = animalService.uploadAnimal(VASKA_DTO);
        Assertions.assertEquals(VASKA.getAnimalName(), actualResult.getBody().getAnimalName());
        Assertions.assertEquals(VASKA.getAnimalType(), actualResult.getBody().getAnimalType());
        Assertions.assertEquals(VASKA.getUser(), actualResult.getBody().getUser());
        Assertions.assertEquals(VASKA.getDescription(), actualResult.getBody().getDescription());

        when(animalRepo.save(PALKAN)).thenReturn(PALKAN_FROM_DB);
        when(nurseryRepo.findById(2L)).thenReturn(Optional.ofNullable(NURSERY_2));
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(USER));
        actualResult = animalService.uploadAnimal(PALKAN_DTO);
        Assertions.assertEquals(PALKAN.getAnimalName(), actualResult.getBody().getAnimalName());
        Assertions.assertEquals(PALKAN.getAnimalType(), actualResult.getBody().getAnimalType());
        Assertions.assertEquals(PALKAN.getUser(), actualResult.getBody().getUser());
        Assertions.assertEquals(PALKAN.getDescription(), actualResult.getBody().getDescription());
    }

}
