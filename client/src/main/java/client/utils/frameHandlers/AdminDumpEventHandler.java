package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import client.utils.FileSystemUtils;
import commons.StatusEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for user/queue/admin/events:read endpoint
 */
public class AdminDumpEventHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;
    private final FileSystemUtils utils;

    /**
     * Constructor for the AdminReadEventsHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public AdminDumpEventHandler(AdminDataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.utils = new FileSystemUtils();
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
                utils.jsonDump(status.getEvent());
            }
            case BAD_REQUEST -> {
                System.out.println("Bad request.");
            }
            case NOT_FOUND -> {
                System.out.println("Event not found");
            }
        }
    }
}
