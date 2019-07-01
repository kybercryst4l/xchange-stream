package info.bitrich.xchangestream.poloniex2.dto;

/**
 * Created by Micha Schena on 19.06.19.
 */
public class PoloniexWebSocketBalanceUpdateEvent extends PoloniexWebSocketEvent {
    private final BalanceUpdateEvent balanceUpdateEvent;

    public PoloniexWebSocketBalanceUpdateEvent(BalanceUpdateEvent balanceUpdateEvent) {
        super("b");
        this.balanceUpdateEvent = balanceUpdateEvent;
    }

    public BalanceUpdateEvent getBalanceUpdateEvent() {
        return balanceUpdateEvent;
    }

    @Override
    public String toString() {
        return getBalanceUpdateEvent().toString();
    }
}
