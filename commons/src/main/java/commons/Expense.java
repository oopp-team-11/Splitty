package commons;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTICIPANT_ID")
    private Participant paidBy;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private double amount;

    public Expense(){}
    public Expense(Participant paidBy, String title, double amount) {
        this.paidBy = paidBy;
        this.title = title;
        this.amount = amount;
        paidBy.addExpense(this);
    }

    // Getters and setters for all the attributes
    public long getId() {
        return id;
    }

    public Participant getPaidBy() {
        return paidBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double cost) {
        this.amount = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Expense expense = (Expense) o;

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