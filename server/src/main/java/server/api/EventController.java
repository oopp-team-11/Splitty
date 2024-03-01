package server.api;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
import commons.Event;
import server.database.EventRepository;

import java.util.Collection;

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
    public ResponseEntity<Event> add(@PathVariable("invitationCode") Long invitationCode, @RequestBody Participant participant) {
        // TODO: More complex correctness check.

        if (invitationCode == null) {
            return ResponseEntity.badRequest().build();
        }
        if (isNullOrEmpty(participant.getFirstName()) ||
                isNullOrEmpty(participant.getLastName()) ||
                isNullOrEmpty(participant.getBic()) ||
                isNullOrEmpty(participant.getEmail()) ||
                isNullOrEmpty(participant.getIban())) {
            return ResponseEntity.badRequest().build();
        }
        Event event_to_update = repo.getReferenceById(invitationCode);
        if (event_to_update == null) {
            return ResponseEntity.badRequest().build();
        }
        event_to_update.getParticipants().add(participant);
        Event saved = repo.save(event_to_update);
        return ResponseEntity.ok(saved);
    }




    @JsonView(Event.Views.StartScreenView.class)
    @GetMapping (path = {"/startScreen", "/startScreen/"})
    public ResponseEntity<Collection<Event>> sendStartScreen() {
        var toReturn = repo.findAll();
        return ResponseEntity.ok(toReturn);
    }
}
