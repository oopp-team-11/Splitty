package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import commons.Event;
import java.time.LocalDateTime;

/**
 * Service for updating the last activity of an event
 */
@Service
public class EventLastActivityService {

    private final EventRepository eventRepository;
    private final SimpMessagingTemplate template;

    /**
     * Constructor for EventLastActivityService
     * @param eventRepository the event repository
     * @param template the messaging template
     */
    @Autowired
    public EventLastActivityService(EventRepository eventRepository, SimpMessagingTemplate template) {
        this.eventRepository = eventRepository;
        this.template = template;
    }

    /**
     * Update the last activity of an event
     * @param event the event to update
     */
    public void updateLastActivity(Event event) {
        Event tempEvent = eventRepository.getReferenceById(event.getId());
        tempEvent.setLastActivity(LocalDateTime.now());
        eventRepository.save(tempEvent);

        template.convertAndSend("/topic/admin/event:update", event);
    }
}