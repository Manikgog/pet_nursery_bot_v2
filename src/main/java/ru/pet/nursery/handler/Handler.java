package ru.pet.nursery.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pet.nursery.data.MessageData;

import java.io.IOException;

@Service
public class Handler {
    private final Logger logger = LoggerFactory.getLogger(Handler.class);
    private final CallbackQueryHandler callbackQueryHandler;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;
    private final ReportHandler reportHandler;

    public Handler(CallbackQueryHandler callbackQueryHandler,
                   CommandHandler commandHandler,
                   MessageHandler messageHandler,
                   ReportHandler reportHandler){
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
        this.reportHandler = reportHandler;
    }
    public void answer(Update update) throws IOException {
        // проверяем есть ли пользователь, отправивший update в базе данных
        // если нет, то добавляем его данные в базу
        logger.info("Processing update in method answer of Handler class: {}", update);

       if(update.callbackQuery() != null){

           if(MessageData.chatId_reportStatus.containsKey(update.callbackQuery().message().chat().id())){
               reportHandler.answer(update);
               return;
           }

            callbackQueryHandler.answer(update);
            return;
       }
       if(update.message() != null){

           if(MessageData.chatId_reportStatus.containsKey(update.message().chat().id())){
               reportHandler.answer(update);
               return;
           }

           Message message = update.message();
           if(message.text() != null){
               if(message.text().startsWith("/")){
                   commandHandler.answer(update);
                   return;
               }
           }
           messageHandler.answer(update);
       }
       logger.info("Неподдерживаемый update: " + update);
    }

}
