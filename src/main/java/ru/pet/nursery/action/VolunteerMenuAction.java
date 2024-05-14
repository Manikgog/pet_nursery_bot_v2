package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.function.BiConsumer;

public class VolunteerMenuAction implements Action {

    /**
     * Метод для отправки меню команд
     * @param update - объект класса обновления
     * @param bot - объект класса TelegramBot
     */
    @Override
    public void handle(Update update, TelegramBot bot) {
        String menu = """
                Введите:
                /phone - для получения телефонов волонтеров
                /list - для получения списка животных
                """;
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), menu);
        bot.execute(sendMessage);
    }

    @Override
    public void callback(Update update, TelegramBot bot) {

    }
}
