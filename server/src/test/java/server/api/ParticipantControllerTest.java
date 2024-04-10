package server.api;

import commons.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.EventLastActivityService;
import server.database.EventRepository;
import server.database.ParticipantRepository;

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
    private EventLastActivityService eventLastActivityService;

    @BeforeEach
    public void setup() {
        participantRepository = new TestParticipantRepository();
        eventRepository = new TestEventRepository();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventLastActivityService = new EventLastActivityService(eventRepository, messagingTemplate);
        participantController = new ParticipantController(participantRepository, eventRepository, messagingTemplate, eventLastActivityService);
    }

    @Test
    void checkCreateParticipant() {
        Event event = new Event("event");

        event = eventRepository.save(event);

        Participant participant = new Participant(event, "foo", "fooman",
                "iban", "bic");

        assertEquals(StatusEntity.StatusCode.OK, participantController.createParticipant(participant).getStatusCode());

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:create"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
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
                 "iban", "bic", null);

        assertEquals(StatusEntity.badRequest(true, "InvitationCode of event should be provided"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantNullFirstName() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), null, "surname",
                 "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest(false, "First name should not be empty"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantNullLastName() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), "name", null,
                 "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest(false, "Last name should not be empty"),
                participantController.isParticipantBadRequest(participant));
    }

    @Test
    void checkParticipantEventNotFound() {
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                 "iban", "bic", UUID.randomUUID());

        assertEquals(StatusEntity.notFound(false, "Provided participant has an invalid invitation code"),
                participantController.createParticipant(participant));
    }

    @Test
    void checkUpdateParticipant() {
        Event event = new Event("event");
        event = eventRepository.save(event);
        Participant participant = new Participant(event, "name", "surname",
                 "iban", "bic");
        participant = participantRepository.save(participant);

        participant = new Participant(participant.getId(), participant.getFirstName(), participant.getLastName(),
                participant.getIban(), participant.getBic(), participant.getEventId());
        participant.setLastName("foowoman");

        assertEquals(StatusEntity.StatusCode.OK, participantController.updateParticipant(participant).getStatusCode());

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:update"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getId(), capturedParticipant.getId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
        assertEquals(participant.getIban(), capturedParticipant.getIban());
        assertEquals(participant.getBic(), capturedParticipant.getBic());
    }


    @Test
    void checkParticipantNullId() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(null, "name", "surname",
                 "iban", "bic", event.getId());

        assertEquals(StatusEntity.badRequest(true, "Id of the participant should be provided"),
                participantController.isExistingParticipantBadRequest(participant));
    }


    @Test
    void checkUpdateParticipantNotFound() {
        Event event = eventRepository.save(new Event());
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                 "iban", "bic", event.getId());

        assertEquals(StatusEntity.notFound(true, "Participant not found"),
                participantController.updateParticipant(participant));
    }

    @Test
    void checkDeleteParticipant() {
        Event event = new Event("event");
        event = eventRepository.save(event);
        Participant participant = new Participant(event, "name", "surname",
                 "iban", "bic");

        participant = participantRepository.save(participant);

        assertTrue(participantRepository.existsById(participant.getId()));

        assertEquals(StatusEntity.ok("Participant was successfully deleted"),
                participantController.deleteParticipant(participant));

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/"+participant.getEventId()+"/participant:delete"),
                argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();
        assertEquals(participant.getEventId(), capturedParticipant.getEventId());
        assertEquals(participant.getId(), capturedParticipant.getId());
        assertEquals(participant.getFirstName(), capturedParticipant.getFirstName());
        assertEquals(participant.getLastName(), capturedParticipant.getLastName());
        assertEquals(participant.getIban(), capturedParticipant.getIban());
        assertEquals(participant.getBic(), capturedParticipant.getBic());
    }

    @Test
    void checkReadParticipants() {
        Event event = new Event("testEvent");
        event = eventRepository.save(event);
        Participant participant1 = new Participant(event, "name1",
                "surname1",  "ibanTest", "bicTest");
        Participant participant2 = new Participant(event, "name2",
                "surname2", "ibanTest", "bicTest");
        participant1 = participantRepository.save(participant1);
        participant2 = participantRepository.save(participant2);
        event.addParticipant(participant1);
        event.addParticipant(participant2);

        StatusEntity status = participantController.readParticipants(event.getId());
        assertEquals(StatusEntity.StatusCode.OK, status.getStatusCode());
        ParticipantList readParticipants = status.getParticipantList();
        Participant readParticipant1 = readParticipants.getFirst();
        Participant readParticipant2 = readParticipants.getLast();
        assertEquals(participant1.getEventId(), readParticipant1.getEventId());
        assertEquals(participant1.getId(), readParticipant1.getId());
        assertEquals(participant1.getFirstName(), readParticipant1.getFirstName());
        assertEquals(participant1.getLastName(), readParticipant1.getLastName());
        assertEquals(participant1.getIban(), readParticipant1.getIban());
        assertEquals(participant1.getBic(), readParticipant1.getBic());
        assertEquals(participant2.getEventId(), readParticipant2.getEventId());
        assertEquals(participant2.getId(), readParticipant2.getId());
        assertEquals(participant2.getFirstName(), readParticipant2.getFirstName());
        assertEquals(participant2.getLastName(), readParticipant2.getLastName());
        assertEquals(participant2.getIban(), readParticipant2.getIban());
        assertEquals(participant2.getBic(), readParticipant2.getBic());
    }

    @Test
    void checkReadParticipantsEventNotFound() {
        UUID uuid = UUID.randomUUID();

        assertEquals(StatusEntity.notFound(true, (ParticipantList) null), participantController.readParticipants(uuid));
    }
}
