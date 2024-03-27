package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import commons.Participant;
import commons.ParticipantList;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class ReadParticipantsHandlerTest {
    private EventDataHandler dataHandler;
    private ReadParticipantsHandler handler;
    private StompHeaders headers;
    private MainCtrl mainCtrl;

    @BeforeEach
    void setUp() {
        mainCtrl = Mockito.mock(MainCtrl.class);
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadParticipantsHandler(dataHandler, mainCtrl);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(StatusEntity.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        ParticipantList participants = new ParticipantList();
        participants.add(new Participant());
        participants.add(new Participant());
        StatusEntity status = StatusEntity.ok(participants);
        handler.handleFrame(headers, status);
        verify(dataHandler).setParticipants(participants);
    }
}