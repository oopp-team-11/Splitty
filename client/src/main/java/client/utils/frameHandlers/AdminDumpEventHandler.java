package client.utils.frameHandlers;

import client.utils.AdminDataHandler;
import client.utils.FileSystemUtils;
import commons.StatusEntity;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for user/queue/admin/event:dump endpoint
 */
public class AdminDumpEventHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;
    private final FileSystemUtils utils;

    /**
     * Constructor for the AdminDumpEventHandler
     *
     * @param dataHandler reference to the dataHandler
     */
    public AdminDumpEventHandler(AdminDataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.utils = new FileSystemUtils(dataHandler.getSessionHandler().getMainCtrl().getTranslationSupplier());
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return StatusEntity.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        StatusEntity status = (StatusEntity) payload;
        switch (status.getStatusCode()) {
            case OK -> {
                utils.jsonDump(dataHandler.getJsonDumpDir(), status.getEvent());
            }
            case BAD_REQUEST, NOT_FOUND -> {
                if(status.isUnsolvable()){
                    Platform.runLater(() ->{
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(dataHandler.getSessionHandler().getMainCtrl()
                                .getTranslationSupplier().getTranslation("Error")
                                .replaceAll("\"", "") + status.getMessage());
                        alert.showAndWait();
                        dataHandler.getSessionHandler().getMainCtrl().showStartScreen();
                    });
                } else {
                    Platform.runLater(() ->{
                        var alert = new Alert(Alert.AlertType.WARNING);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(dataHandler.getSessionHandler().getMainCtrl()
                                .getTranslationSupplier().getTranslation("Warning")
                                .replaceAll("\"", "") + status.getMessage());
                        alert.showAndWait();
                        dataHandler.getSessionHandler().getMainCtrl().showStartScreen();
                    });
                }
            }
        }
    }
}
