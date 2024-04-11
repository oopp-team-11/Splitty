package commons;

/**
 * Class to overriding participant toString for displaying in the UI.
 */
public class ParticipantDisplay {
    Participant participant;

    /**
     * Constructor for ParticipantDisplay wrapper
     *
     * @param participant participant to wrap
     */
    public ParticipantDisplay(Participant participant) {
        this.participant = participant;
    }

    /**
     * getter for wrapped participant
     *
     * @return returns wrapped participant
     */
    public Participant getParticipant() {
        return participant;
    }

    @Override
    public String toString() {
        return participant.getFirstName() + " " + participant.getLastName();
    }
}
