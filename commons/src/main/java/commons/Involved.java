package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

/**
 * model for involved entity
 */
@Entity
public class Involved {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private boolean isSettled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "ExpenseToInvolved")
    @JoinColumn(name = "EXPENSE_ID")
    private Expense expense;

    @JsonIgnoreProperties(value = {"event", "madeExpenses"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTICIPANT_ID")
    private Participant participant;

    @Transient
    private UUID expenseId;

    @Transient
    private UUID participantId;

    @Transient
    private UUID invitationCode;

    /**
     * std getter
     * @return invitation code of the event
     */
    public UUID getInvitationCode() {
        return invitationCode;
    }

    /**
     * std setter
     * @param invitationCode
     */
    public void setInvitationCode(UUID invitationCode) {
        this.invitationCode = invitationCode;
    }

    /**
     * Server2DataBase constructor
     * @param isSettled
     * @param expense
     * @param participant
     */
    public Involved(boolean isSettled, Expense expense, Participant participant) {
        this.isSettled = isSettled;
        this.expense = expense;
        this.participant = participant;
        this.expenseId = expense.getId();
        this.participantId = participant.getId();
        this.invitationCode = participant.getEventId();
    }

    /**
     * empty constructor
     */
    public Involved() {

    }

    /**
     * Server2Client constructor
     * @param id
     * @param isSettled
     * @param expenseId
     * @param participantId
     * @param invitationCode
     */
    public Involved(UUID id, boolean isSettled, UUID expenseId, UUID participantId, UUID invitationCode) {
        this.id = id;
        this.isSettled = isSettled;
        this.expenseId = expenseId;
        this.participantId = participantId;
        this.invitationCode = invitationCode;
    }

    /**
     * std getter
     *
     * @return expense id
     */
    public UUID getExpenseId() {
        return expenseId;
    }

    /**
     * std setter
     *
     * @param expenseId
     */
    public void setExpenseId(UUID expenseId) {
        this.expenseId = expenseId;
    }

    /**
     * std getter
     *
     * @return participant id
     */
    public UUID getParticipantId() {
        return participantId;
    }

    /**
     * std setter
     *
     * @param participantId
     */
    public void setParticipantId(UUID participantId) {
        this.participantId = participantId;
    }

    /**
     * std getter
     *
     * @return id
     */
    public UUID getId() {
        return id;
    }

    /**
     * std getter
     *
     * @return boolean whether debt is settled
     */
    public boolean getIsSettled() {
        return isSettled;
    }

    /**
     * std getter
     *
     * @return expense
     */
    public Expense getExpense() {
        return expense;
    }

    /**
     * std getter
     *
     * @return participant
     */
    public Participant getParticipant() {
        return participant;
    }


    /**
     * std setter
     *
     * @param expense
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * std setter
     *
     * @param participant
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * std setter
     *
     * @param settled
     */
    public void setIsSettled(boolean settled) {
        isSettled = settled;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Involved involved = (Involved) obj;
        return isSettled == involved.isSettled && Objects.equals(id, involved.id) &&
                Objects.equals(expenseId, involved.expenseId) &&
                Objects.equals(participantId, involved.participantId) &&
                Objects.equals(invitationCode, involved.invitationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isSettled, expenseId, participantId, invitationCode);
    }



    @Override
    public String toString() {
        return "Involved{" +
                "id=" + id +
                ", isSettled=" + isSettled +
                ", expenseId=" + expenseId +
                ", participantId=" + participantId +
                ", invitationCode=" + invitationCode +
                '}';
    }
}
