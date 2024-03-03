package client.utils;

import commons.Event;
import javax.json.*;
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
    public List<Long> readInvitationCodes(String path) throws FileNotFoundException {
        if(!checkIfFileExists(path)) {
            throw new FileNotFoundException("File not found");
        }

        JsonReader reader = Json.createReader(new FileReader(path));
        JsonObject json = reader.readObject();
        reader.close();

        List<Long> codes = new ArrayList<>();
        json.getJsonArray("invitationCodes").forEach(code -> codes.add(Long.parseLong(code.toString())));

        return codes;

    }

    /**
     * Method that saves the invitation codes to the config file
     * @param invitationCode invitation code to be saved
     * @param path path of the file
     * @throws IOException if something goes wrong
     */
    public void saveInvitationCodesToConfigFile(long invitationCode, String path)
        throws IOException {
        if(!checkIfFileExists(path)) {
            List<Long> codes = new ArrayList<>();
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

        List<Long> codes = new ArrayList<>(readInvitationCodes(path));

        if(checkIfCodeExists(invitationCode, codes)) {
            return;
        }

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

    /**
     * Method that checks if a code exists in a list of codes
     * @param code code to be checked
     * @param codes list of codes
     * @return true if the code exists, false otherwise
     */
    public boolean checkIfCodeExists(long code, List<Long> codes) {
        return codes.contains(code);
    }

    /**
     * Method that updates the config file
     * @param path path of the file
     * @param codes list of codes
     * @throws IOException if something goes wrong
     */
    public void updateConfigFile(String path, List<Long> codes) throws IOException {
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codes))
                .build();

        FileWriter file = new FileWriter(path);
        file.write(json.toString());
        file.flush();
        file.close();
    }

    /**
     * Method that extracts the invitation codes from a list of events
     * @param events list of events
     * @return list of invitation codes
     */
    public List<Long> extractInvitationCodesFromEventList(List<Event> events) {
        List<Long> codes = new ArrayList<>();
        for(Event event : events) {
            codes.add(event.getId());
        }
        return codes;
    }
}
