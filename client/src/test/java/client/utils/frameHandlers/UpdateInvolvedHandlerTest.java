package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Expense;
import commons.Involved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class UpdateInvolvedHandlerTest {
    private EventDataHandler dataHandler;
    private UpdateInvolvedHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new UpdateInvolvedHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(Involved.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        Involved testInvolved = new Involved();
        handler.handleFrame(headers, testInvolved);
        verify(dataHandler).getUpdateInvolved(testInvolved);
    }
}
