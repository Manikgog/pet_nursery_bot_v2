package ru.pet.nursery.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.repository.UserRepo;


import java.io.IOException;

@Service
@Slf4j
public class Handler {
    private final CallbackQueryHandler callbackQueryHandler;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;
    private final UserRepo userRepo;
    public Handler(CallbackQueryHandler callbackQueryHandler,
                   CommandHandler commandHandler,
                   MessageHandler messageHandler, UserRepo userRepo){
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;

        this.userRepo = userRepo;
    }
    public void answer(Update update) throws IOException {
        // проверяем есть ли пользователь, отправивший update в базе данных
        // если нет, то добавляем его данные в базу
       if(update.callbackQuery() != null){
           Long chatId = update.callbackQuery().message().chat().id();
           if (userRepo.findById(update.callbackQuery().message().chat().id()).isEmpty()) {
               User user = new User();
               user.setTelegramUserId(chatId);
               user.setUserName(update.callbackQuery().message().chat().username());
               user.setFirstName(update.callbackQuery().message().chat().firstName());
               user.setLastName(update.callbackQuery().message().chat().lastName());
               user.setPhoneNumber(update.callbackQuery().message().contact().phoneNumber());
               userRepo.save(user);
           }
           callbackQueryHandler.answer(update);
           return;
       }
       if(update.message() != null){
           Message message = update.message();
           Long chatId = update.message().chat().id();
           if (userRepo.findById(update.message().chat().id()).isEmpty()) {
               User user = new User();
               user.setTelegramUserId(chatId);
               user.setUserName(update.message().chat().username());
               user.setFirstName(update.message().chat().firstName());
               user.setLastName(update.message().chat().lastName());
               user.setPhoneNumber(update.message().contact().phoneNumber());
               userRepo.save(user);
           }
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
