package ru.pet.nursery.action;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

public class StartAction implements BiConsumer<Update, TelegramBot> {
    private final Logger logger = LoggerFactory.getLogger(StartAction.class);

    @Override
    public void accept(Update update, TelegramBot telegramBot) {
        logger.info("Processing update: {}", update);
        // Process your updates here
        SendMessage sendMessage = new SendMessage(update.message().from().id(),
                """
                Приветствую, Дорогой друг! Добро пожаловать!)
                Я - твой помощник по взаимодействию
                с приютами для животных города Астана.
                *********************************************
                Отправь сообщение по интересующему тебя вопросу:
                /info - Узнать информацию о приюте
                /get - Как взять животное из приюта
                /report - Прислать отчет о питомце
                /volunteer - Позвать волонтера
                """);
        telegramBot.execute(sendMessage);
    }
}
