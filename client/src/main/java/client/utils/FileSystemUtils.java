package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

import javax.json.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Client utility class for file handling
 */
public class FileSystemUtils {
    //private static final String CLIENT_JSON_PATH = "client.json";

    /**
     * Set the default backup directory or create it, if it doesn't exist
     *
     * @return returns a File with directory
     */
    public File setBackupsDirectory() {
        String directoryPath = System.getProperty("user.dir") + File.separator + "backups";
        try {
            Files.createDirectories(Path.of(directoryPath));
        } catch (IOException e) {
            System.err.println("Could not create backup directory. Setting a default directory.");
            return new File("");
        }
        return new File(directoryPath);
    }

    /**
     * method tht performs json dump
     * @param jsonDumpDir
     * @param event
     * @throws IOException
     */
    public void jsonDump(File jsonDumpDir, Event event)  {
        try {
            String fileTitle = "event-" + event.getId().toString();
            File fileDir = new File(jsonDumpDir.getPath() + File.separator + fileTitle);
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            String fileData = mapper.writeValueAsString(event);
            FileWriter file = new FileWriter(fileDir);
            file.write(fileData);
            file.flush();
            file.close();
            //TODO: pop-up notification
        } catch (IOException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
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
                    .add("server-ip", "localhost:8080")
                    .add("lang", "en")
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();

            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(path + " doesn't exist. \n" +
                    "Created new default file, please put the server url if it isn't localhost:8080 " +
                    "in client-config.json.\n" +
                    "You need to delete localhost:8080 and put it there.");
            alert.showAndWait();

            return getServerIP(path);
        }

        try{
            JsonReader reader = Json.createReader(new FileReader(path));
            JsonObject object = reader.readObject();
            reader.close();
            String serverIp = object.getString("server-ip");

            if (serverIp == null || serverIp.isEmpty()) {
                JsonObject json = Json.createObjectBuilder()
                        .add("server-ip", "localhost:8080")
                        .add("lang", "en")
                        .build();

                FileWriter file = new FileWriter(path);
                file.write(json.toString());
                file.flush();
                file.close();

                var alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(path + " is the wrong format. \n" +
                        "Created new default file, please put the server url if it isn't localhost:8080 " +
                        "in client-config.json.\n" +
                        "You need to delete localhost:8080 and put it there.");
                alert.showAndWait();
                return getServerIP(path);
            } else if (serverIp.contains("http://") || serverIp.contains("ws://") ||
                    serverIp.contains("https://") || serverIp.contains("wss://")) {
                serverIp = serverIp.replace("https://", "");
                serverIp = serverIp.replace("http://", "");
                serverIp = serverIp.replace("wss://", "");
                serverIp = serverIp.replace("ws://", "");
            } else if (!serverIp.contains(":")) {
                serverIp = serverIp + ":8080";
            }
            return serverIp;
        }
        catch (Exception e){
            JsonObject json = Json.createObjectBuilder()
                    .add("server-ip", "localhost:8080")
                    .add("lang", "en")
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();

            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(path + " is the wrong format. \n" +
                    "Created new default file, please put the server url if it isn't localhost:8080 " +
                    "in client-config.json.\n" +
                    "You need to delete localhost:8080 and put it there.");
            alert.showAndWait();
            return getServerIP(path);
        }
    }

    /**
     * Returns a translation supplier from a config.
     * @param path the path of the config file to read.
     * @return the associated translationsupplier, null otherwise.
     * @throws IOException If an error occurs during config file reading.
     */
    public TranslationSupplier getTranslationSupplier(String path) throws IOException {
        try{
            JsonReader reader = Json.createReader(new FileReader(path));
            JsonObject object = reader.readObject();
            reader.close();
            String lang = object.getString("lang");

            if (lang == null || lang.isEmpty()) {
                JsonObject json = Json.createObjectBuilder()
                        .add("server-ip", "localhost:8080")
                        .add("lang", "en")
                        .build();

                FileWriter file = new FileWriter(path);
                file.write(json.toString());
                file.flush();
                file.close();

                var alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(path + " is the wrong format. \n" +
                        "Created new default file, please put the language if it isn't en(english) " +
                        "in client-config.json.\n" +
                        "You need to delete en and put it there.");
                alert.showAndWait();
                return getTranslationSupplier(path);
            }
            return new TranslationSupplier(lang);
        }
        catch (Exception e) {
            JsonObject json = Json.createObjectBuilder()
                    .add("server-ip", "localhost:8080")
                    .add("lang", "en")
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();

            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(path + " is the wrong format. \n" +
                    "Created new default file, please put the language if it isn't en(english) " +
                    "in client-config.json.\n" +
                    "You need to delete en and put it there.");
            alert.showAndWait();
            return getTranslationSupplier(path);
        }
    }

    /**
     * Method for changing language in config file
     * @param path path to client-config.json
     * @param language language to set it to
     * @throws IOException possible error throw
     */
    public void changeLanguageInFile(String path, String language) throws IOException {
        try{
            JsonReader reader = Json.createReader(new FileReader(path));
            JsonObject oldJson = reader.readObject();
            reader.close();

            JsonObject newJson = Json.createObjectBuilder()
                    .add("server-ip", oldJson.get("server-ip"))
                    .add("lang", language)
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(newJson.toString());
            file.flush();
            file.close();
        }
        catch (Exception e) {
            JsonObject json = Json.createObjectBuilder()
                    .add("server-ip", "localhost:8080")
                    .add("lang", "en")
                    .build();

            FileWriter file = new FileWriter(path);
            file.write(json.toString());
            file.flush();
            file.close();

            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(path + " is the wrong format. \n" +
                    "Created new default file, please put the language if it isn't en(english) " +
                    "in client-config.json.\n" +
                    "You need to delete en and put it there.");
            alert.showAndWait();
        }
    }
}
