package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private Client client;
    @BeforeEach
    void initClient() {
        client = new Client("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
    }

    @Test
    void testEquals() {
        Client client2 = new Client("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertEquals(client, client2);
    }

    @Test
    void testNotEquals() {
        Client client2 = new Client("ABC-123-456",
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertNotEquals(client, client2);
    }

    @Test
    void testHashCode() {
        Client client2 = new Client("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertEquals(client.hashCode(), client2.hashCode());
    }

    @Test
    void testNotEqualsHashCode() {
        Client client2 = new Client("ABC-123-456",
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertNotEquals(client.hashCode(), client2.hashCode());
    }

    @Test
    void testHasToString() {
        String clientToString = client.toString();
        assertTrue(clientToString.contains("firstName"));
        assertTrue(clientToString.contains("lastName"));
        assertTrue(clientToString.contains("email"));
        assertTrue(clientToString.contains("iban"));
        assertTrue(clientToString.contains("bic"));
    }

    @Test
    void getId() {
        assertTrue(client.getId() >= 0);
    }

    @Test
    void getInvitationCode() {
        assertEquals("ABC-123-456", client.getInvitationCode());
    }

    @Test
    void getFirstName() {
        assertEquals("John", client.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals("Doe", client.getLastName());
    }

    @Test
    void getEmail() {
        assertEquals("j.doe@domain.com", client.getEmail());
    }

    @Test
    void getIban() {
        assertEquals("NL91 ABNA 0417 1643 00", client.getIban());
    }

    @Test
    void getBic() {
        assertEquals("ABNANL2A123", client.getBic());
    }

    @Test
    void setFirstName() {
        client.setFirstName("Joe");
        assertEquals("Joe", client.getFirstName());
    }

    @Test
    void setLastName() {
        client.setLastName("Average");
        assertEquals("Average", client.getLastName());
    }

    @Test
    void setEmail() {
        client.setEmail("j.average@gmail.com");
        assertEquals("j.average@gmail.com", client.getEmail());
    }

    @Test
    void setIban() {
        client.setIban("NL91 ABNA 1234 5678 90");
        assertEquals("NL91 ABNA 1234 5678 90", client.getIban());
    }

    @Test
    void setBic() {
        client.setBic("ABNANL2A567");
        assertEquals("ABNANL2A567", client.getBic());
    }
}