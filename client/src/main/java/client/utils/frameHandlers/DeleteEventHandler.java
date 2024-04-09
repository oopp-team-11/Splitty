package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Event;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /event:delete topic
 */
public class DeleteEventHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the DeleteEventHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public DeleteEventHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Event.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.getDeleteEvent();
    }
}
