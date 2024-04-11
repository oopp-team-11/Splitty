package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.EventLastActivityService;
import server.database.EventRepository;
import commons.Event;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class EventLastActivityServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SimpMessagingTemplate template;

    @InjectMocks
    private EventLastActivityService eventLastActivityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateLastActivityEventFound() {
        UUID invitationCode = UUID.randomUUID();
        Event event = new Event("Title");
        when(eventRepository.getReferenceById(invitationCode)).thenReturn(event);

        eventLastActivityService.updateLastActivity(invitationCode);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

        verify(eventRepository, times(1)).getReferenceById(invitationCode);
        verify(eventRepository, times(1)).save(event);
        verify(template, times(1)).convertAndSend(destinationCaptor.capture(),
                eventCaptor.capture());
        assertEquals("/topic/admin/event:update", destinationCaptor.getValue());
        Event sentEvent = eventCaptor.getValue();
        assertEquals(event.getTitle(), sentEvent.getTitle());
        assertNotNull(sentEvent.getLastActivity());
    }

    @Test
    public void testUpdateLastActivityEventNotFound() {
        UUID invitationCode = UUID.randomUUID();
        when(eventRepository.getReferenceById(invitationCode)).thenThrow(new RuntimeException());

        eventLastActivityService.updateLastActivity(invitationCode);

        verify(eventRepository, times(1)).getReferenceById(invitationCode);
        verify(eventRepository, times(0)).save(any(Event.class));
        verify(template, times(0)).convertAndSend(anyString(), any(Event.class));
    }
}