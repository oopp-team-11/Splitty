package client.scenes;

import client.utils.EventStompSessionHandler;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Start Screen controller for showing start screen and entering to events
 */
public class StartScreenCtrl implements Initializable {
    private final MainCtrl mainCtrl;

    @FXML
    private Button createBtn;

    @FXML
    private Button joinBtn;

    @FXML
    private Label newEventLabel;

    @FXML
    private Label joinEventLabel;

    @FXML
    private Label recentEventsLabel;

    @FXML
    private TextField newEventName;

    @FXML
    private TextField joinInvitationCode;

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> eventNameColumn;

    @FXML
    private TableColumn<Event, String> invitationCodeColumn;

    private final FileSystemUtils fileSystemUtils;
    private final ServerUtils serverUtils;
    private StompSessionHandler sessionHandler;
    private TranslationSupplier translationSupplier;

    /**
     * @param mainCtrl main Controller, for displaying this scene.
     */
    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        fileSystemUtils = new FileSystemUtils();
        serverUtils = new ServerUtils();
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
    }

    private void translate() {
        if (this.translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.newEventLabel, "CreateNewEventLabel");
        labels.put(this.joinEventLabel, "JoinEventLabel");
        labels.put(this.recentEventsLabel, "RecentEventsLabel");
        labels.put(this.createBtn, "Create");
        labels.put(this.joinBtn, "Join");
        labels.put(this.newEventName, "EventName");
        labels.put(this.joinInvitationCode, "InvitationCode");
        labels.forEach((k, v) -> {
            var translation = this.translationSupplier.getTranslation(v);
            if (translation == null) return;
            if (k instanceof Labeled)
                ((Labeled) k).setText(translation.replaceAll("\"", ""));
            if (k instanceof TextField)
                ((TextField) k).setPromptText(translation.replaceAll("\"", ""));
        });

        Map<TableColumn<Event, String>, String> tableColumns = new HashMap<>();
        tableColumns.put(this.eventNameColumn, "EventName");
        tableColumns.put(this.invitationCodeColumn, "InvitationCode");
        tableColumns.forEach((k, v) -> {
            var translation = this.translationSupplier.getTranslation(v);
            if (translation == null) return;
            k.setText(translation.replaceAll("\"", ""));
        });
    }

    /**
     * Refreshes the events table.
     */
    public void refresh() throws IOException, InterruptedException {
        var events = serverUtils.getRecentEvents("https://127.0.0.1:8080", "config.json");
        ObservableList<Event> data = FXCollections.observableList(events);
        eventTable.setItems(data);
    }

    /**
     * Method that is called when the create button is clicked
     */
    public void onCreate() {
        System.out.println("ONCREATE");
        String eventName = newEventName.getText();

        // TODO: validate eventName first
        UUID invitationCode;
        try {
            invitationCode = serverUtils.createEvent(eventName, "http://localhost:8080");
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

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        } catch (IOException | InterruptedException e) {
            serverErrorAlert(e);
        }
    }

    /**
     * Method that is called when the join button is clicked
     */
    public void onJoin() {
        // todo: send get request to /events with invitation code
        // if status == 200
        //      event = response body
        //      MainCtrl.showEventScreen(event)
        // else
        //      MainCtrl.showUserCreationScreen(invitationCode)
        System.out.println("ONJOIN: " + joinInvitationCode.getText());
        UUID invitationCode;
        try {
            invitationCode = UUID.fromString(joinInvitationCode.getText());
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid invitation code, try again." +
                    "\nError: " + (e.getMessage() != null ? e.getMessage() : "No error message available."));
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

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        } catch (IOException | InterruptedException e) {
            serverErrorAlert(e);
        }

        startWebSocket();
    }

    //TODO: Potentially move this method to a more appropriate class
    private void startWebSocket() {
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        //TODO:Change dummy event to an actual event
        sessionHandler = new EventStompSessionHandler(UUID.randomUUID());
        stompClient.connectAsync("ws://localhost:8080/event", sessionHandler);
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

    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
        this.translate();
    }
}
