package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private Event event;
    private Event eventEqual;
    private Event eventNotEqual;
    private List<Participant> participants;
    private List<Participant> participantsEqual;

    @BeforeEach
    void setUp() {
        participants = new ArrayList<>();
        event = new Event("The Event we need to pay for", participants);
        event.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0));
        event.setLastActivity(LocalDateTime.of(2024, 2, 14, 12, 0));
        participants.add(new Participant(
                event,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123",
                new ArrayList<>()));
        participants.add(new Participant(
                event,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666",
                new ArrayList<>()));
        participantsEqual = new ArrayList<>();
        eventEqual = new Event("The Event we need to pay for", participantsEqual);
        eventEqual.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0));
        eventEqual.setLastActivity(LocalDateTime.of(2024, 2, 14, 12, 0));
        participantsEqual.add(new Participant(
                eventEqual,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123",
                new ArrayList<>()));
        participantsEqual.add(new Participant(
                eventEqual,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666",
                new ArrayList<>()));
        List<Participant> participantsNotEqual = new ArrayList<>();
        eventNotEqual = new Event("The Event we do not need to pay for", participantsNotEqual);
        eventNotEqual.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0));
        eventNotEqual.setLastActivity(LocalDateTime.of(2024, 2, 14, 12, 0));
        participantsNotEqual.add(new Participant(
                eventNotEqual,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A124",
                new ArrayList<>()));
        participantsNotEqual.add(new Participant(
                eventNotEqual,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2157 00",
                "CDNANL2A666",
                new ArrayList<>()));
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
        assertTrue(eventToString.contains("participants=" + participants.toString()));
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
    void addParticipant() {
        participantsEqual.add(
                new Participant(
                        event,
                        "Average",
                        "Joe",
                        "ajoe@domain.com",
                        "NL69 AJOE 4269 2137 00",
                        "CDNANL2A666",
                        new ArrayList<>())
        );
        event.addParticipant(new Participant(
                event,
                "Average",
                "Joe",
                "ajoe@domain.com",
                "NL69 AJOE 4269 2137 00",
                "CDNANL2A666",
                new ArrayList<>())
        );
        assertEquals(participantsEqual, event.getParticipants());
    }

    @Test
    public void removeParticipant() {
        participantsEqual.remove(new Participant(
                event,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666",
                new ArrayList<>())
        );
        event.removeParticipant(new Participant(
                event,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666",
                new ArrayList<>())
        );
        assertEquals(participantsEqual, event.getParticipants());
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
