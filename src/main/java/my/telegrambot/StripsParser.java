package my.telegrambot;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

class StripsParser {
    private static final Map<Integer, Integer> mapStrips = DataUpdater.getInstance().getMapStrips();
    private static final List<Integer> keyStrips = DataUpdater.getInstance().getKeyStrips();
    private static final Random random = new Random();

     String getRandomStrip() {
        int rnd = random.nextInt(keyStrips.size());
        int keyNumber = keyStrips.get(rnd);
        int linkNumber = mapStrips.get(keyNumber);
        String result = "";
        Optional<Document> document = DocumentDownloader.getInstance().download("https://bash.im/strip/" + linkNumber);
        if (document.isPresent()) {
            Elements elements = document.get().body().getElementsByClass("quote__img");
            String path = elements.get(0).attr("data-src");
            result = "<a href=\"https://bash.im" + path + "\">#" + keyNumber + "</a>";
        }
        return result;
    }
}