package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantDisplayTest {

    @Test
    void testToString() {
        ParticipantDisplay participant = new ParticipantDisplay();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        assertEquals("John Doe", participant.toString());
    }
}