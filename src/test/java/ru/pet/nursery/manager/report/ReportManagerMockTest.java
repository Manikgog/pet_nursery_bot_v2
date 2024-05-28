package ru.pet.nursery.manager.report;

import com.fasterxml.jackson.core.TreeCodec;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Report;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.repository.AnimalRepo;
import ru.pet.nursery.repository.UserRepo;
import ru.pet.nursery.web.exception.IllegalParameterException;
import ru.pet.nursery.web.service.ReportService;
import ru.pet.nursery.web.validator.ReportValidator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.pet.nursery.data.CallbackData.*;

@ExtendWith(MockitoExtension.class)
class ReportManagerMockTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    AnswerMethodFactory answerMethodFactory;
    @Mock
    KeyboardFactory keyboardFactory;
    @Mock
    ReportValidator reportValidator;
    @Mock
    UserRepo userRepo;
    @Mock
    AnimalRepo animalRepo;
    @Mock
    ReportService reportService;
    @Mock
    TreeCodec mapper;
    @InjectMocks
    ReportManager reportManager;
    private final Faker faker = new Faker();

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    private final AnswerMethodFactory answerMethodFactory_ = new AnswerMethodFactory();

    @Test
    void answerCommand_Test() throws IOException {
        Update update = getUpdate("update_report_command.json");

        when(keyboardFactory.getInlineKeyboard(
                List.of("инструкция по отправке отчёта",
                        "фото питомца",
                        "поведение питомца",
                        "диета питомца",
                        "здоровье питомца",
                        "назад"),
                List.of(1, 1, 1, 1, 1, 1),
                List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
        )).thenReturn(keyboardFactory_.getInlineKeyboard(
                List.of("инструкция по отправке отчёта",
                        "фото питомца",
                        "поведение питомца",
                        "диета питомца",
                        "здоровье питомца",
                        "назад"),
                List.of(1, 1, 1, 1, 1, 1),
                List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START))
        );
        when(answerMethodFactory.getSendMessage(update.message().chat().id(),
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)))).thenReturn(
                answerMethodFactory_.getSendMessage(update.message().chat().id(),
                        """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("инструкция по отправке отчёта",
                                        "фото питомца",
                                        "поведение питомца",
                                        "диета питомца",
                                        "здоровье питомца",
                                        "назад"),
                                List.of(1, 1, 1, 1, 1, 1),
                                List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START))
        ));
        reportManager.answerCommand(update);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START))
                );
    }

    @Test
    void answerMessage_Test() {

    }

    @Test
    void answerCallbackQuery_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_report.json");

        when(keyboardFactory.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
                )
        ).thenReturn(keyboardFactory_.getInlineKeyboard(
                List.of("инструкция по отправке отчёта",
                        "фото питомца",
                        "поведение питомца",
                        "диета питомца",
                        "здоровье питомца",
                        "назад"),
                List.of(1, 1, 1, 1, 1, 1),
                List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
        )
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START)
                ))).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("инструкция по отправке отчёта",
                                        "фото питомца",
                                        "поведение питомца",
                                        "диета питомца",
                                        "здоровье питомца",
                                        "назад"),
                                List.of(1, 1, 1, 1, 1, 1),
                                List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START))
                ));
        reportManager.answerCallbackQuery(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Здесь Вы можете посмотреть инструкцию и отправить отчёт
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                        List.of("инструкция по отправке отчёта",
                                "фото питомца",
                                "поведение питомца",
                                "диета питомца",
                                "здоровье питомца",
                                "назад"),
                        List.of(1, 1, 1, 1, 1, 1),
                        List.of(INSTRUCTION, FOTO, BEHAVIOUR, DIET, HEALTH, START))
                );

    }



    @Test
    void answerInstruction_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_instruction.json");

        when(keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        ).thenReturn(keyboardFactory_.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Для выполнения отчёта необходимо
                        1. Сделать фотографию питомца, которого
                        вы усыновили у нас и отправить ее через бота
                        2. Описать как изменилось поведение питомца
                        и отправить через бота
                        3. Описать его диету и отправить через бота
                        4. Описать самочувствие питомца и отправить
                        через бота
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                ))).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Для выполнения отчёта необходимо
                        1. Сделать фотографию питомца, которого
                        вы усыновили у нас и отправить ее через бота
                        2. Описать как изменилось поведение питомца
                        и отправить через бота
                        3. Описать его диету и отправить через бота
                        4. Описать самочувствие питомца и отправить
                        через бота
                        """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                ));
        reportManager.answerInstruction(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Для выполнения отчёта необходимо
                        1. Сделать фотографию питомца, которого
                        вы усыновили у нас и отправить ее через бота
                        2. Описать как изменилось поведение питомца
                        и отправить через бота
                        3. Описать его диету и отправить через бота
                        4. Описать самочувствие питомца и отправить
                        через бота
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                );

    }

    @Test
    void answerPhoto_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_photo.json");
        User user = createUser(callbackQuery);

        when(userRepo.findById(any())).thenReturn(Optional.of(user));

        when(keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        ).thenReturn(keyboardFactory_.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Cфотографируйте питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                ))).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                                Cфотографируйте питомца и отправьте как обычное сообщение.
                                """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                ));
        reportManager.answerPhoto(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Cфотографируйте питомца и отправьте как обычное сообщение.
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                );
    }


    @Test
    public void answerPhoto_negativeTestByUserInNotAdopter() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_photo.json");
        User user = createUser(callbackQuery);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        Mockito.doThrow(new IllegalParameterException("Пользователь с id = " + user.getTelegramUserId() + " не усыновлял питомца"))
                .when(reportValidator).validateIsAdopter(user);

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                Вы не усыновляли нашего питомца
                """,
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Вы не усыновляли нашего питомца
                        """,
                        null)
        );
        reportManager.answerPhoto(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Вы не усыновляли нашего питомца
                        """);
    }

    @Test
    void answerHealth_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_health.json");
        User user = createUser(callbackQuery);

        when(userRepo.findById(any())).thenReturn(Optional.of(user));

        when(keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        ).thenReturn(keyboardFactory_.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Опишите здоровье питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                ))).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                                Опишите здоровье питомца и отправьте как обычное сообщение.
                                """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                ));
        reportManager.answerHealth(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Опишите здоровье питомца и отправьте как обычное сообщение.
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                );

    }


    @Test
    public void answerHealth_negativeTestByUserInNotAdopter() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_health.json");
        User user = createUser(callbackQuery);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        Mockito.doThrow(new IllegalParameterException("Пользователь с id = " + user.getTelegramUserId() + " не усыновлял питомца"))
                .when(reportValidator).validateIsAdopter(user);

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                Вы не усыновляли нашего питомца
                """,
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Вы не усыновляли нашего питомца
                        """,
                        null)
        );
        reportManager.answerHealth(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Вы не усыновляли нашего питомца
                        """);
    }

    @Test
    void answerDiet_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_diet.json");
        User user = createUser(callbackQuery);

        when(userRepo.findById(any())).thenReturn(Optional.of(user));

        when(keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        ).thenReturn(keyboardFactory_.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Опишите диету питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                ))).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                                Опишите диету питомца и отправьте как обычное сообщение.
                                """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                ));
        reportManager.answerDiet(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Опишите диету питомца и отправьте как обычное сообщение.
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                );
    }


    @Test
    public void answerDiet_negativeTestByUserInNotAdopter() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_diet.json");
        User user = createUser(callbackQuery);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        Mockito.doThrow(new IllegalParameterException("Пользователь с id = " + user.getTelegramUserId() + " не усыновлял питомца"))
                .when(reportValidator).validateIsAdopter(user);

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                Вы не усыновляли нашего питомца
                """,
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Вы не усыновляли нашего питомца
                        """,
                        null)
        );
        reportManager.answerDiet(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Вы не усыновляли нашего питомца
                        """);
    }

    @Test
    void answerBehaviour_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_behaviour.json");
        User user = createUser(callbackQuery);

        when(userRepo.findById(any())).thenReturn(Optional.of(user));

        when(keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        ).thenReturn(keyboardFactory_.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                )
        );

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                        Опишите поведение питомца и отправьте как обычное сообщение.
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("назад"),
                        List.of(1),
                        List.of(BACK_TO_REPORT_MENU)
                ))).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                                Опишите поведение питомца и отправьте как обычное сообщение.
                                """,
                        keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                ));
        reportManager.answerBehaviour(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Опишите поведение питомца и отправьте как обычное сообщение.
                        """);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(keyboardFactory_.getInlineKeyboard(
                                List.of("назад"),
                                List.of(1),
                                List.of(BACK_TO_REPORT_MENU)
                        )
                );
    }

    @Test
    public void answerBehaviour_negativeTestByUserInNotAdopter() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_behaviour.json");
        User user = createUser(callbackQuery);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        Mockito.doThrow(new IllegalParameterException("Пользователь с id = " + user.getTelegramUserId() + " не усыновлял питомца"))
                .when(reportValidator).validateIsAdopter(user);

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                Вы не усыновляли нашего питомца
                """,
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Вы не усыновляли нашего питомца
                        """,
                        null)
        );
        reportManager.answerBehaviour(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Вы не усыновляли нашего питомца
                        """);
    }

    @Test
    void answerUserIsNotAdopter_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_report.json");

        when(answerMethodFactory.getSendMessage(callbackQuery.message().chat().id(),
                """
                Вы не усыновляли нашего питомца
                """,
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(callbackQuery.message().chat().id(),
                        """
                        Вы не усыновляли нашего питомца
                        """,
                        null)
                );
        reportManager.answerUserIsNotAdopter(callbackQuery);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                """
                        Вы не усыновляли нашего питомца
                        """);
    }

    @Test
    void uploadPhotoToReport_negativeTestByUserIsNotAdopter() throws IOException {
        Update update = getUpdate("update_photo.json");
        User user = createUser(update);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalParameterException.class, () -> reportManager.uploadPhotoToReport(update));
    }

    @Test
    void uploadDietToReport_Test() throws IOException {
        Update update = getUpdate("update_diet.json");
        User user = createUser(update);
        Animal animal = createAnimal(user);
        Report report = createReport(user);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        when(animalRepo.findByUser(user)).thenReturn(List.of(animal));
        when(reportService.findByUserAndDate(user, LocalDate.now().atStartOfDay())).thenReturn(report);

        when(answerMethodFactory.getSendMessage(user.getTelegramUserId(),
                "Описание диеты вашего питомца добавлено к отчёту",
                null)).thenReturn(
                        answerMethodFactory_.getSendMessage(user.getTelegramUserId(),
                                "Описание диеты вашего питомца добавлено к отчёту",
                                null)
        );

        reportManager.uploadDietToReport(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Описание диеты вашего питомца добавлено к отчёту");
    }


    @Test
    void uploadDietToReport_negativeTestByUserIsNotAdopter() throws IOException {
        Update update = getUpdate("update_diet.json");
        User user = createUser(update);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalParameterException.class, () -> reportManager.uploadDietToReport(update));
    }



    @Test
    void uploadHealthToReport_Test() throws IOException {
        Update update = getUpdate("update_health.json");

        User user = createUser(update);
        Animal animal = createAnimal(user);
        Report report = createReport(user);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        when(animalRepo.findByUser(user)).thenReturn(List.of(animal));
        when(reportService.findByUserAndDate(user, LocalDate.now().atStartOfDay())).thenReturn(report);

        when(answerMethodFactory.getSendMessage(user.getTelegramUserId(),
                "Описание здоровья вашего питомца добавлено к отчёту",
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(user.getTelegramUserId(),
                        "Описание здоровья вашего питомца добавлено к отчёту",
                        null)
        );

        reportManager.uploadHealthToReport(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Описание здоровья вашего питомца добавлено к отчёту");
    }

    @Test
    void uploadHealthToReport_negativeTestByUserIsNotAdopter() throws IOException {
        Update update = getUpdate("update_diet.json");
        User user = createUser(update);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalParameterException.class, () -> reportManager.uploadHealthToReport(update));
    }


    @Test
    void uploadBehaviourToReport_Test() throws IOException {
        Update update = getUpdate("update_behaviour.json");

        User user = createUser(update);
        Animal animal = createAnimal(user);
        Report report = createReport(user);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));
        when(animalRepo.findByUser(user)).thenReturn(List.of(animal));
        when(reportService.findByUserAndDate(user, LocalDate.now().atStartOfDay())).thenReturn(report);

        when(answerMethodFactory.getSendMessage(user.getTelegramUserId(),
                "Описание поведения вашего питомца добавлено к отчёту",
                null)).thenReturn(
                answerMethodFactory_.getSendMessage(user.getTelegramUserId(),
                        "Описание поведения вашего питомца добавлено к отчёту",
                        null)
        );

        reportManager.uploadBehaviourToReport(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                "Описание поведения вашего питомца добавлено к отчёту");
    }


    @Test
    void uploadBehaviourToReport_negativeTestByUserIsNotAdopter() throws IOException {
        Update update = getUpdate("update_diet.json");
        User user = createUser(update);
        when(userRepo.findById(user.getTelegramUserId())).thenReturn(Optional.of(user));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalParameterException.class, () -> reportManager.uploadBehaviourToReport(update));
    }

    @Test
    void sendMessage_Test() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_report.json");
    }

    private CallbackQuery readJsonFromResource(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\report\\" + filename ;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/report/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath)
                )
        );

        return BotUtils.fromJson(json, CallbackQuery.class);
    }


    private Update getUpdate(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if(strPath.contains("\\")){
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\report\\" + filename;
        }else{
            strPath += "/src/test/resources/ru.pet.nursery/manager/report/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath))
        );
        return BotUtils.fromJson(
                json,
                Update.class
        );
    }


    private User createUser(CallbackQuery callbackQuery){
        long chatId = callbackQuery.message().chat().id();
        User user = new User();
        user.setTelegramUserId(chatId);
        user.setUserName(callbackQuery.message().chat().username());
        user.setFirstName(callbackQuery.message().chat().firstName());
        user.setLastName(callbackQuery.message().chat().lastName());
        return user;
    }

    private User createUser(Update update){
        long chatId = update.message().chat().id();
        User user = new User();
        user.setTelegramUserId(chatId);
        user.setUserName(update.message().chat().username());
        user.setFirstName(update.message().chat().firstName());
        user.setLastName(update.message().chat().lastName());
        return user;
    }

    private Animal createAnimal(User user){
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setAnimalName(faker.funnyName().name());
        animal.setAnimalType(faker.random().nextBoolean() ? AnimalType.CAT : AnimalType.DOG);
        animal.setGender(faker.random().nextBoolean() ? Gender.MALE : Gender.FEMALE);
        animal.setUser(user);
        return animal;
    }

    private Report createReport(User user){
        Report report = new Report();
        report.setUser(user);
        report.setReportDate(LocalDate.now().atStartOfDay());
        return report;
    }
}