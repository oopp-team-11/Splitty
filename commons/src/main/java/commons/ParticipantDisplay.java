package commons;

/**
 * Class to overriding participant toString for displaying in the UI.
 */
public class ParticipantDisplay extends Participant{
    @Override
    public String toString() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
