package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pet.nursery.entity.Nursery;

public interface NurseryRepo extends JpaRepository<Nursery, Integer> {
}
