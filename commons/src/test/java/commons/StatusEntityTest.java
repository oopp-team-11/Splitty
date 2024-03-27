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
        StatusEntity statusEntity = StatusEntity.ok(event);
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
        StatusEntity statusEntity = StatusEntity.badRequest(false, event);
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
        StatusEntity statusEntity = StatusEntity.badRequest(true, event);
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
        StatusEntity statusEntity = StatusEntity.notFound(false, event);
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
        StatusEntity statusEntity = StatusEntity.notFound(true, event);
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
        StatusEntity statusEntity = StatusEntity.badRequest(true, event);
        assertEquals(statusEntity.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void isUnsolvable()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, event);
        assertTrue(statusEntity.isUnsolvable());
    }

    @Test
    void getMessage()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, new String("string"));
        assertEquals(new String("string"), statusEntity.getMessage());
    }

    @Test
    void getEvent()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, event);
        assertEquals(event, statusEntity.getEvent());
    }

    @Test
    void getEventList()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, new EventList());
        assertEquals(new EventList(), statusEntity.getEventList());
    }

    @Test
    void getParticipantList()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, new ParticipantList());
        assertEquals(new ParticipantList(), statusEntity.getParticipantList());
    }

    @Test
    void getExpenseList()
    {
        StatusEntity statusEntity = StatusEntity.badRequest(
                true, new ExpenseList());
        assertEquals(new ExpenseList(), statusEntity.getExpenseList());
    }
}
