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

/**
 * Server-side EventController for the Event entity.
 * Handles the CRUD operations under all /events endpoints.
 */
@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository repo;

    /**
     * Constructor for the EventController.
     * Constructed automatically by Spring Boot.
     * @param repo The EventRepository provided automatically by JPA
     */
    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * Handles the GET: /events/{invitationCode} endpoint
     * @param invitationCode The invitationCode of an Event.
     * @return Returns a 200 OK status code and the Event object when the invitationCode exists in the DB.
     * Returns a 400 Bad Request status code when no invitationCode was provided.
     * Returns a 404 Not Found status code when no Event was found with the provided invitationCode.
     */
    @GetMapping (path = {"/{invitationCode}", "/{invitationCode}/"})
    public ResponseEntity<Event> getEventByInvitationCode(@PathVariable("invitationCode") Long invitationCode) {
        if (invitationCode == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> event = repo.findById(invitationCode);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


    /**
     * Checks whether a provided String is null or empty.
     * @param string The String to be checked.
     * @return Returns a boolean.
     */
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Handles the POST: /events endpoint.
     * Sets the creationDate of the Event.
     * @param title The title of the Event from the RequestBody.
     * @return Returns a 200 OK status code and the invitationCode when the Event was successfully saved.
     * Returns a 400 Bad Request status code when no title was provided.
     */
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


    /**
     * Handles the GET: /events?query=title&invitationCodes={} endpoint
     * @param codes The List of invitationCodes provided by the client from their config.json
     * @return Returns a 200 OK status code and a List of partial Event objects containing existing invitationCodes
     * and appropriate titles.
     * Returns a 400 Bad Request status code when no codes were provided.
     */
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
