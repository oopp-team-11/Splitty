package server.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
import commons.Event;
import server.database.EventRepository;
@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository repo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    @PutMapping (path = {"/{invitationCode}/participants", "/{invitationCode}/participants/"})
    public ResponseEntity<Event> add(@PathVariable("invitationCode") String invitationCode, @RequestBody Participant participant) {
        // TODO: More complex correctness check.
        if (isNullOrEmpty(invitationCode)) {
            return ResponseEntity.badRequest().build();
        }
        if (isNullOrEmpty(participant.getFirstName()) ||
                isNullOrEmpty(participant.getLastName()) ||
                isNullOrEmpty(participant.getBic()) ||
                isNullOrEmpty(participant.getEmail()) ||
                isNullOrEmpty(participant.getInvitationCode()) ||
                isNullOrEmpty(participant.getIban())) {
            return ResponseEntity.badRequest().build();
        }
        var event_by_invitationCode = repo.findOneByInvitationCode(invitationCode);
        if (event_by_invitationCode == null) {
            return ResponseEntity.badRequest().build();
        }
        Long id = event_by_invitationCode.getFirst().getId();
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        Event event_to_update = repo.getReferenceById(id);
        event_to_update.getParticipants().add(participant);
        Event saved = repo.save(event_to_update);
        return ResponseEntity.ok(saved);
    }
}