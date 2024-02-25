package server.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Participant;

import java.time.LocalDate;
import java.util.ArrayList;

public class EventControllerTest {
    private TestEventRepository repo;
    private EventController sut;

    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        sut = new EventController(repo);
        repo.save(new Event(1, "defunct", "B", LocalDate.now(), LocalDate.now(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void cannotAddNullParticipant() {
        var actual = sut.add("defunct", new Participant());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsed() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    public void checkParticipantsDetailsA() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        var client = repo.events.getFirst().getParticipants().getFirst();
        assertEquals("A", client.getInvitationCode());
    }

    @Test
    public void checkParticipantsDetailsB() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        var client = repo.events.getFirst().getParticipants().getFirst();
        assertEquals("B", client.getFirstName());
    }

    @Test
    public void checkParticipantsDetailsC() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        var client = repo.events.getFirst().getParticipants().getFirst();
        assertEquals("C", client.getLastName());
    }

    @Test
    public void checkParticipantsDetailsD() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        var client = repo.events.getFirst().getParticipants().getFirst();
        assertEquals("D", client.getEmail());
    }

    @Test
    public void checkParticipantsDetailsE() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        var client = repo.events.getFirst().getParticipants().getFirst();
        assertEquals("E", client.getIban());
    }

    @Test
    public void checkParticipantsDetailsF() {
        sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        var client = repo.events.getFirst().getParticipants().getFirst();
        assertEquals("F", client.getBic());
    }

    @Test
    public void checkStatusCode() {
        var actual = sut.add("defunct", new Participant("A", "B", "C", "D", "E", "F"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkNullInvitationCode() {
        var actual = sut.add(null, new Participant("A", "B", "C", "D", "E", "F"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void checkEmptyInvitationCode() {
        var actual = sut.add("", new Participant("A", "B", "C", "D", "E", "F"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void noSuchEventCreated() {
        var actual = sut.add("Trap", new Participant("A", "B", "C", "D", "E", "F"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
}