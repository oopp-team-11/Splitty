package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
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
    void getIdTest() {
        assertTrue(expense.getId() >= 0);
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
