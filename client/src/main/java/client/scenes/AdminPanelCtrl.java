package client.scenes;

import client.utils.AdminDataHandler;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Inject
    public AdminPanelCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        serverUtils = new ServerUtils();
        fileSystemUtils = new FileSystemUtils();
    }


}
