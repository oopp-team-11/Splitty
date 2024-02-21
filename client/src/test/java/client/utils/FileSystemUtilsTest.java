package client.utils;
import org.junit.jupiter.api.Test;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class FileSystemUtilsTest {

    @Test
    void saveJsonClient() throws FileNotFoundException {
        // Create a new JsonObject
        JsonObject json = Json.createObjectBuilder()
                .add("testKey1", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16))
                .add("testKey2", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16))
                .build();

        // Write File
        try {
            FileSystemUtils.saveJsonClient(json);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        // Read file to check if it was written correctly
        JsonReader reader = Json.createReader(new FileReader("client.json"));

        assertEquals(json, reader.readObject());

        reader.close();

        // Delete created file
        File file = new File("client.json");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void checkIfClientJsonExistsTrue() {
        // Create a new JsonObject
        JsonObject json = Json.createObjectBuilder()
            .add("testKey1", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16))
            .add("testKey2", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16))
            .build();

        // Write File
        try {
            FileSystemUtils.saveJsonClient(json);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        // Check if file exists
        assertTrue(FileSystemUtils.checkIfClientJsonExists());


        // Delete created file
        File file = new File("client.json");

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void checkIfClientJsonExistsFalse() {
        // Delete file if it exists

        File file = new File("client.json");

        if (file.exists()) {
            file.delete();
        }

        // Check if file exists
        assertFalse(FileSystemUtils.checkIfClientJsonExists());
    }
}