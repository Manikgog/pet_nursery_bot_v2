package ru.pet.nursery.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pet.nursery.entity.Animal;

public interface AnimalRepo extends JpaRepository<Animal, Integer> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE animal_table SET photo_path = :pathToPhoto WHERE id = :Id", nativeQuery = true)
    void updatePhotoPathColumn(@Param("pathToPhoto") String pathToPhoto, @Param("Id") int Id);
}
