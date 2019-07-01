package info.bitrich.xchangestream.poloniex2.dto;

import java.math.BigDecimal;

/**
 * Created by Micha Schena on 19.06.19.
 */
public class OrderUpdateEvent {
    private String tradeId;
    private BigDecimal newAmount;
    private OrderType orderType;

    public OrderUpdateEvent(String tradeId, BigDecimal newAmount, OrderType orderType) {
        this.tradeId = tradeId;
        this.newAmount = newAmount;
        this.orderType = orderType;
    }

    public String getTradeId() {
        return tradeId;
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "BalanceUpdateEvent [tradeId=" + tradeId + ", newAmount=" + newAmount + ", orderType=" + orderType + "]";
    }
}
