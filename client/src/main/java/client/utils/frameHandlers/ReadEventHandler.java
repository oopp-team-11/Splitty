package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Event;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /event:read topic
 */
public class ReadEventHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the ReadEventHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public ReadEventHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Event.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.setEvent((Event) payload);
    }
}
