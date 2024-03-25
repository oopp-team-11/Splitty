package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ParticipantControllerTest {

    private ParticipantRepository participantRepository;
    private EventRepository eventRepository;
    private ParticipantController participantController;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setup() {
        participantRepository = new TestParticipantRepository();
        eventRepository = new TestEventRepository();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        participantController = new ParticipantController(participantRepository, eventRepository, messagingTemplate);
    }

    private static void setId(Participant toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }
    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    @Test
    void checkCreateParticipant() {
        Event event = new Event("event");
        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepository.save(event);

        Participant participant = new Participant();
        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@fooomail.com");
        participant.setEventId(event.getId());

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.StatusCode.OK, participantController.createParticipant(participant).getStatusCode());

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:create"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(event, capturedParticipant.getEvent());
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
        assertEquals(participant.getEmail(), capturedParticipant.getEmail());
        assertEquals(participant.getIban(), capturedParticipant.getIban());
        assertEquals(participant.getBic(), capturedParticipant.getBic());
    }

    @Test
    void checkCreateParticipantNullParticipant() {
        Participant participant = null;

        assertEquals(StatusEntity.badRequest("Participant should not be null", true),
                participantController.createParticipant(participant));
    }

    @Test
    void checkCreateParticipantNullEventId() {
        Participant participant = new Participant();

        assertEquals(StatusEntity.badRequest("InvitationCode of event should be provided", true),
                participantController.createParticipant(participant));
    }

    @Test
    void checkCreateParticipantNullId() {
        Participant participant = new Participant();
        participant.setEventId(UUID.randomUUID());

        assertEquals(StatusEntity.badRequest("Id of the participant should be provided", true),
                participantController.createParticipant(participant));
    }

    @Test
    void checkCreateParticipantNullFirstName() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest("First name should not be empty"),
                participantController.createParticipant(participant));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkCreateParticipantNullLastName() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        participant.setFirstName("foo");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest("Last name should not be empty"),
                participantController.createParticipant(participant));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkCreateParticipantInvalidEmail() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foomail");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest("Provided email is invalid"),
                participantController.createParticipant(participant));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkUpdateParticipant() {
        Event event = new Event("event");
        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepository.save(event);

        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foo.com");
        participant.setEventId(event.getId());

        assertEquals(StatusEntity.StatusCode.OK, participantController.updateParticipant(participant).getStatusCode());


        verify(messagingTemplate).convertAndSend("/topic/"+participant.getEventId()+"/participant:update",
                participant);

        assertTrue(participantRepository.existsById(participant.getId()));
        var repoParticipant = participantRepository.getReferenceById(participant.getId());
        assertEquals("foo", repoParticipant.getFirstName());
        assertEquals("fooman", repoParticipant.getLastName());
        assertEquals("foo@foo.com", repoParticipant.getEmail());
        assertEquals(event, repoParticipant.getEvent());

    }

    @Test
    void checkUpdateParticipantNullFirstName() {
        Participant participant = new Participant();

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        assertEquals(StatusEntity.badRequest("First name should not be empty"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantNullLastName() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        assertEquals(StatusEntity.badRequest("Last name should not be empty"),
                participantController.updateParticipant(participant));
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

        assertEquals(StatusEntity.badRequest("Provided email is invalid"),
                participantController.updateParticipant(participant));
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

        assertEquals(StatusEntity.notFound("Participant not found", true),
                participantController.updateParticipant(participant));

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

        assertEquals(StatusEntity.ok("participant:delete " + participant.getId()),
                participantController.deleteParticipant(participant));

        verify(messagingTemplate).convertAndSend("/topic/"+participant.getEventId()+"/participant:delete",
                participant);
        assertFalse(participantRepository.existsById(participant.getId()));
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

        assertEquals(StatusEntity.notFound("Participant not found", true),
                participantController.deleteParticipant(participant));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkReadParticipant() {
        Event event = new Event("event");
        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepository.save(event);

        Participant participant = new Participant();
//        participant.setEvent(event);

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participantRepository.save(participant);

        participant.setEventId(event.getId());

        assertEquals(StatusEntity.ok(participant), participantController.readParticipant(participant.getId()));
    }

    @Test
    void checkReadParticipantNotFound() {
        UUID uuid = UUID.randomUUID();

        assertEquals(StatusEntity.notFound(null, true), participantController.readParticipant(uuid));

        assertFalse(participantRepository.existsById(uuid));
    }
}
