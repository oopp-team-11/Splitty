/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import commons.Event;
import commons.Participant;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.IOException;

public class MainCtrl {

    private Stage primaryStage;

//    private QuoteOverviewCtrl overviewCtrl;
//    private Scene overview;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreenScene;

    private CreateParticipantCtrl createParticipantCtrl;
    private Scene createParticipantScene;

    private EditParticipantCtrl editParticipantCtrl;
    private Scene editParticipantScene;

    public void initialize(Stage primaryStage, Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<CreateParticipantCtrl, Parent> createParticipant,
                           Pair<EditParticipantCtrl, Parent> editParticipant) {
        this.primaryStage = primaryStage;

        this.startScreenCtrl = startScreen.getKey();
        this.startScreenScene = new Scene(startScreen.getValue());

        this.createParticipantCtrl = createParticipant.getKey();
        this.createParticipantScene = new Scene(createParticipant.getValue());

        this.editParticipantCtrl = editParticipant.getKey();
        this.editParticipantScene = new Scene(editParticipant.getValue());

        showStartScreen();

        // showStartScreen() should be used in the final version.
        // Comment out showStartScreen() above and uncomment a scene below to
        // get it to launch as a start screen for debugging reasons.

        //showCreateParticipant(null);
        //showEditParticipant(null);
        primaryStage.show();
    }

    /**
     * Show start screen
     */
    public void showStartScreen() {
        primaryStage.setTitle("Start Screen");
        primaryStage.setScene(startScreenScene);
        primaryStage.setResizable(false);
        try {
            startScreenCtrl.refresh();
        } catch (IOException | InterruptedException ignored) {}
    }

    /**
     * Show create participant UI
     * @param event
     */
    public void showCreateParticipant(Event event) {
        primaryStage.setTitle("Add participant ui");
        primaryStage.setScene(createParticipantScene);
        primaryStage.setResizable(false);
        createParticipantCtrl.setEvent(event);
    }

    /**
     * Show edit participant ui
     * @param participant
     */
    public void showEditParticipant(Participant participant) {
        primaryStage.setTitle("Edit participant ui");
        primaryStage.setScene(editParticipantScene);
        primaryStage.setResizable(false);
        editParticipantCtrl.setParticipant(participant);
    }

}