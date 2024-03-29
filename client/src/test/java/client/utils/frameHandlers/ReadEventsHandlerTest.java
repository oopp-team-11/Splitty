package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.AdminDataHandler;
import client.utils.EventDataHandler;
import commons.Event;
import commons.EventList;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class ReadEventsHandlerTest {
    private AdminDataHandler dataHandler;
    private ReadEventsHandler handler;
    private StompHeaders headers;
    private MainCtrl mainCtrl;

    @BeforeEach
    void setUp() {
        mainCtrl = Mockito.mock(MainCtrl.class);
        dataHandler = Mockito.mock(AdminDataHandler.class);
        handler = new ReadEventsHandler(dataHandler, mainCtrl);
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