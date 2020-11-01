package my.telegrambot;

import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.Date;

class Quote {
    private static Calendar calendar = Calendar.getInstance();
    private Element element;
    private int number;
    private String title;
    private String body;
    private String date;
    private String vote;

    public Quote(Element element) {
        this.element = element;
        setNumber();
        setTitle();
        setDate();
        setVote();
        setBody();
    }

    private void setNumber() {
        number = Integer.parseInt(element.attr("data-quote"));
    }

    private void setTitle() {
        String url = "https://bash.im/quote/" + number;
        title = "<a href=\"" + url + "\">#" + number + "</a>";
    }

    private void setDate() {
        date = element.getElementsByClass("quote__header_date").get(0).html();
    }

    private void setVote() {
        vote = "rating: " + element.getElementsByClass("quote__total").get(0).html();
    }

    private void setBody() {
        Element quote__body = element.getElementsByClass("quote__body").get(0);
        for (Element element : quote__body.children()) {
            if (!element.toString().equals("<br>")) element.remove();
        }
        body = quote__body.html().replaceAll("<br>", "");
    }

    public Date getDate() {
        String[] strings = date.split("[. :]");
        int day = Integer.parseInt(strings[0]);
        int mon = Integer.parseInt(strings[1]);
        int year = Integer.parseInt(strings[2]);
        int hour = Integer.parseInt(strings[4]);
        int min = Integer.parseInt(strings[5]);
        calendar.set(year, mon - 1, day, hour, min);
        return calendar.getTime();
    }

    @Override
    public String toString() {
        return String.join("\n", title, date, vote, body);
    }
}
