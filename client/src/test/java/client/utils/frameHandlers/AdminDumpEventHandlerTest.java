package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.AdminDataHandler;
import client.utils.EventDataHandler;
import client.utils.FileSystemUtils;
import client.utils.WebsocketSessionHandler;
import commons.Event;
import commons.EventList;
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
    private EventDataHandler eventDataHandler;
    private WebsocketSessionHandler sessionHandler;
    private MainCtrl mainCtrl;
    private File file;

    @BeforeEach
    void setUp() {
//        dataHandler = mock(AdminDataHandler.class);
        file = new File("/tmp/test.txt");
        mainCtrl = new MainCtrl();
        eventDataHandler = mock(EventDataHandler.class);
        dataHandler = new AdminDataHandler(new EventList(), sessionHandler, "", file);
        sessionHandler = new WebsocketSessionHandler(eventDataHandler, dataHandler, mainCtrl);
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
        assertEquals(file, dataHandler.getJsonDumpDir());
        StatusEntity status = StatusEntity.ok(event);
        try {
            handler.handleFrame(headers, status);
        } catch (Exception e)
        {
            assertEquals("Toolkit not initialized", e.getMessage());
            // this catch block is because of JavaFX usage in the tested method chain
            // however, this actually tests what should be tested (I think)

        }
        verify(utils).jsonDump(file, event);
    }
}