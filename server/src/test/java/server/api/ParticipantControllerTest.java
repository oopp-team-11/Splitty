package server.api;

import commons.Event;
import commons.Participant;
import commons.ParticipantList;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;
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

        assertEquals(StatusEntity.badRequest(true, "Participant should not be null"),
                participantController.createParticipant(participant));
    }

    @Test
    void checkCreateParticipantNullEventId() {
        Participant participant = new Participant();

        assertEquals(StatusEntity.badRequest(true, "InvitationCode of event should be provided"),
                participantController.createParticipant(participant));
    }

    @Test
    void checkCreateParticipantNullFirstName() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest(false, "First name should not be empty"),
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

        assertEquals(StatusEntity.badRequest(false, "Last name should not be empty"),
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

        assertEquals(StatusEntity.badRequest(false, "Provided email is invalid"),
                participantController.createParticipant(participant));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkCreateParticipantEventNotFound() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foomail@mail.com");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.notFound(false, "Provided participant has an invalid invitation code"),
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

        Participant participant = new Participant(
                event,
                "foo",
                "fooman",
                "foo@foomail.com",
                null,
                null
        );
        participant.setEventId(event.getId());

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        participant = participantRepository.save(participant);

        participant.setLastName("foowoman");
        participant.setEmail("foo42@foo.com");
        participant.setEventId(event.getId());

        assertEquals(StatusEntity.StatusCode.OK, participantController.updateParticipant(participant).getStatusCode());

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:update"),
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
    void checkUpdateParticipantNullParticipant() {
        Participant participant = null;

        assertEquals(StatusEntity.badRequest(true, "Participant should not be null"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantNullEventId() {
        Participant participant = new Participant();

        assertEquals(StatusEntity.badRequest(true, "InvitationCode of event should be provided"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantNullId() {
        Participant participant = new Participant();
        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foomail.com");
        participant.setEventId(UUID.randomUUID());

        assertEquals(StatusEntity.badRequest(true, "Id of the participant should be provided"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantNullFirstName() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest(false, "First name should not be empty"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantNullLastName() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        participant.setFirstName("foo");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest(false, "Last name should not be empty"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantInvalidEmail() {
        Participant participant = new Participant();

        participant.setEventId(UUID.randomUUID());
        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foomail");
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.badRequest(false, "Provided email is invalid"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkUpdateParticipantNotFound() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foomail.com");
        participant.setEventId(UUID.randomUUID());
        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.notFound(true, "Participant not found"),
                participantController.updateParticipant(participant));
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
    void checkDeleteParticipantNullId() {
        Participant participant = new Participant();

        participant.setFirstName("foo");
        participant.setLastName("fooman");
        participant.setEmail("foo@foomail.com");

        assertEquals(StatusEntity.badRequest(true, "Id of the participant should be provided"),
                participantController.deleteParticipant(participant));

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

        assertEquals(StatusEntity.notFound(true, "Participant not found"),
                participantController.deleteParticipant(participant));

        assertFalse(participantRepository.existsById(participant.getId()));
    }

    @Test
    void checkReadParticipants() {
        Event event = new Event("event");
        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        Participant participant = new Participant(
                event,
                "foo",
                "fooman",
                null,
                null,
                null
        );

        try {
            setId(participant, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        event.addParticipant(participant);

        eventRepository.save(event);
        participantController.createParticipant(participant);

        ParticipantList participantList = new ParticipantList();
        participantList.add(participant);

        assertEquals(StatusEntity.ok(participantList), participantController.readParticipants(event.getId()));
    }

    @Test
    void checkReadParticipantsEventNotFound() {
        UUID uuid = UUID.randomUUID();

        assertEquals(StatusEntity.notFound(true, (ParticipantList) null), participantController.readParticipants(uuid));
    }
}
