package info.bitrich.xchangestream.poloniex2.dto;

/**
 * Created by Micha Schena on 19.06.19.
 */
public class PoloniexWebSocketCreatedLimitOrderEvent extends PoloniexWebSocketEvent {
    private final CreatedLimitOrderEvent createdLimitOrderEvent;

    public PoloniexWebSocketCreatedLimitOrderEvent(CreatedLimitOrderEvent createdLimitOrderEvent) {
        super("n");
        this.createdLimitOrderEvent = createdLimitOrderEvent;
    }

    public CreatedLimitOrderEvent getCreatedLimitOrderEvent() {
        return createdLimitOrderEvent;
    }

    @Override
    public String toString() {
        return getCreatedLimitOrderEvent().toString();
    }
}