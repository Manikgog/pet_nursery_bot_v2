package ru.pet.nursery.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShelterDTO {
    private String nameShelter;
    private String address;
    private String phoneNumber;
    private boolean forDog;

    @Override
    public String toString() {
        return "ShelterDTO{" +
                "nameShelter='" + nameShelter + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", forDog=" + kindOfAnimal(forDog) +
                '}';
    }

    private String kindOfAnimal(Boolean kindOfAnimal) {
        if (kindOfAnimal) {
            return " Для собак";
        }else {
            return " Для кошек";
        }
    }

    public String toTelegramString() {
        return "Название приюта:" + nameShelter +
                "\nАдрес: " + address +
                "\nНомер телефона: " + phoneNumber +
                "\nВид животного: " + kindOfAnimal(isForDog());

    }
}
