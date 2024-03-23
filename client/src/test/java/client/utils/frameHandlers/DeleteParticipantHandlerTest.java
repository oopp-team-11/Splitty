package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class DeleteParticipantHandlerTest {
    private EventDataHandler dataHandler;
    private DeleteParticipantHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new DeleteParticipantHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(Participant.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        Participant testParticipant = new Participant();
        handler.handleFrame(headers, testParticipant);
        verify(dataHandler).getDeleteParticipant(testParticipant);
    }
}