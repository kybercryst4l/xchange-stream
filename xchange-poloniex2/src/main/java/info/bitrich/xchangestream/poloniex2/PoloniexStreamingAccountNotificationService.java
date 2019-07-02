package info.bitrich.xchangestream.poloniex2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingAccountNotificationService;
import info.bitrich.xchangestream.core.WebSocketEvent;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketEvent;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Lukas Zaoralek on 10.11.17.
 */
public class PoloniexStreamingAccountNotificationService implements StreamingAccountNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(PoloniexStreamingAccountNotificationService.class);

    private final PoloniexStreamingService service;

    public PoloniexStreamingAccountNotificationService(PoloniexStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<WebSocketEvent> getAccountNotifications() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Observable<PoloniexWebSocketEvent> subscribedApiChannel = service.subscribeAccountNotifications()
                .map(s -> s).share();

        return subscribedApiChannel
                .map(s -> s);
    }

    @Override
    public Observable<WebSocketEvent> getBalanceUpdates() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Observable<PoloniexWebSocketEvent> subscribedApiChannel = service.subscribeAccountNotifications()
                .filter(s -> s.getEventType().equals("b"))
                .map(s -> s).share();

        return subscribedApiChannel
                .map(s -> s);
    }

    @Override
    public Observable<WebSocketEvent> getCreatedLimitOrders() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Observable<PoloniexWebSocketEvent> subscribedApiChannel = service.subscribeAccountNotifications()
                .filter(s -> s.getEventType().equals("n"))
                .map(s -> s).share();

        return subscribedApiChannel
                .map(s -> s);
    }

    @Override
    public Observable<WebSocketEvent> getOrderUpdates() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Observable<PoloniexWebSocketEvent> subscribedApiChannel = service.subscribeAccountNotifications()
                .filter(s -> s.getEventType().equals("o"))
                .map(s -> s).share();

        return subscribedApiChannel
                .map(s -> s);
    }
}