package my.telegrambot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DocumentDownloader {
    private static final Logger logger = Logger.getLogger(DocumentDownloader.class.getName());
    private static DocumentDownloader downloader;

    Optional<Document> download(String url) {
        Optional<Document> document = Optional.empty();
        try {
            document = Optional.ofNullable(Jsoup.connect(url).get());
            logger.log(Level.INFO, String.format("Download document: %s", url));
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Exception with document downloading: %s", url), e);
        }
        return document;
    }

    static DocumentDownloader getInstance() {
        if (downloader == null) downloader = new DocumentDownloader();
        return downloader;
    }

    private DocumentDownloader() {
    }
}
