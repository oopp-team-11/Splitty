package client.utils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSystemUtils {
    //private static final String CLIENT_JSON_PATH = "client.json";

    /**
     * Method that reads the invitation codes from the config file
     * @param path path of the file
     * @return lust of invitation codes
     * @throws FileNotFoundException if the file is not found
     */
    public List<String> readInvitationCodes(String path) throws FileNotFoundException {
        if(!checkIfFileExists(path)) {
            throw new FileNotFoundException("File not found");
        }

        JsonReader reader = Json.createReader(new FileReader(path));
        JsonObject json = reader.readObject();
        reader.close();

        List<String> codes = new ArrayList<>();
        json.getJsonArray("invitationCodes").forEach(code -> codes.add(((JsonString) code).getString()));

        return codes;

    }

    /**
     * Method that saves the invitation codes to the config file
     * @param invitationCode invitation code to be saved
     * @param path path of the file
     * @throws IOException if something goes wrong
     */
    public void saveInvitationCodesToConfigFile(String invitationCode, String path)
        throws IOException {
        if(!checkIfFileExists(path)) {
            List<String> codes = new ArrayList<>();
            codes.add(invitationCode);
            JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codes))
                .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();
            return;
        }

        List<String> codes = new ArrayList<>(readInvitationCodes(path));
        codes.add(invitationCode);

        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codes))
                .build();

        FileWriter file = new FileWriter(path);
        file.write(json.toString());
        file.flush();
        file.close();
    }

    /**
     * Method that checks if a file exists
     * @param path path of the file
     * @return true if the file exists, false otherwise
     */
    public boolean checkIfFileExists(String path) {
        return new File(path).exists();
    }
}
