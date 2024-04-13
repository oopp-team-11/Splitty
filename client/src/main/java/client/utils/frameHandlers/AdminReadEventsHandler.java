package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.AdminDataHandler;
import commons.StatusEntity;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for user/queue/admin/events:read endpoint
 */
public class AdminReadEventsHandler implements StompFrameHandler {
    private final AdminDataHandler dataHandler;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the AdminReadEventsHandler
     *
     * @param dataHandler reference to the dataHandler
     * @param mainCtrl reference to mainCtrl for getting translationSupplier
     */
    public AdminReadEventsHandler(AdminDataHandler dataHandler, MainCtrl mainCtrl) {
        this.dataHandler = dataHandler;
        this.mainCtrl = mainCtrl;
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
                dataHandler.setEvents(status.getEventList());
            }
            case BAD_REQUEST, NOT_FOUND -> {
                if(status.isUnsolvable()){
                    Platform.runLater(() ->{
                        var alert = new Alert(Alert.AlertType.ERROR);
                        var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(dataHandler.getSessionHandler().getMainCtrl()
                                .getTranslationSupplier().getTranslation("Error")
                                + (translatedMessage != null ? translatedMessage : status.getMessage()));
                        alert.showAndWait();
                        dataHandler.getSessionHandler().getMainCtrl().showStartScreen();
                    });
                } else {
                    Platform.runLater(() ->{
                        var alert = new Alert(Alert.AlertType.WARNING);
                        var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(dataHandler.getSessionHandler().getMainCtrl()
                                .getTranslationSupplier().getTranslation("Warning")
                                + (translatedMessage != null ? translatedMessage : status.getMessage()));
                        alert.showAndWait();
                        dataHandler.getSessionHandler().getMainCtrl().showStartScreen();
                    });
                }
            }
        }
    }
}
