package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class AdminCreateEventHandlerTest {
    private AdminDataHandler dataHandler;
    private AdminCreateEventHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(AdminDataHandler.class);
        handler = new AdminCreateEventHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(Event.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        Event event = new Event("testEvent");

        handler.handleFrame(headers, event);
        verify(dataHandler).getCreateEvent(event);
    }
}