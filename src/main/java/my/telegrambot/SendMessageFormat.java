package my.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class SendMessageFormat {
    public static SendMessage getSendMessageBaseFormat(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("HTML");
        sendMessage.disableWebPagePreview();
        sendMessage.setChatId(chatId);
        setMainButtons(sendMessage);
        return sendMessage;
    }

    public static void setMainButtons(SendMessage sendMessage) {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(MessageType.QUOTE.get()));
        keyboardRow.add(new KeyboardButton(MessageType.IMAGE.get()));
        keyboard.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
