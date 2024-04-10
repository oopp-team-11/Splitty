package client.utils;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.*;

class FileSystemUtilsTest {

    @AfterAll
    static void removeUsedFiles(){
        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
            new File("test-client-config.json").delete();
        }
    }

    @Test
    void checkIfFileExistsFalse() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".txt";
        assertFalse(fileSystemUtils.checkIfFileExists(randomFileName));
    }

    @Test
    void checkIfFileExistsTrue() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".txt";
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
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".txt";

        if (new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        assertThrows(FileNotFoundException.class, () -> fileSystemUtils.readInvitationCodes(randomFileName));
    }

    @Test
    void readInvitationCodesFileFound() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".json";

        if (new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        List<UUID> codes = new ArrayList<>();
        UUID randomCode = UUID.randomUUID();
        codes.add(randomCode);
        List<String> codeStrings = codes.stream().map(UUID::toString).toList();
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codeStrings))
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
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".json";

        if (new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        UUID randomCode = UUID.randomUUID();
        fileSystemUtils.saveInvitationCodesToConfigFile(randomCode, randomFileName);
        assertTrue(new File(randomFileName).exists());
        assertEquals(randomCode, fileSystemUtils.readInvitationCodes(randomFileName).get(0));
        new File(randomFileName).delete();
    }

    @Test
    void saveInvitationCodesToConfigFileFileFound() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".json";

        if (new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        List<UUID> codes = new ArrayList<>();
        UUID randomCode = UUID.randomUUID();
        codes.add(randomCode);
        List<String> codeStrings = codes.stream().map(UUID::toString).toList();
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codeStrings))
                .build();

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        UUID randomCode2 = UUID.randomUUID();
        fileSystemUtils.saveInvitationCodesToConfigFile(randomCode2, randomFileName);
        codes.add(randomCode2);
        assertTrue(new File(randomFileName).exists());
        assertEquals(codes, fileSystemUtils.readInvitationCodes(randomFileName));
        new File(randomFileName).delete();
    }

    @Test
    void checkIfCodeExistsTrue() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        List<UUID> codes = new ArrayList<>();
        UUID randomCode = UUID.randomUUID();
        codes.add(randomCode);
        assertTrue(fileSystemUtils.checkIfCodeExists(randomCode, codes));
    }

    @Test
    void checkIfCodeExistsFalse() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        List<UUID> codes = new ArrayList<>();
        UUID randomCode = UUID.randomUUID();
        assertFalse(fileSystemUtils.checkIfCodeExists(randomCode, codes));
    }

    @Test
    void checkIfCodeExistsEmptyList() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        List<UUID> codes = new ArrayList<>();
        UUID randomCode = UUID.randomUUID();
        assertFalse(fileSystemUtils.checkIfCodeExists(randomCode, codes));
    }

    @Test
    void updateConfigFileTest() throws IOException {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        String randomFileName = UUID.randomUUID() + ".json";

        if (new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        List<UUID> codes = new ArrayList<>();
        UUID randomCode = UUID.randomUUID();
        codes.add(randomCode);
        List<String> codeStrings = codes.stream().map(UUID::toString).toList();
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codeStrings))
                .build();

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        UUID randomCode2 = UUID.randomUUID();
        codes.add(randomCode2);
        fileSystemUtils.updateConfigFile(randomFileName, codes);
        assertTrue(new File(randomFileName).exists());
        assertEquals(codes, fileSystemUtils.readInvitationCodes(randomFileName));
        new File(randomFileName).delete();
    }

    @Test
    void extractInvitationCodesFromEventListTest() {
        FileSystemUtils fileSystemUtils = new FileSystemUtils(null);
        List<Event> events = new ArrayList<>();
        List<UUID> codes = new ArrayList<>();

        Event event1 = new Event("The Event we need to pay for");
        Event event2 = new Event("The Event we do not need to pay for");

        new Participant(
                event1,
                "John",
                "Doe",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        new Participant(
                event1,
                "Lorem",
                "Ipsum",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"
        );
        new Participant(
                event2,
                "John",
                "Doe",
                "NL91 ABNA 0417 1643 00",
                "ABNANL2A123"
        );
        new Participant(
                event2,
                "Lorem",
                "Ipsum",
                "NL69 XING 4269 2137 00",
                "CDNANL2A666"
        );

        events.add(event1);
        events.add(event2);

        codes.add(event1.getId());
        codes.add(event2.getId());


        assertEquals(codes, fileSystemUtils.extractInvitationCodesFromEventList(events));
    }

//    @Test
//    void checkClientConfigCreationErrorEmptyFile() throws IOException {
//        FileSystemUtils utils = new FileSystemUtils(null);
//
//        assertEquals("localhost:8080", utils.getServerIP("test-client-config.json"));
//        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
//            new File("test-client-config.json").delete();
//        }
//    }

//    @Test
//    void checkClientConfigCreationErrorEmptyIP() throws IOException {
//        FileSystemUtils utils = new FileSystemUtils(null);
//
//        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
//            new File("test-client-config.json").delete();
//        }
//
//        JsonObject json = Json.createObjectBuilder()
//                .add("server-ip", "")
//                .build();
//
//        FileWriter file = new FileWriter("test-client-config.json");
//        file.write(json.toString());
//        file.flush();
//        file.close();
//
//        assertEquals("localhost:8080", utils.getServerIP("test-client-config.json"));
//        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
//            new File("test-client-config.json").delete();
//        }
//    }

    @Test
    void checkClientConfigCreationCorrect() throws IOException {
        FileSystemUtils utils = new FileSystemUtils(null);

        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
            new File("test-client-config.json").delete();
        }

        JsonObject json = Json.createObjectBuilder()
                .add("server-ip", "localhost:8080")
                .build();

        FileWriter file = new FileWriter("test-client-config.json");
        file.write(json.toString());
        file.flush();
        file.close();

        assertEquals(utils.getServerIP("test-client-config.json"), "localhost:8080");
        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
            new File("test-client-config.json").delete();
        }
    }

    @Test
    void checkClientConfigCreationWrongFormat() throws IOException {
        FileSystemUtils utils = new FileSystemUtils(null);

        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
            new File("test-client-config.json").delete();
        }

        JsonObject json = Json.createObjectBuilder()
                .add("server-ip", "http://localhost:8080")
                .build();

        FileWriter file = new FileWriter("test-client-config.json");
        file.write(json.toString());
        file.flush();
        file.close();

        assertEquals(utils.getServerIP("test-client-config.json"), "localhost:8080");
        if(FileSystemUtils.checkIfFileExists("test-client-config.json")){
            new File("test-client-config.json").delete();
        }
    }

}