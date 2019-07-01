package info.bitrich.xchangestream.poloniex2.dto;

import java.math.BigDecimal;

/**
 * Created by Micha Schena on 19.06.19.
 */
public class BalanceUpdateEvent {
    private String currencyPair;
    private String wallet;
    private BigDecimal amount;

    public BalanceUpdateEvent(String currencyPair, String wallet, BigDecimal amount) {
        this.currencyPair = currencyPair;
        this.wallet = wallet;
        this.amount = amount;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public String getWallet() {
        return wallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "BalanceUpdateEvent [currencyPair=" + currencyPair + ", wallet=" + wallet + ", amount=" + amount + "]";
    }
}
