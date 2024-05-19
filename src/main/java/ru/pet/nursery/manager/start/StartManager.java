package ru.pet.nursery.manager.start;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;

import java.util.List;

import static ru.pet.nursery.data.CallbackData.*;

@Component
public class StartManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;

    public StartManager(AnswerMethodFactory answerMethodFactory,
                        KeyboardFactory keyboardFactory,
                        TelegramBot telegramBot) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }


    /**
     * Метод для отправки пользователю основного меню бота
     * @param update - объект класса Update
     */
    public void answerCommand(Update update){
        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "как усыновить", "отчёт", "связь с волонтером"),
                List.of(2, 2),
                List.of(INFO, GET, REPORT, VOLUNTEER)
        );
        var chatId = update.message().chat().id();
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId, answerMessage, inlineKeyboardMarkup);
        telegramBot.execute(sendMessage);
    }

    @Override
    public void answerMessage(Update update) {

    }


    /**
     * Метод для отправки пользователю основного меню бота
     * @param callbackQuery - объект класса CallbackQuery
     */
    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        String answerMessage = """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                """;
        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardFactory.getInlineKeyboard(
                List.of("информация", "как усыновить", "отчёт", "связь с волонтером"),
                List.of(2, 2),
                List.of(INFO, GET, REPORT, VOLUNTEER)
        );
        var chatId = callbackQuery.message().chat().id();
        SendMessage sendMessage = answerMethodFactory.getSendMessage(chatId, answerMessage, inlineKeyboardMarkup);
        telegramBot.execute(sendMessage);
    }
}
