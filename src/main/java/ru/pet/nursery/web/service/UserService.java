package ru.pet.nursery.web.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.UserNotFoundException;
import ru.pet.nursery.web.exception.UserNotValidException;

import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Метод для создания нового юзера
     *
     * @param user объект для передачи полей пользователя
     * @return пользователя из БД
     */
    public User addUser(User user) {
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
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователя с таким ID = " + userId + " не существует"));
    }

    /**
     * Метод для обновления данных пользователя
     *
     * @param userId идентификационный номер пользователя в БД
     * @param user сущность пользователя с необходимыми изменениями
     * @return пользователя из БД с изменениями
     */
    public User updateUser(Long userId, User user) {
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
        User user = getUserById(userId);
        userRepo.delete(user);
        return user;
    }

    /**
     * Получить список всех пользователей построчно
     * @param pageNo номер страницы
     * @param pageSize размер страницы
     * @return список пользователей страница pageNo и количеством записей на странице pageSize
     */
    public List<User> getAllShelter(Integer pageNo, Integer pageSize) {
        return userRepo.findAll(PageRequest.of(pageNo-1,pageSize)).getContent();
    }

    public List<User> getAll() {
        return userRepo.findAll().stream().toList();
    }

}
