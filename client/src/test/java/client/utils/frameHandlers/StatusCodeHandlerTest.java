package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;

class StatusCodeHandlerTest {
    private MainCtrl mainCtrl;
    private StatusCodeHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        mainCtrl = Mockito.mock(MainCtrl.class);
        handler = new StatusCodeHandler(mainCtrl);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(new ParameterizedTypeReference<StatusEntity<String>>() {}.getType(),
                handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        StatusEntity<String> status = StatusEntity.ok("testMessage");
        handler.handleFrame(headers, status);
        //TODO: verify(mainCtrl).notifyAboutResponse(status);
    }
}