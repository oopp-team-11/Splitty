package server.api;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Event;
import server.database.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import commons.Views;


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
    public ResponseEntity<Event> createEventWithTitle(@RequestBody String title) {
        if (isNullOrEmpty(title)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = new Event(title);
        event.setCreationDate(LocalDateTime.now());
        event.setLastActivity(event.getCreationDate());
        repo.save(event);
        return ResponseEntity.ok(event);
    }


    @JsonView(Views.UpdateInvitationsCodes.class)
    @GetMapping (path = {"?query=title&invitationCodes={}"})
    public ResponseEntity<List<Event>> updateRecentlyAccessedEvents(@RequestParam Long[] codes) {
        if (codes == null) {
            return ResponseEntity.badRequest().build();
        }
        if (codes.length == 0) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<Event> updatedEvents = new ArrayList<>();
        for (var code : codes) {
            Optional<Event> event = repo.findById(code);
            event.ifPresent(updatedEvents::add);
        }
        return ResponseEntity.ok(updatedEvents);
    }

}
