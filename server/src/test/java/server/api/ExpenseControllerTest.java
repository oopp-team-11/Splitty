package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.ArrayList;
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
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setup() {
        expenseRepository = new TestExpenseRepository();
        eventRepository = new TestEventRepository();
        participantRepository = new TestParticipantRepository();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        expenseController = new ExpenseController(eventRepository, expenseRepository,
                participantRepository, messagingTemplate);
    }

    private static void setId(Expense toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    @Test
    void checkCreateExpense() {
        Participant participant = participantRepository.save(new Participant(
                new Event(),
                "abc",
                "def",
                "a@b.c",
                null,
                null
        ));
        Participant sentParticipant = new Participant(participant.getId(), participant.getFirstName(),
                participant.getLastName(), participant.getEmail(), participant.getIban(), participant.getBic(),
                UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, "expense", 21.37);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.createExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:create"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
    }

    @Test
    void checkCreateExpenseParticipantNotFound() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                "abcd@gmail.com", null, null, UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, "expense", 21.37);
        expense = expenseRepository.save(expense);

        assertEquals(StatusEntity.notFound("Provided participant who paid for the expense does not exist"),
                expenseController.createExpense(expense));
    }

    @Test
    void ExpenseNull() {
        assertEquals(StatusEntity.badRequest("Expense object not found in message body", true),
                expenseController.isExpenseBadRequest(null));
    }

    @Test
    void ExpenseTitleNull() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                "abcd@gmail.com", null, null, UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, null, 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest("Expense title should not be empty", true),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseAmountNotPositive() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                "abcd@gmail.com", null, null, UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, "expense", 0);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest("Amount should be positive", true),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseGetPaidByIDNull() {
        Participant sentParticipant = new Participant(null, "name", "surname",
                "abcd@gmail.com", null, null, UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, "expense", 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest("Id of participant who paid should be provided", true),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseInvitationCodeNull() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                "abcd@gmail.com", null, null, null);
        Expense expense = new Expense(sentParticipant, "expense", 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.badRequest("InvitationCode of event should be provided", true),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseOKRequest() {
        Participant sentParticipant = new Participant(UUID.randomUUID(), "name", "surname",
                "abcd@gmail.com", null, null, UUID.randomUUID());
        Expense expense = new Expense(sentParticipant, "expense", 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.ok(null), expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void checkUpdateExpense() {
        Participant participant = participantRepository.save(new Participant(UUID.randomUUID(), "name",
                "surname", "abcd@gmail.com", null, null, UUID.randomUUID()));
        Expense expense = new Expense(participant, "expense", 21.37);
        expense = expenseRepository.save(expense);
        expense.setTitle("NewTitle");
        expense.setAmount(69.42);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.updateExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:update"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
    }

    @Test
    void noExpenseID() {
        assertEquals(StatusEntity.badRequest("Expense ID should be provided", true),
                expenseController.isExistingExpenseBadRequest(new Expense()));
    }

    @Test
    void ExpenseNotExists() {
        Expense expense = new Expense();
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        assertEquals(StatusEntity.notFound("Expense with provided ID does not exist", true),
                expenseController.isExistingExpenseBadRequest(expense));
    }

    @Test
    void ExistingExpenseOK() {
        Expense expense = new Expense();
        expense = expenseRepository.save(expense);
        assertEquals(StatusEntity.ok(null), expenseController.isExistingExpenseBadRequest(expense));
    }

    @Test
    void checkDeleteExpense() {
        Participant participant = participantRepository.save(new Participant(UUID.randomUUID(), "name",
                "surname", "abcd@gmail.com", null, null, UUID.randomUUID()));
        Expense expense = new Expense(participant, "expense", 21.37);
        expense = expenseRepository.save(expense);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.deleteExpense(expense).getStatusCode());
        assertFalse(expenseRepository.existsById(expense.getId()));

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + expense.getInvitationCode() + "/expense:delete"),
                expenseArgumentCaptor.capture());
        assertEquals(expense, expenseArgumentCaptor.getValue());
    }

    @Test
    void checkReadExpenses() {
        Event event = new Event("testEvent");
        event = eventRepository.save(event);
        Participant participant1 = new Participant(event, "name1",
                "surname1", "abc@gmail.com", "ibanTest", "bicTest");
        Participant participant2 = new Participant(event, "name2",
                "surname2", "abc@gmail.com", "ibanTest", "bicTest");
        participant1 = participantRepository.save(participant1);
        participant2 = participantRepository.save(participant2);
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        Expense expense1 = new Expense(participant1, "expense1", 1.1);
        participant1.addExpense(expense1);
        Expense expense2 = new Expense(participant2, "expense2", 2.2);
        participant2.addExpense(expense2);
        expense1 = expenseRepository.save(expense1);
        expense2 = expenseRepository.save(expense2);

        StatusEntity<List<Expense>> status = expenseController.readExpenses(event.getId());
        assertEquals(StatusEntity.StatusCode.OK, status.getStatusCode());
        List<Expense> readExpenses = status.getBody();
        Expense readExpense1 = readExpenses.getFirst();
        Expense readExpense2 = readExpenses.getLast();
        assertEquals(expense1.getId(), readExpense1.getId());
        assertEquals(expense1.getTitle(), readExpense1.getTitle());
        assertEquals(expense1.getAmount(), readExpense1.getAmount());
        assertEquals(expense1.getInvitationCode(), readExpense1.getInvitationCode());
        assertEquals(expense1.getPaidById(), readExpense1.getPaidById());
        assertEquals(expense2.getId(), readExpense2.getId());
        assertEquals(expense2.getTitle(), readExpense2.getTitle());
        assertEquals(expense2.getAmount(), readExpense2.getAmount());
        assertEquals(expense2.getInvitationCode(), readExpense2.getInvitationCode());
        assertEquals(expense2.getPaidById(), readExpense2.getPaidById());
    }

    @Test
    void readExpenseNull() {
        assertEquals(StatusEntity.badRequest(null, true),
                expenseController.readExpenses(null));
    }

    @Test
    void readExpenseEventNotExists() {
        assertEquals(StatusEntity.notFound(null, true),
                expenseController.readExpenses(UUID.randomUUID()));
    }

    @Test
    void deleteExpenseNullObject() {
        assertEquals(StatusEntity.badRequest("Expense object not found in message body"),
                expenseController.deleteExpense(null));
    }
}
