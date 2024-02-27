package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private Event event;
    List<Participant> participants;

    @BeforeEach
    void setUp() {
        participants = new ArrayList<>();
        participants.add( new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"));
        participants.add (new Participant("XYZ-123-456",
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"));

        event = new Event(6662137,
                "ABC-123-456",
                "The Event we need to pay for",
                LocalDateTime.of(2024, 2, 12, 12, 0, 0),
                LocalDateTime.of(2024, 2, 14, 12, 0, 0),
                participants);
    }

    @Test
    void testEquals() {
        Event event2 = new Event(
                6662137,
                "ABC-123-456",
                "The Event we need to pay for",
                LocalDateTime.of(2024, 2, 12, 12, 0, 0),
                LocalDateTime.of(2024, 2, 14, 12, 0, 0),
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
                )
        );

        assertEquals(event, event2);
    }

    @Test
    void testNotEquals() {
        Event event2 = new Event(
                4202112,
                "ABC-666-789",
                "The Event we need to pay for",
                LocalDateTime.of(2024, 1, 16, 12, 0, 0),
                LocalDateTime.of(2024, 2, 19, 12, 0, 0),
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
                )
        );

        assertNotEquals(event, event2);
    }

    @Test
    void testHashCode() {
        Event event2 = new Event(
                6662137,
                "ABC-123-456",
                "The Event we need to pay for",
                LocalDateTime.of(2024, 2, 12, 12, 0, 0),
                LocalDateTime.of(2024, 2, 14, 12, 0, 0),
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
                )
        );

        assertEquals(event.hashCode(), event2.hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        Event event2 = new Event(
                4202112,
                "ABC-666-789",
                "The Event we need to pay for",
                LocalDateTime.of(2024, 1, 16, 12, 0, 0),
                LocalDateTime.of(2024, 2, 19, 12, 0, 0),
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
        event.setParticipants(List.of(new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123")));
        assertEquals(event.getParticipants(), List.of(new Participant("ABC-123-456",
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123")));
    }

    @Test
    void addParticipant() {
        List<Participant> newParticipants = new ArrayList<>();
        newParticipants.add(
                new Participant("ABC-123-456",
                        "John",
                        "Doe",
                        "j.doe@domain.com",
                        "NL91 ABNA 0417 1643 00",
                        "ABNANL2A123")
        );
        newParticipants.add(
                new Participant("XYZ-123-456",
                        "Lorem",
                        "Ipsum",
                        "l.ipsum@domain.com",
                        "NL69 XING 4269 2137 00",
                        "CDNANL2A666")
        );
        newParticipants.add(
                new Participant("XYZ-123-456",
                        "Average",
                        "Joe",
                        "ajoe@domain.com",
                        "NL69 AJOE 4269 2137 00",
                        "CDNANL2A666")
        );
        event.addParticipant(new Participant("XYZ-123-456",
                "Average",
                "Joe",
                "ajoe@domain.com",
                "NL69 AJOE 4269 2137 00",
                "CDNANL2A666"));
        assertEquals(newParticipants, event.getParticipants());
    }

    @Test
    public void removeParticipant() {
        List<Participant> newParticipants = new ArrayList<> ();
        newParticipants.add(
                new Participant("ABC-123-456",
                        "John",
                        "Doe",
                        "j.doe@domain.com",
                        "NL91 ABNA 0417 1643 00",
                        "ABNANL2A123")
        );
        event.removeParticipant(new Participant("XYZ-123-456",
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"));
        assertEquals(newParticipants, event.getParticipants());
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
        assertEquals(event.getCreationDate(), LocalDateTime.of(2024, 2, 12, 12, 0, 0));
    }

    @Test
    void setCreationDateTest() {
        event.setCreationDate(LocalDateTime.of(2024, 2, 12, 12, 0, 0));
        assertEquals(event.getCreationDate(), LocalDateTime.of(2024, 2, 12, 12, 0, 0));
    }

    @Test
    void getLastActivityTest() {
        assertEquals(event.getLastActivity(), LocalDateTime.of(2024, 2, 14, 12, 0, 0));
    }

    @Test
    void setLastActivityTest() {
        event.setLastActivity(LocalDateTime.of(2024, 2, 12, 12, 0, 0));
        assertEquals(event.getLastActivity(), LocalDateTime.of(2024, 2, 12, 12, 0, 0));
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
