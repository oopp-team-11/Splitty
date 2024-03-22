package server.api;

import commons.Event;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;

import java.security.Principal;

/**
 * Controller for websocket endpoints.
 */
@Controller
public class WebSocketController {

    private final EventRepository repo;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor for WebSocketController
     * @param template SimpMessagingTemplate
     * @param repo Event repository
     */
    public WebSocketController(SimpMessagingTemplate template,
                               EventRepository repo) {
        this.repo = repo;
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
        event.setCreationDate(receivedEvent.getCreationDate());
        event.setLastActivity(receivedEvent.getLastActivity());
        repo.save(event);

        template.convertAndSend("/topic/"+receivedEvent.getId(), event);
        template.convertAndSendToUser(principal.getName(), "queue/reply",
                StatusEntity.ok("event:update " + event.getId()));
    }

    /**
     * Handles delete websocket endpoint for event
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/event:delete")
//    @SendTo("/topic/{invitationCode}")
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
        template.convertAndSendToUser(principal.getName(), "queue/reply",
                StatusEntity.ok("event:delete " + event.getId()));
    }
}
