package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /participant:delete topic
 */
public class DeleteParticipantHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the DeleteParticipantHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public DeleteParticipantHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Participant.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.getDeleteParticipant((Participant) payload);
    }
}
