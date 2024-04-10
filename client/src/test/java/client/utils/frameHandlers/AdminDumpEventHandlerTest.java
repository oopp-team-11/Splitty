package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import client.utils.FileSystemUtils;
import commons.Event;
import commons.Expense;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminDumpEventHandlerTest {
    private AdminDataHandler dataHandler;
    private AdminDumpEventHandler handler;
    private StompHeaders headers;
    private FileSystemUtils utils;

    @BeforeEach
    void setUp() {
        dataHandler = mock(AdminDataHandler.class);
        handler = new AdminDumpEventHandler(dataHandler);
        headers = new StompHeaders();
        utils = mock(FileSystemUtils.class);
    }

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setFileUtils(AdminDumpEventHandler toSet, FileSystemUtils newUtils) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "utils", newUtils, true);
    }

    @Test
    void getPayloadType() {
        assertEquals(StatusEntity.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        Event event = new Event("testEvent");
        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        try {
            setFileUtils(handler, utils);
        } catch (IllegalAccessException ignored) {}
        File file = new File("/tmp/test.txt");
        when(dataHandler.getJsonDumpDir()).thenReturn(file);
        StatusEntity status = StatusEntity.ok(event);
        handler.handleFrame(headers, status);
        verify(utils).jsonDump(file, event);
    }
}