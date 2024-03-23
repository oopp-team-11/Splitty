package server.api;

import commons.Participant;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import server.database.ParticipantRepository;

import java.security.Principal;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Class that represents the participant controller
 */
@Controller
public class ParticipantController {
    private final ParticipantRepository participantRepository;
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor
     * @param participantRepository participant repository
     * @param template SimpMessagingTemplate
     */
    public ParticipantController(ParticipantRepository participantRepository,
                                 SimpMessagingTemplate template) {
        this.participantRepository = participantRepository;
        this.template = template;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Handles create websocket endpoint for participant
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/participant:create")
    public void createParticipant(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Participant.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Payload should be a participant", true));
            return;
        }

        Participant receivedParticipant = (Participant) payload;

        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("First name should not be empty"));
            return;
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Last name should not be empty"));
            return;
        }
        if(!receivedParticipant.getEmail().isEmpty() &&
                !Pattern.compile("^(.+)@(\\S+)$").matcher(receivedParticipant.getEmail()).matches())
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Provided email is invalid"));
            return;
        }

        participantRepository.save(receivedParticipant);

        template.convertAndSend("/topic/"+receivedParticipant.getId(), receivedParticipant);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("participant:create " + receivedParticipant.getId()));
    }

    /**
     * Handles read websocket endpoint for participant
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/participant:read")
    public void readParticipant(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != UUID.class) {
            template.convertAndSendToUser(principal.getName(), "/queue/reply",
                    StatusEntity.badRequest("Payload should be a UUID", true));
            return;
        }

        UUID invitationCode = (UUID) payload;

        if(!participantRepository.existsById(invitationCode))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Participant not found", true));
            return;
        }

        Participant participant = participantRepository.getReferenceById(invitationCode);

        template.convertAndSend("/topic/"+invitationCode, participant);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("participant:read " + participant.getId()));
    }

    /**
     * Handles update websocket endpoint for participant
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/participant:update")
    public void updateParticipant(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Participant.class) {
            template.convertAndSendToUser(principal.getName(), "/queue/reply",
                    StatusEntity.badRequest("Payload should be a participant", true));
            return;
        }

        Participant receivedParticipant = (Participant) payload;

        if (isNullOrEmpty(receivedParticipant.getFirstName())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("First name should not be empty"));
            return;
        }
        if (isNullOrEmpty(receivedParticipant.getLastName())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Last name should not be empty"));
            return;
        }
        if(!receivedParticipant.getEmail().isEmpty() &&
                !Pattern.compile("^(.+)@(\\S+)$").matcher(receivedParticipant.getEmail()).matches())
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Provided email is invalid"));
            return;
        }

        if(!participantRepository.existsById(receivedParticipant.getId()))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Participant not found", true));
            return;
        }

        Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
        participant.setFirstName(receivedParticipant.getFirstName());
        participant.setLastName(receivedParticipant.getLastName());
        participant.setEmail(receivedParticipant.getEmail());
        participant.setIban(receivedParticipant.getIban());
        participant.setBic(receivedParticipant.getBic());
        participantRepository.save(participant);

        template.convertAndSend("/topic/"+receivedParticipant.getId(), participant);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("participant:update " + participant.getId()));
    }

    /**
     * Handles delete websocket endpoint for participant
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/participant:delete")
    public void deleteParticipant(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Participant.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Payload should be a participant", true));
            return;
        }

        Participant receivedParticipant = (Participant) payload;
        if(!participantRepository.existsById(receivedParticipant.getId()))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Participant not found", true));
            return;
        }

        Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
        participantRepository.delete(participant);

        template.convertAndSend("/topic/"+receivedParticipant.getId(), participant);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("participant:delete " + participant.getId()));
    }
}
