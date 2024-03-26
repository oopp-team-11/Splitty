package server.api;

import commons.*;
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
        Expense expense = new Expense(participant, "expense", 21.37);
        UUID invitationCode = UUID.randomUUID();
        expense.setPaidById(participant.getId());
        expense.setInvitationCode(invitationCode);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.createExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + invitationCode + "/expense:create"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
    }

    @Test
    void checkCreateExpenseParticipantNotFound() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);
        UUID invitationCode = UUID.randomUUID();
        expense.setPaidById(UUID.randomUUID());
        expense.setInvitationCode(invitationCode);
        expense = expenseRepository.save(expense);

        assertEquals(StatusEntity.notFound(false, "Provided participant who paid for the expense does not exist"),
                expenseController.createExpense(expense));
    }

    @Test
    void ExpenseNull() {
        assertEquals(StatusEntity.badRequest(true, "Expense object not found in message body"),
                expenseController.isExpenseBadRequest(null));
    }

    @Test
    void ExpenseTitleNull() {
        Expense expense = new Expense(new Participant(), null, 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        expense.setPaidById(UUID.randomUUID());
        expense.setInvitationCode(UUID.randomUUID());
        assertEquals(StatusEntity.badRequest(true, "Expense title should not be empty"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseAmountNotPositive() {
        Expense expense = new Expense(new Participant(), "expense", 0);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        expense.setPaidById(UUID.randomUUID());
        expense.setInvitationCode(UUID.randomUUID());
        assertEquals(StatusEntity.badRequest(true, "Amount should be positive"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseGetPaidByIDNull() {
        Expense expense = new Expense(new Participant(), "expense", 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        expense.setInvitationCode(UUID.randomUUID());
        assertEquals(StatusEntity.badRequest(true, "Id of participant who paid should be provided"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseInvitationCodeNull() {
        Expense expense = new Expense(new Participant(), "expense", 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        expense.setPaidById(UUID.randomUUID());
        assertEquals(StatusEntity.badRequest(true, "InvitationCode of event should be provided"),
                expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void ExpenseOKRequest() {
        Expense expense = new Expense(new Participant(), "expense", 69);
        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        expense.setPaidById(UUID.randomUUID());
        expense.setInvitationCode(UUID.randomUUID());
        assertEquals(StatusEntity.ok((ExpenseList) null), expenseController.isExpenseBadRequest(expense));
    }

    @Test
    void checkUpdateExpense() {
        Participant participant = participantRepository.save(new Participant());
        Expense expense = new Expense(participant, "expense", 21.37);
        UUID invitationCode = UUID.randomUUID();
        expense.setPaidById(participant.getId());
        expense.setInvitationCode(invitationCode);
        expense = expenseRepository.save(expense);
        expense.setTitle("NewTitle");
        expense.setAmount(69.42);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.updateExpense(expense).getStatusCode());

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + invitationCode + "/expense:update"),
                expenseArgumentCaptor.capture());
        Expense sentExpense = expenseArgumentCaptor.getValue();
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
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
        Expense expense = new Expense();
        expense = expenseRepository.save(expense);
        assertEquals(StatusEntity.ok((ExpenseList) null), expenseController.isExistingExpenseBadRequest(expense));
    }

    @Test
    void checkDeleteExpense() {
        Participant participant = participantRepository.save(new Participant());
        Expense expense = new Expense(participant, "expense", 21.37);
        UUID invitationCode = UUID.randomUUID();
        expense.setPaidById(participant.getId());
        expense.setInvitationCode(invitationCode);
        expense = expenseRepository.save(expense);

        assertEquals(StatusEntity.StatusCode.OK, expenseController.deleteExpense(expense).getStatusCode());
        assertFalse(expenseRepository.existsById(expense.getId()));

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/" + invitationCode + "/expense:delete"),
                expenseArgumentCaptor.capture());
        assertEquals(expense, expenseArgumentCaptor.getValue());
    }

    @Test
    void checkReadExpenses() {
        Event event = new Event("testEvent");
        Participant participant1 = new Participant(event, "name1",
                "surname1", "abc@gmail.com", "ibanTest", "bicTest");
        Participant participant2 = new Participant(event, "name2",
                "surname2", "abc@gmail.com", "ibanTest", "bicTest");
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        Expense expense1 = new Expense(participant1, "expense1", 1.1);
        participant1.addExpense(expense1);
        Expense expense2 = new Expense(participant2, "expense2", 2.2);
        participant2.addExpense(expense2);
        event = eventRepository.save(event);
        participant1 = participantRepository.save(participant1);
        participant2 = participantRepository.save(participant2);
        expense1 = expenseRepository.save(expense1);
        expense2 = expenseRepository.save(expense2);
        expense1.setInvitationCode(event.getId());
        expense2.setInvitationCode(event.getId());
        expense1.setPaidById(participant1.getId());
        expense2.setPaidById(participant2.getId());
        ExpenseList expenses = new ExpenseList();
        expenses.add(expense1);
        expenses.add(expense2);

        assertEquals(StatusEntity.ok(expenses), expenseController.readExpenses(event.getId()));
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
}
