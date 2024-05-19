package ru.pet.nursery.factory;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AnswerMethodFactory {
    public SendMessage getSendMessage(Long chatId,
                                      String text,
                                      InlineKeyboardMarkup keyboard){
        if(keyboard == null){
            return new SendMessage(chatId, text);
        }
        return new SendMessage(chatId, text).replyMarkup(keyboard);
    }

    public SendPhoto getSendFoto(Long chatId,
                                   byte[] photoArray,
                                      InlineKeyboardMarkup keyboard) throws IOException {

        SendPhoto sendPhoto = new SendPhoto(chatId, photoArray);
        return sendPhoto.replyMarkup(keyboard);
    }

    public EditMessageText getEditMessageText(CallbackQuery callbackQuery,
                                              String text,
                                              InlineKeyboardMarkup keyboard){
        if(keyboard == null){
            return new EditMessageText(callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(),
                    text);
        }
        return new EditMessageText(callbackQuery.message().chat().id(),
                callbackQuery.message().messageId(),
                text)
                .replyMarkup(keyboard);
    }

    public DeleteMessage getDeleteMessage(Long chatId, Integer messageId){
        return new DeleteMessage(chatId, messageId);
    }

}
