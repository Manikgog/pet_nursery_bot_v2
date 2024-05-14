package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;


public interface Action {
    void handle(Update update, TelegramBot bot);

    void callback(Update update, TelegramBot bot);
}