package ru.pet.nursery.web.service;

import com.pengrad.telegrambot.model.Update;
import ru.pet.nursery.entity.User;
import java.util.List;

public interface IUserService {
    User addUser(User user);

    void addUserFromTelegramBot(Update update);

    User getUserById(Long userId);

    Boolean existsUserById(Long userId);

    User updateUser(Long userId, User user);

    User removeUser(Long userId);

    List<User> getAllUser(Integer pageNo, Integer pageSize);

    List<User> getAll();
}
