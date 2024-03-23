package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class DeleteExpenseHandlerTest {

    private EventDataHandler dataHandler;
    private DeleteExpenseHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new DeleteExpenseHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(Expense.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        Expense testExpense = new Expense();
        handler.handleFrame(headers, testExpense);
        verify(dataHandler).getDeleteExpense(testExpense);
    }
}