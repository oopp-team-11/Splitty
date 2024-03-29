package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.AdminDataHandler;
import commons.StatusEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Frame handler for user/queue/events:read endpoint
 */
public class ReadEventsHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the ReadEventHandler
     *
     * @param dataHandler reference to the dataHandler
     * @param mainCtrl reference to mainCtrl
     */
    public ReadEventsHandler(AdminDataHandler dataHandler, MainCtrl mainCtrl) {
        this.dataHandler = dataHandler;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return StatusEntity.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        StatusEntity status = (StatusEntity) payload;
        switch (status.getStatusCode()) {
            case OK -> {
                dataHandler.setEvents(status.getEventList());
            }
            case BAD_REQUEST -> {
                System.out.println("Server malfunctioned somehow. This should never happen.");
            }
            case NOT_FOUND -> {
                dataHandler.setEvents(new ArrayList<>());
            }
        }
    }
}
