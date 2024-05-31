package ru.pet.nursery.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.web.service.UserService;

import java.util.List;

@Tag(name = "Пользователи", description = "Эндпоинт для работы с пользователями")
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Эндопинт для добавления нового пользователя в БД, может вернуть UserNotValidException если имя пользователя и его фамилия небыли заполнены
     * @param user в тело запроса передается объект типа User
     * @return ResponseEntity в котором содержится сущность User из БД
     */

    @Operation (summary = "Добавление пользователя в БД" , responses = {
            @ApiResponse(responseCode = "200",
            description = "Добавление пользователя прошло успешно",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(
                            name = "Пользователь",
                            description = "Объект пользователя добавлен в БД"
                    )
            ))
    })
    @PostMapping
    public ResponseEntity<User> postUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.addUser(user));
    }

    /**
     * Эндпоинт для поиска пользователя по id, может вернуть UserNotFoundException если такого пользователя нет
     * @param id передается в строке запроса
     * @return ResponseEntity в котором содержится найденный объект User
     */
    @Operation(summary = "Поиск пользователя по id", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение пользователя по id прошло успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "Пользователь",
                                    description = "Объект найден"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Эндпоинт для обновления информации о пользователе, может вернуть UserNotFoundException если такого пользователя нет в БД
     * @param id РК таблицы users_table пользователя передается в строке запроса
     * @param user в теле запроса передается объект типа User
     * @return ResponseEntity в котором содержится измененный объект User
     */
    @Operation(summary = "Обновление информации о пользователе где: id - РК пользователя передается в строке запроса," +
            "User - передается в теле запроса", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновление данных о пользователе прошло успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "Пользователь",
                                    description = "Объект пользователя изменен"
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    /**
     * Эндпоинт для удаления пользователя из БД, может вернуть UserNotFoundException если пользователя с id нет
     * @param id РК таблицы users_table передается в строке запроса
     * @return ResponseEntity в котором содержится удаленный объект из БД
     */
    @Operation(summary = "Удаление пользователя из БД по id", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Удаление пользователя из таблицы users_table успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "Пользователь",
                                    description = "Объект пользователя удален"
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.removeUser(id));
    }

    /**
     * Получить всех пользователей из БД
     * @param page номер страницы, передается в параметрах запроса
     * @param pageSize количество элементов в списке
     * @return ResponseEntity в котором содержится список объектов, равное pageSize
     */
    @Operation(summary = "Получить список всех пользователей из БД постранично", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение постраничного списка выполнено",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = User.class)),
                            examples = @ExampleObject(
                                    name = "Список пользователей"
                            )
                    )
            )
    })
    @GetMapping(params = {"page","size"})
    public ResponseEntity<List<User>> paginationUserFromDb(@RequestParam ("page") int page, @RequestParam ("size") int pageSize) {
        return ResponseEntity.ok(userService.getAllUsersPagination(page, pageSize));
    }
}
