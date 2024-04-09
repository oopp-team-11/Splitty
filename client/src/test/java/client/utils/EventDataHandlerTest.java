package client.utils;

import client.Main;
import client.scenes.MainCtrl;
import commons.Event;
import commons.Expense;
import commons.Involved;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    Involved i1;
    Involved i2;
    Involved i3;
    Involved i4;
    List<Participant> participants;
    List<Expense> expenses;
    List<Involved> involveds1;
    List<Involved> involveds2;
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
        p1 = new Participant(event, "1", "2",  "4", "5");
        setId(p1, UUID.randomUUID());
        p2 = new Participant(event, "1", "2",  "4", "5");
        setId(p2, UUID.randomUUID());
        e1 = new Expense(p1, "1", 1.0, LocalDate.now(), null);
        setId(e1, UUID.randomUUID());
        e2 = new Expense(p2, "2", 2.0, LocalDate.now(), null);
        setId(e2, UUID.randomUUID());
        i1 = new Involved(UUID.randomUUID(), true, e1.getId(), p1.getId(), event.getId());
        i2 = new Involved(UUID.randomUUID(), false, e1.getId(), p2.getId(), event.getId());
        i3 = new Involved(UUID.randomUUID(), false, e2.getId(), p1.getId(), event.getId());
        i4 = new Involved(UUID.randomUUID(), true, e2.getId(), p2.getId(), event.getId());
        involveds1 = new ArrayList<>();
        involveds1.add(i1);
        involveds1.add(i2);
        involveds2 = new ArrayList<>();
        involveds2.add(i3);
        involveds2.add(i4);
        e1.setInvolveds(involveds1);
        e2.setInvolveds(involveds2);
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
        handler.getDeleteExpense(new Expense(p1, "Hype Train", 1.5, null, null));
        verify(sessionMock, times(1)).refreshExpenses();
    }
    @Test
    void noExpenseToUpdate() {
        handler.getUpdateExpense(new Expense(p1, "Hype Train", 1.5, null, null));
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
        handler.getUpdateParticipant(new Participant(event, "a", "b",  "d", "e"));
        verify(sessionMock, times(1)).refreshParticipants();
    }

    @Test
    void participantAlreadyDeleted() {
        handler.getDeleteParticipant(new Participant(event, "a", "b",  "d", "e"));
        verify(sessionMock, times(1)).refreshParticipants();
    }

    @Test
    void participantAlreadyCreated() {
        handler.getCreateParticipant(p1);
        verify(sessionMock, times(1)).refreshParticipants();
    }

    @Test
    void receiveParticipantCreate() {
        var p3 = new Participant(event, "A", "B",  "D", "E");
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
        assertEquals(1, e2.getInvolveds().size());
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
        var e3 = new Expense(UUID.randomUUID(), "Antihypetrain", 3.0, p2.getId(), event.getId(),
                LocalDate.now(), null);
        Involved i5 = new Involved(UUID.randomUUID(), true, e3.getId(), p2.getId(), UUID.randomUUID());
        List<Involved> involveds = new ArrayList<>();
        involveds.add(i5);
        e3.setInvolveds(involveds);
        try {
            handler.getCreateExpense(e3);
        }catch (IllegalStateException ignored){}

        assertEquals(e3, handler.getExpenses().getLast());
        assertEquals(p2, e3.getPaidBy());
        assertEquals(p2, e3.getInvolveds().getFirst().getParticipant());
    }

    @Test
    void receiveExpenseUpdate() {
        var newE1 = new Expense(e1.getId(), "Antihype", 1.0, e1.getPaidById(), e1.getInvitationCode(),
                LocalDate.now(), null);
        Involved i5 = new Involved(UUID.randomUUID(), true, newE1.getId(), p2.getId(), event.getId());
        List<Involved> involveds = new ArrayList<>();
        involveds.add(i5);
        newE1.setInvolveds(involveds);
        try {
            handler.getUpdateExpense(newE1);
        }catch (IllegalStateException ignored){}
        assertEquals(e1, handler.getExpenses().getFirst());
        assertEquals("Antihype", e1.getTitle());
        assertEquals(p1, e1.getPaidBy());
        assertEquals(p2, e1.getInvolveds().getFirst().getParticipant());
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
        WebsocketSessionHandler sessionHandler = new WebsocketSessionHandler(handler, new AdminDataHandler(),
                new MainCtrl());
        handler.setSessionHandler(sessionHandler);
        assertEquals(sessionHandler, handler.getSessionHandler());
    }

    @Test
    void assignPaidByRefresh() {
        e1.setPaidById(UUID.randomUUID());
        handler.assignParticipantsInExpense(e1);
        assertNull(handler.getExpenses());
        verify(sessionMock).refreshParticipants();
    }

    @Test
    void assignInvolvedRefresh() {
        e1.getInvolveds().getFirst().setParticipantId(UUID.randomUUID());
        handler.assignParticipantsInExpense(e1);
        assertNull(handler.getExpenses());
        verify(sessionMock).refreshParticipants();
    }

    @Test
    void setAllToNull() {
        handler.setAllToNull();
        assertNull(handler.getExpenses());
        assertNull(handler.getParticipants());
        assertNull(handler.getEvent());
    }

    @Test
    void getExpenseById() {
        assertEquals(e2, handler.getExpenseById(e2.getId()));
    }

    @Test
    void getExpenseByIdNull() {
        assertNull(handler.getExpenseById(UUID.randomUUID()));
    }

    @Test
    void getParticipantById() {
        assertEquals(p2, handler.getParticipantById(p2.getId()));
    }

    @Test
    void getParticipantByIdNull() {
        assertNull(handler.getParticipantById(UUID.randomUUID()));
    }

    @Test
    void getExpensesByParticipant() {
        List<Expense> expenses = handler.getExpensesByParticipant(p1);
        assertNotNull(expenses);
        assertEquals(1, expenses.size());
        assertEquals(e1, expenses.getFirst());
    }

    @Test
    void getExpensesByInvolvedParticipant() {
        List<Expense> expenses = handler.getExpensesByInvolvedParticipant(p1);
        assertNotNull(expenses);
        assertEquals(2, expenses.size());
        assertEquals(e1, expenses.getFirst());
        assertEquals(e2, expenses.getLast());
    }

    @Test
    void setEvent() {
        EventDataHandler handler1 = new EventDataHandler();
        handler1.setEvent(event);
        assertEquals(event, handler1.getEvent());
    }

    @Test
    void setParticipants() {
        EventDataHandler handler1 = new EventDataHandler();
        handler1.setParticipants(participants);
        assertEquals(participants, handler1.getParticipants());
    }

    @Test
    void getInvolvedById() {
        assertEquals(i1, handler.getInvolvedById(e1, i1.getId()));
    }

    @Test
    void getInvolvedByIdNull() {
        assertNull(handler.getInvolvedById(e1, UUID.randomUUID()));
    }

    @Test
    void getUpdateInvolved() {
        boolean notI1Settled = !i1.getIsSettled();
        Involved newI1 = new Involved(i1.getId(), notI1Settled, i1.getExpenseId(), i1.getParticipantId(), event.getId());
        handler.getUpdateInvolved(newI1);
        assertEquals(notI1Settled, i1.getIsSettled());
    }

    @Test
    void getUpdateInvolvedExpenseNotFound() {
        Involved newI1 = new Involved(i1.getId(), i1.getIsSettled(), UUID.randomUUID(), i1.getParticipantId(), event.getId());
        handler.getUpdateInvolved(newI1);
        verify(sessionMock).refreshExpenses();
    }

    @Test
    void getUpdateInvolvedNotFound() {
        Involved newI1 = new Involved(UUID.randomUUID(), i1.getIsSettled(), i1.getExpenseId(), i1.getParticipantId(), event.getId());
        handler.getUpdateInvolved(newI1);
        verify(sessionMock).refreshExpenses();
    }

    @Test
    void sumOfAllExpenses() {
        assertEquals(e1.getAmount() + e2.getAmount(), handler.sumOfAllExpenses());
    }
}