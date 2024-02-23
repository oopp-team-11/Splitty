package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    private Participant participant;
    @BeforeEach
    void initClient() {
        participant = new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
    }

    @Test
    void testEquals() {
        Participant participant2 = new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertEquals(participant, participant2);
    }

    @Test
    void testNotEquals() {
        Participant participant2 = new Participant("ABC-123-456",
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertNotEquals(participant, participant2);
    }

    @Test
    void testHashCode() {
        Participant participant2 = new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertEquals(participant.hashCode(), participant2.hashCode());
    }

    @Test
    void testNotEqualsHashCode() {
        Participant participant2 = new Participant("ABC-123-456",
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertNotEquals(participant.hashCode(), participant2.hashCode());
    }

    @Test
    void testHasToString() {
        String clientToString = participant.toString();
        assertTrue(clientToString.contains("firstName"));
        assertTrue(clientToString.contains("lastName"));
        assertTrue(clientToString.contains("email"));
        assertTrue(clientToString.contains("iban"));
        assertTrue(clientToString.contains("bic"));
    }

    @Test
    void getId() {
        assertTrue(participant.getId() >= 0);
    }

    @Test
    void getInvitationCode() {
        assertEquals("ABC-123-456", participant.getInvitationCode());
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
}