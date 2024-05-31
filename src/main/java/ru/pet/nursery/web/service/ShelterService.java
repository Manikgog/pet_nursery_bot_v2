package ru.pet.nursery.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.repository.ShelterRepo;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.ShelterNotFoundException;
import ru.pet.nursery.web.exception.ShelterNullException;
import java.util.List;

@Service
public class ShelterService implements IShelterService {
    private final Logger log = LoggerFactory.getLogger(ShelterService.class);

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
        log.info("Method addShelter of ShelterService class with parameters Nursery -> {}", nursery);
        nursery.setId(null);
        if (nursery.getAddress() == null || nursery.getPhoneNumber() == null || nursery.getNameShelter() == null) {
            throw new ShelterNullException("Необходимо передать адрес приюта и/или контактные данные");
        }
        return shelterRepo.save(nursery);
    }

    /**
     * Метод для поиска приюта по Id
     *
     * @param nurseryId primary key приюта в таблице nursery_table
     * @return возвращает найденную запись из БД, если такого приюта нет возвращает ShelterNotFoundException.
     */

    public Nursery findShelter(Long nurseryId) {
        log.info("Method findShelter of ShelterService class with parameters Long nurseryId -> {}", nurseryId);
        return getShelter(nurseryId);
    }

    private Nursery getShelter(Long id) {
        return shelterRepo.findById(id).orElseThrow(() -> new ShelterNotFoundException("Приют с id = " + id + " не найден"));
    }

    /**
     * Метод для обновления информации у приюта
     *
     * @param nurseryId   primary key приюта в таблице nursery_table
     * @param nursery объект с изменениями переданный в теле запроса.
     * @return возвращает измененную запись из БД
     */
    public Nursery updateShelter(Long nurseryId, Nursery nursery) {
        log.info("Method updateShelter of ShelterService class with parameters Long nurseryId -> {}, newNursery->{}", nurseryId,nursery);
        Nursery oldNursery = getShelter(nurseryId);
        oldNursery.setAddress(nursery.getAddress());
        oldNursery.setNameShelter(nursery.getNameShelter());
        oldNursery.setPhoneNumber(nursery.getPhoneNumber());
        oldNursery.setForDog(nursery.isForDog());
        oldNursery.setMapLink(nursery.getMapLink());
        return shelterRepo.save(oldNursery);
    }

    /**
     * Метод для удаления приюта из таблицы nursery_table
     *
     * @param nurseryId primary key приюта из таблицы primary key
     * @return возвращает приют, который был удален
     */
    public Nursery removeShelter(Long nurseryId) {
        log.info("Method removeShelter of ShelterService class with parameters Long nurseryId -> {}", nurseryId);
        return shelterRepo.findById(nurseryId)
                .map(shelterToDel -> {
                    shelterRepo.delete(shelterToDel);
                    return shelterToDel;
                })
                .orElseThrow(() -> new ShelterNotFoundException("Приют с nurseryId = " + nurseryId + " не найден"));
    }

    /**
     * Метод для получения списка приютов постранично
     *
     * @param pageNo   номер страницы
     * @param pageSize количество объектов в листе
     * @return лист объектов с информацией для пользователя.
     */
    public List<Nursery> getAllShelter(Integer pageNo, Integer pageSize) {
        log.info("Method getAllShelter of ShelterService class with parameters int page-> {}, size -> {}", pageNo,pageSize);
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
        log.info("Method getShelterForDog of ShelterService class with parameters boolean-> {}, int page-> {}, size -> {}",kindOfAnimal, pageNo,pageSize);
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
        return shelterRepo.findAll(pageable).stream().filter(pet -> pet.isForDog() == kindOfAnimal).toList();
    }

    public List<Nursery> getAll() {
        log.info("Method getAll of ShelterService class");
        return shelterRepo.findAll();
    }

    /**
     * Метод для внесения изменений в поле со ссылкой на карту приюта
     * @param id - идентификатор приюта
     * @param link - строка со ссылкой
     * @return измененный объект приюта
     */
    public Nursery updateMap(Long id, String link) {
        if(link == null || link.isEmpty() || link.isBlank()) {
            throw new IllegalParameterException("Строка ссылки не должна быть пустой");
        }
        Nursery nurseryOld = getShelter(id);
        nurseryOld.setMapLink(link);
        return shelterRepo.save(nurseryOld);
    }
}
