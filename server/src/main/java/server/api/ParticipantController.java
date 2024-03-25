package server.api;

import commons.Event;
import commons.Participant;
import commons.StatusEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Class that represents the participant controller
 */
@Controller
public class ParticipantController {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final SimpMessagingTemplate template;

    /**
     * Constructor
     * @param participantRepository participant repository
     * @param eventRepository event repository
     * @param template SimpMessagingTemplate
     */
    public ParticipantController(ParticipantRepository participantRepository, EventRepository eventRepository,
                                 SimpMessagingTemplate template) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.template = template;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Handles create websocket endpoint for participant
     * @param receivedParticipant Participant we want to create
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:create")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity<String> createParticipant(Participant receivedParticipant)
    {
        if (receivedParticipant == null)
            return StatusEntity.badRequest("Participant should not be null", true);
        if(receivedParticipant.getEventId() == null)
            return StatusEntity.badRequest("InvitationCode of event should be provided", true);
        if(receivedParticipant.getId() == null)
            return StatusEntity.badRequest("Id of the participant should be provided", true);
        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            return StatusEntity.badRequest("First name should not be empty");
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            return StatusEntity.badRequest("Last name should not be empty");
        }
        if(isNullOrEmpty(receivedParticipant.getEmail()) ||
                !Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").
                        matcher(receivedParticipant.getEmail()).matches())
        {
            return StatusEntity.badRequest("Provided email is invalid");
        }

        Participant participant = new Participant(
                eventRepository.getReferenceById(receivedParticipant.getEventId()),
                receivedParticipant.getFirstName(),
                receivedParticipant.getLastName(),
                receivedParticipant.getEmail(),
                receivedParticipant.getIban(),
                receivedParticipant.getBic()
        );
        participant = participantRepository.save(participant);

        participant.setEventId(receivedParticipant.getEventId());

        template.convertAndSend("/topic/"+participant.getEventId()+"/participant:create",
                participant);
        return StatusEntity.ok("participant:create " + participant.getId());
    }

    /**
     * Handles read websocket endpoint for participant
     * @param invitationCode invitation code of the event which we want to read participants from
     * @return returns a StatusEntity<Event> body contains List<Participant> if status code is OK
     * returns null in body otherwise
     */
    @MessageMapping("/participants:read")
    @SendToUser(value = "/queue/event:read", broadcast = false)
    public StatusEntity<List<Participant>> readParticipants(UUID invitationCode)
    {
        if(!eventRepository.existsById(invitationCode))
            return StatusEntity.notFound(null, true);

        Event event = eventRepository.getReferenceById(invitationCode);
        List<Participant> participants = event.getParticipants();

        for(Participant participant : participants)
        {
            participant.setEventId(participant.getEvent().getId());
        }

        return StatusEntity.ok(participants);
    }

    /**
     * Handles update websocket endpoint for participant
     * @param receivedParticipant Participant that we want to update
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:update")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity<String> updateParticipant(Participant receivedParticipant)
    {
        if (receivedParticipant == null)
            return StatusEntity.badRequest("Participant should not be null", true);
        if(receivedParticipant.getEventId() == null)
            return StatusEntity.badRequest("InvitationCode of event should be provided", true);
        if(receivedParticipant.getId() == null)
            return StatusEntity.badRequest("Id of the participant should be provided", true);
        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            return StatusEntity.badRequest("First name should not be empty");
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            return StatusEntity.badRequest("Last name should not be empty");
        }
        if(isNullOrEmpty(receivedParticipant.getEmail()) ||
                !Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").
                        matcher(receivedParticipant.getEmail()).matches())
        {
            return StatusEntity.badRequest("Provided email is invalid");
        }

        if(!participantRepository.existsById(receivedParticipant.getId()))
        {
            return StatusEntity.notFound("Participant not found", true);
        }

        Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
        participant.setFirstName(receivedParticipant.getFirstName());
        participant.setLastName(receivedParticipant.getLastName());
        participant.setEmail(receivedParticipant.getEmail());
        participant.setIban(receivedParticipant.getIban());
        participant.setBic(receivedParticipant.getBic());
        participant = participantRepository.save(participant);

        participant.setEventId(receivedParticipant.getEventId());

        template.convertAndSend("/topic/"+participant.getEventId()+"/participant:update",
                participant);
        return StatusEntity.ok("participant:update " + participant.getId());
    }

    /**
     * Handles delete websocket endpoint for participant
     * @param receivedParticipant Participant that we want to delete
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:delete")
    @SendToUser("/queue/reply")
    public StatusEntity<String> deleteParticipant(Participant receivedParticipant)
    {
        if(!participantRepository.existsById(receivedParticipant.getId()))
        {
            return StatusEntity.notFound("Participant not found", true);
        }

        Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
        participantRepository.delete(participant);

        template.convertAndSend("/topic/"+receivedParticipant.getEventId()+"/participant:delete", participant);
        return StatusEntity.ok("participant:delete " + participant.getId());
    }
}
