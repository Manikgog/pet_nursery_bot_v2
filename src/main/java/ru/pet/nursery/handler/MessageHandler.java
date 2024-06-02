package ru.pet.nursery.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

import static ru.pet.nursery.data.MessageData.ERROR_MESSAGE;


@Service
public class MessageHandler {
    private final TelegramBot telegramBot;

    public MessageHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void answer(Update update) {
        String text = update.message().text();
        if(text.equals(ERROR_MESSAGE)){
            defaultAnswer(update);
            return;
        }
        defaultAnswer(update);
    }

    private void defaultAnswer(Update update) {
        String answerMessage = """
                Неподдерживаемое сообщение.
                """;
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), answerMessage);
        telegramBot.execute(sendMessage);
    }
}
