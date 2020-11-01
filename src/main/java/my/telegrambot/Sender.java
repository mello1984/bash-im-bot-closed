package my.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {
    private final QuoteParser quoteParser = new QuoteParser();
    private final BashBot bot;
    final Logger logger = Logger.getLogger(this.getClass().getName());


    public Sender(BashBot bot) {
        this.bot = bot;
    }

    public synchronized void sendText(Long chatId, String s) {
        SendMessage sendMessage = SendMessageFormat.getSendMessageBaseFormat(chatId)
                .setText(s);
        send(sendMessage, MessageType.QUOTE);
    }

    public synchronized void sendRandomQuote(Long chatId) {
        sendText(chatId, quoteParser.getRandomQuote());
    }

    private void send(SendMessage message, MessageType messageType) {
        try {
            bot.execute(message);
            logger.log(Level.INFO, String.format("Message %s sent to: %s", messageType, message.getChatId()));
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, String.format("Exception of sending message %s to: %s", messageType, message.getChatId()), e);
        }
    }


}
