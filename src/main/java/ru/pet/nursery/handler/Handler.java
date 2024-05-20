package ru.pet.nursery.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class Handler {
    private final CallbackQueryHandler callbackQueryHandler;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;
    public Handler(CallbackQueryHandler callbackQueryHandler,
                   CommandHandler commandHandler,
                   MessageHandler messageHandler){
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
    }
    public void answer(Update update) throws IOException {
        // проверяем есть ли пользователь, отправивший update в базе данных
        // если нет, то добавляем его данные в базу
       if(update.callbackQuery() != null){
            callbackQueryHandler.answer(update);
            return;
       }
       if(update.message() != null){
           Message message = update.message();
           if(message.text() != null){
               if(message.text().startsWith("/")){
                   commandHandler.answer(update);
                   return;
               }
           }
           messageHandler.answer(update);
       }
       log.info("Неподдерживаемый update: " + update);
    }
}
