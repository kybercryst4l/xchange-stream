package info.bitrich.xchangestream.poloniex2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Micha Schena on 19.06.19.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class PoloniexWebSocketAccountNotificationEvents {
    public String channelId;
    public String seqId;
    public JsonNode[] jsonEvents;

    public PoloniexWebSocketEvent[] getEvents() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (jsonEvents == null) {
            return new PoloniexWebSocketEvent[0];
        }

        List<PoloniexWebSocketEvent> events = new ArrayList<>(jsonEvents.length);
        for (JsonNode jsonNode : jsonEvents) {
            String eventType = jsonNode.get(0).asText();
            if (eventType.equals("b")) {
                BalanceUpdateEvent balanceUpdateEvent = new BalanceUpdateEvent(jsonNode.get(1).asText(),
                        jsonNode.get(2).asText(),
                        new BigDecimal(jsonNode.get(3).asText()));
                PoloniexWebSocketBalanceUpdateEvent event = new PoloniexWebSocketBalanceUpdateEvent(balanceUpdateEvent);
                events.add(event);
            } else if (eventType.equals("n")) {
                String formatedDate = "";
                Instant timestamp = Instant.ofEpochSecond(jsonNode.get(6).asInt());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                formatedDate = sdf.format(Date.from(timestamp));
                TradeEvent tradeEvent = new TradeEvent(jsonNode.get(2).asText(),
                        jsonNode.get(3).asText(),
                        new BigDecimal(jsonNode.get(4).asText()),
                        new BigDecimal(jsonNode.get(5).asText()),
                        formatedDate);

                CreatedLimitOrderEvent createdLimitOrderEvent = new CreatedLimitOrderEvent(jsonNode.get(1).asText(), tradeEvent);
                PoloniexWebSocketCreatedLimitOrderEvent event = new PoloniexWebSocketCreatedLimitOrderEvent(createdLimitOrderEvent);
                events.add(event);
            } else if (eventType.equals("o")) {
                OrderUpdateEvent orderUpdateEvent = new OrderUpdateEvent(jsonNode.get(1).asText(),
                        new BigDecimal(jsonNode.get(2).asText()),
                        OrderType.fromText(jsonNode.get(3).asText()));
                PoloniexWebSocketOderUpdateEvent event = new PoloniexWebSocketOderUpdateEvent(orderUpdateEvent);
                events.add(event);
            }
        }

        return events.toArray(new PoloniexWebSocketEvent[events.size()]);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getSeqId() {
        return seqId;
    }
}