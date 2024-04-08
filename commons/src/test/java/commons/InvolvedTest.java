package commons;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.codec.multipart.Part;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvolvedTest {

    private static void setId(Expense toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setId(Participant toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setId(Involved toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    Expense expense;
    Participant participant;

    Involved involved;

    UUID parId;

    UUID expId;

    UUID invId;

    UUID invitationCode;

    @BeforeEach
    void makeSetUp() throws IllegalAccessException {
        participant = new Participant();
        parId = UUID.randomUUID();
        setId(participant, parId);
        expense = new Expense();
        expId = UUID.randomUUID();
        setId(expense, expId);
        involved = new Involved(false, expense, participant);
        invId = UUID.randomUUID();
        setId(involved, invId);
        invitationCode = UUID.randomUUID();
        involved.setInvitationCode(invitationCode);
    }
    @Test
    void getExpenseId() {
        assertEquals(expId, involved.getExpenseId());
    }

    @Test
    void setExpenseId() {
        involved.setExpenseId(parId);
        assertEquals(parId, involved.getExpenseId());
    }

    @Test
    void getParticipantId() {
        assertEquals(parId, involved.getParticipantId());
    }

    @Test
    void setParticipantId() {
        involved.setParticipantId(expId);
        assertEquals(expId, involved.getParticipantId());
    }

    @Test
    void getId() {
        assertEquals(invId, involved.getId());
    }

    @Test
    void getIsSettled() {
        assertFalse(involved.getIsSettled());
    }

    @Test
    void getExpense() {
        assertEquals(expense, involved.getExpense());
    }

    @Test
    void getParticipant() {
        assertEquals(participant, involved.getParticipant());
    }

    @Test
    void setParticipant() {
        involved.setParticipant(participant);
        assertEquals(participant, involved.getParticipant());
    }

    @Test
    void setIsSettled() {
        involved.setIsSettled(true);
        assertTrue(involved.getIsSettled());
    }

    @Test
    void testEquals() {
        Involved involved2 = new Involved(involved.getId(), involved.getIsSettled(),
                involved.getExpenseId(), involved.getParticipantId(), invitationCode);
        try {
            setId(involved2, involved.getId());
        } catch (IllegalAccessException ignored) {}
        assertEquals(involved2, involved);
        involved2 = new Involved();
        assertNotEquals(involved2, involved);
    }

    @Test
    void testHashCode() {
        Involved involved1 = involved;
        assertEquals(involved1.hashCode(), involved.hashCode());
    }

    @Test
    void testToString() {
        Involved involved1 = involved;
        assertEquals(involved1.toString(), involved.toString());
    }
}