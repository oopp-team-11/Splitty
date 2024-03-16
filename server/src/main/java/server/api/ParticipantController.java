package server.api;

import commons.Event;
import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.net.URI;
import java.util.UUID;

/**
 * Class that represents the participant controller
 */
@RestController
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    /**
     * Constructor
     * @param participantRepository participant repository
     * @param eventRepository event repository
     */
    public ParticipantController(ParticipantRepository participantRepository, EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
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
    @PostMapping (path = {"", "/"})
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
    @PutMapping (path = {"/{participantId}", "/{participantId}"})
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
    @DeleteMapping (path = {"/{participantId}", "/{participantId}"})
    public ResponseEntity<Participant> deleteParticipant(@PathVariable("participantId") UUID participantId) {
        if (!participantRepository.existsById(participantId)) {
            return ResponseEntity.notFound().build();
        }
        Participant participant = participantRepository.getReferenceById(participantId);
        //automatically removes participant from Event and its expenses due to CascadeType.REMOVE
        participantRepository.delete(participant);
        return ResponseEntity.ok(participant);
    }
}
