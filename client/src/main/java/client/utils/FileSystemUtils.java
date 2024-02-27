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

    // Method that reads invitation codes from config file
    public static List<String> readInvitationCodes(String path) throws FileNotFoundException {
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

    // Method that saves list of invitation codes as json to config file
    public static void saveInvitationCodesToConfigFile(String invitationCode, String path)
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

        //System.out.println(codes);

        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codes))
                .build();

        FileWriter file = new FileWriter(path);
        file.write(json.toString());
        file.flush();
        file.close();
    }

    // Method that checks if the client.json file exists
    public static boolean checkIfFileExists(String path) {
        return new File(path).exists();
    }
}
