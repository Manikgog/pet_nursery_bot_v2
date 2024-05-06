package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pet.nursery.entity.Volunteer;

public interface VolunteerRepo extends JpaRepository<Volunteer, Integer> {
}
