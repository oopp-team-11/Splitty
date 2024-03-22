package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
import commons.StatusEntity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller for websocket endpoints.
 */
@Controller
public class WebSocketController {

    private final EventRepository repo;
    private final ParticipantRepository participantRepository;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor for WebSocketController
     * @param template SimpMessagingTemplate
     * @param repo Event repository
     * @param participantRepository Participant repository
     */
    public WebSocketController(SimpMessagingTemplate template,
                               EventRepository repo, ParticipantRepository participantRepository) {
        this.repo = repo;
        this.participantRepository = participantRepository;
        this.template = template;
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
     * Handles the messages from server
     *
     * @param headers Message headers
     * @param payload Updated object
     */
    @MessageMapping("/event")
    public void incomingMessage(StompHeaders headers, Object payload){
        String modelType = headers.getFirst("model");
        String methodType = headers.getFirst("method");
        switch (modelType) {
            case "Event" -> {
                Event receivedEvent = (Event) payload;
                ResponseEntity<Event> response = receiveEvent(methodType, receivedEvent);
            }
            case "Participant" -> {
                Participant receivedParticipant = (Participant) payload;
                ResponseEntity<Participant> response = receiveParticipant(methodType, receivedParticipant);
            }
            case "Expense" -> {
                Expense receivedExpense = (Expense) payload;
                ResponseEntity<Expense> response = receiveExpense(methodType, receivedExpense);
            }
            case null, default -> System.out.println("Model type not specified in the message headers");
        }
    }

    //TODO: This method should be split into three and fixed
    /**
     * Handles websocket endpoints for event
     * @param methodType type of endpoint
     * @param receivedEvent received event that we want to interact with
     * @return server response
     */
    public ResponseEntity<Event> receiveEvent(String methodType, Event receivedEvent){
        switch(methodType){
            case "create" -> {
                if (isNullOrEmpty(receivedEvent.getTitle())) {
                    return ResponseEntity.badRequest().build();
                }
                Event event = new Event(receivedEvent.getTitle());
                event.setCreationDate(LocalDateTime.now());
                event.setLastActivity(event.getCreationDate());
                repo.save(event);
                return ResponseEntity.ok(event);
            }
            case "update" -> {
                if (receivedEvent.getId() == null) {
                    return ResponseEntity.badRequest().build();
                }
                Optional<Event> event = repo.findById(receivedEvent.getId());
                return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
            }
            case "delete" -> {
                if (receivedEvent.getId() == null) {
                    return ResponseEntity.badRequest().build();
                }
                if (!repo.existsById(receivedEvent.getId())) {
                    return ResponseEntity.notFound().build();
                }
                Event event = repo.getReferenceById(receivedEvent.getId());
                repo.delete(event);
                return ResponseEntity.ok(event);
            }
            case null, default -> {return ResponseEntity.badRequest().build();}
        }
    }

//    //todo: Check if the method works with Principal
//    //todo: if not, then change it to SimpMessageHeaderAccessor
//    //todo: and implement all the necessary interfaces
//    /**
//     * Handles create websocket endpoint for event
//     * @param principal connection data about user
//     * @param headers Stomp headers
//     * @param payload content of a websocket message
//     * @return Status entity
//     */
//    @MessageMapping("/event:create")
//    @SendTo("/topic/{invitationCode}")
//    public StatusEntity<Event> createEvent(Principal principal, @Headers StompHeaders headers, @Payload Object payload)
//    {
//        // If true: payload is not of event class
//        if(payload.getClass() != Event.class) {
//            template.convertAndSendToUser(principal.getName(),"/queue/reply",
//                    new ErrorMessage(new IllegalArgumentException()));
//            return StatusEntity.badRequest(null);
//        }
//
//        Event receivedEvent = (Event) payload;
//
//        // If true: headers are mismatched
//        if(!headers.getFirst("model").equals("Event")
//            || !headers.getFirst("method").equals("create")) {
//            template.convertAndSendToUser(principal.getName(),"/queue/reply",
//                    new ErrorMessage(new IllegalArgumentException()));
//            return StatusEntity.badRequest(receivedEvent);
//        }
//
//        // If true: event has null or empty title
//        if (isNullOrEmpty(receivedEvent.getTitle())) {
//            template.convertAndSendToUser(principal.getName(),"/queue/reply",
//                    new ErrorMessage(new IllegalArgumentException()));
//            return StatusEntity.badRequest(receivedEvent);
//        }
//
//        Event event = new Event(receivedEvent.getTitle());
//        event.setCreationDate(LocalDateTime.now());
//        event.setLastActivity(event.getCreationDate());
//        repo.save(event);
//
//        template.convertAndSend(event);
//        return StatusEntity.ok(event);
//    }

    /**
     * Handles update websocket endpoint for event
     * @param principal connection data about user
     * @param headers Stomp headers
     * @param payload content of a websocket message
     * @return Status entity (status code, body, boolean unsolvable)
     */
    @MessageMapping("/event:update")
    @SendTo("/topic/{invitationCode}")
    public StatusEntity<String> updateEvent(Principal principal, @Headers StompHeaders headers, @Payload Object payload)
    {
        // If true: payload is not of event class
        if(payload.getClass() != Event.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.badRequest("Payload should be an Event", true);
        }

        Event receivedEvent = (Event) payload;

        // If true: headers are mismatched
        if(!headers.getFirst("model").equals("Event")
                || !headers.getFirst("method").equals("update")) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.badRequest("Headers do not match the method", true);
        }

        if (isNullOrEmpty(receivedEvent.getTitle())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.badRequest("Title should not be empty");
        }

        if(!repo.existsById(receivedEvent.getId()))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.notFound("Event not found", true);
        }

