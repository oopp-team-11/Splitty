package client.utils;

import client.Main;
import client.scenes.MainCtrl;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.application.Application;
import javafx.application.Platform;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EventDataHandlerTest {

    EventDataHandler handler;
    Event event;
    Participant p1;
    Participant p2;
    Expense e1;
    Expense e2;
    List<Participant> participants;
    List<Expense> expenses;
    WebsocketSessionHandler sessionMock;
    Application app;

    @BeforeAll
    public static void start(){
//        Platform.startup(()->{});
    }

    @BeforeEach
    public void setup() throws IllegalAccessException {
        event = new Event("Trap");
        setId(event, UUID.randomUUID());
        p1 = new Participant(event, "1", "2", "3", "4", "5");
        setId(p1, UUID.randomUUID());
        p2 = new Participant(event, "1", "2", "3", "4", "5");
        setId(p2, UUID.randomUUID());
        e1 = new Expense(p1, "1", 1.0);
        setId(e1, UUID.randomUUID());
        e2 = new Expense(p2, "2", 2.0);
        setId(e2, UUID.randomUUID());
        participants = new ArrayList<>();
        participants.add(p1);
        participants.add(p2);
        expenses = new ArrayList<>();
        expenses.add(e1);
        expenses.add(e2);
        this.handler = new EventDataHandler(event, participants, expenses);
        this.sessionMock = Mockito.mock(WebsocketSessionHandler.class);
        this.handler.setSessionHandler(this.sessionMock);
    }

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setId(Expense toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setId(Participant toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }



    @Test
    void noExpenseToDelete() {
        handler.getDeleteExpense(new Expense(p1, "Hype Train", 1.5));
        verify(sessionMock, times(1)).refreshExpenses();
    }
    @Test
    void noExpenseToUpdate() {
        handler.getUpdateExpense(new Expense(p1, "Hype Train", 1.5));
        verify(sessionMock, times(1)).refreshExpenses();
    }
    @Test
    void expenseAlreadyCreated() {
        handler.getCreateExpense(e1);
        verify(sessionMock, times(1)).refreshExpenses();
    }

    @Test
    void noEventToUpdate() {
        try {
            handler.setEvent(null);
        }catch (IllegalStateException ignored){}
        handler.getUpdateEvent(event);
        verify(sessionMock, times(1)).refreshEvent();
    }

    @Test
    void noParticipantForUpdate() {
        handler.getUpdateParticipant(new Participant(event, "a", "b", "c", "d", "e"));
        verify(sessionMock, times(1)).refreshParticipants();
    }

    @Test
    void participantAlreadyDeleted() {
        handler.getDeleteParticipant(new Participant(event, "a", "b", "c", "d", "e"));
        verify(sessionMock, times(1)).refreshParticipants();
    }

    @Test
    void participantAlreadyCreated() {
        handler.getCreateParticipant(p1);
        verify(sessionMock, times(1)).refreshParticipants();
    }

    @Test
    void receiveParticipantCreate() {
        var p3 = new Participant(event, "A", "B", "C", "D", "E");
        try {
            handler.getCreateParticipant(p3);
        }catch (IllegalStateException ignored){}
        assertEquals(handler.getParticipants().getLast(), p3);
    }

    @Test
    void receiveParticipantUpdate() {
        p1.setFirstName("Antihype");
        try {
            handler.getUpdateParticipant(p1);
        }catch (IllegalStateException ignored){}
        assertEquals(handler.getParticipants().getFirst().getFirstName(), p1.getFirstName());
    }

    @Test
    void receiveParticipantDelete() {
        try {
            handler.getDeleteParticipant(p1);
        }catch (IllegalStateException ignored){}
        assertEquals(1, handler.getParticipants().size());
        assertEquals(p2, handler.getParticipants().getFirst());
        assertEquals(1, handler.getExpenses().size());
        assertEquals(e2, handler.getExpenses().getFirst());
    }

    @Test
    void receiveEventUpdate() {
        event.setTitle("Antihype");
        try {
            handler.getUpdateEvent(event);
        }catch (IllegalStateException ignored){}
        assertEquals(event, handler.getEvent());
    }

    @Test
    void receiveExpenseCreate() {
        var e3 = new Expense(p2, "Antihypetrain", 3.0);
        try {
            handler.getCreateExpense(e3);
        }catch (IllegalStateException ignored){}

        assertEquals(e3, handler.getExpenses().getLast());
    }

    @Test
    void receiveExpenseUpdate() {
        e1.setTitle("Antihype");
        try {
            handler.getUpdateExpense(e1);
        }catch (IllegalStateException ignored){}
        assertEquals(e1, handler.getExpenses().getFirst());
    }

    @Test
    void receiveExpenseDelete() {
        try {
            handler.getDeleteExpense(e1);
        }catch (IllegalStateException ignored){}
        assertEquals(1, handler.getExpenses().size());
        assertEquals(e2, handler.getExpenses().getFirst());
    }

    @Test
    void setSessionHandler() {
        WebsocketSessionHandler sessionHandler = new WebsocketSessionHandler(handler,
                new MainCtrl());
        handler.setSessionHandler(sessionHandler);
        assertEquals(sessionHandler, handler.getSessionHandler());
    }

    @Test
    void checkAlreadyCreatedParticipant() {

    }
}