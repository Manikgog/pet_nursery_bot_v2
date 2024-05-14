package ru.pet.nursery.mapper;

import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.web.dto.ShelterDTO;

public class ShelterMapper implements Mapper <Nursery, ShelterDTO> {

    @Override
    public ShelterDTO perform(Nursery nursery) {
        ShelterDTO shelterDTO = new ShelterDTO();
        shelterDTO.setNameShelter(nursery.getNameShelter());
        shelterDTO.setAddress(nursery.getAddress());
        shelterDTO.setPhoneNumber(nursery.getPhoneNumber());
        shelterDTO.setForDog(nursery.isForDog());
        return shelterDTO;
    }
}
