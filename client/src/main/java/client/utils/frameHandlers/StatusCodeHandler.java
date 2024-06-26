package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import commons.StatusEntity;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /user/queue/reply topic
 */
public class StatusCodeHandler implements StompFrameHandler {
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the StatusCodeHandler
     *
     * @param mainCtrl mainCtrl reference to use for notifying about received status codes
     */
    public StatusCodeHandler(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return StatusEntity.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        StatusEntity status = (StatusEntity) payload;
        switch (status.getStatusCode()){
            case OK -> Platform.runLater(() -> {
                var alert = new Alert(Alert.AlertType.INFORMATION);
                var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(translatedMessage != null ? translatedMessage : status.getMessage());
                alert.setHeaderText(mainCtrl.getTranslationSupplier().getTranslation("Success"));
                alert.showAndWait();
                if (!mainCtrl.getPrimaryStageTitle().equals("Admin Panel"))
                    mainCtrl.showEventOverview();
            });
            case NOT_FOUND -> {
                if(status.isUnsolvable()) {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(translatedMessage != null ? translatedMessage : status.getMessage());
                        alert.setHeaderText(mainCtrl.getTranslationSupplier().getTranslation("NotFound"));
                        alert.showAndWait();
                    });
                }else {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.WARNING);
                        var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(translatedMessage != null ? translatedMessage : status.getMessage());
                        alert.setHeaderText(mainCtrl.getTranslationSupplier().getTranslation("NotFound"));
                        alert.showAndWait();
                    });
                }
            }
            case BAD_REQUEST -> {
                if(status.isUnsolvable()) {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(translatedMessage != null ? translatedMessage : status.getMessage());
                        alert.setHeaderText(mainCtrl.getTranslationSupplier().getTranslation("BadRequest"));
                        alert.showAndWait();
                    });
                }else {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.WARNING);
                        var translatedMessage = mainCtrl.getTranslationSupplier().getTranslation(status.getMessage());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(translatedMessage != null ? translatedMessage : status.getMessage());
                        alert.setHeaderText(mainCtrl.getTranslationSupplier().getTranslation("BadRequest")
                                .replaceAll("\"", ""));
                        alert.showAndWait();
                    });
                }
            }
        }
    }
}
