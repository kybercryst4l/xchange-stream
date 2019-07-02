package info.bitrich.xchangestream.poloniex2.dto;

import java.util.Arrays;

public enum OrderType {
    FILLED_ORDER("f"),
    CANCELLED_ORDER("c"),
    FILLED_SELF_TRADE("s");

    private String text;

    OrderType(String text) {
        this.text = text;
    }

    public static OrderType fromText(String text) {
        return Arrays.stream(values())
                .filter(o -> o.text.equalsIgnoreCase(text))
                .findFirst().get();
    }
}