package ru.pet.nursery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.service.AnimalService;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.Constants.VASKA;
import static ru.pet.nursery.Constants.VASKA_DTO;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        when(animalRepo.save(VASKA)).thenReturn(VASKA);
        ResponseEntity<Animal> actualResult = animalService.uploadAnimal(VASKA_DTO);
        Assertions.assertEquals(VASKA, actualResult.getBody());
    }
}
