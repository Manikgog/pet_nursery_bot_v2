package ru.pet.nursery.manager;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractManager {
    protected final Logger logger = LoggerFactory.getLogger(AbstractManager.class);

    protected final TelegramBot telegramBot;
    public AbstractManager(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public abstract void answerCommand(Update update);
    public abstract void answerMessage(Update update);
    public abstract void answerCallbackQuery(CallbackQuery callbackQuery);
}
