// CHECKSTYLE:OFF
package client.scenes;

import client.Main;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(ApplicationExtension.class)
class StartScreenCtrlTest {
    /**
     * Will be called with {@code @Before} semantics, i.e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    //@Start
    private void start(Stage stage) {
        var app = new Main();
        try {
            app.start(stage);
        } catch (IOException e) {
            fail();
        }
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    //@Test
    void shouldContainCreateEventBtn(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#createBtn").queryAs(Button.class)).hasText("Create");
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    //@Test
    void shouldContainJoinEventBtn(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#joinBtn").queryAs(Button.class)).hasText("Join");
    }

    /**
     * Tests if there is a TableView with recent events.
     *
     * @param robot - Will be injected by the test runner.
     */
    //@Test
    void shouldContainRecentEvents(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#eventTable").queryAs(TableView.class))
                .hasChild("#eventNameColumn");
    }

    /**
     * Tests if the text field for join event by invitation code works
     *
     * @param robot - Will be injected by the test runner.
     */
    //@Test
    void shouldContainJoinEventTextField(FxRobot robot) {
        robot.clickOn("#joinInvitationCode", MouseButton.PRIMARY);
        robot.write("123456789");
        Assertions.assertThat(robot.lookup("#joinInvitationCode").queryAs(TextField.class))
                .hasText("123456789");
    }

    /**
     * Tests if the text field for create a new event by name works
     *
     * @param robot - Will be injected by the test runner.
     */
    //@Test
    void shouldContainCreateEventTextField(FxRobot robot) {
        robot.clickOn("#newEventName", MouseButton.PRIMARY);
        robot.write("Breakfast");
        Assertions.assertThat(robot.lookup("#newEventName").queryAs(TextField.class))
                .hasText("Breakfast");
    }

    /**
     * Tests if the window is resizeable, which it should not do
     *
     * @throws TimeoutException may throw this, if getting the stage takes to long
     */
    //@Test
    void shouldBeResizeable() throws TimeoutException {
        var stage = FxToolkit.registerPrimaryStage();
        assertFalse(stage.isResizable());
    }
}
