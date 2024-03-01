package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;

//Adjustment
@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String iban;
    private String bic;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL, orphanRemoval = true)
    Collection<Expense> madeExpenses;

    public void addExpense(Expense expense) {
        madeExpenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        madeExpenses.remove(expense);
    }

    public Participant() {

    }

    public Participant(String firstName, String lastName, String email, String iban, String bic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    public long getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Participant participant = (Participant) o;

        return new EqualsBuilder().append(id, participant.id).append(firstName, participant.firstName).append(lastName, participant.lastName).append(email, participant.email).append(iban, participant.iban).append(bic, participant.bic).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(firstName).append(lastName).append(email).append(iban).append(bic).toHashCode();
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
                .toString();
    }
}
