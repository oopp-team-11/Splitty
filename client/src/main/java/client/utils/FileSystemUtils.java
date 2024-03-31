package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;

import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Client utility class for file handling
 */
public class FileSystemUtils {
    //private static final String CLIENT_JSON_PATH = "client.json";

    /**
     * method tht performs json dump
     * @param event
     * @throws IOException
     */
    public void jsonDump(Event event)  {
        try {
            String fileTitle = event.getId().toString();
            String fileData = new ObjectMapper().writeValueAsString(event);
            FileWriter file = new FileWriter(fileTitle);
            file.write(fileData);
            file.flush();
            file.close();
        } catch (IOException e) {
            // TODO: pop-up notification
        }
    }

    /**
     * Method that reads the invitation codes from the config file
     *
     * @param path path of the file
     * @return lust of invitation codes
     * @throws FileNotFoundException if the file is not found
     */
    public List<UUID> readInvitationCodes(String path) throws FileNotFoundException {
        if (!checkIfFileExists(path)) {
            throw new FileNotFoundException("File not found");
        }

        JsonReader reader = Json.createReader(new FileReader(path));
        JsonObject json = reader.readObject();
        reader.close();

        List<UUID> codes = new ArrayList<>();
        json.getJsonArray("invitationCodes").forEach(code -> {
            codes.add(UUID.fromString(code.toString().replaceAll("^\"|\"$", "")));
        });

        return codes;

    }

    /**
     * Method that saves the invitation codes to the config file
     *
     * @param invitationCode invitation code to be saved
     * @param path           path of the file
     * @throws IOException if something goes wrong
     */
    public void saveInvitationCodesToConfigFile(UUID invitationCode, String path)
            throws IOException {
        if (!checkIfFileExists(path)) {
            List<UUID> codes = new ArrayList<>();
            codes.add(invitationCode);
            List<String> codeStrings = codes.stream().map(UUID::toString).toList();
            JsonObject json = Json.createObjectBuilder()
                    .add("invitationCodes", Json.createArrayBuilder(codeStrings))
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();
            return;
        }

        List<UUID> codes = new ArrayList<>(readInvitationCodes(path));

        if (checkIfCodeExists(invitationCode, codes)) {
            return;
        }

        codes.add(invitationCode);

        List<String> codeStrings = codes.stream().map(UUID::toString).toList();
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codeStrings))
                .build();

        FileWriter file = new FileWriter(path);
        file.write(json.toString());
        file.flush();
        file.close();
    }

    /**
     * Method that checks if a file exists
     *
     * @param path path of the file
     * @return true if the file exists, false otherwise
     */
    public static boolean checkIfFileExists(String path) {
        return new File(path).exists();
    }

    /**
     * Method that checks if a code exists in a list of codes
     *
     * @param code  code to be checked
     * @param codes list of codes
     * @return true if the code exists, false otherwise
     */
    public boolean checkIfCodeExists(UUID code, List<UUID> codes) {
        return codes.contains(code);
    }

    /**
     * Method that updates the config file
     *
     * @param path  path of the file
     * @param codes list of codes
     * @throws IOException if something goes wrong
     */
    public void updateConfigFile(String path, List<UUID> codes) throws IOException {
        List<String> codeStrings = codes.stream().map(UUID::toString).toList();
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codeStrings))
                .build();

        FileWriter file = new FileWriter(path);
        file.write(json.toString());
        file.flush();
        file.close();
    }

    /**
     * Method that extracts the invitation codes from a list of events
     *
     * @param events list of events
     * @return list of invitation codes
     */
    public List<UUID> extractInvitationCodesFromEventList(List<Event> events) {
        List<UUID> codes = new ArrayList<>();
        for (Event event : events) {
            codes.add(event.getId());
        }
        return codes;
    }

    /**
     * Gets the server ip from the client-config.json
     *
     * @param path path to client config file
     * @return String of server ip address
     */
    public String getServerIP(String path) throws IOException {
        if (!checkIfFileExists(path)) {
            JsonObject json = Json.createObjectBuilder()
                    .add("server-ip", "SERVER_URL")
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();

            throw new FileNotFoundException(path + " doesn't exist. " +
                    "Created new file, please put the server url in client-config.json." +
                    "You need to delete the SERVER_URL and put it there.");
        }

        JsonReader reader = Json.createReader(new FileReader(path));
        JsonObject object = reader.readObject();
        reader.close();
        String serverIp = object.getString("server-ip");

        if (serverIp == null || serverIp.isEmpty() || serverIp.equals("SERVER_URL")) {
            throw new RuntimeException("Server IP is empty, please fill it. The file is called client-config.json");
        } else if (serverIp.contains("http://") || serverIp.contains("ws://") ||
                serverIp.contains("https://") || serverIp.contains("wss://")) {
            serverIp = serverIp.replace("https://", "");
            serverIp = serverIp.replace("http://", "");
            serverIp = serverIp.replace("wss://", "");
            serverIp = serverIp.replace("ws://", "");
        }

        return serverIp;
    }

    /**
     * Returns a translation supplier from a config.
     * @param path the path of the config file to read.
     * @return the associated translationsupplier, null otherwise.
     * @throws IOException If an error occurs during config file reading.
     */
    public TranslationSupplier getTranslationSupplier(String path) throws IOException {
        JsonReader reader = Json.createReader(new FileReader(path));
        JsonObject object = reader.readObject();
        reader.close();
        try {
            String lang = object.getString("lang");
            return new TranslationSupplier(lang);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
