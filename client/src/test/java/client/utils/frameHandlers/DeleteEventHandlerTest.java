package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class DeleteEventHandlerTest {
    private EventDataHandler dataHandler;
    private DeleteEventHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new DeleteEventHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(Event.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        handler.handleFrame(headers, new Event());
        verify(dataHandler).getDeleteEvent();
    }
}