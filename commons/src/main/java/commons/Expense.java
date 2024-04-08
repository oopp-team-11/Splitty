package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Expense persistent commons' entity.
 * An item or activity that has been paid by one participant, for at least one other participant.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTICIPANT_ID")
    private Participant paidBy;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private double amount;

    @JsonManagedReference(value = "ExpenseToInvolved")
    @OneToMany(mappedBy = "expense", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Involved> involveds;

    private LocalDate date;

    @Transient
    private UUID paidById;

    @Transient
    private UUID invitationCode;

    @Transient
    private double amountOwed;

    /**
     * std getter
     * @return date of the expense
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * std setter
     * @param date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * std getter
     * @return amount owed
     */
    public double getAmountOwed() {
        return amountOwed;
    }

    /**
     * std setter
     * @param amountOwed
     */
    public void setAmountOwed(double amountOwed) {
        this.amountOwed = amountOwed;
    }

    /**
     * std getter
     * @return list of involveds
     */
    public List<Involved> getInvolveds() {
        return involveds;
    }

    /**
     * Setter for the involved list
     * @param involveds
     */
    public void setInvolveds(List<Involved> involveds) {
        this.involveds = involveds;
    }

    /**
     * An empty Expense constructor for object mappers.
     */
    public Expense() {
    }

    /**
     * Expense constructor for sending an Expense object to the client
     *
     * @param id id of the Expense
     * @param title title of the Expense
     * @param amount amount paid of the Expense
     * @param paidById ID of the Participant who paid for the expense
     * @param invitationCode Event invitationCode of the Expense
     * @param date
     */
    public Expense(UUID id, String title, double amount, UUID paidById, UUID invitationCode, LocalDate date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.paidById = paidById;
        this.invitationCode = invitationCode;
        this.date = date;
    }

    /**
     * Default constructor for the Expense entity.
     *
     * @param paidBy Participant (i.e. the parent entity) who created this Expense
     * @param title String representation of the title of this Expense
     * @param amount A double storing the amount spent on this Expense
     */
    public Expense(Participant paidBy, String title, double amount) {
        this.paidBy = paidBy;
        this.title = title;
        this.amount = amount;
        this.paidById = paidBy.getId();
        this.invitationCode = paidBy.getEventId();
        this.date = LocalDate.now();
    }

    /***
     * std getter
     * @return UUID of participant who paid for expense
     */
    public UUID getPaidById() {
        return paidById;
    }

    /**
     * Setter for the PaidById used to update an expense in EventDataHandler
     * @param paidById new PaidById
     */
    public void setPaidById(UUID paidById) {
        this.paidById = paidById;
    }

    /***
     * std getter
     * @return invitation code of event
     */
    public UUID getInvitationCode() {
        return invitationCode;
    }

    /**
     * Getter for the id of this Expense.
     * @return Returns a UUID id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Getter for the Participant who created this Expense.
     * @return Returns the Participant who created this Expense
     */
    public Participant getPaidBy() {
        return paidBy;
    }

    /**
     * Sets the paidBy participant.
     * Used for updating the Expense.
     *
     * @param paidBy reference to the paidBy participant
     */
    public void setPaidBy(Participant paidBy) {
        this.paidBy = paidBy;
    }

    /**
     * Getter for the title of this Expense.
     * @return Returns a String storing the title of this Expense
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of this Expense.
     * @param title String containing a new title for this Expense
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the amount spent on this Expense.
     * @return Returns a double storing the amount spent on this Expense.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Setter for the amount spent on this Expense.
     * @param cost A double storing an edited amount spent on this Expense.
     */
    public void setAmount(double cost) {
        this.amount = cost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Expense expense = (Expense) obj;

        return new EqualsBuilder().append(amount, expense.amount).append(id, expense.id)
                .append(title, expense.title).append(paidById, expense.paidById)
                .append(invitationCode, expense.invitationCode).append(involveds, expense.involveds)
                .append(date, expense.date).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(title)
                .append(amount).append(paidById).append(invitationCode).append(involveds)
                .append(date).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("title", title)
                .append("amount", amount)
                .append("paidById", paidById)
                .append("invitationCode", invitationCode)
                .append(involveds)
                .append(date)
                .toString();
    }
}