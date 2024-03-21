package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class ReadEventHandlerTest {
    private EventDataHandler dataHandler;
    private ReadEventHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadEventHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(Event.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        Event testEvent = new Event("newTitle");
        handler.handleFrame(headers, testEvent);
        verify(dataHandler).setEvent(testEvent);
    }
}