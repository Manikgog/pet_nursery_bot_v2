package ru.pet.nursery.manager.start;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;
import ru.pet.nursery.repository.UserRepo;

import java.util.List;

import static ru.pet.nursery.enumerations.CallbackDataEnum.*;

@Component
public class StartManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserRepo userRepo;

    public StartManager(AnswerMethodFactory answerMethodFactory,
                        KeyboardFactory keyboardFactory,
                        TelegramBot telegramBot, UserRepo userRepo) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userRepo = userRepo;
    }


    /**
     * Метод для отправки пользователю основного меню бота
     *
     * @param update - объект класса Update
     */
    public void answerCommand(Update update) {
        if (!userRepo.existsById(update.message().chat().id())) {
            addUser(update.message().chat());
            firstGrittings(update);
        } else {
            repeatGrittings(update);
        }
    }

    /**
     * Метод для отправки пользователю основного меню бота
     *
     * @param callbackQuery - объект класса CallbackQuery
     */
    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        if (!userRepo.existsById(callbackQuery.message().chat().id())) {
            addUser(callbackQuery.message().chat());
            firstGrittings(callbackQuery);
        } else {
            repeatGrittings(callbackQuery);
        }
    }

    //приветствие update

    /**
     * Метод для отправки пользователю основного меню бота
     *
     * @param update - объект класса Update
     */
    private void firstGrittings(Update update) {
        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );
        long chatId = update.message().chat().id();
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId, answerMessage, inlineKeyboardMarkup);

        telegramBot.execute(sendMessage);
    }

    /**
     * Метод для отправки пользователю основного меню бота
     *
     * @param update - объект класса Update
     */
    private void repeatGrittings(Update update) {
        String answerMessage = """
                И снова здравствуй, Дорогой друг! Решил вернуться к нам!)
                Ты уже готов обзавестись пушистым питомцем?!
                Тогда выбирай приют в соответствующем разделе!
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );
        long chatId = update.message().chat().id();
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId, answerMessage, inlineKeyboardMarkup);

        telegramBot.execute(sendMessage);
    }

    //приветствие callbackquery

    /**
     * Метод для отправки пользователю основного меню бота
     *
     * @param callbackQuery - объект класса CallbackQuery
     */
    private void firstGrittings(CallbackQuery callbackQuery) {
        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );
        long chatId = callbackQuery.message().chat().id();
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId, answerMessage, inlineKeyboardMarkup);

        telegramBot.execute(sendMessage);
    }

    /**
     * Метод для отправки пользователю основного меню бота
     *
     * @param callbackQuery - объект класса CallbackQuery
     */
    private void repeatGrittings(CallbackQuery callbackQuery) {
        String answerMessage = """
                И снова здравствуй, Дорогой друг! Решил вернуться к нам!)
                Ты уже готов обзавестись пушистым питомцем?!
                Тогда выбирай приют в соответствующем разделе!
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "отчёт", "связь с волонтером"),
                List.of(1, 2),
                List.of(INFO.toString(), REPORT.toString(), VOLUNTEER.toString())
        );
        long chatId = callbackQuery.message().chat().id();
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId, answerMessage, inlineKeyboardMarkup);

        telegramBot.execute(sendMessage);
    }

    /**
     * Метод для создания пользователя из чата телеграм и добавления его в БД
     *
     * @param chat - объект класса Chat
     */
    private void addUser(Chat chat) {
        User addUser = User.builder()
                .telegramUserId(chat.id())
                .userName(chat.username())
                .firstName(chat.firstName())
                .lastName(chat.lastName())
                .build();
        userRepo.save(addUser);
    }
}
