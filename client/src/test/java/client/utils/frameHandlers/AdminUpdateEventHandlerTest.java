package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import commons.Event;
import commons.EventList;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class AdminUpdateEventHandlerTest {
    private AdminDataHandler dataHandler;
    private AdminUpdateEventHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(AdminDataHandler.class);
        handler = new AdminUpdateEventHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(StatusEntity.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        Event event = new Event("testEvent");
        handler.handleFrame(headers, event);
        verify(dataHandler).getUpdateEvent(event);
    }
}