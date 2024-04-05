package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import commons.Event;
import commons.StatusEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for user/queue/admin/event:create endpoint
 */
public class AdminCreateEventHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;

    /**
     * Constructor for the AdminCreateEventHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public AdminCreateEventHandler(AdminDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return StatusEntity.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        Event receivedEvent = (Event) payload;
        dataHandler.getCreateEvent(receivedEvent);
    }
}
