package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class ReadParticipantsHandlerTest {
    private EventDataHandler dataHandler;
    private ReadParticipantsHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadParticipantsHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(new ParameterizedTypeReference<List<Participant>>() {}.getType(), handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant());
        participants.add(new Participant());
        handler.handleFrame(headers, participants);
        verify(dataHandler).setParticipants(participants);
    }
}