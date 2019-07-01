package info.bitrich.xchangestream.poloniex2.dto;

/**
 * Created by Micha Schena on 19.06.19.
 */
public class PoloniexWebSocketOderUpdateEvent extends PoloniexWebSocketEvent {
    private final OrderUpdateEvent orderUpdateEvent;

    public PoloniexWebSocketOderUpdateEvent(OrderUpdateEvent orderUpdateEvent) {
        super("o");
        this.orderUpdateEvent = orderUpdateEvent;
    }

    public OrderUpdateEvent getOrderUpdateEvent() {
        return orderUpdateEvent;
    }

    @Override
    public String toString() {
        return getOrderUpdateEvent().toString();
    }
}
