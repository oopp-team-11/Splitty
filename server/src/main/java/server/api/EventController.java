package server.api;


import com.fasterxml.jackson.annotation.JsonView;
import commons.Participant;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import commons.Event;
import server.database.EventRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import commons.Views;

/**
 * Server-side EventController for the Event entity.
 * Handles the CRUD operations under all /events endpoints.
 */
@Controller
public class EventController {
    private final EventRepository repo;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor for the EventController.
     * Constructed automatically by Spring Boot.
     * @param template SimpMessagingTemplate
     * @param repo The EventRepository provided automatically by JPA
     */
    public EventController(SimpMessagingTemplate template, EventRepository repo) {
        this.template = template;
        this.repo = repo;
    }

    /**
     * Handles the GET: /events/{invitationCode} endpoint
     * @param invitationCode The invitationCode of an Event.
     * @return Returns a 200 OK status code and the Event object when the invitationCode exists in the DB.
     * Returns a 400 Bad Request status code when no invitationCode was provided.
     * Returns a 404 Not Found status code when no Event was found with the provided invitationCode.
     */
    @GetMapping (path = {"events/{invitationCode}", "events/{invitationCode}/"})
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
    @PostMapping (path = {"events", "events/"})
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
    @GetMapping(path = {"events", "events/"})
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
    @GetMapping (path = {"events/{invitationCode}/participants"})
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

    /**
     * Handles update websocket endpoint for event
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/event:update")
    public void updateEvent(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Event.class) {
            template.convertAndSendToUser(principal.getName(), "/queue/reply",
                    StatusEntity.badRequest("Payload should be an Event", true));
            return;
        }

        Event receivedEvent = (Event) payload;

        if (isNullOrEmpty(receivedEvent.getTitle())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Title should not be empty"));
            return;
        }

        if(!repo.existsById(receivedEvent.getId()))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Event not found", true));
            return;
        }

        Event event = repo.getReferenceById(receivedEvent.getId());
        event.setTitle(receivedEvent.getTitle());
        repo.save(event);

        template.convertAndSend("/topic/"+receivedEvent.getId(), event);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("event:update " + event.getId()));
    }

    /**
     * Handles read websocket endpoint for event
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/event:read")
    public void readEvent(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != UUID.class) {
            template.convertAndSendToUser(principal.getName(), "/queue/reply",
                    StatusEntity.badRequest("Payload should be a UUID", true));
            return;
        }

        UUID invitationCode = (UUID) payload;

        if(!repo.existsById(invitationCode))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Event not found", true));
            return;
        }

        Event event = repo.getReferenceById(invitationCode);

        template.convertAndSend("/topic/"+invitationCode, event);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("event:read " + event.getId()));
    }

    /**
     * Handles delete websocket endpoint for event
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/event:delete")
    public void deleteEvent(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Event.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Payload should be an Event", true));
            return;
        }

        /*TODO: Implement admin passcode verification*/
//        if()
//        {
//            template.convertAndSendToUser(principal.getName(),"/queue/reply",
//                    new ErrorMessage(new IllegalAccessException()));
//            return;
//        }

        Event receivedEvent = (Event) payload;
        if(!repo.existsById(receivedEvent.getId()))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Event not found", true));
            return;
        }

        Event event = repo.getReferenceById(receivedEvent.getId());
        repo.delete(event);

        template.convertAndSend("/topic/"+receivedEvent.getId(), event);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("event:delete " + event.getId()));
    }

}
