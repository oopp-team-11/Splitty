package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private Event event;
    private Event eventEqual;
    private Event eventNotEqual;

    @BeforeEach
    void setUp() {
        event = new Event("The Event we need to pay for");
        event.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0));
        event.setLastActivity(LocalDateTime.of(2024, 2, 14, 12, 0));
        new Participant(
                event,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        new Participant(
                event,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"
        );
        eventEqual = new Event("The Event we need to pay for");
        eventEqual.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0));
        eventEqual.setLastActivity(LocalDateTime.of(2024, 2, 14, 12, 0));
        new Participant(
                eventEqual,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        new Participant(
                eventEqual,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"
        );
        eventNotEqual = new Event("The Event we do not need to pay for");
        eventNotEqual.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0));
        eventNotEqual.setLastActivity(LocalDateTime.of(2024, 2, 14, 12, 0));
        new Participant(
                eventNotEqual,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A124"
        );
        new Participant(
                eventNotEqual,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2157 00",
                "CDNANL2A666"
        );
    }

    @Test
    void testEquals() {
        assertEquals(event, eventEqual);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(event, eventNotEqual);
    }

    @Test
    void testHashCode() {
        assertEquals(event.hashCode(), eventEqual.hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        assertNotEquals(event.hashCode(), eventNotEqual.hashCode());
    }

    @Test
    void testToString() {
        String eventToString = event.toString();
        assertTrue(eventToString.contains("id="));
        assertTrue(eventToString.contains("title=The Event we need to pay for"));
        assertTrue(eventToString.contains("creationDate=2024-02-12"));
        assertTrue(eventToString.contains("lastActivity=2024-02-14"));
        assertTrue(eventToString.contains("participants="));
    }

    @Test
    void getIdTest() {
        assertTrue(event.getId() >= 0);
    }

    @Test
    void getParticipantsTest() {
        List<Participant> participants = event.getParticipants();
        assertEquals(2, participants.size());
        assertEquals("John", participants.getFirst().getFirstName());
    }

    @Test
    void addParticipant() {
        Participant participant = new Participant(
            event,
            "Average",
            "Joe",
            "ajoe@domain.com",
            "NL69 AJOE 4269 2137 00",
            "CDNANL2A666"
        );
        assertEquals(participant, event.getParticipants().getLast());
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
        assertEquals(event.getCreationDate(), LocalDateTime.of(2024, 2,
                12, 12, 0, 0));
    }

    @Test
    void setCreationDateTest() {
        event.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0, 0));
        assertEquals(event.getCreationDate(), LocalDateTime.of(2024, 2, 12, 12,
                0, 0));
    }

    @Test
    void getLastActivityTest() {
        assertEquals(event.getLastActivity(), LocalDateTime.of(2024, 2,
                14, 12, 0, 0));
    }

    @Test
    void setLastActivityTest() {
        event.setLastActivity(LocalDateTime.of(2024, 2, 12, 12, 0, 0));
        assertEquals(event.getLastActivity(), LocalDateTime.of(2024, 2, 12, 12, 0
                , 0));
    }
}
