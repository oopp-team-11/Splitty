package commons;

import org.springframework.http.codec.multipart.Part;

/**
 * Class to overriding participant toString for displaying in the UI.
 */
public class ParticipantDisplay {
    Participant participant;
    public ParticipantDisplay(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    @Override
    public String toString() {
        return participant.getFirstName() + " " + participant.getLastName();
    }
}
