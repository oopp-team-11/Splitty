package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import client.utils.WebsocketSessionHandler;
import commons.Participant;
import commons.ParticipantList;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReadParticipantsHandlerTest {
    private EventDataHandler dataHandler;
    private ReadParticipantsHandler handler;
    private StompHeaders headers;
    private MainCtrl mainCtrl;
    private WebsocketSessionHandler sessionHandler;

    @BeforeEach
    void setUp() {
        mainCtrl = new MainCtrl();
        sessionHandler = Mockito.mock(WebsocketSessionHandler.class);
        mainCtrl.setSessionHandler(sessionHandler);
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
        when(dataHandler.getParticipants()).thenReturn(null);
        handler.handleFrame(headers, status);
        verify(dataHandler).setParticipants(participants);
        verify(sessionHandler).afterInitialParticipantsRead();
    }
}