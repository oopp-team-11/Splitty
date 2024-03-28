package server.api;

import commons.Event;
import commons.Participant;
import commons.ParticipantList;
import commons.StatusEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Class that represents the participant controller
 */
@Transactional
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
     * Evaluates whether the received Participant object has all the correct field values
     *
     * @param receivedParticipant received Participant object
     * @return Returns a statusEntity with an error message, if it is a bad request
     * Returns an OK status with null body otherwise
     */
    public StatusEntity isParticipantBadRequest(Participant receivedParticipant)
    {
        if (receivedParticipant == null)
            return StatusEntity.badRequest(true, "Participant should not be null");
        if(receivedParticipant.getEventId() == null)
            return StatusEntity.badRequest(true, "InvitationCode of event should be provided");
        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            return StatusEntity.badRequest(false, "First name should not be empty");
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            return StatusEntity.badRequest(false, "Last name should not be empty");
        }
        if(isNullOrEmpty(receivedParticipant.getEmail()) ||
                !Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").
                        matcher(receivedParticipant.getEmail()).matches())
        {
            return StatusEntity.badRequest(false, "Provided email is of invalid format");
        }

        return StatusEntity.ok((String) null);
    }

    /**
     * Evaluates whether the received existing Participant object has the correct field values
     *
     * @param receivedParticipant received Participant object
     * @return Returns a statusEntity with an error message, if it is a bad request
     * Returns an OK status with null body otherwise
     */
    public StatusEntity isExistingParticipantBadRequest(Participant receivedParticipant) {
        if(receivedParticipant.getId() == null)
            return StatusEntity.badRequest(true, "Id of the participant should be provided");
        if(!participantRepository.existsById(receivedParticipant.getId()))
        {
            return StatusEntity.notFound(true, "Participant not found");
        }
        return StatusEntity.ok((String)null);
    }

    /**
     * Handles create websocket endpoint for participant
     * @param receivedParticipant Participant we want to create
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:create")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity createParticipant(Participant receivedParticipant)
    {
        var isBadRequest = isParticipantBadRequest(receivedParticipant);
        if(isBadRequest.getStatusCode() != StatusEntity.StatusCode.OK)
            return isBadRequest;

        if(!eventRepository.existsById(receivedParticipant.getEventId()))
            return StatusEntity.notFound(false, "Provided participant has an invalid invitation code");

        Event event = eventRepository.getReferenceById(receivedParticipant.getEventId());
        Participant participant = new Participant(
                event,
                receivedParticipant.getFirstName(),
                receivedParticipant.getLastName(),
                receivedParticipant.getEmail(),
                receivedParticipant.getIban(),
                receivedParticipant.getBic()
        );
        participant = participantRepository.save(participant);

        Participant sentParticipant = new Participant(participant.getId(), participant.getFirstName(),
                participant.getLastName(), participant.getEmail(), participant.getIban(), participant.getBic(),
                receivedParticipant.getEventId());
        template.convertAndSend("/topic/" + sentParticipant.getEventId() + "/participant:create",
                sentParticipant);
        return StatusEntity.ok("participant:create " + sentParticipant.getId());
    }

    /**
     * Handles read websocket endpoint for participant
     * @param invitationCode invitation code of the event which we want to read participants from
     * @return returns a StatusEntity<Event> body contains List<Participant> if status code is OK
     * returns null in body otherwise
     */
    @MessageMapping("/participants:read")
    @SendToUser(value = "/queue/participants:read", broadcast = false)
    public StatusEntity readParticipants(UUID invitationCode)
    {
        if(!eventRepository.existsById(invitationCode))
            return StatusEntity.notFound(true, (ParticipantList) null);

        Event event = eventRepository.getReferenceById(invitationCode);
        List<Participant> participants = event.getParticipants();
        ParticipantList sentParticipants = new ParticipantList();

        for(Participant participant : participants) {
            Participant sentParticipant = new Participant(participant.getId(), participant.getFirstName(),
                    participant.getLastName(), participant.getEmail(), participant.getIban(), participant.getBic(),
                    invitationCode);
            sentParticipants.add(sentParticipant);
        }

        return StatusEntity.ok(sentParticipants);
    }

    /**
     * Handles update websocket endpoint for participant
     * @param receivedParticipant Participant that we want to update
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:update")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity updateParticipant(Participant receivedParticipant)
    {
        var isBadRequest = isParticipantBadRequest(receivedParticipant);
        if (isBadRequest.getStatusCode() != StatusEntity.StatusCode.OK)
            return isBadRequest;

        var isExistingBadRequest = isExistingParticipantBadRequest(receivedParticipant);
        if (isExistingBadRequest.getStatusCode() != StatusEntity.StatusCode.OK)
            return isExistingBadRequest;

        Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
        participant.setFirstName(receivedParticipant.getFirstName());
        participant.setLastName(receivedParticipant.getLastName());
        participant.setEmail(receivedParticipant.getEmail());
        participant.setIban(receivedParticipant.getIban());
        participant.setBic(receivedParticipant.getBic());
        participant = participantRepository.save(participant);

        Participant sentParticipant = new Participant(participant.getId(), participant.getFirstName(),
                participant.getLastName(), participant.getEmail(), participant.getIban(), participant.getBic(),
                receivedParticipant.getEventId());
        template.convertAndSend("/topic/" + sentParticipant.getEventId() + "/participant:update",
                sentParticipant);
        return StatusEntity.ok("participant:update " + sentParticipant.getId());
    }

    /**
     * Handles delete websocket endpoint for participant
     * @param receivedParticipant Participant that we want to delete
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:delete")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity deleteParticipant(Participant receivedParticipant)
    {
        var isExistingBadRequest = isExistingParticipantBadRequest(receivedParticipant);
        if (isExistingBadRequest.getStatusCode() != StatusEntity.StatusCode.OK)
            return isExistingBadRequest;

        Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
        participantRepository.delete(participant);

        Participant sentParticipant = new Participant(participant.getId(), participant.getFirstName(),
                participant.getLastName(), participant.getEmail(), participant.getIban(), participant.getBic(),
                receivedParticipant.getEventId());
        template.convertAndSend("/topic/" + sentParticipant.getEventId() + "/participant:delete",
                sentParticipant);
        return StatusEntity.ok("participant:delete " + sentParticipant.getId());
    }
}
