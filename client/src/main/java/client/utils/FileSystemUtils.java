package client.utils;

import javax.json.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSystemUtils {
    private static final String CLIENT_JSON_PATH = "client.json";


    //Method that stores the client data in a json file
    public static void saveJsonClient(JsonObject json) throws IOException {
        FileWriter file = new FileWriter(CLIENT_JSON_PATH);
        file.write(json.toString());
        file.flush();
        file.close();
    }

    // Method that checks if the client.json file exists
    public static boolean checkIfClientJsonExists() {
        return new File(CLIENT_JSON_PATH).exists();
    }
}
