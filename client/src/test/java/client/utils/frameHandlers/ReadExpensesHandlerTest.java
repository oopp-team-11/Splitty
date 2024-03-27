package client.utils.frameHandlers;

import client.Main;
import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import commons.Expense;
import commons.ExpenseList;
import commons.StatusEntity;
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
    private MainCtrl mainCtrl;

    @BeforeEach
    void setUp() {
        mainCtrl = Mockito.mock(MainCtrl.class);
        dataHandler = Mockito.mock(EventDataHandler.class);
        handler = new ReadExpensesHandler(dataHandler, mainCtrl);
        headers = new StompHeaders();
    }

    @Test
    void getPayloadType() {
        assertEquals(new ParameterizedTypeReference<StatusEntity>() {}.getType(),
                handler.getPayloadType(headers));
    }

    @Test
    void handleFrameOK() {
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense());
        expenses.add(new Expense());
        StatusEntity status = StatusEntity.ok(expenses);
        handler.handleFrame(headers, status);
        verify(dataHandler).setExpenses(expenses);
    }
}