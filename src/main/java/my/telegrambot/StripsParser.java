package my.telegrambot;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

class StripsParser {
    private static final Random random = new Random();
    private static final List<Integer> stripsCollection = new ArrayList<>(UpdaterService.getInstance().getStripsCollection());

    String getRandomStrip() {
        int rnd = random.nextInt(stripsCollection.size());

        int linkNumber = stripsCollection.get(rnd);
        String result = "";
        Optional<Document> document = DocumentDownloader.getInstance().download("https://bash.im/strip/" + linkNumber);
        if (document.isPresent()) {
            Elements elements = document.get().body().getElementsByClass("quote__img");
            String path = elements.get(0).attr("data-src");
            result = String.format("<a href=\"https://bash.im%s\">#%d</a>", path, linkNumber);
        }
        return result;
    }
}