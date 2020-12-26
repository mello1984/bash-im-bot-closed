package my.telegrambot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Element;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class Quote {
    String title;
    String body;
    String date;
    String vote;

    public Quote(Element element) {
        int number = Integer.parseInt(element.attr("data-quote"));
        title = String.format("<a href=\"https://bash.im/quote/%1$d\">#%1$d</a>", number);
        date = element.getElementsByClass("quote__header_date").get(0).html();
        vote = "rating: " + element.getElementsByClass("quote__total").get(0).html();
        body = setBody(element);
    }

    private String setBody(Element element) {
        Element quote__body = element.getElementsByClass("quote__body").get(0);
        for (Element el : quote__body.children()) {
            if (!el.toString().equals("<br>")) el.remove();
        }
        return quote__body.html().replaceAll("<br>", "");
    }

    @Override
    public String toString() {
        return String.join("\n", title, date, vote, body);
    }
}
