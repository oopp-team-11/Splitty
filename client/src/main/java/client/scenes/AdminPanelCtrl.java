package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.Event;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * admin panel controller
 */
public class AdminPanelCtrl {
    @FXML
    public Pane languageSwitchPlaceHolder;
    @FXML
    public Button startScreenButton;
    @FXML
    public Label changeLanguageLabel;
    @FXML
    public Label goToStartScreenLabel;
    @FXML
    private TableColumn<Event, String> invitationCode;
    @FXML
    private TableColumn<Event, String> title;
    @FXML
    private TableColumn<Event, String> creationDate;
    @FXML
    private TableColumn<Event, String> lastActivityDate;
    @FXML
    private TableColumn<Event, Button> deleteEvent;
    @FXML
    private TableColumn<Event, Button> jsonDump;
    @FXML
    private TableView<Event> eventTableView;

    private MainCtrl mainCtrl;
    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    /**
     * Constructor
     *
     * @param mainCtrl
     */
    @Inject
    public AdminPanelCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        serverUtils = new ServerUtils();
        fileSystemUtils = new FileSystemUtils();
    }

    /**
     * method that performs initial setup for scene
     */
    public void makeSetUp() {
        invitationCode.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getId(), toString()));
        title.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getTitle()));
        creationDate.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getCreationDate().toString()));
        lastActivityDate.
                setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getLastActivity().toString()));

        deleteEvent.setCellValueFactory(event -> {
            Button button = new Button("X");
            button.setOnAction(event1 -> deleteEvent(event.getValue()));
            return new SimpleObjectProperty<>(button);
        });

        jsonDump.setCellValueFactory(event -> {
            Button button = new Button("⬇");
            button.setOnAction(event1 -> jsonDump(event.getValue()));
            return new SimpleObjectProperty<>(button);
        });

        languageSwitchPlaceHolder.getChildren().clear();
        languageSwitchPlaceHolder.getChildren().add(mainCtrl.getLanguageSwitchButton());

        refreshData();
    }

    /**
     * methods that refreshes data on the scene
     */
    public void refreshData() {
        eventTableView.getColumns().getFirst().setVisible(false);
        eventTableView.getColumns().getFirst().setVisible(false);
        eventTableView.setItems(FXCollections.observableList(mainCtrl.getAdminDataHandler().getEvents()));
    }

    /**
     * method for json dump
     * @param event
     */
    public void jsonDump(Event event) {
        fileSystemUtils.jsonDump(event);
    }

    /**
     * method that deletes event
     * @param event
     */
    public void deleteEvent(Event event) {
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
