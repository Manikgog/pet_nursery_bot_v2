package ru.pet.nursery.web.service;

import ru.pet.nursery.entity.User;

import java.util.List;

public interface IUserService {
    User addUser(User user);

    User getUserById(Long userId);

    User updateUser(Long userId, User user);

    User removeUser(Long userId);

    List<User> getAllUser(Integer pageNo, Integer pageSize);

    List<User> getAll();
}
