package server.api;

import commons.Participant;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Class that represents the participant controller
 */
@Controller
public class ParticipantController {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    @Autowired
    private SimpMessagingTemplate template;

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
    @SendToUser("/queue/reply")
    public StatusEntity<String> createParticipant(Participant receivedParticipant)
    {
        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            return StatusEntity.badRequest("First name should not be empty");
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            return StatusEntity.badRequest("Last name should not be empty");
        }
        if(!receivedParticipant.getEmail().isEmpty() &&
                !Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").
                        matcher(receivedParticipant.getEmail()).matches())
        {
            return StatusEntity.badRequest("Provided email is invalid");
        }

        receivedParticipant.setEvent(eventRepository.getReferenceById(receivedParticipant.getEventId()));
        participantRepository.save(receivedParticipant);

        template.convertAndSend("/topic/"+receivedParticipant.getId()+"/participant:create",
                receivedParticipant);
        return StatusEntity.ok("participant:create " + receivedParticipant.getId());
    }

    /**
     * Handles read websocket endpoint for participant
     * @param id UUID of a participant we want to read
     * @return returns a StatusEntity<Event> body contains Participant if status code is OK
     * returns null in body otherwise
     */
    @MessageMapping("/participant:read")
    @SendToUser("/queue/event:read")
    public StatusEntity<Participant> readParticipant(UUID id)
    {
        if(!participantRepository.existsById(id))
        {
            return StatusEntity.notFound(null, true);
        }

        Participant participant = participantRepository.getReferenceById(id);
        participant.setEventId(participant.getEvent().getId());

        return StatusEntity.ok(participant);
    }

    /**
     * Handles update websocket endpoint for participant
     * @param receivedParticipant Participant that we want to update
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/participant:update")
    @SendToUser("/queue/reply")
    public StatusEntity<String> updateParticipant(Participant receivedParticipant)
    {
        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            return StatusEntity.badRequest("First name should not be empty");
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            return StatusEntity.badRequest("Last name should not be empty");
        }
        if(!receivedParticipant.getEmail().isEmpty() &&
                !Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").
                        matcher(receivedParticipant.getEmail()).matches())
        {
            return StatusEntity.badRequest("Provided email is invalid");
        }

        if(!participantRepository.existsById(receivedParticipant.getId()))
        {
            return StatusEntity.notFound("Participant not found", true);
        }

        receivedParticipant.setEvent(eventRepository.getReferenceById(receivedParticipant.getEventId()));

        participantRepository.save(receivedParticipant);

        template.convertAndSend("/topic/"+receivedParticipant.getId()+"/participant:update",
                receivedParticipant);
        return StatusEntity.ok("participant:update " + receivedParticipant.getId());
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

        template.convertAndSend("/topic/"+receivedParticipant.getId()+"/participant:delete", participant);
        return StatusEntity.ok("participant:delete " + participant.getId());
    }
}
