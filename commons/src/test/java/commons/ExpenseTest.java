package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
    private Expense expense;
    @BeforeEach
    void setup(){
        Participant paidBy = new Participant(
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        expense = new Expense(paidBy, "Cookies", 69.69);
    }

    @Test
    void testEquals() {
        Participant paidBy = new Participant(
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        Expense expense2 = new Expense(paidBy, "Cookies", 69.69);
        assertEquals(expense, expense2);
    }

    @Test
    void testNotEquals() {
        Participant participant2 = new Participant(
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        Expense expense2 = new Expense(participant2, "Dog food", 2.4);
        assertNotEquals(expense, expense2);
    }

    @Test
    void testHashCodeEquals() {
        Participant paidBy = new Participant(
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        Expense expense2 = new Expense(paidBy, "Cookies", 69.69);
        assertEquals(expense.hashCode(), expense2.hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        Participant paidBy = new Participant(
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        Expense expense2 = new Expense(paidBy, "Dog food", 2.4);
        assertNotEquals(expense.hashCode(), expense2.hashCode());
    }

    @Test
    void testToString() {
        String expenseToString = expense.toString();
        assertTrue(expenseToString.contains("id="));
        assertTrue(expenseToString.contains("paidBy="));
        assertTrue(expenseToString.contains("title="));
        assertTrue(expenseToString.contains("amount="));
    }

    @Test
    void getIdTest() {
        assertTrue(expense.getId() >= 0);
    }
    @Test
    void getPaidByTest() {
        Participant paidBy = new Participant(
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        assertEquals(expense.getPaidBy(), paidBy);
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
    void setPaidByTest() {
        Participant participant2 = new Participant(
                "John",
                "Burger",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123");
        expense.setPaidBy(participant2);
        assertEquals(expense.getPaidBy(), participant2);
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
