package client.scenes.modelWrappers;

import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * Class to overriding participant toString for displaying in the UI.
 */
public class ParticipantDisplay {
    private final Participant participant;
    @FXML
    private CheckBox checkBox;

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

    /**
     * Getter for CheckBox
     *
     * @return checkBox of Participant
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Setter for CheckBox
     *
     * @param checkBox checkbox for wrapper
     */
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    @Override
    public String toString() {
        return participant.getFirstName() + " " + participant.getLastName();
    }
}
