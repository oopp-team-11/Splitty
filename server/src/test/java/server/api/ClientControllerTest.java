package server.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Client;
public class ClientControllerTest {
    private TestClientRepository repo;
    private ClientController sut;

    @BeforeEach
    public void setup() {
        repo = new TestClientRepository();
        sut = new ClientController(repo);
    }

    @Test
    public void cannotAddNullClient() {
        var actual = sut.add(new Client());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsed() {
        sut.add(new Client());
        repo.calledMethods.contains("save");
    }

    @Test
    public void checkClientsDetailsA() {
        sut.add(new Client("A", "B", "C", "D", "E", "F"));
        var client = repo.clients.getFirst();
        assertEquals("A", client.getInvitationCode());
    }

    @Test
    public void checkClientsDetailsB() {
        sut.add(new Client("A", "B", "C", "D", "E", "F"));
        var client = repo.clients.getFirst();
        assertEquals("B", client.getFirstName());
    }

    @Test
    public void checkClientsDetailsC() {
        sut.add(new Client("A", "B", "C", "D", "E", "F"));
        var client = repo.clients.getFirst();
        assertEquals("C", client.getLastName());
    }

    @Test
    public void checkClientsDetailsD() {
        sut.add(new Client("A", "B", "C", "D", "E", "F"));
        var client = repo.clients.getFirst();
        assertEquals("D", client.getEmail());
    }

    @Test
    public void checkClientsDetailsE() {
        sut.add(new Client("A", "B", "C", "D", "E", "F"));
        var client = repo.clients.getFirst();
        assertEquals("E", client.getIban());
    }

    @Test
    public void checkClientsDetailsF() {
        sut.add(new Client("A", "B", "C", "D", "E", "F"));
        var client = repo.clients.getFirst();
        assertEquals("F", client.getBic());
    }

    @Test
    public void checkStatusCode() {
        var actual = sut.add(new Client("A", "B", "C", "D", "E", "F"));
        assertEquals(OK, actual.getStatusCode());
    }
}
