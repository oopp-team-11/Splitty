package client.utils;

import commons.Event;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AdminDataHandlerTest {

    AdminDataHandler handler;
    WebsocketSessionHandler session;
    Event event1;
    @BeforeEach
    void setUp() throws IllegalAccessException {
        event1 = new Event("Antihype1");
        setId(event1, UUID.randomUUID());
        List<Event> events = new ArrayList<>();
        events.add(event1);
        session = mock(WebsocketSessionHandler.class);
        handler = new AdminDataHandler(events, session, "42");
    }

    @Test
    void getCreateEvent() throws IllegalAccessException {
        Event event2 = new Event("Antihype2");
        setId(event2, UUID.randomUUID());
        handler.getCreateEvent(event2);
        assertEquals(event2, handler.getEvents().getLast());
    }

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }
    @Test
    void getUpdateEvent() throws IllegalAccessException {
        Event updating = new Event("Han Zamay");
        setId(updating, handler.getEvents().getFirst().getId());
        handler.getUpdateEvent(updating);
        assertEquals(updating, handler.getEvents().getFirst());
    }

    @Test
    void getDeleteEvent() {
        handler.getDeleteEvent(handler.getEvents().getFirst());
        assertEquals(0, handler.getEvents().size());
    }

    @Test
    void testGetEvents() {
        assertEquals(List.of(event1), handler.getEvents());
    }

    @Test
    void testSetEvents() {
        handler.setEvents(List.of(new Event(), new Event()));
        assertEquals(List.of(new Event(), new Event()), handler.getEvents());
        verify(session).subscribeToAdmin("42");
    }

    @Test
    void testGetPasscode() {
        assertEquals("42", handler.getPasscode());
    }

    @Test
    void testSetPasscode() {
        handler.setPasscode("44");
        assertEquals("44", handler.getPasscode());
    }
}