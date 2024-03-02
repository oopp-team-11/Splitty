package client.scenes;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.ArrayList;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;

public class StartScreenCtrl implements Initializable {
    private final MainCtrl mainCtrl;

    @FXML
    private TextField newEventName;

    @FXML
    private TextField joinInvitationCode;

    private ObservableList<Event> data;

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> eventNameColumn;

    @FXML
    private TableColumn<Event, String> invitationCodeColumn;

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        fileSystemUtils = new FileSystemUtils();
        serverUtils = new ServerUtils();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventNameColumn.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getTitle()));
        invitationCodeColumn.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getId().toString()));
        eventTable.setRowFactory( tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Event rowData = row.getItem();
                    joinInvitationCode.setText(rowData.getId().toString());
                    onJoin();
                }
            });
            return row;
        });
    }

    /**
     * Refreshes the events table.
     */
    public void refresh() {
        // TODO: marios implement this method
        // getEventsFromConfig should first grab all invitation codes in
        // the config json, then for each invitation code fetch the Event from
        // the /events/{invitationCode} endpoint.
        //var events = serverUtils.getEventsFromConfig

        var events = new ArrayList<Event>();
        var event = new Event(6662137,
                "The Event we need to pay for",
                LocalDateTime.of(2024, 2, 12, 12, 0, 0),
                LocalDateTime.of(2024, 2, 14, 12, 0, 0),
                null);
        events.add(event);
        data = FXCollections.observableList(events);
        eventTable.setItems(data);
    }

    /**
     * Method that is called when the create button is clicked
     */
    public void onCreate() {
        // todo: send create request (PUT) to /events
        System.out.println("ONCREATE");
        String eventName = newEventName.getText();
        long invitationCode;

        try {
            invitationCode = serverUtils.createEvent(eventName, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error while sending create request to server");
            return;
        }

        try {
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                "config.json");
        }
        catch (IOException e) {
            System.err.println("Error while saving invitation code to config file");
        }

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error while sending get request to server");
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
        long invitationCode = Integer.parseInt(joinInvitationCode.getText());

        try{
            fileSystemUtils.saveInvitationCodesToConfigFile(invitationCode,
                "config.json");
        }
        catch (IOException e) {
            System.err.println("Error while saving invitation code to config file");
        }

        try {
            serverUtils.getEvent(invitationCode, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error while sending get request to server");
        }
    }
}