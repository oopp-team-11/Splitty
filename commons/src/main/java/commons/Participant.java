package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.UUID;

//Adjustment

/**
 * Class that represents a participant
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String iban;
    private String bic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    @JsonBackReference
    private Event event;

    @JsonManagedReference
    @OneToMany(mappedBy = "paidBy", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Expense> madeExpenses;


    @JsonManagedReference(value = "ParticipantToInvolved")
    @OneToMany(mappedBy = "participant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Involved> involvedIn;

    @Transient
    private UUID eventId;

    /**
     * std getter
     * @return list of involved
     */
    public List<Involved> getInvolvedIn() {
        return involvedIn;
    }

    /**
     * std setter
     * @param involvedIn
     */
    public void setInvolvedIn(List<Involved> involvedIn) {
        this.involvedIn = involvedIn;
    }

    /**
     * Constructor
     */
    public Participant() {

    }

    /**
     * Constructor for sending Participant from server to client
     *
     * @param id ID of the Participant
     * @param firstName firstName of the Participant
     * @param lastName lastName of the Participant
     * @param email email of the Participant
     * @param iban iban of the Participant
     * @param bic bic of the Participant
     * @param eventId eventID of the Participant
     */
    public Participant(UUID id, String firstName, String lastName, String email, String iban, String bic,
                       UUID eventId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
        this.eventId = eventId;
    }

    /**
     * Default constructor for Participant
     *
     * @param event event the participant is participating in
     * @param firstName first name of the participant
     * @param lastName last name of the participant
     * @param email email of the participant
     * @param iban iban of the participant
     * @param bic bic of the participant
     */
    public Participant(Event event, String firstName, String lastName, String email, String iban, String bic) {
        this.event = event;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
        this.madeExpenses = new ExpenseList();
        this.eventId = event.getId();
        this.involvedIn = new InvolvedList();
    }

    /**
     * Method that adds an expense to the list of expenses
     * @param expense expense to be added
     */
    public void addExpense(Expense expense) {
        madeExpenses.add(expense);
    }

    /***
     * std getter
     * @return UUID of parent event
     */
    public UUID getEventId() {
        return eventId;
    }

    /**
     * Method that returns the id of the participant
     * @return id of the participant
     */
    public UUID getId() {
        return id;
    }

    /**
     * Method that returns the event the participant is participating in
     * @return event the participant is participating in
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Method that returns the list of expenses the participant has made
     * @return list of expenses the participant has made
     */
    public List<Expense> getMadeExpenses() {
        return madeExpenses;
    }

    /**
     * Method that returns the first name of the participant
     * @return first name of the participant
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Method that returns the last name of the participant
     * @return last name of the participant
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Method that returns the email of the participant
     * @return email of the participant
     */
    public String getEmail() {
        return email;
    }

    /**
     * Method that returns the iban of the participant
     * @return iban of the participant
     */
    public String getIban() {
        return iban;
    }

    /**
     * Method that returns the bic of the participant
     * @return bic of the participant
     */
    public String getBic() {
        return bic;
    }

    /**
     * Method that sets the first name of the participant
     * @param firstName first name of the participant
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Method that sets the last name of the participant
     * @param lastName last name of the participant
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Method that sets the email of the participant
     * @param email email of the participant
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method that sets the iban of the participant
     * @param iban iban of the participant
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Method that sets the bic of the participant
     * @param bic bic of the participant
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Participant that = (Participant) obj;

        return new EqualsBuilder().append(id, that.id).append(firstName, that.firstName)
                .append(lastName, that.lastName).append(email, that.email).append(iban, that.iban)
                .append(bic, that.bic).append(madeExpenses, that.madeExpenses)
                .append(eventId, that.eventId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(firstName)
                .append(lastName).append(email).append(iban).append(bic).append(madeExpenses)
                .append(eventId).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("iban", iban)
                .append("bic", bic)
                .append("madeExpenses", madeExpenses)
                .append("eventId", eventId)
                .toString();
    }
}
