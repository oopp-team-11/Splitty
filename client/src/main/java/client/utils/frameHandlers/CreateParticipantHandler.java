package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /participant:create topic
 */
public class CreateParticipantHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the CreateParticipantHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public CreateParticipantHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Participant.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.getCreateParticipant((Participant) payload);
    }
}
