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

import client.utils.EventStompSessionHandler;
import commons.Event;
import commons.Participant;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
//import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.util.UUID;

/**
 * Main scene controller. It oversights currently active scenes, switches between them,
 * loads them with the initialize() method
 */
public class MainCtrl {

    private Stage primaryStage;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreenScene;

    private CreateParticipantCtrl createParticipantCtrl;
    private Scene createParticipantScene;

    private EditParticipantCtrl editParticipantCtrl;
    private Scene editParticipantScene;

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverviewScene;
    private EventStompSessionHandler sessionHandler;

    /**
     * Initializes javafx scenes and their controllers, sets start screen as the currently shown screen
     * @param primaryStage stage
     * @param startScreen a pair of start screen controller and javafx start screen scene
     * @param createParticipant a pair of create participant controller and javafx create participant scene
     * @param editParticipant a pair of edit participant controller and javafx edit participant scene
     * @param eventOverview a pair of event overview controller and javafx event overview scene
     */

    public void initialize(Stage primaryStage, Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<CreateParticipantCtrl, Parent> createParticipant,
                           Pair<EditParticipantCtrl, Parent> editParticipant,
                           Pair<EventOverviewCtrl, Parent> eventOverview) {
        this.primaryStage = primaryStage;

        this.startScreenCtrl = startScreen.getKey();
        this.startScreenScene = new Scene(startScreen.getValue());

        this.createParticipantCtrl = createParticipant.getKey();
        this.createParticipantScene = new Scene(createParticipant.getValue());

        this.editParticipantCtrl = editParticipant.getKey();
        this.editParticipantScene = new Scene(editParticipant.getValue());

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverviewScene = new Scene(eventOverview.getValue());

//        Event event = new Event("My Event");
//        Participant part = new Participant(event, "Boaz", "Bakhuijzen", "bbboaz.bb@gmail.com", null, null);
//        try {
//            FieldUtils.writeField(event, "id", UUID.fromString("748d9079-8450-4b6d-adb3-736afd312ad7"), true);
//        }
//        catch (IllegalAccessException e){
//
//        }
//        showEventOverview(event);

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
     * @param event Event, which the participant will belong to
     */
    public void showCreateParticipant(Event event) {
        primaryStage.setTitle("Add participant ui");
        primaryStage.setScene(createParticipantScene);
        primaryStage.setResizable(false);
        createParticipantCtrl.setEvent(event, sessionHandler);
    }

    /**
     * Show edit participant ui
     * @param participant Participant that will be edited
     */
    public void showEditParticipant(Participant participant) {
        primaryStage.setTitle("Edit participant ui");
        primaryStage.setScene(editParticipantScene);
        primaryStage.setResizable(false);
        editParticipantCtrl.setParticipant(participant, sessionHandler);
    }

    /**
     * Show event overview
     * @param event Event, which will be shown
     */
    public void showEventOverview(Event event) {
        primaryStage.setTitle("Event overview");
        primaryStage.setScene(eventOverviewScene);
        primaryStage.setResizable(false);
        eventOverviewCtrl.setEvent(event, sessionHandler);
    }


    /**
     * Start websocket connection
     * @return Stomp session handler, so you can share the websocket connection between scenes
     */
    public StompSessionHandler startWebSocket(UUID invitationCode) {
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        sessionHandler = new EventStompSessionHandler(invitationCode);
        stompClient.connectAsync("ws://localhost:8080/event", sessionHandler);
        return sessionHandler;
    }

}