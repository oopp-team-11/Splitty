package client.scenes;

import client.MyFXML;
import client.MyModule;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import org.junit.jupiter.api.extension.ExtendWith;

import client.Main;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ApplicationExtension.class)
public class CreateParticipantCtrlTest {

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
        var createParticipantScene = FXML.load(CreateParticipantCtrl.class,
                        "client", "scenes", "CreateParticipant.fxml")
                .getValue();

        stage.setScene(new Scene(createParticipantScene));
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    //@Test
    void shouldContainCreateParticipantButton(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#CreateParticipant").queryAs(Button.class)).hasText("Add");
    }
}
