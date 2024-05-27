package ru.pet.nursery.web.service;


import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.web.exception.AnimalNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AdoptedService {

    private final static int BASIC_TRIAL_DAYS = 14;
    private final AnimalRepo animalRepo;
    private final UserService userService;

    public AdoptedService(AnimalRepo animalRepo, UserService userService) {
        this.animalRepo = animalRepo;
        this.userService = userService;
    }


    /**
     * Метод для назначения пользователя как усыновителя для питомца
     * @param animalId из таблицы animal_table
     * @param adopterId пользователь, который станет усыновителем
     * @return питомца с назначенным усыновителем и установленными периодами взятия и возврата из приюта
     */
    public Animal setAdopterForAnimal(Long animalId, Long adopterId) {
        Animal animal = getAnimalById(animalId);
        User adopter = userService.getUserById(adopterId);
        animal.setUser(adopter);
        animal.setTookDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        animal.setPetReturnDate(LocalDateTime.now().plusDays(BASIC_TRIAL_DAYS).truncatedTo(ChronoUnit.DAYS));
        return animalRepo.save(animal);
    }

    /**
     * Метод для пролонгации времени пребывания в приемной семье
     * @param animalId из таблицы animal_table
     * @param days количество дней
     * @return пролонгированное содержание питомца усыновителями
     */
    public Animal prolongTrialForNDays(Long animalId, Integer days) {
        Animal animal = getAnimalById(animalId);
        LocalDateTime adoptionDate = animal.getPetReturnDate();
        if (adoptionDate == null) {
            adoptionDate = animal.getTookDate().truncatedTo(ChronoUnit.DAYS).plusDays(days);
        }
        animal.setPetReturnDate(adoptionDate.truncatedTo(ChronoUnit.DAYS).plusDays(days));
        return animalRepo.save(animal);
    }

    private Animal getAnimalById(Long id) {
        return animalRepo.findById(id).orElseThrow(() -> new AnimalNotFoundException("Питомца с таким ID = " + id + " нет в БД"));
    }

    /**
     * Метод для завершения адаптационного периода
     * @param animalId из таблицы animal_table
     * @return измененные данные о животном из таблицы animal_table
     */
    public Animal cancelTrial(Long animalId) {
        Animal animal = getAnimalById(animalId);
        animal.setTookDate(null);
        animal.setPetReturnDate(null);
        animal.setUser(null);
        return animalRepo.save(animal);
    }
}
