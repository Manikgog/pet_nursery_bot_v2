package ru.pet.nursery.mapper;

import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.web.dto.AnimalDTOForUser;

public class AnimalDTOForUserMapper  implements Mapper<Animal, AnimalDTOForUser>{
    @Override
    public AnimalDTOForUser perform(Animal animal) {
        AnimalDTOForUser animalDTOForUser = new AnimalDTOForUser();
        animalDTOForUser.setId(animal.getId());
        animalDTOForUser.setAnimalName(animal.getAnimalName());
        animalDTOForUser.setAnimalType(animal.getAnimalType());
        animalDTOForUser.setGender(animal.getGender());
        animalDTOForUser.setNursery(animal.getNursery());
        animalDTOForUser.setBirthDate(animal.getBirthDate());
        animalDTOForUser.setDescription(animal.getDescription());
        return animalDTOForUser;
    }
}
