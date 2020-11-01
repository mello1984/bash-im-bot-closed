package my.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BashBot extends TelegramLongPollingBot {
    Sender sender;

    public BashBot() {
        sender = new Sender(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (message.equals(ButtonsType.Random.name())) sender.sendRandomQuote(chatId);
        else sender.sendText(chatId, message);
    }

    @Override
    public String getBotUsername() {
        return System.getenv("telegrambotusername");
    }

    @Override
    public String getBotToken() {
        return System.getenv("telegrambottoken");
    }
}
