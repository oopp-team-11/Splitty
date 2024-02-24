package commons;

import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventTest {

    private Event event;
    private Event nullEvent;


    @BeforeEach
    void setUp() {
        List<Participant> participants = Arrays.asList(
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

        List<Expense> expenses = Arrays.asList(
                new Expense(123456789, "Cookies", 69.69
                        , LocalDate.of(2024, 2, 14)
                        , toBePaidBy, expenseType)
        );

        event = new Event(6662137,
                "The Event we need to pay for",
                LocalDate.of(2024,2,12),
                LocalDate.of(2024,2,14),
                participants,
                expenses);
    }
}
