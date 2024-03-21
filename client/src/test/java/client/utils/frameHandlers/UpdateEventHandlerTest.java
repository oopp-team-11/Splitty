package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Event;
import commons.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class UpdateEventHandlerTest {
    private EventDataHandler dataHandler;
    private UpdateEventHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new UpdateEventHandler(dataHandler);
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
        verify(dataHandler).getUpdateEvent(testEvent);
    }
}