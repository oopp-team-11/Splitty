package client.utils.frameHandlers;

import client.utils.EventDataHandler;
import commons.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class ReadExpensesHandlerTest {
    private EventDataHandler dataHandler;
    private ReadExpensesHandler handler;
    private StompHeaders headers;

    @BeforeEach
    void setUp() {
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadExpensesHandler(dataHandler);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(new ParameterizedTypeReference<List<Expense>>() {}.getType(), handler.getPayloadType(headers));
    }

    @Test
    void handleFrame() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense());
        expenses.add(new Expense());
        handler.handleFrame(headers, expenses);
        verify(dataHandler).setExpenses(expenses);
    }
}