package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.net.URI;
import java.security.Principal;
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
     * Method that creates a participant
     * @param invitationCode invitation code of the event
     * @param firstName first name of the participant
     * @param lastName last name of the participant
     * @param email email of the participant
     * @param iban IBAN of the participant
     * @param bic BIC of the participant
     * @return response entity of the participant
     */
    @PostMapping (path = {"participants", "participants/"})
    public ResponseEntity<Participant> createParticipant(@RequestBody UUID invitationCode,
                                                         @RequestBody String firstName,
                                                         @RequestBody String lastName,
                                                         @RequestBody String email,
                                                         @RequestBody String iban,
                                                         @RequestBody String bic) {
        if (!eventRepository.existsById(invitationCode)) {
            return ResponseEntity.notFound().build();
        }
        if (isNullOrEmpty(firstName) || isNullOrEmpty(lastName)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventRepository.getReferenceById(invitationCode);
        Participant participant = new Participant(event,
                firstName,
                lastName,
                email,
                iban,
                bic
        );
        eventRepository.save(event); //saves participant reference in event model
        participantRepository.save(participant);
        return ResponseEntity.created(URI.create("/participants/" + participant.getId())).body(participant);
    }

    /**
     * Method that updates a participant
     * @param participantId id of the participant
     * @param firstName first name of the participant
     * @param lastName last name of the participant
     * @param email email of the participant
     * @param iban IBAN of the participant
     * @param bic BIC of the participant
     * @return response entity of the participant
     */
    @PutMapping (path = {"participants/{participantId}", "participants/{participantId}"})
    public ResponseEntity<Participant> updateParticipant(@PathVariable("participantId") UUID participantId,
                                                         @RequestBody String firstName,
                                                         @RequestBody String lastName,
                                                         @RequestBody String email,
                                                         @RequestBody String iban,
                                                         @RequestBody String bic) {
        if (!participantRepository.existsById(participantId)) {
            return ResponseEntity.notFound().build();
        }
        if (isNullOrEmpty(firstName) || isNullOrEmpty(lastName)) {
            return ResponseEntity.badRequest().build();
        }
        Participant participant = participantRepository.getReferenceById(participantId);
        participant.setFirstName(firstName);
        participant.setLastName(lastName);
        participant.setEmail(email);
        participant.setIban(iban);
        participant.setBic(bic);
        participantRepository.save(participant);
        return ResponseEntity.ok(participant);
    }

    /**
     * Method that deletes a participant
     * @param participantId id of the participant
     * @return response entity of the participant
     */
    @DeleteMapping (path = {"participants/{participantId}", "participants/{participantId}"})
    public ResponseEntity<Participant> deleteParticipant(@PathVariable("participantId") UUID participantId) {
        if (!participantRepository.existsById(participantId)) {
            return ResponseEntity.notFound().build();
        }
        Participant participant = participantRepository.getReferenceById(participantId);
        //automatically removes participant from Event and its expenses due to CascadeType.REMOVE
        participantRepository.delete(participant);
        return ResponseEntity.ok(participant);
    }

    /**
     * Handles the GET: /{participantId}/expenses endpoint
     * @param participantId The id of a participant.
     * @return Returns a 200 OK status code and the Participants list when the participant exists in the DB.
     * Returns a 400 Bad Request status code when no participantId was provided.
     * Returns a 404 Not Found status code when no Participant was found with the provided participantId.
     */
    @GetMapping (path = {"participants/{participantId}/expenses"})
    public ResponseEntity<List<Expense>> getExpensesByParticipantId(@PathVariable("participantId") UUID participantId)
    {
        if (participantId == null) {
            return ResponseEntity.badRequest().build();
        }

        if(!participantRepository.existsById(participantId))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(participantRepository.getReferenceById(participantId).getMadeExpenses());
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
