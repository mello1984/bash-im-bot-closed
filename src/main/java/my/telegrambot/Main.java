package my.telegrambot;

import my.telegrambot.updater.UpdaterService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        BashBot bot = new BashBot();
        try {
            telegramBotsApi.registerBot(bot);
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException e) {
            e.printStackTrace();
        }

        Thread updaterThread = new Thread(UpdaterService.getInstance());
        updaterThread.setDaemon(true);
        updaterThread.start();
    }
}