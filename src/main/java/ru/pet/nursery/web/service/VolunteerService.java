package ru.pet.nursery.web.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.validator.VolunteerValidator;

import java.util.List;
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
        volunteer.setId(0);
        validator.validate(volunteer);
        Volunteer volunteerFromDB = volunteerRepo.save(volunteer);
        return ResponseEntity.of(Optional.of(volunteerFromDB));
    }

    /**
     * Метод служит для изменения имени в строке с идентификатором id
     * @param name - новое имя
     * @param id - идентификатор
     * @return ResponseEntity<Volunteer>
     */
    public ResponseEntity<Volunteer> updateName(String name, int id) {
        validator.stringValidate(name);
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
    public ResponseEntity<Volunteer> updateStatus(Boolean status, Integer id) {
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).get();
        if(volunteerOld.isActive() == status){
            return ResponseEntity.of(Optional.of(volunteerOld));
        }
        volunteerOld.setActive(status);
        Volunteer volunteer = volunteerRepo.save(volunteerOld);
        return ResponseEntity.of(Optional.of(volunteer));
    }


    /**
     * Метод для изменения номера телефона волонтера
     * @param phone - новое значение номера телефона
     * @param id - идентификатор волонтера в таблице волонтеров
     * @return ResponseEntity.of(volunteer) - объект класса Volunteer
     */
    public ResponseEntity<Volunteer> updatePhone(String phone, Integer id) {
        validator.stringValidate(phone);
        validator.phoneValidate(phone);
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).get();
        if(volunteerOld.getName().equals(phone)){
            return ResponseEntity.of(Optional.of(volunteerOld));
        }
        volunteerOld.setPhoneNumber(phone);
        Volunteer volunteer = volunteerRepo.save(volunteerOld);
        return ResponseEntity.of(Optional.of(volunteer));
    }

    /**
     * Метод для внесения изменений в строку базы таблицы волонтеров с идентификатором id
     * @param id - идентификатор (первичный ключ) таблицы волонтеров
     * @param volunteer - объект Volunteer с новыми полями
     * @return ResponseEntity.of(Optional.of(volunteer))
     */
    public ResponseEntity<Volunteer> updateVolunteer(Integer id, Volunteer volunteer) {
        validator.validateId(id);
        validator.validate(volunteer);
        Volunteer volunteerOld = volunteerRepo.findById(volunteer.getId()).get();
        if(volunteerOld.equals(volunteer)){
            return ResponseEntity.of(Optional.of(volunteerOld));
        }
        volunteerOld.setTelegramUserId(volunteer.getTelegramUserId());
        volunteerOld.setActive(volunteer.isActive());
        volunteerOld.setName(volunteer.getName());
        volunteerOld.setPhoneNumber(volunteer.getPhoneNumber());
        Volunteer volunteerNew = volunteerRepo.save(volunteerOld);
        return ResponseEntity.of(Optional.of(volunteerNew));
    }

    /**
     * Метод для получения объекта Volunteer из базы данных по id
     * @param id - идентификатор (первичный ключ) таблицы волонтеров
     * @return ResponseEntity.of(Optional.of(volunteer))
     */
    public ResponseEntity<Volunteer> get(Integer id) {
        validator.validateId(id);
        return ResponseEntity.of(volunteerRepo.findById(id));
    }

    /**
     * Метод для удаления объекта Volunteer из базы данных по id
     * @param id - идентификатор (первичный ключ) таблицы волонтеров
     * @return ResponseEntity.of(Optional.of(volunteer))
     */
    public ResponseEntity<Volunteer> delete(Integer id) {
        validator.validateId(id);
        Optional<Volunteer> volunteer = volunteerRepo.findById(id);
        volunteerRepo.delete(volunteer.get());
        return ResponseEntity.of(volunteer);
    }


    /**
     * Метод для получения из базы данных всего списка волонтёров
     * @return ResponseEntity.of(Optional.of(volunteers))
     */
    public ResponseEntity<List<Volunteer>> getAll() {
        List<Volunteer> volunteers = volunteerRepo.findAll();
        return ResponseEntity.of(Optional.of(volunteers));
    }
}