        Event event = repo.getReferenceById(receivedEvent.getId());
        event.setTitle(receivedEvent.getTitle());
        event.setCreationDate(receivedEvent.getCreationDate());
        event.setLastActivity(receivedEvent.getLastActivity());
        repo.save(event);

        template.convertAndSend(event);

        return StatusEntity.ok("event:update " + event.getId());
    }

    /**
     * Handles delete websocket endpoint for event
     * @param principal connection data about user
     * @param headers Stomp headers
     * @param payload content of a websocket message
     * @return Status entity (status code, body, boolean unsolvable)
     */
    @MessageMapping("/event:delete")
    @SendTo("/topic/{invitationCode}")
    public StatusEntity<String> deleteEvent(Principal principal, @Headers StompHeaders headers, @Payload Object payload)
    {
        if(payload.getClass() != Event.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.badRequest("Payload should be an Event", true);
        }

        if(!headers.getFirst("model").equals("Event")
                || !headers.getFirst("method").equals("delete")) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.badRequest("Headers do not match the method", true);
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
                    new ErrorMessage(new IllegalArgumentException()));
            return StatusEntity.notFound("Event not found", true);
        }

        Event event = repo.getReferenceById(receivedEvent.getId());
        repo.delete(event);

        template.convertAndSend(event);
        return StatusEntity.ok("event:delete " + event.getId());
    }

    //TODO: This method should be split into three and fixed
    /**
     * Handles websocket endpoints for participant
     * @param methodType type of endpoint
     * @param receivedParticipant received participant that we want to interact with
     * @return server response
     */
    public ResponseEntity<Participant> receiveParticipant(String methodType, Participant receivedParticipant){
        switch(methodType){
            case "create" -> {
                if (!repo.existsById(receivedParticipant.getEvent().getId())) {
                    return ResponseEntity.notFound().build();
                }
                if (isNullOrEmpty(receivedParticipant.getFirstName())
                        || isNullOrEmpty(receivedParticipant.getLastName())) {
                    return ResponseEntity.badRequest().build();
                }
                Event event = repo.getReferenceById(receivedParticipant.getEvent().getId());
                Participant participant = new Participant(event,
                        receivedParticipant.getFirstName(),
                        receivedParticipant.getLastName(),
                        receivedParticipant.getEmail(),
                        receivedParticipant.getIban(),
                        receivedParticipant.getBic()
                );
                repo.save(event); //saves participant reference in event model
                participantRepository.save(participant);
                return ResponseEntity.created(URI.create("/participants/" + participant.getId())).body(participant);
            }
            case "update" -> {
                if (!participantRepository.existsById(receivedParticipant.getId())) {
                    return ResponseEntity.notFound().build();
                }
                if (isNullOrEmpty(receivedParticipant.getFirstName())
                        || isNullOrEmpty(receivedParticipant.getLastName())) {
                    return ResponseEntity.badRequest().build();
                }
                Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
                participant.setFirstName(receivedParticipant.getFirstName());
                participant.setLastName(receivedParticipant.getLastName());
                participant.setEmail(receivedParticipant.getEmail());
                participant.setIban(receivedParticipant.getIban());
                participant.setBic(receivedParticipant.getBic());
                participantRepository.save(participant);
                return ResponseEntity.ok(participant);
            }
            case "delete" -> {
                if (!participantRepository.existsById(receivedParticipant.getId())) {
                    return ResponseEntity.notFound().build();
                }
                Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
                //automatically removes participant from Event and its expenses due to CascadeType.REMOVE
                participantRepository.delete(participant);
                return ResponseEntity.ok(participant);
            }
            case null, default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    //TODO: Expense controller / Expense repository without it we cannot do anything
    //TODO: This method should be split into three and fixed
    /**
     * Handles websocket endpoints for expense.
     * @param methodType type of endpoint
     * @param receivedExpense received expense that we want to interact with
     * @return server response
     */
    public ResponseEntity<Expense> receiveExpense(String methodType, Expense receivedExpense){
        switch(methodType){
            case "create" -> {
                return ResponseEntity.status(501).build();
            }
            case "update" -> {
                return ResponseEntity.status(501).build();
            }
            case "delete" -> {
                return ResponseEntity.status(501).build();
            }
            case null, default -> {return ResponseEntity.status(501).build();}
        }
    }
}
