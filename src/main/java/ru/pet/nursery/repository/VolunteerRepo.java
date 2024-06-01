package ru.pet.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pet.nursery.entity.Volunteer;

import java.util.List;

@Repository
public interface VolunteerRepo extends JpaRepository<Volunteer, Integer> {
    @Query(value = "SELECT * FROM volunteer_table where is_active = true limit 1",nativeQuery = true)
    Volunteer getVolunteerIsActive();

    Volunteer getVolunteerByTelegramUserId(Long telegramUserId);
}
