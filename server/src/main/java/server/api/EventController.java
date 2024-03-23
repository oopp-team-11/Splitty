package server.api;


import com.fasterxml.jackson.annotation.JsonView;
import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Event;
import server.database.EventRepository;

import java.time.LocalDateTime;
import java.util.*;

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
    public ResponseEntity<Event> getEventByInvitationCode(@PathVariable("invitationCode") UUID invitationCode) {
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
     * Handles the GET: /events endpoint with query parameters
     * @param query The query parameter provided by the client.
     * @param codes The List of invitationCodes provided by the client from their config.json
     * @return Returns a 200 OK status code and a List of partial Event objects containing existing invitationCodes
     * and appropriate titles.
     * Returns a 400 Bad Request status code when no codes were provided.
     */
    @JsonView(Views.UpdateInvitationsCodes.class)
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Event>> updateRecentlyAccessedEvents(@RequestParam("query") String query,
                                                                    @RequestParam("invitationCodes") UUID[] codes) {
        if (codes == null || !"titles".equals(query)) {
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

    /**
     * Handles the GET: /{invitationCode}/participants endpoint
     * @param invitationCode The invitationCode of an Event.
     * @return Returns a 200 OK status code and the Participants list when the invitationCode exists in the DB.
     * Returns a 400 Bad Request status code when no invitationCode was provided.
     * Returns a 404 Not Found status code when no Event was found with the provided invitationCode.
     */
    @GetMapping (path = {"/{invitationCode}/participants"})
    public ResponseEntity<List<Participant>> getParticipantsByInvitationCode(
            @PathVariable("invitationCode") UUID invitationCode)
    {
        if (invitationCode == null) {
            return ResponseEntity.badRequest().build();
        }

        if(!repo.existsById(invitationCode))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(repo.getReferenceById(invitationCode).getParticipants());
    }

}
