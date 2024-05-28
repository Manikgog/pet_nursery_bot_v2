package ru.pet.nursery.web.service;

import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Nursery;

import java.util.List;

@Service
public interface IShelterService {
    Nursery addShelter(Nursery nursery);

    Nursery findShelter(Long id);

    Nursery updateShelter(Long id, Nursery nursery);

    Nursery removeShelter(Long id);

    List<Nursery> getAllShelter(Integer pageNo, Integer pageSize);

    List<Nursery> getShelterForDog(Boolean kindOfAnimal, Integer pageNo, Integer pageSize);

    List<Nursery> getAll();
}
