package server.api;

import commons.Event;
import commons.Expense;
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

    @Test
    void checkCreateParticipant() {
        Event event = new Event("event");

        event = eventRepository.save(event);

        Participant participant = new Participant(event, "foo", "fooman", "foo@fooomail.com",
                "iban", "bic");

        assertEquals(StatusEntity.StatusCode.OK, participantController.createParticipant(participant).getStatusCode());

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:create"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
        assertEquals(participant.getEmail(), capturedParticipant.getEmail());
        assertEquals(participant.getIban(), capturedParticipant.getIban());
        assertEquals(participant.getBic(), capturedParticipant.getBic());
    }

    @Test
    void checkNullParticipant() {
        assertEquals(StatusEntity.badRequest(true, "Participant should not be null"),
                participantController.isParticipantBadRequest(null));
    }

    @Test
    void checkParticipantNullEventId() {
        Participant participant = new Participant(UUID.randomUUID(), null, "surname",
                "email@email.com", "iban", "bic", null);

        assertEquals(StatusEntity.badRequest(true, "InvitationCode of event should be provided"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantNullFirstName() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), null, "surname",
                "email@email.com", "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest(false, "First name should not be empty"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantNullLastName() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), "name", null,
                "email@email.com", "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest(false, "Last name should not be empty"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantInvalidEmail() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                "email", "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest(false, "Provided email is of invalid format"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantEventNotFound() {
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                "email@email.com", "iban", "bic", UUID.randomUUID());

        assertEquals(StatusEntity.notFound(false, "Provided participant has an invalid invitation code"),
                participantController.createParticipant(participant));
    }

    @Test
    void checkUpdateParticipant() {
        Event event = new Event("event");
        event = eventRepository.save(event);
        Participant participant = new Participant(event, "name", "surname",
                "email@gmail.com", "iban", "bic");
        participant = participantRepository.save(participant);

        participant.setLastName("foowoman");
        participant.setEmail("foo42@foo.com");

        assertEquals(StatusEntity.StatusCode.OK, participantController.updateParticipant(participant).getStatusCode());

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:update"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getId(), capturedParticipant.getId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
        assertEquals(participant.getEmail(), capturedParticipant.getEmail());
        assertEquals(participant.getIban(), capturedParticipant.getIban());
        assertEquals(participant.getBic(), capturedParticipant.getBic());
    }


    @Test
    void checkParticipantNullId() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(null, "name", "surname",
                "email@email.com", "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest("Id of the participant should be provided", true),
                participantController.isExistingParticipantBadRequest(participant));
    }


    @Test
    void checkUpdateParticipantNotFound() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                "email@email.com", "iban", "bic", event.getId());

        assertEquals(StatusEntity.notFound(true, "Participant not found"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkDeleteParticipant() {
        Event event = new Event("event");
        event = eventRepository.save(event);
        Participant participant = new Participant(event, "name", "surname",
                "email@gmail.com", "iban", "bic");

        participant = participantRepository.save(participant);

        assertTrue(participantRepository.existsById(participant.getId()));

        assertEquals(StatusEntity.ok("participant:delete " + participant.getId()),
                participantController.deleteParticipant(participant));

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:delete"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getId(), capturedParticipant.getId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
        assertEquals(participant.getEmail(), capturedParticipant.getEmail());
        assertEquals(participant.getIban(), capturedParticipant.getIban());
        assertEquals(participant.getBic(), capturedParticipant.getBic());
    }

    @Test
    void checkReadParticipants() {
        Event event = new Event("testEvent");
        event = eventRepository.save(event);
        Participant participant1 = new Participant(event, "name1",
                "surname1", "abc@gmail.com", "ibanTest", "bicTest");
        Participant participant2 = new Participant(event, "name2",
                "surname2", "abc@gmail.com", "ibanTest", "bicTest");
        participant1 = participantRepository.save(participant1);
        participant2 = participantRepository.save(participant2);
        event.addParticipant(participant1);
        event.addParticipant(participant2);

        StatusEntity<List<Participant>> status = participantController.readParticipants(event.getId());
        assertEquals(StatusEntity.StatusCode.OK, status.getStatusCode());
        List<Participant> readParticipants = status.getBody();
        Participant readParticipant1 = readParticipants.getFirst();
        Participant readParticipant2 = readParticipants.getLast();
        assertEquals(participant1.getEventId(), readParticipant1.getEventId());
        assertEquals(participant1.getId(), readParticipant1.getId());
        assertEquals(participant1.getFirstName(), readParticipant1.getFirstName());
        assertEquals(participant1.getLastName(), readParticipant1.getLastName());
        assertEquals(participant1.getEmail(), readParticipant1.getEmail());
        assertEquals(participant1.getIban(), readParticipant1.getIban());
        assertEquals(participant1.getBic(), readParticipant1.getBic());
        assertEquals(participant2.getEventId(), readParticipant2.getEventId());
        assertEquals(participant2.getId(), readParticipant2.getId());
        assertEquals(participant2.getFirstName(), readParticipant2.getFirstName());
        assertEquals(participant2.getLastName(), readParticipant2.getLastName());
        assertEquals(participant2.getEmail(), readParticipant2.getEmail());
        assertEquals(participant2.getIban(), readParticipant2.getIban());
        assertEquals(participant2.getBic(), readParticipant2.getBic());
    }

    @Test
    void checkReadParticipantsEventNotFound() {
        UUID uuid = UUID.randomUUID();

        assertEquals(StatusEntity.notFound(true, (ParticipantList) null), participantController.readParticipants(uuid));
    }
}
