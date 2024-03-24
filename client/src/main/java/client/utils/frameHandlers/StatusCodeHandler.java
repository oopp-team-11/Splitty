package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import commons.StatusEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /user/queue/reply topic
 */
public class StatusCodeHandler implements StompFrameHandler {
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the StatusCodeHandler
     *
     * @param mainCtrl mainCtrl reference to use for notifying about received status codes
     */
    public StatusCodeHandler(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return new ParameterizedTypeReference<StatusEntity<String>>() {
        }.getType();
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        StatusEntity<String> status = (StatusEntity<String>) payload;
        //TODO: mainCtrl.notifyAboutResponse(status)
    }
}
