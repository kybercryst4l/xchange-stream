package info.bitrich.xchangestream.poloniex2;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.poloniex2.dto.OrderType;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketEvent;
import org.knowm.xchange.ExchangeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoloniexAccountNotificationTest {
    private static final Logger LOG = LoggerFactory.getLogger(PoloniexAccountNotificationTest.class);

    public static void main(String[] args) {
        ExchangeSpecification exSpec = new ExchangeSpecification(PoloniexStreamingExchange.class);
        exSpec.setApiKey("api_key");
        exSpec.setSecretKey("api_secret");

        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exSpec);
        exchange.connect().blockingAwait();

        /*
        exchange.getStreamingAccountNotificationService().getAccountNotifications().subscribe(accountNotification -> {
            PoloniexWebSocketEvent events = (PoloniexWebSocketEvent) accountNotification;
            LOG.warn("ACCOUNT NOTIFICATION: {}", accountNotification);
        }, throwable -> LOG.error("ERROR in getting account notification: ", throwable));
        */

        exchange.getStreamingAccountNotificationService().getBalanceUpdates().subscribe(balanceUpdate -> {
            LOG.warn("BALANCE UPDATE: {}", balanceUpdate);
        });

        exchange.getStreamingAccountNotificationService().getCreatedLimitOrders().subscribe(createdLimitOrder -> {
            LOG.warn("CREATED LIMIT ORDER: {}", createdLimitOrder);
        });

        exchange.getStreamingAccountNotificationService().getOrderUpdates().subscribe(orderUpdate -> {
            LOG.warn("ORDER UPDATE: {}", orderUpdate);
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}