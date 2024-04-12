package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Involved;
import commons.InvolvedList;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /involved:update topic
 */
public class UpdateInvolvedHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the UpdateInvolvedHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public UpdateInvolvedHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Involvedq.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.getUpdateInvolved((Involved) payload);
    }
}
