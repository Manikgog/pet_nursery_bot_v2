package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;

@Component("/start")
public class StartAction implements Action {

    @Override
    public void handle(Update update, TelegramBot bot) {

    }

    @Override
    public void callback(Update update, TelegramBot bot) {

    }
}
