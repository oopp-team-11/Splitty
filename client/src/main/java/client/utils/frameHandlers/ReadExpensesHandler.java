package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import commons.StatusEntity;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /user/queue/expenses:read endpoint
 */
public class ReadExpensesHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the ReadParticipantsHandler
     *
     * @param dataHandler reference to the dataHandler
     * @param mainCtrl reference to mainCtrl
     */
    public ReadExpensesHandler(EventDataHandler dataHandler, MainCtrl mainCtrl) {
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
                boolean init = dataHandler.getExpenses() == null;
                dataHandler.setExpenses(status.getExpenseList());
                if (init)
                    mainCtrl.getSessionHandler().afterInitialExpenseRead();
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
