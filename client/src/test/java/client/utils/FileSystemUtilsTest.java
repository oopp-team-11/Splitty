package client.utils;

import org.junit.jupiter.api.Test;
import javax.json.Json;
import javax.json.JsonObject;
import static org.junit.jupiter.api.Assertions.*;
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

        List<Integer> codes = new ArrayList<>();
        int randomCode = UUID.randomUUID().hashCode();
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

        int randomCode = UUID.randomUUID().hashCode();
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

        List<Integer> codes = new ArrayList<>();
        int randomCode = UUID.randomUUID().hashCode();
        codes.add(randomCode);
        JsonObject json = Json.createObjectBuilder()
            .add("invitationCodes", Json.createArrayBuilder(codes))
            .build();

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        int randomCode2 = UUID.randomUUID().hashCode();
        fileSystemUtils.saveInvitationCodesToConfigFile(randomCode2, randomFileName);
        codes.add(randomCode2);
        assertTrue(new File(randomFileName).exists());
        assertEquals(codes, fileSystemUtils.readInvitationCodes(randomFileName));
        new File(randomFileName).delete();
    }

}