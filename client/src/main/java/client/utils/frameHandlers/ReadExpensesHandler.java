package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Expense;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Frame handler for /expenses:read topic
 */
public class ReadExpensesHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;

    /**
     * Constructor for the ReadParticipantsHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public ReadExpensesHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return new ParameterizedTypeReference<List<Expense>>() {}.getType();
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        dataHandler.setExpenses((List<Expense>) payload);
    }
}
