package ru.pet.nursery.web.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.AnimalNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AdoptedService {

    private final static int BASIC_TRIAL_DAYS = 14;
    private final AnimalRepo animalRepo;
    private final UserService userService;

    public AdoptedService(AnimalRepo animalRepo, UserRepo userRepo, UserService userService) {
        this.animalRepo = animalRepo;
        this.userService = userService;
    }
    /**
     * Метод поиска животных от даты когда его взяли
     * @return список всех животных которых взяли
     */
    @Transactional(readOnly = true)
    public List<Animal> findByPetReturnDate() {
        return animalRepo.findByPetReturnDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    /**
     * Метод для назначения пользователя как усыновителя для питомца
     * @param animalId из таблицы animal_table
     * @param userId пользователь, который станет усыновителем
     * @return питомца с назначенным усыновителем и установленными периодами взятия и возврата из приюта
     */
    public Animal setAdopterForAnimal(Long animalId, Long userId) {
        Animal animal = animalRepo.findById(animalId).orElseThrow(() -> new AnimalNotFoundException("Питомца с таким ID = " + animalId + " нет в БД"));
        User user = userService.getUserById(userId);
        animal.setUser(user);
        animal.setTookDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        animal.setPetReturnDate(LocalDateTime.now().plusDays(BASIC_TRIAL_DAYS));
        return animalRepo.save(animal);
    }

    /**
     * Метод для пролонгации времени пребывания в приемной семьи
     * @param animalId из таблицы animal_table
     * @param days количество дней
     * @return пролонгированное содержание питомца усыновителями
     */
    public Animal prolongTrialForNDays(Long animalId, Integer days) {
        Animal animal = animalRepo.findById(animalId).orElseThrow(() -> new AnimalNotFoundException("Питомца с таким ID = " + animalId + " нет в БД"));
        LocalDateTime adoptionDate = animal.getPetReturnDate();
        if (adoptionDate == null) {
            adoptionDate = LocalDateTime.now();
        }
        adoptionDate = adoptionDate.plusDays(days);
        animal.setPetReturnDate(adoptionDate);
        return animalRepo.save(animal);
    }

    /**
     * Метод для завершения адаптационного периода
     * @param animalId из таблицы animal_table
     * @return измененные данные о животном из таблицы animal_table
     */
    public Animal cancelTrial(Long animalId) {
        Animal animal = animalRepo.findById(animalId).orElseThrow(() -> new AnimalNotFoundException("Питомца с таким ID = " + animalId + " нет в БД"));
        animal.setTookDate(null);
        animal.setPetReturnDate(null);
        return animalRepo.save(animal);
    }
}
