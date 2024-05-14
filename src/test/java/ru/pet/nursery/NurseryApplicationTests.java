package ru.pet.nursery;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Test;


class NurseryApplicationTests {
	@Test
	void contextLoads() {
		String update_ = Constants.volunteerCommand;
		Update update = BotUtils.parseUpdate(update_);
		Message message = update.message();

		System.out.println(update);

	}

}
