package ru.pet.nursery.manager;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;

public abstract class AbstractManager {
    protected final TelegramBot telegramBot;
    public AbstractManager(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public abstract void answerCommand(Update update);
    public abstract void answerMessage(Update update);
    public abstract void answerCallbackQuery(CallbackQuery callbackQuery);
}
