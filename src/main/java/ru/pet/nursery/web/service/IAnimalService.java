package ru.pet.nursery.web.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.web.dto.AnimalDTO;
import ru.pet.nursery.web.dto.AnimalDTOForUser;

import java.io.IOException;
import java.util.List;

public interface IAnimalService {
    Animal uploadAnimal(AnimalDTO animalDTO);

    Animal uploadPhoto(long animalId, MultipartFile animalPhoto) throws IOException;

    void getAnimalPhoto(long id, HttpServletResponse response);

    byte[] getPhotoByteArray(long id);

    Animal delete(long id);

    Animal insertDataOfHuman(long animalId, Long adoptedId);

    List<AnimalDTOForUser> getPageList(Integer pageNumber, Integer pageSize);

    List<AnimalDTOForUser> convertListAnimalToListAnimalDTO(List<Animal> animals);

    Animal insertDateOfReturn(long animalId);

    AnimalDTOForUser getById(long animalId);

    List<Animal> getAll();

    List<Animal> getAllAnimalsByType(AnimalType animalType);

    Animal get(long id);

}
