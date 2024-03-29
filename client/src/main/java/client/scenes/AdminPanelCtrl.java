package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * admin panel controller
 */
public class AdminPanelCtrl {


    @FXML
    private TableColumn<Event, UUID> invitationCode;
    @FXML
    private TableColumn<Event, String> title;
    @FXML
    private TableColumn<Event, LocalDateTime> creationDate;
    @FXML
    private TableColumn<Event, LocalDateTime> lastActivityDate;
    @FXML
    private TableView<Event> eventTableView;

    private MainCtrl mainCtrl;
    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    /**
     * Constructor
     * @param mainCtrl
     */
    @Inject
    public AdminPanelCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        serverUtils = new ServerUtils();
        fileSystemUtils = new FileSystemUtils();
    }

    /**
     * methods that refreshes data on the scene
     */
    public void refreshData() {
        List<Event> events = mainCtrl.getAdminDataHandler().getEvents();
        eventTableView.getItems().addAll(events);
    }

    /**
     * method for json dump
     */
    public void jsonDump() {
        var positions = eventTableView.getSelectionModel().getSelectedCells();
        Event event = eventTableView.getItems().get(positions.getFirst().getRow());
        fileSystemUtils.jsonDump(event);
    }

    /**
     * method that deletes event
     */
    public void deleteEvent() {
        var positions = eventTableView.getSelectionModel().getSelectedCells();
        Event event = eventTableView.getItems().get(positions.getFirst().getRow());
        // TODO: propagate change to the websockets
    }

    /**
     * json import method
     */
    public void jsonImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.setInitialFileName("");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Event event;
            try {
                event = new ObjectMapper().readValue(selectedFile, Event.class);
                // TODO: Import event into application (WebSockets)
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * method that switches to the start screen
     */
    public void goToStartScreen() {
        // TODO: logic of unsubscribing from the websocket's endpoints
        // mainCtrl.getAdminSessionHandler().unsubscribeFromCurrentAdminPanel();
        mainCtrl.getAdminDataHandler().setEvents(new ArrayList<>());
        mainCtrl.showStartScreen();
    }


}
