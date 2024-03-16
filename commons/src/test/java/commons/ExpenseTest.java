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
        Participant participant2 = new Participant(
                event,
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        expense = new Expense(participant1, "Cookies", 69.69);
        expenseEqual = new Expense(participant1, "Cookies", 69.69);
        expenseNotEqual = new Expense(participant2, "Chocolate", 69.69);
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
        assertTrue(expenseToString.contains("paidByID="));
        assertTrue(expenseToString.contains("title="));
        assertTrue(expenseToString.contains("amount="));
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
