package my.telegrambot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Sender {
    QuoteParser quoteParser = new QuoteParser();
    StripsParser stripsParser = new StripsParser();
    BashBot bot;

    public void sendText(Long chatId, String s) {
        SendMessage sendMessage = SendMessageFormat.getSendMessageBaseFormat(chatId)
                .setText(s);
        send(sendMessage, MessageType.QUOTE);
    }

    public void sendRandomQuote(Long chatId) {
        sendText(chatId, quoteParser.getRandomQuote());
    }

    public void sendStrip(Long chatId) {
        SendMessage sendMessage = SendMessageFormat.getSendMessageBaseFormat(chatId)
                .enableWebPagePreview()
                .setText(stripsParser.getRandomStrip());
        send(sendMessage, MessageType.IMAGE);
    }

    private synchronized void send(SendMessage message, MessageType messageType) {
        try {
            bot.execute(message);
            log.info(String.format("Message %s sent to: %s", messageType, message.getChatId()));
        } catch (TelegramApiException e) {
            log.warn(String.format("Exception of sending message %s to: %s", messageType, message.getChatId()), e);
        }
    }


}
