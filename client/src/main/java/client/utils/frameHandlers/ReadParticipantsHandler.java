package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Participant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Frame handler for /participants:read topic
 */
public class ReadParticipantsHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the ReadParticipantsHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public ReadParticipantsHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return new ParameterizedTypeReference<List<Participant>>() {}.getType();
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.setParticipants((List<Participant>) payload);
    }
}
