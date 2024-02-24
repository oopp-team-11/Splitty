package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import java.time.LocalDate;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private LocalDate creationDate;

    private LocalDate lastActivity;

    @OneToMany
    private List<Participant> participants;

    @OneToMany
    private List<Expense> expenses;

    public Event(long id, String title, LocalDate creationDate, LocalDate lastActivity,
                 List<Participant> participants, List<Expense> expenses) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.participants = participants;
        this.expenses = expenses;
    }

    public Event() {
    }

    public Long getId() {
        return id;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDate lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Event event)) return false;

        return new EqualsBuilder().append(id, event.id).append(title, event.title)
                .append(creationDate, event.creationDate).append(lastActivity, event.lastActivity)
                .append(participants, event.participants).append(expenses, event.expenses).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(title)
                .append(creationDate).append(lastActivity).append(participants).append(expenses).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("creationDate", creationDate)
                .append("lastActivity", lastActivity)
                .append("participants", participants.toString())
                .append("expenses", expenses.toString())
                .toString();
    }
}
