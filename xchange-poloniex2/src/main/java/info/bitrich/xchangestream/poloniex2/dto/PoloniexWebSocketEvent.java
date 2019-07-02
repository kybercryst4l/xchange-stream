package info.bitrich.xchangestream.poloniex2.dto;

import info.bitrich.xchangestream.core.WebSocketEvent;

/**
 * Created by Lukas Zaoralek on 11.11.17.
 */
public abstract class PoloniexWebSocketEvent extends WebSocketEvent {
    private String eventType;

    public PoloniexWebSocketEvent(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return getEventType();
    }
}
