package info.bitrich.xchangestream.poloniex2.dto;

/**
 * Created by Micha Schena on 19.06.19.
 */
public class CreatedLimitOrderEvent {
    private String currencyPair;
    private TradeEvent tradeEvent;

    public CreatedLimitOrderEvent(String currencyPair, TradeEvent tradeEvent) {
        this.currencyPair = currencyPair;
        this.tradeEvent = tradeEvent;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public TradeEvent getTradeEvent() {
        return tradeEvent;
    }

    @Override
    public String toString() {
        return "BalanceUpdateEvent [currencyPair=" + currencyPair + ", TradeEvent=" + tradeEvent.toPoloniexPublicTrade().toString() + "]";
    }
}