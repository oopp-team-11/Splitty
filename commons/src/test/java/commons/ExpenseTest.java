package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ExpenseTest {
    private Expense expense;
    @BeforeEach
    void setup(){
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);

        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");

        expense = new Expense(123456789, "Cookies", 69.69
                , LocalDate.of(2024, 2, 14)
                , toBePaidBy, expenseType);
    }

    @Test
    void testEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);

        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");

        Expense expense2 = new Expense(123456789, "Cookies", 69.69
                , LocalDate.of(2024, 2, 14)
                , toBePaidBy, expenseType);
        assertEquals(expense, expense2);
    }

    @Test
    void testNotEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(111111111L);

        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Pets");

        Expense expense2 = new Expense(1212121212, "Dog food", 2.4
                , LocalDate.of(2019, 2, 1)
                , toBePaidBy, expenseType);
        assertNotEquals(expense, expense2);
    }

    @Test
    void testHashCodeEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);

        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");

        Expense expense2 = new Expense(123456789, "Cookies", 69.69
                , LocalDate.of(2024, 2, 14)
                , toBePaidBy, expenseType);
        assertEquals(expense.hashCode(), expense2.hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(111111111L);

        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Pets");

        Expense expense2 = new Expense(1212121212, "Dog food", 2.4
                , LocalDate.of(2019, 2, 1)
                , toBePaidBy, expenseType);
        assertNotEquals(expense.hashCode(), expense2.hashCode());
    }

    @Test
    void testToString() {
        String expenseToString = expense.toString();
        assertTrue(expenseToString.contains("id="));
        assertTrue(expenseToString.contains("paidBy=123456789"));
        assertTrue(expenseToString.contains("title=Cookies"));
        assertTrue(expenseToString.contains("cost=69.69"));
        assertTrue(expenseToString.contains("date=2024-02-14"));
        assertTrue(expenseToString.contains("toBePaidBy=[123456789]"));
        assertTrue(expenseToString.contains("expenseType=[Groceries]"));
    }

    @Test
    void getIdTest() {
        assertTrue(expense.getId() >= 0);
    }
    @Test
    void getPaidByTest() {
        assertEquals(expense.getPaidBy(), 123456789L);
    }
    @Test
    void getTitleTest() {
        assertEquals(expense.getTitle(), "Cookies");
    }
    @Test
    void getCostTest() {
        assertEquals(expense.getCost(), 69.69);
    }
    @Test
    void getDateTest() {
        assertEquals(expense.getDate(), LocalDate.of(2024, 2, 14));
    }
    @Test
    void getToBePaidByTest() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);
        assertEquals(expense.getToBePaidBy(), toBePaidBy);
    }
    @Test
    void getExpenseTypeTest() {
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");
        assertEquals(expense.getExpenseType(), expenseType);
    }
    @Test
    void setPaidByTest() {
        expense.setPaidBy(4545454545L);
        assertEquals(expense.getPaidBy(), 4545454545L);
    }
    @Test
    void setTitleTest() {
        expense.setTitle("Fishes");
        assertEquals(expense.getTitle(), "Fishes");
    }
    @Test
    void setCostTest() {
        expense.setCost(420.69);
        assertEquals(expense.getCost(), 420.69);
    }
    @Test
    void setDateTest() {
        expense.setDate(LocalDate.of(2023, 8, 4));
        assertEquals(expense.getDate(), LocalDate.of(2023, 8, 4));
    }
    @Test
    void setToBePaidByTest() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(6969696969L);

        expense.setToBePaidBy(toBePaidBy);
        assertEquals(expense.getToBePaidBy(), toBePaidBy);
    }
    @Test
    void setExpenseTypeTest() {
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Fishing");

        expense.setExpenseType(expenseType);
        assertEquals(expense.getExpenseType(), expenseType);
    }
}
