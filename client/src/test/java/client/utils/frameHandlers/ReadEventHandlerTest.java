package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import client.utils.WebsocketSessionHandler;
import commons.Event;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReadEventHandlerTest {
    private EventDataHandler dataHandler;
    private ReadEventHandler handler;
    private StompHeaders headers;
    private MainCtrl mainCtrl;
    private WebsocketSessionHandler sessionHandler;

    @BeforeEach
    void setUp() {
        mainCtrl = new MainCtrl();
        sessionHandler = Mockito.mock(WebsocketSessionHandler.class);
        mainCtrl.setSessionHandler(sessionHandler);
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadEventHandler(dataHandler, mainCtrl);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(StatusEntity.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        Event event = new Event("testEvent");
        StatusEntity status = StatusEntity.ok(event);
        handler.handleFrame(headers, status);
        verify(dataHandler).setEvent(event);
        verify(sessionHandler).afterInitialEventRead();
    }
}