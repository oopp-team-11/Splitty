package client.utils;

import static org.junit.jupiter.api.Assertions.*;
import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.Test;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class FileSystemUtilsTest {
    @Test
    void checkIfFileExistsFalse() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".txt";
        assertFalse(fileSystemUtils.checkIfFileExists(randomFileName));
    }

    @Test
    void checkIfFileExistsTrue() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".txt";
        try {
            new File(randomFileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(fileSystemUtils.checkIfFileExists(randomFileName));
        new File(randomFileName).delete();
    }

    @Test
    void readInvitationCodesFileMotFound() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".txt";

        if(new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        assertThrows(FileNotFoundException.class, () -> fileSystemUtils.readInvitationCodes(randomFileName));
    }

    @Test
    void readInvitationCodesFileFound() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".json";

        if(new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        List<Long> codes = new ArrayList<>();
        long randomCode = UUID.randomUUID().hashCode();
        codes.add(randomCode);
        JsonObject json = Json.createObjectBuilder()
            .add("invitationCodes", Json.createArrayBuilder(codes))
            .build();

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        assertEquals(codes, fileSystemUtils.readInvitationCodes(randomFileName));
        new File(randomFileName).delete();
    }

    @Test
    void saveInvitationCodesToConfigFileFileNotFound() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".json";

        if(new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        long randomCode = UUID.randomUUID().hashCode();
        fileSystemUtils.saveInvitationCodesToConfigFile(randomCode, randomFileName);
        assertTrue(new File(randomFileName).exists());
        assertEquals(randomCode, fileSystemUtils.readInvitationCodes(randomFileName).get(0));
        new File(randomFileName).delete();
    }

    @Test
    void saveInvitationCodesToConfigFileFileFound() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".json";

        if(new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        List<Long> codes = new ArrayList<>();
        long randomCode = UUID.randomUUID().hashCode();
        codes.add(randomCode);
        JsonObject json = Json.createObjectBuilder()
            .add("invitationCodes", Json.createArrayBuilder(codes))
            .build();

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        long randomCode2 = UUID.randomUUID().hashCode();
        fileSystemUtils.saveInvitationCodesToConfigFile(randomCode2, randomFileName);
        codes.add(randomCode2);
        assertTrue(new File(randomFileName).exists());
        assertEquals(codes, fileSystemUtils.readInvitationCodes(randomFileName));
        new File(randomFileName).delete();
    }

    @Test
    void checkIfCodeExistsTrue() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        List<Long> codes = new ArrayList<>();
        long randomCode = UUID.randomUUID().hashCode();
        codes.add(randomCode);
        assertTrue(fileSystemUtils.checkIfCodeExists(randomCode, codes));
    }

    @Test
    void checkIfCodeExistsFalse() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        List<Long> codes = new ArrayList<>();
        long randomCode = UUID.randomUUID().hashCode();
        assertFalse(fileSystemUtils.checkIfCodeExists(randomCode, codes));
    }

    @Test
    void checkIfCodeExistsEmptyList() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        List<Long> codes = new ArrayList<>();
        long randomCode = UUID.randomUUID().hashCode();
        assertFalse(fileSystemUtils.checkIfCodeExists(randomCode, codes));
    }

    @Test
    void updateConfigFileTest() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String randomFileName = UUID.randomUUID().toString() + ".json";

        if(new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        List<Long> codes = new ArrayList<>();
        long randomCode = UUID.randomUUID().hashCode();
        codes.add(randomCode);
        JsonObject json = Json.createObjectBuilder()
            .add("invitationCodes", Json.createArrayBuilder(codes))
            .build();

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        long randomCode2 = UUID.randomUUID().hashCode();
        codes.add(randomCode2);
        fileSystemUtils.updateConfigFile(randomFileName, codes);
        assertTrue(new File(randomFileName).exists());
        assertEquals(codes, fileSystemUtils.readInvitationCodes(randomFileName));
        new File(randomFileName).delete();
    }

    @Test
    void extractInvitationCodesFromEventListTest() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        List<Event> events = new ArrayList<>();
        List<Long> codes = new ArrayList<>();

        Event event1 = new Event("The Event we need to pay for");
        Event event2 = new Event("The Event we do not need to pay for");

        new Participant(
                event1,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        new Participant(
                event1,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"
        );
        new Participant(
                event2,
                "John",
                "Doe",
                "j.doe@domain.com",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        new Participant(
                event2,
                "Lorem",
                "Ipsum",
                "l.ipsum@domain.com",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"
        );

        events.add(event1);
        events.add(event2);

        codes.add(event1.getId());
        codes.add(event2.getId());


        assertEquals(codes, fileSystemUtils.extractInvitationCodesFromEventList(events));
    }

}