package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import commons.Event;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for user/queue/admin/event:delete endpoint
 */
public class AdminDeleteEventHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;

    /**
     * Constructor for the AdminDeleteEventHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public AdminDeleteEventHandler(AdminDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Event.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        Event receivedEvent = (Event) payload;
        dataHandler.getDeleteEvent(receivedEvent);
    }
}
