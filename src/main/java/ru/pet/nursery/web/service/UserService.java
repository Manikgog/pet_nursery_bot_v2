package ru.pet.nursery.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.ReportRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.EntityNotFoundException;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.exception.UserNotFoundException;
import ru.pet.nursery.web.exception.UserNotValidException;
import java.util.List;

@Service
public class UserService implements IUserService {
    private final UserRepo userRepo;
    private final ReportRepo reportRepo;
    private final AnimalRepo animalRepo;

    public UserService(UserRepo userRepo,
                       ReportRepo reportRepo,
                       AnimalRepo animalRepo) {
        this.userRepo = userRepo;
        this.reportRepo = reportRepo;
        this.animalRepo = animalRepo;
    }
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Метод для создания нового юзера
     *
     * @param user объект для передачи полей пользователя
     * @return пользователя из БД
     */
    public User addUser(User user) {
        log.info("Method addUser of UserService class with parameters User -> {}", user);
        if (user.getFirstName() == null || user.getLastName() == null) {
            throw new UserNotValidException("Необходимо ввести имя/или фамилию");
        }
        return userRepo.save(user);
    }

    /**
     * Метод для поиска пользователей в БД
     *
     * @param userId идентификационный номер пользователя
     * @return пользователя если такой есть в БД, если такого пользователя нет то выбрасывает исключение UserNotFoundException
     */
    public User getUserById(Long userId) {
        log.info("Method getUserById of UserService class with parameters Long userID -> {}", userId);
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователя с таким ID = " + userId + " не существует"));
    }

    /**
     * Метод для обновления данных пользователя
     *
     * @param userId идентификационный номер пользователя в БД
     * @param user   сущность пользователя с необходимыми изменениями
     * @return пользователя из БД с изменениями
     */
    public User updateUser(Long userId, User user) {
        log.info("Method updateShelter of UserService class with parameters Long userID -> {}, newUser->{}", userId,user);
        User oldUser = getUserById(userId);
        oldUser.setUserName(user.getUserName());
        oldUser.setAddress(user.getAddress());
        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        return userRepo.save(oldUser);
    }

    /**
     * Метод для удаления пользователя из БД
     * @param userId идентификационный номер пользователя в БД
     * @return пользователя у которого была проведена операция удаления
     */
    public User removeUser(Long userId) {
        log.info("Method removeUser of UserService class with parameters Long userID -> {}", userId);
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId));
        if (!reportRepo.findByUser(user).isEmpty() || !animalRepo.findByUser(user).isEmpty()){
            throw new IllegalParameterException("Удаление невозможно т.к. пользователь id=" + user.getTelegramUserId() + " присутствует в других таблицах");
        }
        userRepo.delete(user);
        return user;
    }

    /**
     * Получить список всех пользователей построчно
     * @param pageNo номер страницы
     * @param pageSize размер страницы
     * @return список пользователей страница pageNo и количеством записей на странице pageSize
     */
    public List<User> getAllUser(Integer pageNo, Integer pageSize) {
        log.info("Method getAllUser of UserService class with parameters int page-> {}, size -> {}", pageNo,pageSize);
        return userRepo.findAll(PageRequest.of(pageNo-1,pageSize)).getContent();
    }

    /**
     * Получить список всех пользователей
     *
     * @return список всех пользователей
     */
    public List<User> getAll() {
        log.info("Method getAll of ShelterService class");
        return userRepo.findAll().stream().toList();
    }

}
