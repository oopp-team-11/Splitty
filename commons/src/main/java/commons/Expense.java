package commons;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.ArrayList;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // Paid by is a long, because it will be the id of that participant.
    // You cannot have a Participant as attribute
    private long paidBy;
    private String title;
    private double cost;
    private LocalDate date;

    // Same as paidBy, this uses the id's of the participants.
    // It is a simple arraylist containing all id's of whom need to pay
    @ElementCollection
    private ArrayList<Long> toBePaidBy;

    // This is maybe used later, when we are implementing tags
    @ElementCollection
    private ArrayList<String> expenseType;

    public Expense(){}
    public Expense(long paidBy, String title, double cost, LocalDate date,
                   ArrayList<Long> toBePaidBy, ArrayList<String> expenseType) {
        this.paidBy = paidBy;
        this.title = title;
        this.cost = cost;
        this.date = date;
        this.toBePaidBy = toBePaidBy;
        this.expenseType = expenseType;
    }

    // Getters and setters for all the attributes
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(long paidBy) {
        this.paidBy = paidBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ArrayList<Long> getToBePaidBy() {
        return toBePaidBy;
    }

    public void setToBePaidBy(ArrayList<Long> toBePaidBy) {
        this.toBePaidBy = toBePaidBy;
    }

    public ArrayList<String> getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ArrayList<String> expenseType) {
        this.expenseType = expenseType;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Expense expense = (Expense) o;

        return new EqualsBuilder().append(id, expense.id).append(paidBy, expense.paidBy)
                .append(title, expense.title).append(cost, expense.cost).append(date, expense.date)
                .append(toBePaidBy, expense.toBePaidBy).append(expenseType, expense.expenseType).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(paidBy)
                .append(title).append(cost).append(date)
                .append(toBePaidBy).append(expenseType).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("paidBy", paidBy)
                .append("title", title)
                .append("cost", cost)
                .append("date", date)
                .append("toBePaidBy", toBePaidBy)
                .append("expenseType", expenseType)
                .toString();
    }
}