package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
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
        assertEquals(new ParameterizedTypeReference<StatusEntity<List<Participant>>>() {}.getType(),
                handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant());
        participants.add(new Participant());
        StatusEntity<List<Participant>> status = StatusEntity.ok(participants);
        handler.handleFrame(headers, status);
        verify(dataHandler).setParticipants(participants);
    }
}