package server.api;
import com.fasterxml.jackson.annotation.JsonView;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import commons.Event;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import commons.Views;

/**
 * Server-side EventController for the Event entity.
 * Handles the CRUD operations under all /events endpoints.
 */
@Controller
public class EventController {
    private final EventRepository repo;
    private final Map<UUID, List<DeferredResult<ResponseEntity<Map<UUID, String>>>>> deferredResults;

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
        this.deferredResults = new ConcurrentHashMap<>();
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
     * Handles update websocket endpoint for event
     *
     * @param receivedEvent Event received from the client
     * @return returns statusEntity<String> to user with status code and error message
     */
    @MessageMapping("/event:update")
    @SendToUser("/queue/reply")
    public StatusEntity<String> updateEvent(Event receivedEvent)
    {
        if(receivedEvent == null)
            return StatusEntity.badRequest("Event object not found in message body", true);

        if (isNullOrEmpty(receivedEvent.getTitle()))
            return StatusEntity.badRequest("Event title should not be empty", true);

        if(!repo.existsById(receivedEvent.getId()))
            return StatusEntity.notFound("Event not found", true);

        Event event = repo.getReferenceById(receivedEvent.getId());
        event.setTitle(receivedEvent.getTitle());
        //TODO: Call update last activity service
        repo.save(event);

        template.convertAndSend("/topic/" + receivedEvent.getId() + "/event:update", event);
        return StatusEntity.ok("event:update " + event.getId());
    }

    /**
     * Handles read websocket endpoint for event
     *
     * @param invitationCode invitationCode of the requested event
     * @return returns a StatusEntity<Event> body contains Event if status code is OK
     * returns null in body otherwise
     */
    @MessageMapping("/event:read")
    @SendToUser("/queue/event:read")
    public StatusEntity<Event> readEvent(UUID invitationCode)
    {
        if(invitationCode == null)
            return StatusEntity.badRequest(null, true);
        if(!repo.existsById(invitationCode))
            return StatusEntity.notFound(null, true);

        Event event = repo.getReferenceById(invitationCode);

        return StatusEntity.ok(event);
    }

    /**
     * Handles delete websocket endpoint for event
     * @param receivedEvent Event that we want to delete
     * @return StatusEntity<String> body contains description of success/failure
     */
    @MessageMapping("/event:delete")
    @SendToUser("/queue/reply")
    public StatusEntity<String> deleteEvent(Event receivedEvent)
    {
        /*TODO: Implement admin passcode verification*/

        if(!repo.existsById(receivedEvent.getId()))
        {
            return StatusEntity.notFound("Event not found", true);
        }

        Event event = repo.getReferenceById(receivedEvent.getId());
        repo.delete(event);

        template.convertAndSend("/topic/" + receivedEvent.getId() + "/event:delete", event);
        return StatusEntity.ok("event:delete " + event.getId());
    }

    /**
     * Handles the PUT: /{invitationCode} endpoint
     * @param invitationCode The invitationCode of an Event.
     * @param newTitle The new title of the Event from the RequestBody.
     * @return Returns a 200 OK status code when the Event title was successfully updated.
     * Returns a 400 Bad Request status code when no invitationCode or newTitle was provided.
     */
    @PutMapping(path = {"/events/{invitationCode}/", "/events/{invitationCode}"})
    public ResponseEntity<Void> updateEventTitle(@PathVariable("invitationCode") UUID invitationCode,
                                                 @RequestBody String newTitle) {
        Optional<Event> eventOptional = repo.findById(invitationCode);
        if (eventOptional.isEmpty() || newTitle == null || newTitle.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Event event = eventOptional.get();
        event.setTitle(newTitle);
        repo.save(event);

        eventUpdated(invitationCode, newTitle);

        return ResponseEntity.ok().build();
    }

    /**
     * Handles the DELETE: /{invitationCode} endpoint
     * @param invitationCode The invitationCode of an Event.
     * @return Returns a 200 OK status code when the Event was successfully deleted.
     * Returns a 400 Bad Request status code when no invitationCode was provided.
     */
    @DeleteMapping(path = "/events/{invitationCode}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("invitationCode") UUID invitationCode) {
        if (!repo.existsById(invitationCode)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(invitationCode);

        eventUpdated(invitationCode, null);

        return ResponseEntity.ok().build();
    }

    /**
     * Handles the GET: /updates endpoint
     * @param query The query parameter provided by the client.
     * @param invitationCodes The List of invitationCodes provided by the client from their config.json
     * @return Returns a DeferredResult with a 200 OK status code and a Map of updated Events.
     * Returns a 400 Bad Request status code when no invitationCodes were provided.
     */
    @GetMapping(path = {"/events/updates", "/events/updates/"})
    public DeferredResult<ResponseEntity<Map<UUID, String>>> getUpdatedEvents(@RequestParam("query") String query,
                                                                              @RequestParam("invitationCodes")
                                                                              List<UUID> invitationCodes) {
        if (invitationCodes == null || !"updates".equals(query)) {
            DeferredResult<ResponseEntity<Map<UUID, String>>> badRequestResult = new DeferredResult<>();
            badRequestResult.setResult(ResponseEntity.badRequest().build());
            return badRequestResult;
        }

        DeferredResult<ResponseEntity<Map<UUID, String>>> deferredResult = new DeferredResult<>(5000L);

        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out."));
        });

        deferredResult.onCompletion(() -> {
            for (UUID invitationCode : invitationCodes) {
                deferredResults.remove(invitationCode, deferredResult);
            }
        });

        for (UUID invitationCode : invitationCodes) {
            deferredResults.computeIfAbsent(invitationCode, key -> new ArrayList<>()).add(deferredResult);
        }

        return deferredResult;
    }

    private void eventUpdated(UUID invitationCode, String updatedTitle) {
        List<DeferredResult<ResponseEntity<Map<UUID, String>>>> results = deferredResults.get(invitationCode);
        if (results != null) {
            Iterator<DeferredResult<ResponseEntity<Map<UUID, String>>>> iterator = results.iterator();
            while (iterator.hasNext()) {
                DeferredResult<ResponseEntity<Map<UUID, String>>> deferredResult = iterator.next();
                Map<UUID, String> updatedEvent = new HashMap<>();
                updatedEvent.put(invitationCode, updatedTitle);
                deferredResult.setResult(ResponseEntity.ok(updatedEvent));
                iterator.remove();
            }
        }
    }

}
