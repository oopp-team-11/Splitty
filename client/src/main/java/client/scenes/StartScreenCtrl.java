package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.application.Platform;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;
import client.interfaces.Translatable;
import org.json.JSONException;

/**
 * Start Screen controller for showing start screen and entering to events
 */
public class StartScreenCtrl implements Initializable, Translatable {
    private final MainCtrl mainCtrl;
    @FXML
    public Pane languageSwitchPlaceHolder;
    @FXML
    public Label changeLanguageLabel;

    @FXML
    private Button createBtn;

    @FXML
    private Button joinBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private Label newEventLabel;

    @FXML
    private Label joinEventLabel;

    @FXML
    private Label recentEventsLabel;

    @FXML
    private Label adminPanelLabel;

    @FXML
    private TextField newEventName;

    @FXML
    private TextField joinInvitationCode;

    @FXML
    private PasswordField adminPassword;

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> eventNameColumn;

    @FXML
    private TableColumn<Event, String> invitationCodeColumn;

    @FXML
    private TextField serverUrlBox;

    @FXML
    private Button editServerUrlBtn;

    @FXML
    private Label editServerUrlLabel;

    private final FileSystemUtils fileSystemUtils;
    private final ServerUtils serverUtils;
    private Thread pollingThread;
    boolean serverReachable;

