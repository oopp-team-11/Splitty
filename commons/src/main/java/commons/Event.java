package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.Collection;


@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String invitationCode;

    @Column(nullable = false)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastActivity;

    //@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Participant> participants;

    public Event(long id, String invitationCode, String title, LocalDateTime creationDate,
                 LocalDateTime lastActivity, Collection<Participant> participants) {
        this.id = id;
        this.invitationCode = invitationCode;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.participants = participants;
    }

    public Event() {
    }

    public Long getId() {
        return id;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Collection<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Event event)) return false;

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
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("creationDate", creationDate)
                .append("lastActivity", lastActivity)
                .append("participants", participants.toString())
                .toString();
    }
}
