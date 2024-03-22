package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Expense;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /expense:delete topic
 */
public class DeleteExpenseHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the DeleteExpenseHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public DeleteExpenseHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Expense.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.getDeleteExpense((Expense) payload);
    }
}
