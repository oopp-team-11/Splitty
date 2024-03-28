package commons;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
    private UUID shouldBeId;
    private Expense expense;
    private Expense expenseEqual;
    private Expense expenseNotEqual;
    private Participant participant1;
    private Participant participant2;

    @BeforeEach
    void setup() {
        Event event = new Event("Event");
        participant1 = new Participant(
                event,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        participant2 = new Participant(
                event,
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        expense = new Expense(participant1, "Cookies", 69.69);
        expenseEqual = new Expense(participant1, "Cookies", 69.69);
        expenseNotEqual = new Expense();
        try {
            UUID id = UUID.randomUUID();
            setId(expense, id);
            setId(expenseEqual, id);
            this.shouldBeId = id;
        } catch (IllegalAccessException ignored) {}
    }

    private static void setId(Expense toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    @Test
    void setPaidBy() {
        expense.setPaidBy(participant2);
        assertEquals(participant2, expense.getPaidBy());
    }

    @Test
    void setPaidById() {
        UUID newId = UUID.randomUUID();
        expense.setPaidById(newId);
        assertEquals(newId, expense.getPaidById());
    }

    @Test
    void server2ClientConstructor() {
        Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(),
                expense.getPaidById(), expense.getInvitationCode());
        assertEquals(expense.getId(), sentExpense.getId());
        assertEquals(expense.getTitle(), sentExpense.getTitle());
        assertEquals(expense.getAmount(), sentExpense.getAmount());
        assertEquals(expense.getPaidById(), sentExpense.getPaidById());
        assertEquals(expense.getInvitationCode(), sentExpense.getInvitationCode());
    }

    @Test
    void testGetId() {
        assertEquals(expense.getId(), shouldBeId);
        assertEquals(expenseEqual.getId(), shouldBeId);
    }

    @Test
    void testEquals() {
        assertEquals(expense, expenseEqual);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(expense, expenseNotEqual);
    }

    @Test
    void testHashCodeEquals() {
        assertEquals(expense.hashCode(), expenseEqual.hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        assertNotEquals(expense.hashCode(), expenseNotEqual.hashCode());
    }

    @Test
    void testToString() {
        String expenseToString = expense.toString();
        assertTrue(expenseToString.contains("id="));
        assertTrue(expenseToString.contains("title="));
        assertTrue(expenseToString.contains("amount="));
        assertTrue(expenseToString.contains("paidById="));
        assertTrue(expenseToString.contains("invitationCode="));
    }

    @Test
    void getPaidByTest() {
        assertEquals(expense.getPaidBy(), participant1);
    }

    @Test
    void getTitleTest() {
        assertEquals(expense.getTitle(), "Cookies");
    }

    @Test
    void getAmountTest() {
        assertEquals(expense.getAmount(), 69.69);
    }

    @Test
    void setTitleTest() {
        expense.setTitle("Fishes");
        assertEquals(expense.getTitle(), "Fishes");
    }

    @Test
    void setAmountTest() {
        expense.setAmount(420.69);
        assertEquals(expense.getAmount(), 420.69);
    }
}
