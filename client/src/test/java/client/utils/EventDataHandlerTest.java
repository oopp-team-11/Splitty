package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventDataHandlerTest {

    EventDataHandler handler;
    Event event;
    Participant p1;
    Participant p2;
    Expense e1;
    Expense e2;
    List<Participant> participants;
    List<Expense> expenses;

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
    void receiveParticipantCreate() {
        handler.getCreateParticipant(p1);
        assertEquals(handler.getParticipants().getLast(), p1);
    }

    @Test
    void receiveParticipantUpdate() {
        p1.setFirstName("Antihype");
        handler.getUpdateParticipant(p1);
        assertEquals(handler.getParticipants().getFirst().getFirstName(), p1.getFirstName());
    }

    @Test
    void receiveParticipantDelete() {
        handler.getDeleteParticipant(p1);
        assertEquals(1, handler.getParticipants().size());
        assertEquals(p2, handler.getParticipants().getFirst());
        assertEquals(1, handler.getExpenses().size());
        assertEquals(e2, handler.getExpenses().getFirst());
    }

    @Test
    void receiveEventUpdate() {
        event.setTitle("Antihype");
        handler.getUpdateEvent(event);
        assertEquals(event, handler.getEvent());
    }

    @Test
    void receiveExpenseCreate() {
        handler.getCreateExpense(e1);
        assertEquals(e1, handler.getExpenses().getLast());
    }

    @Test
    void receiveExpenseUpdate() {
        e1.setTitle("Antihype");
        handler.getUpdateExpense(e1);
        assertEquals(e1, handler.getExpenses().getFirst());
    }

    @Test
    void receiveExpenseDelete() {
        handler.getDeleteExpense(e1);
        assertEquals(1, handler.getExpenses().size());
        assertEquals(e2, handler.getExpenses().getFirst());
    }

    @Test
    void setSessionHandler() {
        EventStompSessionHandler sessionHandler = new EventStompSessionHandler(UUID.randomUUID(), handler);
        handler.setSessionHandler(sessionHandler);
        assertEquals(sessionHandler, handler.getSessionHandler());
    }
}