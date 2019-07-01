package info.bitrich.xchangestream.core;


import io.reactivex.Observable;

/**
 * Created by Micha Schena on 19.06.19.
 */
public interface StreamingAccountNotificationService {

    Observable<WebSocketEvent> getAccountNotifications();

    Observable<WebSocketEvent> getBalanceUpdates();

    Observable<WebSocketEvent> getCreatedLimitOrders();

    Observable<WebSocketEvent> getOrderUpdates();
}
