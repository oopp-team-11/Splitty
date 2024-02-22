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
}