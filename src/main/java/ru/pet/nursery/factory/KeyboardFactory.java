package ru.pet.nursery.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {
    public InlineKeyboardMarkup getInlineKeyboard(
            List<String> text,                      // надписи на кнопках
            List<Integer> rows,                     // количество кнопок в ряду
            List<String> data                       // названия команд
    ){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        int index = 0;
        for(Integer amountOfColumns : rows){
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (Integer i = 0; i < amountOfColumns; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton(text.get(index));
                button.callbackData(data.get(index));
                row.add(button);
                index++;
            }
            InlineKeyboardButton[] inlineKeyboardButtons = new InlineKeyboardButton[row.size()];
            inlineKeyboardMarkup.addRow(row.toArray(inlineKeyboardButtons));
        }
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getReplyKeyboard(
            List<String> text,                      // надписи на кнопках
            List<Integer> rows                      // количество кнопок в ряду
    ){
        String[][] keyboard = new String[rows.size()][];
        int index = 0;
        for(Integer j = 0; j < rows.size(); j++){
            keyboard[j] = new String[rows.get(j)];
            for (Integer i = 0; i < rows.get(j); i++) {
                keyboard[j][i] = text.get(index);
                index++;
            }
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        return keyboardMarkup;
    }
}
