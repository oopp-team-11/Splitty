package commons;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    private UUID shouldBeId;
    private Participant participant;
    private Participant participantEqual;
    private Participant participantNotEqual;
    private Event event;
    @BeforeEach
    void initClient() {
        event = new Event("Event");
        participant = new Participant(
                event,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        participantEqual = new Participant(
                event,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        participantNotEqual = new Participant(
                event,
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        try {
            UUID id = UUID.randomUUID();
            setId(participant, id);
            setId(participantEqual, id);
            this.shouldBeId = id;
        } catch (IllegalAccessException ignored) {}
    }

    private static void setId(Participant toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    @Test
    void testGetId() {
        assertEquals(participant.getId(), shouldBeId);
        assertEquals(participantEqual.getId(), shouldBeId);
    }

    @Test
    void testEquals() {
        assertEquals(participant, participantEqual);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(participant, participantNotEqual);
    }

    @Test
    void testHashCode() {
        assertEquals(participant.hashCode(), participantEqual.hashCode());
    }

    @Test
    void testNotEqualsHashCode() {
        assertNotEquals(participant.hashCode(), participantNotEqual.hashCode());
    }

    @Test
    void testHasToString() {
        String clientToString = participant.toString();
        assertTrue(clientToString.contains("id"));
        assertTrue(clientToString.contains("firstName"));
        assertTrue(clientToString.contains("lastName"));
        assertTrue(clientToString.contains("email"));
        assertTrue(clientToString.contains("iban"));
        assertTrue(clientToString.contains("bic"));
        assertTrue(clientToString.contains("invitationCode"));
        assertTrue(clientToString.contains("madeExpenses"));
    }

    @Test
    void getFirstName() {
        assertEquals("John", participant.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals("Doe", participant.getLastName());
    }

    @Test
    void getEmail() {
        assertEquals("j.doe@domain.com", participant.getEmail());
    }

    @Test
    void getIban() {
        assertEquals("NL91 ABNA 0417 1643 00", participant.getIban());
    }

    @Test
    void getBic() {
        assertEquals("ABNANL2A123", participant.getBic());
    }

    @Test
    void setFirstName() {
        participant.setFirstName("Joe");
        assertEquals("Joe", participant.getFirstName());
    }

    @Test
    void setLastName() {
        participant.setLastName("Average");
        assertEquals("Average", participant.getLastName());
    }

    @Test
    void setEmail() {
        participant.setEmail("j.average@gmail.com");
        assertEquals("j.average@gmail.com", participant.getEmail());
    }

    @Test
    void setIban() {
        participant.setIban("NL91 ABNA 1234 5678 90");
        assertEquals("NL91 ABNA 1234 5678 90", participant.getIban());
    }

    @Test
    void setBic() {
        participant.setBic("ABNANL2A567");
        assertEquals("ABNANL2A567", participant.getBic());
    }

    @Test
    void setEventId() {
        UUID uuid = UUID.randomUUID();
        participant.setEventId(uuid);
        assertEquals(uuid, participant.getEventId());
    }

    @Test
    void setEvent() {
        Event event = new Event("event");
        participant.setEvent(event);
        assertEquals(event, participant.getEvent());
    }

    @Test
    void addExpense() {
        Expense expense = new Expense(participant, "Expense", 69.);
        assertEquals(expense, participant.getMadeExpenses().getLast());
    }

    @Test
    void getEvent() {
        assertEquals(participant.getEvent(), event);
    }

    @Test
    void getMadeExpenses() {
        Expense expense = new Expense(participant, "Expense", 69.);
        assertEquals(expense, participant.getMadeExpenses().getFirst());
    }
}