package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static commons.StatusEntity.StatusCode.*;
import static org.junit.jupiter.api.Assertions.*;

public class StatusEntityTest {
    Event event;

    @BeforeEach
    void setUp() {
        event = new Event("event");
    }

    @Test
    void checkOk() {
        StatusEntity statusEntity = StatusEntity.ok(
                null, event, null, null, null);
        assertNull(statusEntity.getMessage());
        assertNull(statusEntity.getEventList());
        assertNull(statusEntity.getParticipantList());
        assertNull(statusEntity.getExpenseList());
        assertEquals(statusEntity.getEvent(), event);
        assertEquals(statusEntity.getStatusCode(), OK);
        assertFalse(statusEntity.isUnsolvable());
    }

    @Test
    void checkBadRequestSolvable() {
        StatusEntity statusEntity = StatusEntity.badRequest(
                null, event, null, null, null);
        assertNull(statusEntity.getMessage());
        assertNull(statusEntity.getEventList());
        assertNull(statusEntity.getParticipantList());
        assertNull(statusEntity.getExpenseList());
        assertEquals(statusEntity.getEvent(), event);
        assertEquals(statusEntity.getStatusCode(), BAD_REQUEST);
        assertFalse(statusEntity.isUnsolvable());
    }

    @Test
    void checkBadRequestUnsolvable() {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, event, null, null, null);
        assertNull(statusEntity.getMessage());
        assertNull(statusEntity.getEventList());
        assertNull(statusEntity.getParticipantList());
        assertNull(statusEntity.getExpenseList());
        assertEquals(statusEntity.getEvent(), event);
        assertEquals(statusEntity.getStatusCode(), BAD_REQUEST);
        assertTrue(statusEntity.isUnsolvable());
    }

    @Test
    void checkNotFoundSolvable() {
        StatusEntity statusEntity = StatusEntity.notFound(
                null, event, null, null, null);
        assertNull(statusEntity.getMessage());
        assertNull(statusEntity.getEventList());
        assertNull(statusEntity.getParticipantList());
        assertNull(statusEntity.getExpenseList());
        assertEquals(statusEntity.getEvent(), event);
        assertEquals(statusEntity.getStatusCode(), NOT_FOUND);
        assertFalse(statusEntity.isUnsolvable());
    }

    @Test
    void checkNotFoundUnsolvable() {
        StatusEntity statusEntity = StatusEntity.notFound(
                true, null, event, null, null, null
        );
        assertNull(statusEntity.getMessage());
        assertNull(statusEntity.getEventList());
        assertNull(statusEntity.getParticipantList());
        assertNull(statusEntity.getExpenseList());
        assertEquals(statusEntity.getEvent(), event);
        assertEquals(statusEntity.getStatusCode(), NOT_FOUND);
        assertTrue(statusEntity.isUnsolvable());
    }

    @Test
    void getStatusCode() {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, event, null, null, null);
        assertEquals(statusEntity.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void isUnsolvable()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, event, null, null, null);
        assertTrue(statusEntity.isUnsolvable());
    }

    @Test
    void getMessage()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, new String("string"), null, null, null, null);
        assertEquals(new String("string"), statusEntity.getMessage());
    }

    @Test
    void getEvent()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, event, null, null, null);
        assertEquals(event, statusEntity.getEvent());
    }

    @Test
    void getEventList()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, null, new EventList(), null, null);
        assertEquals(new EventList(), statusEntity.getEventList());
    }

    @Test
    void getParticipantList()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, null, null, new ParticipantList(), null);
        assertEquals(new ParticipantList(), statusEntity.getParticipantList());
    }

    @Test
    void getExpenseList()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, null, null, null, null, new ExpenseList());
        assertEquals(new ExpenseList(), statusEntity.getExpenseList());
    }
}
