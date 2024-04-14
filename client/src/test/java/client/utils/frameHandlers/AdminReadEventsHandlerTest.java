package client.utils.frameHandlers;

import client.scenes.MainCtrl;
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

class AdminReadEventsHandlerTest {
    private AdminDataHandler dataHandler;
    private MainCtrl mainCtrl;
    private AdminReadEventsHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(AdminDataHandler.class);
        mainCtrl = Mockito.mock(MainCtrl.class);
        handler = new AdminReadEventsHandler(dataHandler, mainCtrl);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(StatusEntity.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        Event event = new Event("testEvent");
        Event event2 = new Event("testEvent2");

        EventList events = new EventList();
        events.add(event);
        events.add(event2);
        StatusEntity status = StatusEntity.ok(events);
        handler.handleFrame(headers, status);
        verify(dataHandler).setEvents(events);
    }
}