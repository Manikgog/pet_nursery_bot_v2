package ru.pet.nursery.mapper;

import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.web.dto.AnimalDTO;

public class AnimalMapper implements Mapper<Animal, AnimalDTO>{
    /**
     * Метод для преобразования объекта класса Animal в объект класса AnimalDTO
     * @param animal - объект класса Animal
     * @return animalDTO - объект класса AnimalDTO
     */
    @Override
    public AnimalDTO perform(Animal animal){
        AnimalDTO animalDTO = new AnimalDTO();
        animalDTO.setAnimalName(animal.getAnimalName());
        animalDTO.setAnimalType(animal.getAnimalType());
        animalDTO.setGender(animal.getGender());
        animalDTO.setBirthDate(animal.getBirthDate());
        animalDTO.setNurseryId((long) Math.toIntExact(animal.getNursery().getId()));
        animalDTO.setDescription(animal.getDescription());
        return animalDTO;
    }
}
