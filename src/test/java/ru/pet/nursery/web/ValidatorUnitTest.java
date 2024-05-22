package ru.pet.nursery.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.repository.NurseryRepo;
import ru.pet.nursery.web.exception.IllegalFieldException;
import ru.pet.nursery.web.exception.PageNumberException;
import ru.pet.nursery.web.exception.PageSizeException;
import ru.pet.nursery.web.validator.Validator;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.web.Constants.*;


@ExtendWith(MockitoExtension.class)
public class ValidatorUnitTest {
    @Mock
    NurseryRepo nurseryRepo;
    @InjectMocks
    Validator validator;

    /**
     * Тестирование метода validateAnimalDTO
     * при валидных исходных данных
     */
    @Test
    public void validateAnimalDTO_positiveTest(){
        when(nurseryRepo.findById(VASKA_DTO.getNurseryId())).thenReturn(Optional.ofNullable(NURSERY_1));
        validator.validateAnimalDTO(VASKA_DTO);
    }

    /**
     * Тестирование метода validateAnimalDTO при невалидных
     * исходных данных: пустое имя, при несоответствии номера приюта,
     * пустое описание, неправильная дата рождения
     */
    @Test
    public void validateAnimalDTO_negativeTest(){
        when(nurseryRepo.findById(VASKA_DTO.getNurseryId())).thenReturn(Optional.ofNullable(NURSERY_1));
        VASKA_DTO.setAnimalName("");
        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validateAnimalDTO(VASKA_DTO));

        when(nurseryRepo.findById(VASKA_DTO.getNurseryId())).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validateAnimalDTO(VASKA_DTO));

        when(nurseryRepo.findById(VASKA_DTO.getNurseryId())).thenReturn(Optional.ofNullable(NURSERY_1));
        VASKA_DTO.setDescription("");
        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validateAnimalDTO(VASKA_DTO));

        VASKA_DTO.setBirthDate(LocalDate.now().plusDays(1));
        Assertions.assertThrows(IllegalFieldException.class, () -> validator.validateAnimalDTO(VASKA_DTO));
    }

    @Test
    public void validatePageNumber_Test(){
        Assertions.assertThrows(PageNumberException.class, () -> validator.validatePageNumber(0));
        Assertions.assertThrows(PageNumberException.class, () -> validator.validatePageNumber(-1));
    }


    @Test
    public void validatePageSize_Test(){
        Assertions.assertThrows(PageSizeException.class, () -> validator.validatePageSize(0));
        Assertions.assertThrows(PageSizeException.class, () -> validator.validatePageSize(-1));
    }
}
