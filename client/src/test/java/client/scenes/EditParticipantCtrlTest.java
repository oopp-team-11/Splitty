package client.scenes;

import client.Main;
import client.MyFXML;
import client.MyModule;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ApplicationExtension.class)
public class EditParticipantCtrlTest {

    private Button button;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        var app = new Main();
        try {
            app.start(stage);
        } catch (IOException e) {
            fail();
        }

        var FXML = new MyFXML(createInjector(new MyModule()));
        var editParticipantScene = FXML.load(EditParticipantCtrl.class,
                        "client", "scenes", "EditParticipant.fxml")
                        .getValue();

        stage.setScene(new Scene(editParticipantScene));
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void shouldContainEditParticipantButton(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#EditParticipant").queryAs(Button.class)).hasText("Edit");
    }
}
