package ru.pet.nursery.web.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.validator.VolunteerValidator;

import java.util.Optional;

@Service
public class VolunteerService {
    private final VolunteerRepo volunteerRepo;
    private final VolunteerValidator validator;

    public VolunteerService(VolunteerRepo volunteerRepo,
                            VolunteerValidator validator) {
        this.volunteerRepo = volunteerRepo;
        this.validator = validator;
    }

    /**
     * Метод для сохранения в базе данных объекта Volunteer,
     * переданного из контроллера VolunteerController
     * @param volunteer - объект класса Volunteer
     * @return ResponseEntity.of(Optional.of(volunteerFromDB))
     */
    public ResponseEntity<Volunteer> upload(Volunteer volunteer) {
        validator.validate(volunteer);
        Volunteer volunteerFromDB = volunteerRepo.save(volunteer);
        return ResponseEntity.of(Optional.of(volunteerFromDB));
    }

    /**
     * Метод служит для изменения имени в строке в идентификатором id
     * @param name - новое имя
     * @param id - идентификатор
     * @return ResponseEntity<Volunteer>
     */
    public ResponseEntity<Volunteer> uploadName(String name, int id) {
        validator.validateName(name);
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).get();
        if(volunteerOld.getName().equals(name)){
            return ResponseEntity.of(Optional.of(volunteerOld));
        }
        volunteerOld.setName(name);
        Volunteer volunteer = volunteerRepo.save(volunteerOld);
        return ResponseEntity.of(Optional.of(volunteer));
    }

    /**
     * Метод для изменения статуса волонтера: если значение в столбце is_active = true,
     * то волонтер принимает обращения пользователей бота
     * @param status - булева переменная
     * @param id - идентификатор волонтера в таблице волонтеров
     * @return ResponseEntity.of(volunteer) - объект класса Volunteer
     */
    public ResponseEntity<Volunteer> uploadStatus(Boolean status, Integer id) {
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).get();
        if(volunteerOld.isActive() == status){
            return ResponseEntity.of(Optional.of(volunteerOld));
        }
        volunteerOld.setActive(status);
        Volunteer volunteer = volunteerRepo.save(volunteerOld);
        return ResponseEntity.of(Optional.of(volunteer));
    }

    public ResponseEntity<Volunteer> uploadPhone(String phone, Integer id) {
        return null;
    }
}
