package commons;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event persistent commons' entity.
 * Any event that involved a group of people, which have shared expenses.
 */
@Entity
public class Event {

    @JsonView(Views.UpdateInvitationsCodes.class)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @JsonView(Views.UpdateInvitationsCodes.class)
    @Column(nullable = false)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastActivity;

    @JsonManagedReference
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ParticipantList participants;

    /**
     * Event constructor for sending Event to client using WebSockets
     *
     * @param id id of the Event
     * @param title title of the Event
     * @param creationDate creationDate of the Event
     * @param lastActivity lastActivity of the Event
     */
    public Event(UUID id, String title, LocalDateTime creationDate, LocalDateTime lastActivity) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
    }

    /**
     * Default constructor for the Event entity.
     *
     * Initialises the participants list with a new ArrayList.
     * @param title The title of the event
     */
    public Event(String title) {
        this.title = title;
        this.participants = new ParticipantList();
    }

    /**
     * An empty Event constructor for object mappers.
     */
    public Event() {
    }

    /**
     * Getter for the invitationCode (i.e. id field).
     * @return Returns an invitationCode as a UUID object.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Getter for the list of Participants that are part of the Event.
     * @return Returns a List of Participant objects (i.e. children of this Event entity)
     */
    public ParticipantList getParticipants() {
        return participants;
    }

    /**
     * Adds the provided Participant to the list of Participants of this Event.
     * @param participant The Participant object that is added to the list of Participants
     */
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    /**
     * Getter for the title of this Event.
     * @return returns a String storing the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of this Event.
     * @param title A String storing a new title for this Event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the creationDate of this Event.
     * Set by the server.
     * @return Returns the creationDate stored as a LocalDateTime object.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Setter for the creationDate of this Event.
     * NOTE: ONLY TO BE USED BY THE SERVER
     * @param creationDate LocalDateTime object storing the creationDate of this Event.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Getter for the lastActivity time of this Event.
     * Set by the server.
     * @return Returns the lastActivity time stored as a LocalDateTime object.
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Setter for the lastActivity time of this Event.
     * NOTE: ONLY TO BE USED BY THE SERVER
     * @param lastActivity LocalDateTime object storing the lastActivity time of this Event.
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Event event)) return false;

        return new EqualsBuilder().append(id, event.id).append(title, event.title)
                .append(creationDate, event.creationDate).append(lastActivity, event.lastActivity)
                .append(participants, event.participants).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(title)
                .append(creationDate).append(lastActivity).append(participants).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("title", title)
                .append("creationDate", creationDate)
                .append("lastActivity", lastActivity)
                .append("participants", participants == null ? "null" : participants.toString())
                .toString();
    }
}
