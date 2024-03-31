package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import commons.StatusEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class AdminDeleteEventHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;

    /**
     * Constructor for the AdminReadEventsHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public AdminDeleteEventHandler(AdminDataHandler dataHandler) {
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
//                dataHandler.setEvents(status.getEventList());
            }
            case BAD_REQUEST -> {
//                System.out.println("Server malfunctioned somehow. This should never happen.");
            }
            case NOT_FOUND -> {
//                dataHandler.setEvents(new ArrayList<>());
            }
        }
    }
}
