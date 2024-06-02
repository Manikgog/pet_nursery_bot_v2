package ru.pet.nursery.web.service;

import ru.pet.nursery.entity.Volunteer;
import java.util.List;

public interface IVolunteerService {
    Volunteer upload(Volunteer volunteer);

    Volunteer updateName(String name, int id);

    Volunteer updateStatus(Boolean status, Integer id);

    Volunteer updatePhone(String phone, Integer id);

    Volunteer updateVolunteer(Integer id, Volunteer volunteer);

    Volunteer get(Integer id);

    Volunteer delete(Integer id);

    List<Volunteer> getAll();
}
