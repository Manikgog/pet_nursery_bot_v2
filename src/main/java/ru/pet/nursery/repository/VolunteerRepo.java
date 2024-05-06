package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pet.nursery.entity.Volunteer;

@Repository
public interface VolunteerRepo extends JpaRepository<Volunteer, Integer> {
}
