package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;

import java.util.List;

@Repository
public interface AnimalRepo extends JpaRepository<Animal, Integer> {
    Animal findByUser(User user);
    List<Animal> findByAnimalType(AnimalType animalType);
}
