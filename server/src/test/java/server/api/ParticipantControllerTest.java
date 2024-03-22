package server.api;

import commons.Event;
import commons.Participant;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.*;

public class ParticipantControllerTest {

    private ParticipantRepository participantRepository;

    private EventRepository eventRepository;

    private ParticipantController participantController;
    private SimpMessagingTemplate messagingTemplate;
    private Principal principal;

    @BeforeEach
    public void setup() {
        participantRepository = new TestParticipantRepository();
        eventRepository = new TestEventRepository();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        principal = mock(Principal.class);
        participantController = new ParticipantController(participantRepository, eventRepository, messagingTemplate);
    }

    private static void setId(Participant toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
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
        var actualId = UUID.randomUUID();
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

    @Test
    void checkCreateParticipant() {
        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foo.com");

        participantController.createParticipant(principal, participant);

        assertTrue(participantRepository.existsById(participant.getId()));

        verify(messagingTemplate).convertAndSend("/topic/"+participant.getId(), participant);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("participant:create "+participant.getId())));
    }

    @Test
    void checkCreateParticipantNullFirstName() {
        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantController.createParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("First name should not be empty")));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkCreateParticipantNullLastName() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantController.createParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Last name should not be empty")));
        assertFalse(participantRepository.existsById(participant.getId()));

    }

    @Test
    void checkCreateParticipantInvalidEmail() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foomail");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantController.createParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Provided email is invalid")));
        assertFalse(participantRepository.existsById(participant.getId()));

    }

    @Test
    void checkCreateParticipantWrongClass() {
        Event event = new Event();

        participantController.createParticipant(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be a participant", true)));

        assertFalse(participantRepository.existsById(event.getId()));
    }

    @Test
    void checkUpdateParticipant() {
        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foo.com");

        participantController.updateParticipant(principal, participant);

        verify(messagingTemplate).convertAndSend("/topic/"+participant.getId(), participant);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("participant:update "+participant.getId())));

        assertTrue(participantRepository.existsById(participant.getId()));
        var repoParticipant = participantRepository.getReferenceById(participant.getId());
        assertEquals("foo", repoParticipant.getFirstName());
        assertEquals("fooman", repoParticipant.getLastName());
        assertEquals("foo@foo.com", repoParticipant.getEmail());
    }

    @Test
    void checkUpdateParticipantNullFirstName() {
        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participantController.updateParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("First name should not be empty")));
    }

    @Test
    void checkUpdateParticipantNullLastName() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participantController.updateParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Last name should not be empty")));
    }

    @Test
    void checkUpdateParticipantInvalidEmail() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foomail");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participantController.updateParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Provided email is invalid")));
    }

    @Test
    void checkUpdateParticipantWrongClass() {
        Event event = new Event();

        participantController.updateParticipant(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be a participant", true)));

        assertFalse(participantRepository.existsById(event.getId()));
    }

    @Test
    void checkUpdateParticipantNotFound() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foomail.com");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantController.updateParticipant(principal, participant);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Participant not found",true)));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkDeleteParticipant() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foomail.com");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        assertTrue(participantRepository.existsById(participant.getId()));

        participantController.deleteParticipant(principal, participant);

        verify(messagingTemplate).convertAndSend("/topic/"+participant.getId(), participant);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("participant:delete "+participant.getId())));
        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkDeleteParticipantWrongClass() {
        Event event = new Event();

        participantController.deleteParticipant(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be a participant",true)));
    }

    @Test
    void checkDeleteParticipantNotFound() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foomail.com");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantController.deleteParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Participant not found",true)));
        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkReadParticipant() {
        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participantController.readParticipant(principal, participant.getId());

        verify(messagingTemplate).convertAndSend("/topic/"+participant.getId(), participant);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("participant:read "+participant.getId())));
    }

    @Test
    void checkReadParticipantWrongClass() {
        Participant participant = new Participant();

        participantController.readParticipant(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be a UUID", true)));
    }

    @Test
    void checkReadParticipantNotFound() {
        UUID uuid = UUID.randomUUID();

        participantController.readParticipant(principal, uuid);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Participant not found",true)));

        assertFalse(participantRepository.existsById(uuid));
    }
}
