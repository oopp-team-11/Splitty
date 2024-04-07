package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import client.utils.WebsocketSessionHandler;
import commons.Expense;
import commons.ExpenseList;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReadExpensesHandlerTest {
    private EventDataHandler dataHandler;
    private ReadExpensesHandler handler;
    private StompHeaders headers;
    private MainCtrl mainCtrl;
    private WebsocketSessionHandler sessionHandler;

    @BeforeEach
    void setUp() {
        mainCtrl = new MainCtrl();
        sessionHandler = Mockito.mock(WebsocketSessionHandler.class);
        mainCtrl.setSessionHandler(sessionHandler);
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadExpensesHandler(dataHandler, mainCtrl);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(StatusEntity.class, handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense());
        expenses.add(new Expense());
        when(dataHandler.getExpenses()).thenReturn(null);
        StatusEntity status = StatusEntity.ok(expenses);
        handler.handleFrame(headers, status);
        verify(dataHandler).setExpenses(expenses);
        verify(sessionHandler).afterInitialExpenseRead();
    }
}