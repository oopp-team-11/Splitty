package client.utils.frameHandlers;

import client.scenes.MainCtrl;
import client.utils.EventDataHandler;
import commons.StatusEntity;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * Frame handler for /user/queue/event:read endpoint
 */
public class ReadEventHandler implements StompFrameHandler {
    private final EventDataHandler dataHandler;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for the ReadEventHandler
     *
     * @param dataHandler reference to the dataHandler
     * @param mainCtrl reference to mainCtrl
     */
    public ReadEventHandler(EventDataHandler dataHandler, MainCtrl mainCtrl) {
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
                boolean init = dataHandler.getEvent() == null;
                dataHandler.setEvent(status.getEvent());
                if (init)
                    mainCtrl.getSessionHandler().afterInitialEventRead();
            }
            case BAD_REQUEST -> {
                if(status.isUnsolvable()) {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(mainCtrl.getTranslationSupplier()
                                .getTranslation("InvalidInvitationCode")
                                + "\n" + mainCtrl.getTranslationSupplier().getTranslation("Error")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("ReadEventBadRequest")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("InvitationCodeForm"));
                        alert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                } else {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.WARNING);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(mainCtrl.getTranslationSupplier()
                                .getTranslation("InvalidInvitationCode")
                                + "\n" + mainCtrl.getTranslationSupplier().getTranslation("Warning")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("ReadEventBadRequest")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("InvitationCodeForm"));
                        alert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                }
            }
            case NOT_FOUND -> {
                if(status.isUnsolvable()) {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(mainCtrl.getTranslationSupplier()
                                .getTranslation("InvalidInvitationCode")
                                + "\n" + mainCtrl.getTranslationSupplier().getTranslation("Error")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("ReadEventNotFound")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("InvitationCodeForm"));
                        alert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                } else {
                    Platform.runLater(() -> {
                        var alert = new Alert(Alert.AlertType.WARNING);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(mainCtrl.getTranslationSupplier()
                                .getTranslation("InvalidInvitationCode")
                                + "\n" + mainCtrl.getTranslationSupplier().getTranslation("Warning")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("ReadEventNotFound")
                                + mainCtrl.getTranslationSupplier()
                                .getTranslation("InvitationCodeForm"));
                        alert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                }
            }
        }
    }
}
