package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /participant:update topic
 */
public class UpdateParticipantHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the UpdateParticipantHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public UpdateParticipantHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Participant.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.getUpdateParticipant((Participant) payload);
    }
}
