package ru.pet.nursery.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pet.nursery.handler.Handler;

import java.io.IOException;
import java.util.List;
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final Handler handler;
    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      Handler handler){
        this.telegramBot = telegramBot;
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            //String jsonUpdate = BotUtils.toJson(update);
            logger.info("Processing update: {}", update);
            if(update != null) {
                try {
                    handler.answer(update);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
