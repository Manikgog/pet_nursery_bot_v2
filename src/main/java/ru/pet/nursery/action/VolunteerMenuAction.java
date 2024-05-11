package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.function.BiConsumer;

public class VolunteerMenuAction implements BiConsumer<Update, TelegramBot> {
    @Override
    public void accept(Update update, TelegramBot telegramBot) {
        String menu = """
                Введите:
                /phone - для получения телефонов волонтеров
                /list - для получения списка животных
                """;
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), menu);
        telegramBot.execute(sendMessage);
    }
}
