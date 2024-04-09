package client.utils;

import commons.Event;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
    File file;
    @BeforeEach
    void setUp() throws IllegalAccessException {
        file = new File("test.txt");
        event1 = new Event("Antihype1");
        setId(event1, UUID.randomUUID());
        List<Event> events = new ArrayList<>();
        events.add(event1);
        session = mock(WebsocketSessionHandler.class);
        handler = new AdminDataHandler(events, session, "42", file);
    }

    @Test
    void getCreateEvent() throws IllegalAccessException {
        Event event2 = new Event("Antihype2");
        setId(event2, UUID.randomUUID());
        try {
            handler.getCreateEvent(event2);
        } catch (IllegalStateException ignored) {}
        assertEquals(event2, handler.getEvents().getLast());
    }

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }
    @Test
    void getUpdateEvent() throws IllegalAccessException {
        Event updating = new Event("Han Zamay");
        setId(updating, handler.getEvents().getFirst().getId());
        try {
            handler.getUpdateEvent(updating);
        } catch (IllegalStateException ignored) {}
        assertEquals(updating, handler.getEvents().getFirst());
    }

    @Test
    void getDeleteEvent() {
        try {
            handler.getDeleteEvent(handler.getEvents().getFirst());
        } catch (IllegalStateException ignored) {}
        assertEquals(0, handler.getEvents().size());
    }

    @Test
    void testGetEvents() {
        assertEquals(List.of(event1), handler.getEvents());
    }

    @Test
    void testSetEvents() {
        AdminDataHandler newHandler = new AdminDataHandler();
        newHandler.setPasscode(handler.getPasscode());
        newHandler.setSessionHandler(handler.getSessionHandler());
        try {
            newHandler.setEvents(List.of(new Event(), new Event()));
        } catch (IllegalStateException ignored) {}
        assertEquals(List.of(new Event(), new Event()), newHandler.getEvents());
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

    @Test
    void getJsonDumpDir() {
        assertEquals(file, handler.getJsonDumpDir());
    }

    @Test
    void testSetJsonDumpDir() {
        File file1 = new File("test1.txt");
        handler.setJsonDumpDir(file1);
        assertEquals(file1, handler.getJsonDumpDir());
    }

    @Test
    void setDataToNull() {
        handler.setDataToNull();
        assertNull(handler.getEvents());
        assertNull(handler.getPasscode());
    }

    @Test
    void getCreateEventExists() {
        handler.getCreateEvent(event1);
        verify(session).sendReadEvents(handler.getPasscode());
    }

    @Test
    void getUpdateEventNotExists() {
        Event event2 = new Event("Antihype2");
        try {
            setId(event2, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        handler.getUpdateEvent(event2);
        verify(session).sendReadEvents(handler.getPasscode());
    }

    @Test
    void getDeleteEventNotExists() {
        Event event2 = new Event("Antihype2");
        try {
            setId(event2, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        handler.getDeleteEvent(event2);
        verify(session).sendReadEvents(handler.getPasscode());
    }
}