package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import commons.Event;
import commons.StatusEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /user/queue/event:read endpoint
 */
public class ReadEventHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the ReadEventHandler
     *
     * @param dataHandler reference to the dataHandler
     * @param mainCtrl reference to mainCtrl
     */
    public ReadEventHandler(EventDataHandler dataHandler, MainCtrl mainCtrl) {
        this.dataHandler = dataHandler;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return new ParameterizedTypeReference<StatusEntity<Event>>() {
        }.getType();
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        StatusEntity<Event> status = (StatusEntity<Event>) payload;
        switch (status.getStatusCode()) {
            case OK -> {
                dataHandler.setEvent(status.getBody());
            }
            case BAD_REQUEST -> {
                System.out.println("Server did not find invitationCode in the message. This should never happen.");
            }
            case NOT_FOUND -> {
                //TODO: Fallback to start-screen
            }
        }
    }
}
