package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.EventLastActivityService;
import server.database.InvolvedRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class InvolvedControllerTest {

    @InjectMocks
    private InvolvedController involvedController;

    @Mock
    private InvolvedRepository involvedRepository;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private EventLastActivityService eventLastActivityService;

    @Mock
    private TestParticipantRepository participantRepository;

    @Mock
    private TestExpenseRepository expenseRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateInvolved_ValidRequest() {
        UUID id = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID expenseId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();


        Event event = new Event(eventId, "Test Event", LocalDateTime.now(), LocalDateTime.now());

        Participant participant = new Participant(participantId, "John", "Doe" , "john.doe@example.com"
        , "DE89370400440532013000", "COBADEFFXXX", event.getId());

        Expense expense = new Expense(expenseId,"Lunch", 10.0, participant.getId(), event.getId());
        expense.setPaidBy(participant);

        Involved involved = new Involved(id, true, expenseId, participantId);
        involved.setParticipant(participant);
        involved.setExpense(expense);


        when(involvedRepository.existsById(involved.getId())).thenReturn(true);
        when(involvedRepository.getReferenceById(involved.getId())).thenReturn(involved);
        when(involvedRepository.save(involved)).thenReturn(involved);

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        StatusEntity result = involvedController.updateInvolved(involved);
        StatusEntity expected = StatusEntity.ok("involved:update " + involved.getId());
        ArgumentCaptor<Involved> captor = ArgumentCaptor.forClass(Involved.class);


        verify(involvedRepository, times(1)).getReferenceById(involved.getId());
        verify(involvedRepository, times(1)).save(involved);
        verify(eventLastActivityService, times(1)).updateLastActivity(participant.getEventId());
        verify(template, times(1)).convertAndSend("/topic/" + involved.getId() + "/involved:update", involved);
        verify(template, times(1)).convertAndSend(eq("/topic/" + involved.getId() + "/involved:update"), captor.capture());


        assertEquals(involved, captor.getValue());
        assertEquals(expected, result);
    }

    @Test
    public void testUpdateInvolved_EmptyRequestBody() {
        StatusEntity result = involvedController.updateInvolved(null);
        StatusEntity expected = StatusEntity.badRequest(true, "Involved object not found in request body");
        verify(involvedRepository, times(0)).getReferenceById(any(UUID.class));
        verify(involvedRepository, times(0)).save(any(Involved.class));
        verify(eventLastActivityService, times(0)).updateLastActivity(any(UUID.class));

        assertEquals(expected, result);
    }

    @Test
    public void testUpdateInvolved_InvalidInvolvedId() {
        UUID id = UUID.randomUUID();
        Involved involved = new Involved(id, true, UUID.randomUUID(), UUID.randomUUID());
        when(involvedRepository.existsById(involved.getId())).thenReturn(false);

        StatusEntity result = involvedController.updateInvolved(involved);
        StatusEntity expected = StatusEntity.badRequest(true, "Involved object not found in database");

        verify(involvedRepository, times(0)).getReferenceById(involved.getId());
        verify(involvedRepository, times(0)).save(any(Involved.class));
        verify(eventLastActivityService, times(0)).updateLastActivity(any(UUID.class));

        assertEquals(expected, result);
    }
}