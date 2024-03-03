package server.api;


//import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
import commons.Event;
import server.database.EventRepository;

import java.time.LocalDateTime;
import java.util.Optional;

//import java.util.Collection;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository repo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    @GetMapping (path = {"/{invitationCode}", "/{invitationCode}/"})
    public ResponseEntity<Event> getEventByInvitationCode(@PathVariable("invitationCode") Long invitationCode) {
        if (invitationCode == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> event = repo.findById(invitationCode);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @PostMapping (path = {"", "/"})
    public ResponseEntity<Event> createEventWithTitle(String title) {
        if (isNullOrEmpty(title)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = new Event(title);
        event.setCreationDate(LocalDateTime.now());
        event.setLastActivity(event.getCreationDate());
        repo.save(event);
        return ResponseEntity.ok(event);
    }

    /*
    @JsonView(Event.Views.StartScreenView.class)
    @GetMapping (path = {"/startScreen", "/startScreen/"})
    public ResponseEntity<Collection<Event>> sendStartScreen() {
        var toReturn = repo.findAll();
        return ResponseEntity.ok(toReturn);
    }
    */
}
