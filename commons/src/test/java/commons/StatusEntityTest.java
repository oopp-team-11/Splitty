package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static commons.StatusCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusEntityTest {
    Event event;

    @BeforeEach
    void setUp() {
        event = new Event("event");
    }

    @Test
    void checkOk() {
        StatusEntity statusEntity = StatusEntity.ok(event);
        assertEquals(statusEntity.getBody(), event);
        assertEquals(statusEntity.getStatusCode(), OK);
        assertEquals(statusEntity.isUnsolvable(), false);
    }

    @Test
    void checkBadRequestSolvable() {
        StatusEntity statusEntity = StatusEntity.badRequest(event);
        assertEquals(statusEntity.getBody(), event);
        assertEquals(statusEntity.getStatusCode(), BAD_REQUEST);
        assertEquals(statusEntity.isUnsolvable(), false);
    }

    @Test
    void checkBadRequestUnsolvable() {
        StatusEntity statusEntity = StatusEntity.badRequest(event, true);
        assertEquals(statusEntity.getBody(), event);
        assertEquals(statusEntity.getStatusCode(), BAD_REQUEST);
        assertEquals(statusEntity.isUnsolvable(), true);
    }

    @Test
    void checkNotFoundSolvable() {
        StatusEntity statusEntity = StatusEntity.notFound(event);
        assertEquals(statusEntity.getBody(), event);
        assertEquals(statusEntity.getStatusCode(), NOT_FOUND);
        assertEquals(statusEntity.isUnsolvable(), false);
    }

    @Test
    void checkNotFoundUnsolvable() {
        StatusEntity statusEntity = StatusEntity.notFound(event, true);
        assertEquals(statusEntity.getBody(), event);
        assertEquals(statusEntity.getStatusCode(), NOT_FOUND);
        assertEquals(statusEntity.isUnsolvable(), true);
    }
}
