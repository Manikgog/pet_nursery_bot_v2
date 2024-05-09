package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pet.nursery.entity.Nursery;

public interface ShelterRepo extends JpaRepository<Nursery,Long> {
    Nursery findByKindOfAnimal(Boolean kindOfAnimal);
}
