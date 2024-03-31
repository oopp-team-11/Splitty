package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import commons.StatusEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AdminUpdateEventHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;

    /**
     * Constructor for the AdminReadEventsHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public AdminUpdateEventHandler(AdminDataHandler dataHandler) {
        this.dataHandler = dataHandler;
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
                  dataHandler.getUpdateEvent(status.getEvent());
            }
            case BAD_REQUEST -> {
                System.out.println("Invalid request.");
            }
            case NOT_FOUND -> {
                System.out.println("Event not found.");
            }
        }
    }
}
