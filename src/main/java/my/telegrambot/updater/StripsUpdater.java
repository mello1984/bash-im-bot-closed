package my.telegrambot.updater;

import my.telegrambot.DocumentDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

public class StripsUpdater {
    static boolean updateDataBase() {
        Calendar calendar = Calendar.getInstance();
        Collection<Integer> stripsTwoLastMonth = mapWithMonthStrips(calendar);
        calendar.add(Calendar.MONTH, -1);
        stripsTwoLastMonth.addAll(mapWithMonthStrips(calendar));

        UpdaterService.getInstance().getStripsCollection().removeAll(stripsTwoLastMonth);
        boolean needUpdate = !stripsTwoLastMonth.isEmpty();

        if (needUpdate) {
            UpdaterService.getInstance().getStripsCollection().addAll(stripsTwoLastMonth);
        }
        return needUpdate;
    }

    static Collection<Integer> mapWithMonthStrips(Calendar period) {
        int urlPeriod = period.get(Calendar.YEAR) * 100 + period.get(Calendar.MONTH) + 1;
        String url = "https://bash.im/strips/" + urlPeriod;
        Optional<Document> document = DocumentDownloader.getInstance().download(url);
        Collection<Integer> collection = new ArrayList<>();
        if (document.isPresent()) {
            Elements elements = document.get().body().getElementsByClass("quote__author");
            elements.forEach(e -> {
                int value = Integer.parseInt(e.child(0).attr("href").replaceAll("/strip/", ""));
                collection.add(value);
            });
        }
        return collection;
    }
}
