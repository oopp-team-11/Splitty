package server.api;

import commons.Event;
import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.net.URI;

@RestController
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    public ParticipantController(ParticipantRepository participantRepository, EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @PostMapping (path = {"", "/"})
    public ResponseEntity<Participant> createParticipant(@RequestBody Participant participant) {
        if (participant == null
                || isNullOrEmpty(participant.getFirstName()) || isNullOrEmpty(participant.getLastName())) {
            return ResponseEntity.badRequest().build();
        }
        Event event = participant.getEvent();
        if (!eventRepository.existsById(event.getId()) || participantRepository.existsById(participant.getId())) {
            return ResponseEntity.badRequest().build();
        }
        eventRepository.save(event); //saves participant reference in event model
        participantRepository.save(participant);
        return ResponseEntity.created(URI.create("/participants/" + participant.getId())).body(participant);
    }

    @PutMapping (path = {"/{participantId}", "/{participantId}"})
    public ResponseEntity<Participant> updateParticipant(@PathVariable("participantId") Long participantId,
                                                         @RequestBody Participant participant) {
        if (!participantRepository.existsById(participantId)) {
            return ResponseEntity.notFound().build();
        }
        if (participant == null || participantId != participant.getId()
                || isNullOrEmpty(participant.getFirstName()) || isNullOrEmpty(participant.getLastName())) {
            return ResponseEntity.badRequest().build();
        }
        participantRepository.save(participant);
        return ResponseEntity.ok(participant);
    }

    @DeleteMapping (path = {"/{participantId}", "/{participantId}"})
    public ResponseEntity<Participant> deleteParticipant(@PathVariable("participantId") Long participantId) {
        if (!participantRepository.existsById(participantId)) {
            return ResponseEntity.notFound().build();
        }
        Participant participant = participantRepository.getReferenceById(participantId);
        //automatically removes participant from Event and its expenses due to CascadeType.REMOVE
        participantRepository.delete(participant);
        return ResponseEntity.ok(participant);
    }
}
