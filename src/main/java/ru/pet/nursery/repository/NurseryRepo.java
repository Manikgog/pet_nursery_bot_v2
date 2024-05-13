package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pet.nursery.entity.Nursery;
@Repository
public interface NurseryRepo extends JpaRepository<Nursery, Integer> {
}