    /**
     * @param mainCtrl main Controller, for displaying this scene.
     */
    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        fileSystemUtils = new FileSystemUtils();
        serverUtils = new ServerUtils();
        pollingThread = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventNameColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getTitle()));
        invitationCodeColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getId().toString()));
        eventTable.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Event rowData = row.getItem();
                    joinInvitationCode.setText(rowData.getId().toString());
                    onJoin();
                }
            });
            return row;
        });
        newEventName.onKeyPressedProperty().set(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) onCreate();
        });
        joinInvitationCode.onKeyPressedProperty().set(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) onJoin();
        });
        serverUrlBox.onKeyPressedProperty().set(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) onEditURL();
        });
    }

    /**
     * Translates the current scene using a translationSupplier
     * @param translationSupplier an instance of a translationSupplier. If null, the default english will be displayed.
     */
    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.newEventLabel, "CreateNewEventLabel");
        labels.put(this.joinEventLabel, "JoinEventLabel");
        labels.put(this.recentEventsLabel, "RecentEventsLabel");
        labels.put(this.adminPanelLabel, "AdminPanelLabel");
        labels.put(this.createBtn, "Create");
        labels.put(this.joinBtn, "Join");
        labels.put(this.loginBtn, "Log in");
        labels.put(this.newEventName, "EventName");
        labels.put(this.joinInvitationCode, "InvitationCode");
        labels.put(this.adminPassword, "AdminPassword");
        labels.put(this.changeLanguageLabel, "ChangeLanguageLabel");
        labels.put(this.editServerUrlLabel, "EditServerUrlLabel");
        labels.put(this.editServerUrlBtn, "Edit");
        labels.put(this.serverUrlBox, "ServerUrlBox");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation.replaceAll("\"", ""));
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation.replaceAll("\"", ""));
        });

        Map<TableColumn<Event, String>, String> tableColumns = new HashMap<>();
        tableColumns.put(this.eventNameColumn, "EventName");
        tableColumns.put(this.invitationCodeColumn, "InvitationCode");
        tableColumns.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            key.setText(translation.replaceAll("\"", ""));
        });
    }

    /**
     * Refreshes the events table.
     */
    public void refresh() {
        System.out.println("REFRESH");
        serverReachable = true;
        try {
            var events = serverUtils.getRecentEvents("http://" + mainCtrl.getServerIp(), "config.json");
            ObservableList<Event> data = FXCollections.observableList(events);
            eventTable.setItems(data);
            newEventName.clear();
            joinInvitationCode.clear();
            List<UUID> eventIds = data.stream().map(Event::getId).collect(Collectors.toList());
            startLongPolling(eventIds);

            languageSwitchPlaceHolder.getChildren().clear();
            languageSwitchPlaceHolder.getChildren().add(mainCtrl.getLanguageSwitchButton());

        } catch (IOException | InterruptedException e) {
            serverReachable = false;
        } catch (JSONException e) {
            System.out.println("Failed to parse server response: " + e.getMessage());
        }

        if(serverReachable && (mainCtrl.getSessionHandler() == null || mainCtrl.getSessionHandler().isSessionNull())) {
            if (mainCtrl.getSessionHandler() != null) {
                var alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Successfully connected to the server.");
                alert.showAndWait();
            }
            mainCtrl.startWebSocket();
        }


        serverUrlBox.setText(mainCtrl.getServerIp());
    }

    /**
     * Method that is called when the create button is clicked
     */
    public void onCreate() {
        if (!serverReachable)
            return;
        System.out.println("ONCREATE");
        String eventName = newEventName.getText();

        if(eventName.isEmpty()){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Event name is empty, please add a name");
            alert.showAndWait();
            return;
        }

        UUID invitationCode;
        try {
            invitationCode = serverUtils.createEvent(eventName, "http://" + mainCtrl.getServerIp());
        } catch (IOException | InterruptedException e) {
            serverErrorAlert(e);
            return;
        }

        try {
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                    "config.json");
        } catch (IOException e) {
            fileSaveErrorAlert(e);
            return;
        }
        mainCtrl.getSessionHandler().subscribeToEvent(invitationCode);
    }

    /**
     * Method that is called when the join button is clicked
     */
    public void onJoin() {
        if (!serverReachable)
            return;
        System.out.println("ONJOIN: " + joinInvitationCode.getText());
        UUID invitationCode;
        try {
            invitationCode = UUID.fromString(joinInvitationCode.getText());
        } catch (IllegalArgumentException e) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid invitation code, try again." +
                    "\nError: " + (e.getMessage() != null ? e.getMessage() : "No error message available.") +
                    "\n\nThe invitation code should be in the form of:\nXXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX " +
                    "(in UUID format)");
            alert.showAndWait();
            return;
        }

        try {
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                    "config.json");
        } catch (IOException e) {
            fileSaveErrorAlert(e);
            return;
        }

        mainCtrl.getSessionHandler().subscribeToEvent(invitationCode);
    }

    /**
     * Method that is called when the client wants to edit the server URL
     */
    public void onEditURL() {
        try{
            fileSystemUtils.replaceServerIPInConfigFile("client-config.json", serverUrlBox.getText());
        }
        catch (IOException e){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Failed to save server IP to config file.");
            alert.showAndWait();
        }

        mainCtrl.setServerIp();
        if (mainCtrl.getSessionHandler() != null)
            mainCtrl.getSessionHandler().disconnectFromServer();
        refresh();
    }

    /**
     * Method that is called when the user tries to log in to admin panel
     */
    public void onAdmin() {
        if (!serverReachable)
            return;
        System.out.println("ONADMIN");
        String password = adminPassword.getText();
        mainCtrl.getAdminDataHandler().setPasscode(password);
        mainCtrl.getSessionHandler().sendReadEvents(password);
    }

    private static void serverErrorAlert(Exception exception) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Something went wrong while trying to connect to server." +
                "\nError: " +
                (exception.getMessage() != null ? exception.getMessage() : "No error message available."));
        alert.showAndWait();
    }

    private static void fileSaveErrorAlert(Exception exception) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Something went wrong while saving invitation code to disk." +
                "\nError: " +
                (exception.getMessage() != null ? exception.getMessage() : "No error message available."));
        alert.showAndWait();
    }

    private void startLongPolling(List<UUID> eventIds) {

        var threads = Thread.getAllStackTraces().keySet();
        var oldPollingThread = threads.stream()
                .filter(thread -> thread.getName().equals("Polling thread"))
                .toList();
        if (!oldPollingThread.isEmpty()){
            oldPollingThread.getFirst().interrupt();
        }

        if (pollingThread != null) {
            pollingThread.interrupt();
        }

        pollingThread = new Thread(() -> {
            int prevStatusCode = -1;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String eventIdsParam = String.join(",", eventIds.stream().map(UUID::toString)
                            .collect(Collectors.toList()));
                    Client client = ClientBuilder.newClient();
                    Response response = client.target("http://" + mainCtrl.getServerIp() + "/events/updates")
                            .queryParam("query", "updates")
                            .queryParam("invitationCodes", eventIdsParam)
                            .request(MediaType.APPLICATION_JSON)
                            .get();

                    if(prevStatusCode != response.getStatus()) System.out.println(response.getStatus());
                    prevStatusCode = response.getStatus();

                    if (response.getStatus() == 200) {
                        System.out.println("Got updated events");
                        Map<UUID, String> updatedEvents = response.readEntity(new GenericType<Map<UUID, String>>() {});
                        if (!updatedEvents.isEmpty()) {
                            updateUI(updatedEvents);
                        }
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText("""
                            Server connection error,
                            Server is unavailable, please try again.""");
                        alert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                    System.out.println("Failed to get updated events: " + e.getMessage());
                    break;
                }
            }
        });
        pollingThread.setName("Polling thread");

        pollingThread.start();
    }

    private void updateUI(Map<UUID, String> updatedEvents) {
        Platform.runLater(() -> {
            for (Map.Entry<UUID, String> entry : updatedEvents.entrySet()) {
                UUID invitationCode = entry.getKey();
                String updatedTitle = entry.getValue();

                for (Event event : eventTable.getItems()) {
                    if (event.getId().equals(invitationCode)) {
                        if (updatedTitle == null || updatedTitle.isEmpty()) {
                            eventTable.getItems().remove(event);
                        } else {
                            event.setTitle(updatedTitle);
                            eventTable.refresh();
                        }
                        break;
                    }
                }
            }
        });
    }

    /**
     * Getter for event table
     * @return event table
     */
    public TableView<Event> getEventTable() {
        return eventTable;
    }

    /**
     * Sets invitation code in join invitation code
     * @param joinInvitationCode invitation code for event we are joining
     */
    public void setJoinInvitationCode(String joinInvitationCode) {
        this.joinInvitationCode.setText(joinInvitationCode);
    }

    /**
     * Getter for admin password field
     * @return admin password field
     */
    public PasswordField getAdminPassword() {
        return adminPassword;
    }
}
