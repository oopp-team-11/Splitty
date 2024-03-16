package server.api;

import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

public class ParticipantControllerTest {

    private ParticipantRepository participantRepository;

    private EventRepository eventRepository;

    private ParticipantController participantController;

    @BeforeEach
    public void setup() {
        participantRepository = new TestParticipantRepository();
        eventRepository = new TestEventRepository();
        participantController = new ParticipantController(participantRepository, eventRepository);
    }

    @Test
    public void cannotAddNullInvitationCodeTest(){
        var returnValue = participantController.createParticipant(null, null,
                null, null, null, null);
        assertEquals(NOT_FOUND, returnValue.getStatusCode());
    }

    @Test
    public void cannotDeleteNonExistingParticipant(){
        var returnValue = participantController.deleteParticipant(null);
        assertEquals(NOT_FOUND, returnValue.getStatusCode());
    }

    @Test
    public void cannotUpdateNonExistingParticipant(){
        var returnValue = participantController.updateParticipant(null, null,
                null, null, null, null);
        assertEquals(NOT_FOUND, returnValue.getStatusCode());
    }

    @Test
    public void cannotAddEmptyNamesTest(){
        var eventId = eventRepository.save(new Event("hi")).getId();
        var returnValue = participantController.createParticipant(eventId, null,
                null, null, null, null);
        assertEquals(BAD_REQUEST, returnValue.getStatusCode());
    }

    @Test
    public void AddParticipantTest(){
        var eventId = eventRepository.save(new Event("hi")).getId();
        var returnValue = participantController.createParticipant(eventId, "John",
                "Doe", null, null, null);
        assertEquals(CREATED, returnValue.getStatusCode());
        assertTrue(participantRepository.existsById(returnValue.getBody().getId()));
    }

    @Test
    public void UpdateParticipantTest(){
        var eventId = eventRepository.save(new Event("hi")).getId();
        var participantId = participantController.createParticipant(eventId, "John",
                "Doe", null, null, null).getBody().getId();
        var returnValue = participantController.updateParticipant(participantId, "John", "Doe",
                null, null, null);
        assertEquals(OK, returnValue.getStatusCode());
    }

    @Test
    public void DeleteParticipantTest(){
        var eventId = eventRepository.save(new Event("hi")).getId();
        var participantId = participantController.createParticipant(eventId, "John",
                "Doe", null, null, null).getBody().getId();
        var returnValue = participantController.deleteParticipant(participantId);
        assertEquals(OK, returnValue.getStatusCode());
        assertFalse(participantRepository.existsById(returnValue.getBody().getId()));
    }
    @Test
    public void checkParticipantThatDoesntExistGetExpenses() {
        var eventId = eventRepository.save(new Event("hi")).getId();
        var returnValue = participantController.createParticipant(eventId, "John",
                "Doe", null, null, null);

        var actualId = returnValue.getBody().getId() + 1;
        var actual = participantController.getExpensesByParticipantId(actualId);

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void checkParticipantThatExistsGetExpenses() {
        var eventId = eventRepository.save(new Event("hi")).getId();
        var returnValue = participantController.createParticipant(eventId, "John",
                "Doe", "jdoe@gmail.com", "NL666", "NL42");
        var actualId = returnValue.getBody().getId();

        var actual = participantController.getExpensesByParticipantId(actualId);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkParticipantThatExistsGetExpensesNotNull() {
        var eventId = eventRepository.save(new Event("hi")).getId();
        var returnValue = participantController.createParticipant(eventId, "John",
                "Doe", null, null, null);
        var actualId = returnValue.getBody().getId();

        var actual = participantController.getExpensesByParticipantId(actualId);
        assertNotNull(actual.getBody());
    }

    @Test
    void checkParticipantGetExpensesNullId() {
        var actual = participantController.getExpensesByParticipantId(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
}
