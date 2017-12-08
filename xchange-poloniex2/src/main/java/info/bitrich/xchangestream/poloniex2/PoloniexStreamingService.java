package info.bitrich.xchangestream.poloniex2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketEvent;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketEventsTransaction;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukas Zaoralek on 10.11.17.
 */
public class PoloniexStreamingService extends JsonNettyStreamingService {
  private static final Logger LOG = LoggerFactory.getLogger(PoloniexStreamingService.class);

  private static final String HEARTBEAT = "1010";

  private final Map<String, String> subscribedChannels = new HashMap<>();
  private final Map<String, Observable<JsonNode>> subscriptions = new HashMap<>();
  private boolean isManualDisconnect = false;

  private Instant lastHeartBeat = null;

  private synchronized void setLastHeartBeat(Instant lastHeartBeat) {
    this.lastHeartBeat = lastHeartBeat;
  }

  private synchronized Instant getLastHeartBeat() {
    return lastHeartBeat;
  }

  public PoloniexStreamingService(String apiUrl) {
    super(apiUrl, Integer.MAX_VALUE);
  }

  @Override
  protected void handleMessage(JsonNode message) {
    if (message.isArray()) {
      Integer channelId = new Integer(message.get(0).toString());
      if (channelId > 0 && channelId < 1000) {
        JsonNode events = message.get(2);
        if (events.isArray()) {
          JsonNode event = events.get(0);
          if (event.get(0).toString().equals("\"i\"")) {
            if (event.get(1).has("orderBook")) {
              String currencyPair = event.get(1).get("currencyPair").asText();
              LOG.info("Register {} as {}", String.valueOf(channelId), currencyPair);
              subscribedChannels.put(String.valueOf(channelId), currencyPair);
            }
          }
        }
      }
    }

    super.handleMessage(message);
  }

  @Override
  public void messageHandler(String message) {
    LOG.debug("Received message: {}", message);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode;

    // Parse incoming message to JSON
    try {
      jsonNode = objectMapper.readTree(message);
    } catch (IOException e) {
      LOG.error("Error parsing incoming message to JSON: {}", message);
      return;
    }

    if (jsonNode.isArray() && jsonNode.size() < 3) {
      if (jsonNode.get(0).asText().equals(HEARTBEAT)) {
        setLastHeartBeat(Instant.now());
        return;
      }
      else if (jsonNode.get(0).asText().equals("1002")) return;
    }

    handleMessage(jsonNode);
  }

  @Override
  public Observable<JsonNode> subscribeChannel(String channelName, Object... args) {
    if (!channels.containsKey(channelName)) {
      Observable<JsonNode> subscription = super.subscribeChannel(channelName, args);
      subscriptions.put(channelName, subscription);
    }

    return subscriptions.get(channelName);
  }

  public Observable<PoloniexWebSocketEvent> subscribeCurrencyPairChannel(CurrencyPair currencyPair) {
    String channelName = currencyPair.counter.toString() + "_" + currencyPair.base.toString();
    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return subscribeChannel(channelName)
      .flatMapIterable(s -> {
        PoloniexWebSocketEventsTransaction transaction = mapper.readValue(s.toString(), PoloniexWebSocketEventsTransaction.class);
        return Arrays.asList(transaction.getEvents());
      }).share();
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    String strChannelId = message.get(0).asText();
    Integer channelId = new Integer(strChannelId);
    if (channelId >= 1000) return strChannelId;
    else return subscribedChannels.get(message.get(0).asText());
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    PoloniexWebSocketSubscriptionMessage subscribeMessage = new PoloniexWebSocketSubscriptionMessage("subscribe",
            channelName);

    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(subscribeMessage);
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    PoloniexWebSocketSubscriptionMessage subscribeMessage = new PoloniexWebSocketSubscriptionMessage("unsubscribe",
            channelName);

    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(subscribeMessage);
  }

  @Override
  public Completable disconnect() {
    isManualDisconnect = true;
    return super.disconnect();
  }

  @Override
  public Completable connect() {
    Completable connect = super.connect();
    connect.subscribe(this::startWebsocketHealthWatcher);
    return connect;
  }

  private void startWebsocketHealthWatcher() {
    Duration maxLag = Duration.ofSeconds(5);
    LOG.info("Starting weboscket health watcher for poloniex2");
    new Thread(() -> {
      if (getLastHeartBeat() != null && getLastHeartBeat().plus(maxLag).isBefore(Instant.now())) {
        LOG.warn("Websocket is lagging behind!");
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ignored) {}
    }).start();
  }

  @Override
  protected WebSocketClientHandler getWebSocketClientHandler(WebSocketClientHandshaker handshaker,
                                                             WebSocketClientHandler.WebSocketMessageHandler handler) {
    LOG.info("Registering Poloniex2WebSocketClientHandler");
    return new Poloniex2WebSocketClientHandler(handshaker, handler);
  }

  private class Poloniex2WebSocketClientHandler extends  WebSocketClientHandler{
    Poloniex2WebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
      super(handshaker, handler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      if (isManualDisconnect) {
        isManualDisconnect = false;
      } else {
        super.channelInactive(ctx);
        LOG.info("Reopening websocket because it was closed by the host");
        connect().blockingAwait();
        LOG.info("Resubscribing channels");
        resubscribeChannels();
      }
    }
  }
}
