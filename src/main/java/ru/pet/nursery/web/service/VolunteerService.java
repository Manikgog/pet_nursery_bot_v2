package ru.pet.nursery.web.service;

import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Volunteer;
import ru.pet.nursery.repository.VolunteerRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.validator.VolunteerValidator;
import java.util.List;

@Service
public class VolunteerService implements IVolunteerService {
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
     * @return объект класса Volunteer
     */
    public Volunteer upload(Volunteer volunteer) {
        validator.validate(volunteer);
        return volunteerRepo.save(volunteer);
    }

    /**
     * Метод служит для изменения имени в строке с идентификатором id
     * @param name - новое имя
     * @param id - идентификатор
     * @return объект класса Volunteer
     */
    public Volunteer updateName(String name, int id) {
        validator.stringValidate(name);
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException((long)id));
        if(volunteerOld.getName().equals(name)){
            return volunteerOld;
        }
        volunteerOld.setName(name);
        return volunteerRepo.save(volunteerOld);
    }

    /**
     * Метод для изменения статуса волонтера: если значение в столбце is_active = true,
     * то волонтер принимает обращения пользователей бота
     * @param status - булева переменная
     * @param id - идентификатор волонтера в таблице волонтеров
     * @return объект класса Volunteer
     */
    public Volunteer updateStatus(Boolean status, Integer id) {
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException((long)id));
        if(volunteerOld.isActive() == status){
            return volunteerOld;
        }
        volunteerOld.setActive(status);
        return volunteerRepo.save(volunteerOld);
    }


    /**
     * Метод для изменения номера телефона волонтера
     * @param phone - новое значение номера телефона
     * @param id - идентификатор волонтера в таблице волонтеров
     * @return объект класса Volunteer
     */
    public Volunteer updatePhone(String phone, Integer id) {
        validator.stringValidate(phone);
        validator.phoneValidate(phone);
        validator.validateId(id);
        Volunteer volunteerOld = volunteerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException((long)id));
        if(volunteerOld.getPhoneNumber().equals(phone)){
            return volunteerOld;
        }
        volunteerOld.setPhoneNumber(phone);
        return volunteerRepo.save(volunteerOld);
    }

    /**
     * Метод для внесения изменений в строку базы таблицы волонтеров с идентификатором id
     * @param id - идентификатор (первичный ключ) таблицы волонтеров
     * @param volunteer - объект Volunteer с новыми полями
     * @return объект класса Volunteer
     */
    public Volunteer updateVolunteer(Integer id, Volunteer volunteer) {
        validator.validateId(id);
        validator.validate(volunteer);
        Volunteer volunteerOld = volunteerRepo.findById(volunteer.getId()).orElseThrow(() -> new EntityNotFoundException((long)id));
        if(volunteerOld.equals(volunteer)){
            return volunteerOld;
        }
        volunteerOld.setTelegramUserId(volunteer.getTelegramUserId());
        volunteerOld.setActive(volunteer.isActive());
        volunteerOld.setName(volunteer.getName());
        volunteerOld.setPhoneNumber(volunteer.getPhoneNumber());
        return volunteerRepo.save(volunteerOld);
    }

    /**
     * Метод для получения объекта Volunteer из базы данных по id
     * @param id - идентификатор (первичный ключ) таблицы волонтеров
     * @return объект класса Volunteer
     */
    public Volunteer get(Integer id) {
        validator.validateId(id);
        return volunteerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException((long)id));
    }

    /**
     * Метод для удаления объекта Volunteer из базы данных по id
     * @param id - идентификатор (первичный ключ) таблицы волонтеров
     * @return объект класса Volunteer
     */
    public Volunteer delete(Integer id) {
        validator.validateId(id);
        Volunteer volunteer = volunteerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException((long)id));
        volunteerRepo.delete(volunteer);
        return volunteer;
    }


    /**
     * Метод для получения из базы данных всего списка волонтёров
     * @return список объектов Volunteer
     */
    public List<Volunteer> getAll() {
        return volunteerRepo.findAll();
    }

    public List<Volunteer> findIsActive() {
        return volunteerRepo.findByIsActiveTrue();
    }
}
