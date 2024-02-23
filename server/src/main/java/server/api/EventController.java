package server.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
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
    public ResponseEntity<Participant> add(@PathVariable("invitationCode") String invitationCode, @RequestBody Participant participant) {
        if (isNullOrEmpty(participant.getFirstName()) ||
                isNullOrEmpty(participant.getLastName()) ||
                isNullOrEmpty(participant.getBic()) ||
                isNullOrEmpty(participant.getEmail()) ||
                isNullOrEmpty(participant.getInvitationCode()) ||
                isNullOrEmpty(participant.getIban())) {
            return ResponseEntity.badRequest().build();
        }
        Participant saved = repo.save(participant);
        return ResponseEntity.ok(saved);
    }
}
