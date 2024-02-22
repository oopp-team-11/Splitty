package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String invitationCode;
    private String firstName;
    private String lastName;
    private String email;
    private String iban;
    private String bic;

    public Client() {

    }

    public Client(String invitationCode, String firstName, String lastName, String email, String iban, String bic) {
        this.invitationCode = invitationCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    public long getId() {
        return id;
    }

    public String getInvitationCode() {
        return invitationCode;
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

        Client client = (Client) o;

        return new EqualsBuilder().append(id, client.id).append(invitationCode, client.invitationCode).append(firstName, client.firstName).append(lastName, client.lastName).append(email, client.email).append(iban, client.iban).append(bic, client.bic).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(invitationCode).append(firstName).append(lastName).append(email).append(iban).append(bic).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("invitationCode", invitationCode)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("iban", iban)
                .append("bic", bic)
                .toString();
    }
}
