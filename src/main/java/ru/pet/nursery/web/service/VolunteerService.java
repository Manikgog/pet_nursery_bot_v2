package ru.pet.nursery.web.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;

@Service
public class VolunteerService {
    private final VolunteerRepo volunteerRepo;

    public VolunteerService(VolunteerRepo volunteerRepo) {
        this.volunteerRepo = volunteerRepo;
    }


    public ResponseEntity<Volunteer> upload(Volunteer volunteer) {

        return null;
    }
}
