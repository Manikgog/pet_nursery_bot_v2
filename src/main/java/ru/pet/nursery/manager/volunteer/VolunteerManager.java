package ru.pet.nursery.manager.volunteer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.pet.nursery.factory.AnswerMethodFactory;
import ru.pet.nursery.factory.KeyboardFactory;
import ru.pet.nursery.manager.AbstractManager;

@Component
public class VolunteerManager extends AbstractManager {
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;

    public VolunteerManager(AnswerMethodFactory answerMethodFactory,
                            KeyboardFactory keyboardFactory,
                            TelegramBot telegramBot) {
        super(telegramBot);
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public void answerCommand(Update update){
        // связь с волонтером через телеграм
        String answerMessage = """
                  // связь с волонтером через телеграм
                """;
        SendMessage sendMessage = answerMethodFactory.getSendMessage(update.message().chat().id(),
                answerMessage,
                null);
        telegramBot.execute(sendMessage);
    }

    @Override
    public void answerMessage(Update update) {

    }

    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery){
        // связь с волонтером через телеграм
        String answerMessage = """
                  // связь с волонтером через телеграм
                """;
        EditMessageText editMessageText = answerMethodFactory.getEditMessageText(callbackQuery, answerMessage, null);
        telegramBot.execute(editMessageText);
    }
}
