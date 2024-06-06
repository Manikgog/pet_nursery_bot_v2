package ru.pet.nursery.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.pet.nursery.data.MessageData;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.UserRepo;

import java.io.IOException;

@Service
public class Handler {
    private final Logger logger = LoggerFactory.getLogger(Handler.class);
    private final CallbackQueryHandler callbackQueryHandler;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;
    private final ReportHandler reportHandler;
    private final UserRepo userRepo;
    private final MessageData messageData;

    public Handler(CallbackQueryHandler callbackQueryHandler,
                   CommandHandler commandHandler,
                   MessageHandler messageHandler,
                   ReportHandler reportHandler,
                   UserRepo userRepo,
                   MessageData messageData){
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
        this.reportHandler = reportHandler;
        this.userRepo = userRepo;
        this.messageData = messageData;
    }
    public void answer(Update update) throws IOException {
        logger.info("Processing update in method answer of Handler class: {}", update);
        if(update.callbackQuery() != null){
            addUserByCallbackQuery(update.callbackQuery());
            if(messageData.chatIdReportStatus.containsKey(update.callbackQuery().message().chat().id())){
                reportHandler.answer(update);
                return;
            }

            callbackQueryHandler.answer(update);
            return;
        }
        if(update.message() != null){
            addUserByMessage(update.message());
            if(messageData.chatIdReportStatus.containsKey(update.message().chat().id())){
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

        logger.info("Неподдерживаемый update: {}", update);
    }

    private void addUserByMessage(Message message) {
        if (userRepo.findById(message.chat().id()).isEmpty()) {
            User user = new User();
            user.setTelegramUserId(message.chat().id());
            user.setFirstName(message.chat().firstName());
            user.setLastName(message.chat().lastName());
            user.setUserName(message.chat().username());
            userRepo.save(user);
        }
    }

    private void addUserByCallbackQuery(CallbackQuery callbackQuery){
        Long chatId = callbackQuery.message().chat().id();
        if (userRepo.findById(chatId).isEmpty()) {
            User user = new User();
            user.setTelegramUserId(chatId);
            user.setFirstName(callbackQuery.message().chat().firstName());
            user.setLastName(callbackQuery.message().chat().lastName());
            user.setUserName(callbackQuery.message().chat().username());
            userRepo.save(user);
        }
    }
}
