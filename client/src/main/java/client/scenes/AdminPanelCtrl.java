package client.scenes;

import client.interfaces.Translatable;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.Event;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * admin panel controller
 */
public class AdminPanelCtrl implements Translatable {
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
    @FXML
    private Button jsonImport;

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
        invitationCode.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getId().toString()));
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

        mainCtrl.getAdminDataHandler().setJsonDumpDir(fileSystemUtils.setBackupsDirectory());

        refreshData();
    }

    /**
     * methods that refreshes data on the scene
     */
    public void refreshData() {
        eventTableView.getColumns().getFirst().setVisible(false);
        eventTableView.getColumns().getFirst().setVisible(true);
        eventTableView.setItems(FXCollections.observableList(mainCtrl.getAdminDataHandler().getEvents()));
    }

    /**
     * method for json dump
     * @param event
     */
    public void jsonDump(Event event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Download");
        directoryChooser.setInitialDirectory(mainCtrl.getAdminDataHandler().getJsonDumpDir());
        mainCtrl.getAdminDataHandler().setJsonDumpDir(directoryChooser.showDialog(null));
        mainCtrl.getSessionHandler().sendAdminEvent(mainCtrl.getAdminDataHandler().getPasscode(),
                event, "dump");
    }

    /**
     * method that deletes event
     * @param event
     */
    public void deleteEvent(Event event) {
        mainCtrl.getSessionHandler().sendAdminEvent(mainCtrl.getAdminDataHandler().getPasscode(),
                event, "delete");
    }

    /**
     * json import method
     */
    public void jsonImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.setInitialFileName("");
        fileChooser.setInitialDirectory(mainCtrl.getAdminDataHandler().getJsonDumpDir());
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Event event;
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.findAndRegisterModules();
                event = mapper.readValue(selectedFile, Event.class);
                mainCtrl.getSessionHandler().sendAdminEvent(mainCtrl.getAdminDataHandler().getPasscode(),
                        event, "import");
            } catch (IOException e) {
                //TODO: Add a pop-up
            }
        }
    }

    /**
     * method that switches to the start screen
     */
    public void goToStartScreen() {
        mainCtrl.getSessionHandler().unsubscribeFromAdmin();
        mainCtrl.getAdminDataHandler().setDataToNull();
        mainCtrl.showStartScreen();
    }


    /**
     * Translates the current scene using a translationSupplier
     * @param translationSupplier an instance of a translationSupplier. If null, the default english will be displayed.
     */
    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();

        labels.put(this.jsonImport, "JSONImport");
        labels.put(this.changeLanguageLabel, "ChangeLanguageLabel");
        labels.put(this.goToStartScreenLabel, "GoToStartScreenLabel");

        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation.replaceAll("\"", ""));
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation.replaceAll("\"", ""));
        });
        Map<TableColumn, String> tableColumns = new HashMap<>();

        tableColumns.put(this.title, "EventName");
        tableColumns.put(this.invitationCode, "InvitationCode");
        tableColumns.put(this.creationDate, "CreationDate");
        tableColumns.put(this.deleteEvent, "DeleteEvent");
        tableColumns.put(this.lastActivityDate, "LastActivityDate");
        tableColumns.put(this.jsonDump, "JSONDump");

        tableColumns.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            key.setText(translation.replaceAll("\"", ""));
        });
    }
}
