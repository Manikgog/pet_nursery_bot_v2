package ru.pet.nursery.web.service;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.UserNotFoundException;
import ru.pet.nursery.web.exception.UserNotValidException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private UserService userService;

    private final Faker faker = new Faker();

    private final List<User> userList = new ArrayList<>(11);
    @AfterEach
    public void afterEach() {
        userList.clear();
    }

    @BeforeEach
    public void beforeEach() {
        for (int i = 0; i < 9; i++) {
            User user = new User();
            user.setTelegramUserId(faker.random().nextLong(1231231));
            user.setUserName(faker.harryPotter().character());
            user.setFirstName(faker.harryPotter().character());
            user.setLastName(faker.harryPotter().character());
            user.setAddress(faker.harryPotter().location());
            user.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
            userList.add(user);
        }
    }

    @Test
    void addUserPositiveTest() {
        User expected = new User();
        expected.setTelegramUserId(faker.random().nextLong(1231231));
        expected.setUserName(faker.harryPotter().character());
        expected.setFirstName(faker.harryPotter().character());
        expected.setLastName(faker.harryPotter().character());
        expected.setAddress(faker.harryPotter().location());
        expected.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
        when(userRepo.save(any())).thenReturn(expected);
        userList.add(expected);
        userService.addUser(expected);
        when(userRepo.findById(expected.getTelegramUserId())).thenReturn(Optional.of(expected));
        assertThat(userService.getUserById(expected.getTelegramUserId())).isEqualTo(expected);
    }
    @Test
    void addUserNegativeIfFirstName_NullTest() {
        User expected = new User();
        expected.setTelegramUserId(faker.random().nextLong(1231231));
        expected.setUserName(faker.harryPotter().character());
        expected.setFirstName(null);
        expected.setLastName(null);
        expected.setAddress(faker.harryPotter().location());
        expected.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
        lenient().when(userRepo.save(expected)).thenThrow(UserNotValidException.class);
        assertThatThrownBy(() -> userService.addUser(expected)).isInstanceOf(UserNotValidException.class);
    }
    @Test
    void addUserNegativeIfLastNameNullTest() {
        User expected = new User();
        expected.setTelegramUserId(faker.random().nextLong(1231231));
        expected.setUserName(faker.harryPotter().character());
        expected.setFirstName(null);
        expected.setLastName(faker.harryPotter().character());
        expected.setAddress(faker.harryPotter().location());
        expected.setPhoneNumber(String.valueOf(faker.random().nextInt(12312331)));
        when(userRepo.save(expected)).thenThrow(UserNotValidException.class);
        assertThatThrownBy(() -> userService.addUser(expected)).isInstanceOf(UserNotValidException.class);
    }

    @Test
    void getUserByIdPositiveTest() {
        User expected = userList.get(faker.random().nextInt(userList.size()));
        when(userRepo.save(expected)).thenReturn(expected);
        userService.addUser(expected);
        when(userRepo.findById(expected.getTelegramUserId())).thenReturn(Optional.of(expected));
        assertThat(userService.getUserById(expected.getTelegramUserId())).isEqualTo(expected);
    }

    @Test
    void getUserByIdNegativeTest_ifNotFoundUser() {
        User expected = userList.get(faker.random().nextInt(userList.size()));
        when(userRepo.findById(expected.getTelegramUserId())).thenThrow(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.getUserById(expected.getTelegramUserId()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUserPositiveTest() {
        User expected = userList.get(faker.random().nextInt(userList.size()));
        Long id = expected.getTelegramUserId();
        User actual = new User();
        actual.setTelegramUserId(id);
        actual.setUserName("newName");
        actual.setAddress(expected.getAddress());
        actual.setFirstName(expected.getFirstName());
        actual.setLastName(expected.getLastName());
        actual.setPhoneNumber(expected.getPhoneNumber()+2);
        userList.add(expected);
        when(userRepo.findAll()).thenReturn(userList);
        assertThat(userService.getAll()).contains(expected);
        when(userRepo.findById(expected.getTelegramUserId())).thenReturn(Optional.of(actual));
        when(userRepo.save(actual)).thenReturn(actual);
        assertThat(userService.updateUser(expected.getTelegramUserId(), actual)).isEqualTo(actual);
    }

    @Test
    void updateUserNegativeTest() {
        User expected = userList.get(faker.random().nextInt(userList.size()));
        Long userId = -1L;
        when(userRepo.findById(userId)).thenThrow(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.updateUser(-1L, expected)).isInstanceOf(UserNotFoundException.class);
    }


    @Test
    void removeUserPositiveTest() {
        User expected = userList.get(faker.random().nextInt(userList.size()));
        when(userRepo.save(expected)).thenReturn(expected);
        userService.addUser(expected);
        when(userRepo.findById(expected.getTelegramUserId())).thenReturn(Optional.of(expected));
        assertThat(userService.removeUser(expected.getTelegramUserId())).isEqualTo(expected);
        userList.remove(expected);
        assertThat(userService.getAll()).doesNotContain(expected);
    }
    @Test
    void removeUserNegativeTest() {
        when(userRepo.findById(11L)).thenThrow(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.removeUser(11L)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsersPagination() {
        int limit = 2;
        int finalPage = 1;
        int finalSize = 2;
        List<User> list = userList.stream().limit(limit).toList();
        Page<User> page = new PageImpl<>(list);
        when(userRepo.findAll(any(Pageable.class))).thenReturn(page);
        Collection<User> actual = userService.getAllUsersPagination(finalPage, finalSize);
        assertThat(actual).isNotNull().containsExactlyInAnyOrderElementsOf(list);
        assertThat(actual.size()).isEqualTo(limit);
    }

    @Test
    void getAll() {
        when(userRepo.findAll()).thenReturn(userList);
        assertThat(userService.getAll()).isEqualTo(userList);
    }
}