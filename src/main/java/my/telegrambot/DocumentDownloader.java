package my.telegrambot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDownloader {
    private static DocumentDownloader downloader;

    public Optional<Document> download(String url) {
        Optional<Document> document = Optional.empty();
        try {
            document = Optional.ofNullable(Jsoup.connect(url).get());
            log.info(String.format("Download document: %s", url));
        } catch (IOException e) {
            log.error(String.format("Exception with document downloading: %s", url), e);
        }
        return document;
    }

    public static DocumentDownloader getInstance() {
        if (downloader == null) downloader = new DocumentDownloader();
        return downloader;
    }
}
