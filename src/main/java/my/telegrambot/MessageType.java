package my.telegrambot;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MessageType {
    QUOTE("Quote"), IMAGE("Image"), EMPTY("");
    private final String buttonText;

    MessageType(String buttonText) {
        this.buttonText = buttonText;
    }

    private static final Map<String, MessageType> map = Arrays.stream(values()).collect(Collectors.toMap(g -> g.buttonText, g -> g));

    public String get() {
        return buttonText;
    }

    public static MessageType getButton(String string) {
        return map.getOrDefault(string, EMPTY);
    }
}
