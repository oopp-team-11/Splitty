package server.api;

import commons.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.EventLastActivityService;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.InvolvedRepository;
import server.database.ParticipantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExpenseControllerTest {

    private ExpenseRepository expenseRepository;
    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;
    private ExpenseController expenseController;
    private InvolvedRepository involvedRepository;
    private SimpMessagingTemplate messagingTemplate;
    private EventLastActivityService eventLastActivityService;


    @BeforeEach
    public void setup() {
        expenseRepository = new TestExpenseRepository();
        eventRepository = new TestEventRepository();
        participantRepository = new TestParticipantRepository();
        involvedRepository = new TestInvolvedRepository();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventLastActivityService = new EventLastActivityService(eventRepository, messagingTemplate);
        expenseController = new ExpenseController(eventRepository, expenseRepository,
                participantRepository, involvedRepository, messagingTemplate, eventLastActivityService);
    }

    private static void setId(Expense toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setId(Involved toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    @Test
    void checkCreateExpense() {
        Event event = eventRepository.save(new Event("testEvent"));

        System.out.println(event.getId());

        Participant participant = participantRepository.save(new Participant(
                event,
                "abc",
                "def",
                null,
                null
        ));
        Expense expense = new Expense(participant, "expense", 21.37, LocalDate.now(), null);
        Involved involved = new Involved(false, expense, participant);
        expense.setInvolveds(List.of(involved));

        System.out.println(expense.getInvitationCode());
        assertEquals(StatusEntity.StatusCode.OK, expenseController.createExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:create"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
        List<Involved> involveds = expense.getInvolveds();
        for(Involved thisInvolved : involveds)
            thisInvolved.setExpenseId(sentExpense.getId());
        assertEquals(involveds, sentExpense.getInvolveds());
        assertEquals(expense.getDate(), sentExpense.getDate());
        assertEquals(expense.getAmount() / expense.getInvolveds().size(), sentExpense.getAmountOwed());
    }

    @Test
    void expenseParticipantNotFound() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                 null, null, UUID.randomUUID());
        // TODO: maybe some smarter initialising of the involveds
        Expense expense = new Expense(sentParticipant, "expense", 21.37, null, null);
        expense = expenseRepository.save(expense);

        assertEquals(StatusEntity.notFound(true, "Provided participant who paid for the expense does not exist"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseNull() {
        assertEquals(StatusEntity.badRequest(true, "Expense object not found in message body"),
                expenseController.isExpenseBadRequest(null));
    }

    @Test
    void ExpenseTitleNull() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                 null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, null, 69, null, null);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {
        }
        assertEquals(StatusEntity.badRequest(true, "Expense title should not be empty"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseAmountNotPositive() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                 null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 0, null, null);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "Amount should be positive"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseGetPaidByIDNull() {
        Participant sentParticipant = new Participant(null, "name", "surname",
                 null, null, UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, "expense", 69, null, null);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "Id of participant who paid should be provided"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseInvitationCodeNull() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                 null, null, null);
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, null, null);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "InvitationCode of event should be provided"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseDateNull() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, null, null);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "Date should be provided"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseInvolvedListNull() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, LocalDate.now(), null);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "Expense should involve a participant"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseInvolvedListEmpty() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, LocalDate.now(), null);
        expense.setInvolveds(List.of());
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "Expense should involve a participant"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseInvolvedParticipantNotFound() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, LocalDate.now(), null);
        Involved involved = new Involved(false, expense, sentParticipant);
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        Involved involved2 = new Involved(false, expense, participant);
        expense.setInvolveds(List.of(involved, involved2));
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.notFound(true, "Involved participant not found"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseDuplicateParticipantInvolveds() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, LocalDate.now(), null);
        Involved involved = new Involved(false, expense, sentParticipant);
        Involved involved2 = new Involved(false, expense, sentParticipant);
        expense.setInvolveds(List.of(involved, involved2));
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest(true, "Expense cannot involve duplicates of participants"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseOKRequest() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                 null, null, UUID.randomUUID());
        sentParticipant = participantRepository.save(sentParticipant);
        Expense expense = new Expense(sentParticipant, "expense", 69, LocalDate.now(), null);
        Involved involved = new Involved(false, expense, sentParticipant);
        expense.setInvolveds(List.of(involved));
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.ok((ExpenseList) null), expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void checkUpdateExpense() {
        Event event = eventRepository.save(new Event("testEvent"));
        Participant participant = participantRepository.save(new Participant(UUID.randomUUID(), "name",
                "surname", null, null, event.getId()));
        Expense expense = new Expense(participant, "expense", 21.37, LocalDate.now(), null);
        Involved involved = new Involved(true, expense, participant);
        try {
            setId(involved, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        List<Involved> involveds = new InvolvedList();
        involveds.add(involved);
        expense.setInvolveds(involveds);
        expense = expenseRepository.save(expense);
        expense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(), expense.getPaidById(),
                expense.getInvitationCode(), expense.getDate(), expense.getInvolveds());
        Participant newParticipant = participantRepository.save(new Participant(UUID.randomUUID(), "new name",
                "new surname",  null, null, event.getId()));
        expense.setTitle("NewTitle");
        expense.setAmount(69.42);
        expense.setPaidById(newParticipant.getId());
        Involved involved2 = new Involved(false, expense, newParticipant);
        try {
            setId(involved2, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        List<Involved> involveds2 = new InvolvedList();
        involveds2.add(involved);
        involveds2.add(involved2);
        expense.setInvolveds(involveds2);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.updateExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:update"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
        List<Involved> receivedInvolveds = expense.getInvolveds();
        for(Involved thisInvolved : receivedInvolveds)
            thisInvolved.setExpenseId(expense.getId());
        assertEquals(receivedInvolveds, sentExpense.getInvolveds());
        assertEquals(expense.getDate(), sentExpense.getDate());
        assertEquals(expense.getAmount()/expense.getInvolveds().size(),sentExpense.getAmountOwed());
        assertFalse(sentExpense.getInvolveds().getFirst().getIsSettled());
    }

    @Test
    void checkUpdateExpenseWithoutInvolvedChange() {
        Event event = eventRepository.save(new Event("testEvent"));
        Participant participant = participantRepository.save(new Participant(UUID.randomUUID(), "name",
                "surname", null, null, event.getId()));
        Expense expense = new Expense(participant, "expense", 21.37, LocalDate.now(), null);
        Involved involved = new Involved(true, expense, participant);
        List<Involved> involveds = new InvolvedList();
        involveds.add(involved);
        expense.setInvolveds(involveds);
        expense = expenseRepository.save(expense);
        List<Involved> involveds1 = new InvolvedList();
        involveds1.add(involved);
        expense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(), expense.getPaidById(),
                expense.getInvitationCode(), expense.getDate(), involveds1);
        expense.setTitle("NewTitle");
        expense.setPaidById(participant.getId());

        assertEquals(StatusEntity.StatusCode.OK, expenseController.updateExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:update"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
        List<Involved> receivedInvolveds = expense.getInvolveds();
        for(Involved thisInvolved : receivedInvolveds)
            thisInvolved.setExpenseId(sentExpense.getId());
        assertEquals(receivedInvolveds, sentExpense.getInvolveds());
        assertEquals(expense.getAmount(),sentExpense.getAmountOwed());
        assertEquals(expense.getDate(), sentExpense.getDate());
        assertEquals(expense.getAmount() / expense.getInvolveds().size(),sentExpense.getAmountOwed());
        assertTrue(sentExpense.getInvolveds().getFirst().getIsSettled());
    }

    @Test
    void noExpenseID() {
        assertEquals(StatusEntity.badRequest(true, "Expense ID should be provided"),
                expenseController.isExistingExpenseBadRequest(new Expense()));
    }

    @Test
    void ExpenseNotExists() {
        Expense expense = new Expense();
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.notFound(true, "Expense with provided ID does not exist"),
                expenseController.isExistingExpenseBadRequest(expense));
    }

    @Test
    void ExistingExpenseOK() {
        Participant participant = new Participant(UUID.randomUUID(), "name", "surname",
                null, null, UUID.randomUUID());
        Expense expense = new Expense();
        participant = participantRepository.save(participant);
        expense.setInvolveds(List.of(new Involved(false, expense, participant)));
        expense = expenseRepository.save(expense);
        assertEquals(StatusEntity.ok((ExpenseList) null), expenseController.isExistingExpenseBadRequest(expense));
    }

    @Test
    void checkDeleteExpense() {
        Event event = eventRepository.save(new Event("testEvent"));
        Participant participant = participantRepository.save(new Participant(UUID.randomUUID(), "name",
                "surname",  null, null, event.getId()));
        Expense expense = new Expense(participant, "expense", 21.37, null, null);
        expense.setInvolveds(List.of(new Involved(false, expense, participant)));
        expense = expenseRepository.save(expense);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.deleteExpense(expense).getStatusCode());
        assertFalse(expenseRepository.existsById(expense.getId()));

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:delete"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getId(), sentExpense.getId());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getTitle(), sentExpense.getTitle());
    }

    @Test
    void checkReadExpenses() {
        Event event = new Event("testEvent");
        event = eventRepository.save(event);
        Participant participant1 = new Participant(event, "name1",
                "surname1",  "ibanTest", "bicTest");
        Participant participant2 = new Participant(event, "name2",
                "surname2",  "ibanTest", "bicTest");
        participant1 = participantRepository.save(participant1);
        participant2 = participantRepository.save(participant2);
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        Expense expense1 = new Expense(participant1, "expense1", 1.1, null, null);
        Involved involved1 = new Involved(false, expense1, participant1);
        Involved involved2 = new Involved(false, expense1, participant2);
        expense1.setInvolveds(List.of(involved1, involved2));
        participant1.addExpense(expense1);
        Expense expense2 = new Expense(participant2, "expense2", 2.2, null, null);
        Involved involved3 = new Involved(false, expense2, participant1);
        expense2.setInvolveds(List.of(involved3));
        participant2.addExpense(expense2);
        expense1 = expenseRepository.save(expense1);
        expense2 = expenseRepository.save(expense2);

        StatusEntity status = expenseController.readExpenses(event.getId());
        assertEquals(StatusEntity.StatusCode.OK, status.getStatusCode());
        ExpenseList readExpenses = status.getExpenseList();
        Expense readExpense1 = readExpenses.getFirst();
        Expense readExpense2 = readExpenses.getLast();
        assertEquals(expense1.getId(), readExpense1.getId());
        assertEquals(expense1.getTitle(), readExpense1.getTitle());
        assertEquals(expense1.getAmount(), readExpense1.getAmount());
        assertEquals(expense1.getInvitationCode(), readExpense1.getInvitationCode());
        assertEquals(expense1.getPaidById(), readExpense1.getPaidById());
        List<Involved> involveds = expense1.getInvolveds();
        for(Involved thisInvolved : involveds)
            thisInvolved.setExpenseId(readExpense1.getId());
        assertEquals(involveds, readExpense1.getInvolveds());
        assertEquals(expense1.getAmount()/expense1.getInvolveds().size(), readExpense1.getAmountOwed());
        assertEquals(expense2.getId(), readExpense2.getId());
        assertEquals(expense2.getTitle(), readExpense2.getTitle());
        assertEquals(expense2.getAmount(), readExpense2.getAmount());
        assertEquals(expense2.getInvitationCode(), readExpense2.getInvitationCode());
        assertEquals(expense2.getPaidById(), readExpense2.getPaidById());
        involveds = expense2.getInvolveds();
        for(Involved thisInvolved : involveds)
            thisInvolved.setExpenseId(readExpense2.getId());
        assertEquals(involveds, readExpense2.getInvolveds());
        assertEquals(expense2.getAmount()/expense2.getInvolveds().size(), readExpense2.getAmountOwed());
    }

    @Test
    void readExpenseNull() {
        assertEquals(StatusEntity.badRequest(true, (ExpenseList) null),
                expenseController.readExpenses(null));
    }

    @Test
    void readExpenseEventNotExists() {
        assertEquals(StatusEntity.notFound(true, (ExpenseList) null),
                expenseController.readExpenses(UUID.randomUUID()));
    }

    @Test
    void deleteExpenseNullObject() {
        assertEquals(StatusEntity.badRequest(false, "Expense object not found in message body"),
                expenseController.deleteExpense(null));
    }
}
