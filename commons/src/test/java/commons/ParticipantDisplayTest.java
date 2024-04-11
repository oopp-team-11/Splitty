package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantDisplayTest {

    @Test
    void getParticipant() {
        Participant participant = new Participant();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        ParticipantDisplay participantDisplay = new ParticipantDisplay(participant);
        assertEquals(participant, participantDisplay.getParticipant());
    }

    @Test
    void testToString() {
        Participant participant = new Participant();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        ParticipantDisplay participantDisplay = new ParticipantDisplay(participant);
        assertEquals("John Doe", participantDisplay.toString());
    }
}