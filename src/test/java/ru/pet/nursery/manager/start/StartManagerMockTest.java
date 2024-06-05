package ru.pet.nursery.manager.start;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.repository.UserRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static ru.pet.nursery.enumerations.CallbackDataEnum.INFO;
import static ru.pet.nursery.enumerations.CallbackDataEnum.REPORT;
import static ru.pet.nursery.enumerations.CallbackDataEnum.VOLUNTEER;

@ExtendWith(MockitoExtension.class)
class StartManagerMockTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    AnswerMethodFactory answerMethodFactory;
    @Mock
    UserRepo userRepo;
    @Mock
    KeyboardFactory keyboardFactory;
    @InjectMocks
    StartManager startManager;

    private final KeyboardFactory keyboardFactory_ = new KeyboardFactory();
    private final AnswerMethodFactory answerMethodFactory_ = new AnswerMethodFactory();


    //update
    @Test
    void answerCommand_FirstGrittingTest() throws IOException {
        Update update = getUpdate("update_start.json");
        User user = new User();
        user.setTelegramUserId(1874598997L);
        when(userRepo.existsById(user.getTelegramUserId())).thenReturn(false);

        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        )).thenReturn(
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(
                update.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        update.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );

        startManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }

    @Test
    void answerCommand_RepeatGrittingTest() throws IOException {
        Update update = getUpdate("update_start.json");
        User user = new User();
        user.setTelegramUserId(1874598997L);
        when(userRepo.existsById(user.getTelegramUserId())).thenReturn(true);

        String answerMessage = """
                И снова здравствуй, Дорогой друг! Решил вернуться к нам!)
                Ты уже готов обзавестись пушистым питомцем?!
                Тогда выбирай приют в соответствующем разделе!
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        )).thenReturn(
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(
                update.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        update.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );

        startManager.answerCommand(update);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }

    //update
    @Test
    void answerCallbackQuery_FirstGrittingTest() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_start.json");
        User user = new User();
        user.setTelegramUserId(1874598997L);
        when(userRepo.existsById(user.getTelegramUserId())).thenReturn(false);

        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        )).thenReturn(
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(
                callbackQuery.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        callbackQuery.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );

        startManager.answerCallbackQuery(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }

    @Test
    void answerCallbackQuery_RepeatGrittingTest() throws IOException {
        CallbackQuery callbackQuery = readJsonFromResource("callback_query_start.json");
        User user = new User();
        user.setTelegramUserId(1874598997L);
        when(userRepo.existsById(user.getTelegramUserId())).thenReturn(true);

        String answerMessage = """
                И снова здравствуй, Дорогой друг! Решил вернуться к нам!)
                Ты уже готов обзавестись пушистым питомцем?!
                Тогда выбирай приют в соответствующем разделе!
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory_.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );

        when(keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        )).thenReturn(
                inlineKeyboardMarkup
        );

        when(answerMethodFactory.getSendMessage(
                callbackQuery.message().chat().id(),
                answerMessage,
                inlineKeyboardMarkup
        )).thenReturn(
                answerMethodFactory_.getSendMessage(
                        callbackQuery.message().chat().id(),
                        answerMessage,
                        inlineKeyboardMarkup
                )
        );

        startManager.answerCallbackQuery(callbackQuery);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1874598997L);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(
                answerMessage);
        Assertions.assertThat(actual.getParameters().get("reply_markup"))
                .isEqualTo(inlineKeyboardMarkup);
    }

    private Update getUpdate(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if (strPath.contains("\\")) {
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\start\\" + filename;
        } else {
            strPath += "/src/test/resources/ru.pet.nursery/manager/start/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath))
        );
        return BotUtils.fromJson(
                json,
                Update.class
        );
    }

    private CallbackQuery readJsonFromResource(String filename) throws IOException {
        String strPath = System.getProperty("user.dir");
        if (strPath.contains("\\")) {
            strPath += "\\src\\test\\resources\\ru.pet.nursery\\manager\\start\\" + filename;
        } else {
            strPath += "/src/test/resources/ru.pet.nursery/manager/start/" + filename;
        }
        String json = Files.readString(
                Paths.get(Objects.requireNonNull(strPath)
                )
        );

        return BotUtils.fromJson(json, CallbackQuery.class);
    }
}