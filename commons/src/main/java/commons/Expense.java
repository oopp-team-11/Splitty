package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

/**
 * Expense persistent commons' entity.
 * An item or activity that has been paid by one participant, for at least one other participant.
 */
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTICIPANT_ID")
    private Participant paidBy;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private double amount;

    @Transient
    private UUID participantId;

    /***
     * std getter
     * @return UUID of parent participant
     */
    public UUID getParticipantId() {
        return participantId;
    }

    /**
     * An empty Expense constructor for object mappers.
     */
    public Expense() {
    }

    /**
     * Constructor for the Expense entity.
     * @param paidBy Participant (i.e. the parent entity) who created this Expense
     * @param title String representation of the title of this Expense
     * @param amount A double storing the amount spent on this Expense
     */
    public Expense(Participant paidBy, String title, double amount) {
        this.paidBy = paidBy;
        this.title = title;
        this.amount = amount;
        paidBy.addExpense(this);
        this.participantId = paidBy.getId();
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

        return new EqualsBuilder().append(id, expense.id).append(paidBy.getId(), expense.paidBy.getId())
                .append(title, expense.title).append(amount, expense.amount).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(paidBy.getId())
                .append(title).append(amount).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("paidByID", paidBy.getId())
                .append("title", title)
                .append("amount", amount)
                .toString();
    }
}