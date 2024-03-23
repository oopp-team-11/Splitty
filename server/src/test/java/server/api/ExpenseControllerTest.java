package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.ExpenseRepository;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExpenseControllerTest {

    private ExpenseRepository expenseRepository;

    private ExpenseController expenseController;
    private SimpMessagingTemplate messagingTemplate;
    private Principal principal;

    @BeforeEach
    public void setup() {
        expenseRepository = new TestExpenseRepository();
        messagingTemplate = mock(SimpMessagingTemplate.class);
        principal = mock(Principal.class);
        expenseController = new ExpenseController(expenseRepository, messagingTemplate);
    }

    private static void setId(Expense toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    @Test
    void checkCreateExpense() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseController.createExpense(principal, expense);

        assertTrue(expenseRepository.existsById(expense.getId()));

        verify(messagingTemplate).convertAndSend("/topic/"+expense.getId(), expense);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("expense:create "+expense.getId())));
    }

    @Test
    void checkCreateExpenseNullTitle() {
        Expense expense = new Expense();

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseController.createExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Title should not be empty")));

        assertFalse(expenseRepository.existsById(expense.getId()));
    }

    @Test
    void checkCreateExpenseNullPaidBy() {
        Expense expense = new Expense();

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expense.setTitle("expense");

        expenseController.createExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Did not receive who made the expense")));

        assertFalse(expenseRepository.existsById(expense.getId()));
    }

    @Test
    void checkCreateExpenseInvalidAmount() {
        Expense expense = new Expense(new Participant(), "expense", -6.9);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseController.createExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Amount should be positive")));

        assertFalse(expenseRepository.existsById(expense.getId()));

    }

    @Test
    void checkCreateExpenseWrongClass() {
        Event event = new Event();

        expenseController.createExpense(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be an expense", true)));

        assertFalse(expenseRepository.existsById(event.getId()));
    }

    @Test
    void checkUpdateExpense() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseRepository.save(expense);

        expense.setTitle("title");

        expenseController.updateExpense(principal, expense);

        verify(messagingTemplate).convertAndSend("/topic/"+expense.getId(), expense);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("expense:update "+expense.getId())));
    }

    @Test
    void checkUpdateExpenseNullTitle() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseRepository.save(expense);

        expense.setTitle(null);

        expenseController.updateExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Title should not be empty")));
    }

    @Test
    void checkUpdateExpenseNullPaidBy() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseRepository.save(expense);

        try {
            FieldUtils.writeField(expense, "paidBy", null, true);
        } catch (IllegalAccessException ignored) {}

        expense.setTitle("title");

        expenseController.updateExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Did not receive who made the expense")));
    }

    @Test
    void checkUpdateExpenseInvalidAmount() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseRepository.save(expense);

        expense.setAmount(-6.9);

        expenseController.updateExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Amount should be positive")));
    }

    @Test
    void checkUpdateExpenseWrongClass() {
        Event event = new Event();

        expenseController.updateExpense(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be an expense", true)));
    }

    @Test
    void checkUpdateParticipantNotFound() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseController.updateExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Expense not found",true)));

        assertFalse(expenseRepository.existsById(expense.getId()));
    }

    @Test
    void checkDeleteExpense() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseRepository.save(expense);

        expenseController.deleteExpense(principal, expense);

        verify(messagingTemplate).convertAndSend("/topic/"+expense.getId(), expense);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("expense:delete "+expense.getId())));
        assertFalse(expenseRepository.existsById(expense.getId()));
    }

    @Test
    void checkDeleteExpenseWrongClass() {
        Event event = new Event();

        expenseController.deleteExpense(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be an expense",true)));
    }

    @Test
    void checkDeleteExpenseNotFound() {
        Expense expense = new Expense(new Participant(), "expense", 21.37);

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseController.deleteExpense(principal, expense);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Expense not found",true)));
        assertFalse(expenseRepository.existsById(expense.getId()));
    }

    @Test
    void checkReadExpense() {
        Expense expense = new Expense();

        try {
            setId(expense, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        expenseRepository.save(expense);

        expenseController.readExpense(principal, expense.getId());

        verify(messagingTemplate).convertAndSend("/topic/"+expense.getId(), expense);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
                eq(StatusEntity.ok("expense:read "+expense.getId())));
    }

    @Test
    void checkReadExpenseWrongClass() {
        Participant participant = new Participant();

        expenseController.readExpense(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be a UUID", true)));
    }

    @Test
    void checkReadExpenseNotFound() {
        UUID uuid = UUID.randomUUID();

        expenseController.readExpense(principal, uuid);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Expense not found",true)));

        assertFalse(expenseRepository.existsById(uuid));
    }
}
