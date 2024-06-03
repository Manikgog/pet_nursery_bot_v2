package ru.pet.nursery.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import java.time.LocalDate;
import java.util.List;

public interface AnimalRepo extends JpaRepository<Animal, Long> {
    List<Animal> findByUser(User user);
    List<Animal> findByAnimalType(AnimalType animalType);
    List<Animal> findByUserIsNull(PageRequest pageRequest);
    List<Animal> findByPetReturnDate(LocalDate localDate);
    List<Animal> findByNursery(Nursery nursery);

}
