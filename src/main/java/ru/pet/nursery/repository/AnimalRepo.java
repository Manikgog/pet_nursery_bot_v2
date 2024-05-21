package ru.pet.nursery.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnimalRepo extends JpaRepository<Animal, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE animal_table SET photo_path = :pathToPhoto WHERE id = :Id", nativeQuery = true)
    void updatePhotoPathColumn(@Param("pathToPhoto") String pathToPhoto, @Param("Id") int Id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE animal_table SET telegram_user_id = :adoptedId, took_date = :date WHERE id = :id", nativeQuery = true)
    void updateWhoTookPetAndTookDate(Long adoptedId, LocalDate date, Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE animal_table SET telegram_user_id = 1, took_date = null, pet_return_date = :date WHERE id = :id", nativeQuery = true)
    void updateReturnDateAnimal(LocalDate date, Long id);
    Animal findByUser(User user);
    List<Animal> findByAnimalType(AnimalType animalType);

}
