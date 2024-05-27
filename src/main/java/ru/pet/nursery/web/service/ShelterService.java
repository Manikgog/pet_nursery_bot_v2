package ru.pet.nursery.web.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.mapper.ShelterMapper;
import ru.pet.nursery.repository.ShelterRepo;
import ru.pet.nursery.web.dto.ShelterDTO;
import ru.pet.nursery.web.exception.ShelterNotFoundException;
import ru.pet.nursery.web.exception.ShelterNullException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ShelterService {

    private final ShelterRepo shelterRepo;

    public ShelterService(ShelterRepo shelterRepo) {
        this.shelterRepo = shelterRepo;
    }

    /**
     * Метод для добавления нового приюта в таблицу nursery_table БД
     *
     * @param nursery объект для передачи полей приюта
     * @return объект ResponseEntity с объектом Nursery, который извлечен из БД после загрузки
     */

    public Nursery addShelter(Nursery nursery) {
        nursery.setId(null);
        if (nursery.getAddress() == null || nursery.getPhoneNumber() == null || nursery.getNameShelter() == null) {
            throw new ShelterNullException("Необходимо передать адрес приюта и/или контактные данные");
        }
        return shelterRepo.save(nursery);
    }

    /**
     * Метод для поиска приюта по Id
     *
     * @param id primary key приюта в таблице nursery_table
     * @return возвращает найденную запись из БД, если такого приюта нет возвращает ShelterNotFoundException.
     */

    public Nursery findShelter(Long id) {
        return getShelter(id);
    }

    private Nursery getShelter(Long id) {
        return shelterRepo.findById(id).orElseThrow(() -> new ShelterNotFoundException("Приют с id = " + id + " не найден"));
    }

    /**
     * Метод для обновления информации у приюта
     *
     * @param id      primary key приюта в таблице nursery_table
     * @param nursery объект с изменениями переданный в теле запроса.
     * @return возвращает измененную запись из БД
     */
    public Nursery updateShelter(Long id, Nursery nursery) {
        Nursery oldNursery = getShelter(id);
        oldNursery.setAddress(nursery.getAddress());
        oldNursery.setNameShelter(nursery.getNameShelter());
        oldNursery.setPhoneNumber(nursery.getPhoneNumber());
        oldNursery.setForDog(nursery.isForDog());
        return shelterRepo.save(oldNursery);
    }

    /**
     * Метод для удаления приюта из таблицы nursery_table
     *
     * @param id primary key приюта из таблицы primary key
     * @return возвращает приют, который был удален
     */
    public Nursery removeShelter(Long id) {
        return shelterRepo.findById(id)
                .map(shelterToDel -> {
                    shelterRepo.delete(shelterToDel);
                    return shelterToDel;
                })
                .orElseThrow(() -> new ShelterNotFoundException("Приют с id = " + id + " не найден"));
    }

    /**
     * Метод для получения списка приютов постранично
     *
     * @param pageNo   номер страницы
     * @param pageSize количество объектов в листе
     * @return лист объектов с информацией для пользователя.
     */
    public List<Nursery> getAllShelter(Integer pageNo, Integer pageSize) {
        return shelterRepo.findAll(PageRequest.of(pageNo-1,pageSize)).getContent();
    }

    /**
     * Метод для фильтрации приютов по видам животных
     *
     * @param kindOfAnimal вид животного из таблицы nursery_table, где true - собака, false - кошка
     * @param pageNo       номер страницы
     * @param pageSize     количество объектов в листе
     * @return лист объектов с информацией для пользователя.
     */
    public List<Nursery> getShelterForDog(Boolean kindOfAnimal, Integer pageNo, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
        return shelterRepo.findAll(pageable).stream().filter(pet -> pet.isForDog() == kindOfAnimal).toList();
    }

    public List<Nursery> getAll() {
        return shelterRepo.findAll();
    }

}
