package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private Event event;
    List<Participant> participants;
    List<Expense> expenses;


    @BeforeEach
    void setUp() {
        participants = Arrays.asList(
                new Participant("ABC-123-456",
                        "John",
                        "Doe",
                        "j.doe@domain.com",
                        "NL91 ABNA 0417 1643 00",
                        "ABNANL2A123"),
                new Participant("XYZ-123-456",
                        "Lorem",
                        "Ipsum",
                        "l.ipsum@domain.com",
                        "NL69 XING 4269 2137 00",
                        "CDNANL2A666")
        );

        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);

        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");

        expenses = Arrays.asList(
                new Expense(123456789, "Cookies", 69.69
                        , LocalDate.of(2024, 2, 14)
                        , toBePaidBy, expenseType)
        );

        event = new Event(6662137,
                "ABC-123-456",
                "The Event we need to pay for",
                LocalDate.of(2024, 2, 12),
                LocalDate.of(2024, 2, 14),
                participants,
                expenses);
    }

    @Test
    void testEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");

        Event event2 = new Event(
                6662137,
                "ABC-123-456",
                "The Event we need to pay for",
                LocalDate.of(2024, 2, 12),
                LocalDate.of(2024, 2, 14),
                Arrays.asList(
                        new Participant("ABC-123-456",
                                "John",
                                "Doe",
                                "j.doe@domain.com",
                                "NL91 ABNA 0417 1643 00",
                                "ABNANL2A123"),
                        new Participant("XYZ-123-456",
                                "Lorem",
                                "Ipsum",
                                "l.ipsum@domain.com",
                                "NL69 XING 4269 2137 00",
                                "CDNANL2A666")
                ),
                Arrays.asList(
                        new Expense(123456789, "Cookies", 69.69
                                , LocalDate.of(2024, 2, 14)
                                , toBePaidBy, expenseType)
                )
        );

        assertEquals(event, event2);
    }

    @Test
    void testNotEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456780L);
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Travel");

        Event event2 = new Event(
                4202112,
                "ABC-666-789",
                "The Event we need to pay for",
                LocalDate.of(2024, 1, 16),
                LocalDate.of(2024, 2, 19),
                Arrays.asList(
                        new Participant("ABC-123-456",
                                "John",
                                "Doe",
                                "j.doe@domain.com",
                                "NL91 ABNA 0417 1643 00",
                                "ABNANL2A123"),
                        new Participant("XYZ-123-456",
                                "Lorem",
                                "Ipsum",
                                "l.ipsum@domain.com",
                                "NL69 XING 4269 2137 00",
                                "CDNANL2A666")
                ),
                Arrays.asList(
                        new Expense(987654321, "Taxi", 420.69
                                , LocalDate.of(2024, 2, 19)
                                , toBePaidBy, expenseType)
                )
        );

        assertNotEquals(event, event2);
    }

    @Test
    void testHashCode() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Groceries");

        Event event2 = new Event(
                6662137,
                "ABC-123-456",
                "The Event we need to pay for",
                LocalDate.of(2024, 2, 12),
                LocalDate.of(2024, 2, 14),
                Arrays.asList(
                        new Participant("ABC-123-456",
                                "John",
                                "Doe",
                                "j.doe@domain.com",
                                "NL91 ABNA 0417 1643 00",
                                "ABNANL2A123"),
                        new Participant("XYZ-123-456",
                                "Lorem",
                                "Ipsum",
                                "l.ipsum@domain.com",
                                "NL69 XING 4269 2137 00",
                                "CDNANL2A666")
                ),
                Arrays.asList(
                        new Expense(123456789, "Cookies", 69.69
                                , LocalDate.of(2024, 2, 14)
                                , toBePaidBy, expenseType)
                )
        );

        assertEquals(event.hashCode(), event2.hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456780L);
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Travel");

        Event event2 = new Event(
                4202112,
                "ABC-666-789",
                "The Event we need to pay for",
                LocalDate.of(2024, 1, 16),
                LocalDate.of(2024, 2, 19),
                Arrays.asList(
                        new Participant("ABC-123-456",
                                "John",
                                "Doe",
                                "j.doe@domain.com",
                                "NL91 ABNA 0417 1643 00",
                                "ABNANL2A123"),
                        new Participant("XYZ-123-456",
                                "Lorem",
                                "Ipsum",
                                "l.ipsum@domain.com",
                                "NL69 XING 4269 2137 00",
                                "CDNANL2A666")
                ),
                Arrays.asList(
                        new Expense(987654321, "Taxi", 420.69
                                , LocalDate.of(2024, 2, 19)
                                , toBePaidBy, expenseType)
                )
        );

        assertNotEquals(event.hashCode(), event2.hashCode());
    }

    @Test
    void testToString() {
        String eventToString = event.toString();
        assertTrue(eventToString.contains("id="));
        assertTrue(eventToString.contains("title=The Event we need to pay for"));
        assertTrue(eventToString.contains("creationDate=2024-02-12"));
        assertTrue(eventToString.contains("lastActivity=2024-02-14"));
        assertTrue(eventToString.contains("participants=" + participants.toString()));
        assertTrue(eventToString.contains("expenses=" + expenses.toString()));
    }

    @Test
    void getIdTest() {
        assertTrue(event.getId() >= 0);
    }

    @Test
    void getParticipantsTest() {
        assertEquals(event.getParticipants(), participants);
    }

    @Test
    void setParticipantsTest() {
        event.setParticipants(Arrays.asList(new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123")));
        assertEquals(event.getParticipants(), Arrays.asList(new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123")));
    }

    @Test
    void getExpensesTest() {
        assertEquals(event.getExpenses(), expenses);
    }

    @Test
    void setExpensesTest() {
        ArrayList<Long> toBePaidBy = new ArrayList<>();
        toBePaidBy.add(123456789L);
        ArrayList<String> expenseType = new ArrayList<>();
        expenseType.add("Weaponry");

        event.setExpenses(Arrays.asList(new Expense(123456789, "Nukes", 69.69
                , LocalDate.of(2024, 2, 14)
                , toBePaidBy, expenseType)));
        assertEquals(event.getExpenses(), Arrays.asList(new Expense(123456789, "Nukes", 69.69
                , LocalDate.of(2024, 2, 14)
                , toBePaidBy, expenseType)));
    }

    @Test
    void getTitleTest() {
        assertEquals(event.getTitle(), "The Event we need to pay for");
    }

    @Test
    void setTitleTest() {
        event.setTitle("The event II");
        assertEquals(event.getTitle(), "The event II");
    }

    @Test
    void getCreationDateTest() {
        assertEquals(event.getCreationDate(), LocalDate.of(2024, 2, 12));
    }

    @Test
    void setCreationDateTest() {
        event.setCreationDate(LocalDate.of(2024,1,12));
        assertEquals(event.getCreationDate(), LocalDate.of(2024, 1, 12));
    }

    @Test
    void getLastActivityTest() {
        assertEquals(event.getLastActivity(), LocalDate.of(2024,2,14));
    }

    @Test
    void setLastActivityTest() {
        event.setLastActivity(LocalDate.of(2024,1,14));
        assertEquals(event.getLastActivity(), LocalDate.of(2024,1,14));
    }

    @Test
    void getInvitationCodeTest() {
        assertEquals(event.getInvitationCode(),"ABC-123-456");
    }

    @Test
    void setInvitationCodeTest() {
        event.setInvitationCode("ABC-666-789");
        assertEquals(event.getInvitationCode(), "ABC-666-789");
    }
}
