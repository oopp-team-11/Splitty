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

import client.utils.EventDataHandler;
import client.utils.WebsocketSessionHandler;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

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

    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpenseScene;

    private EditExpenseCtrl editExpenseCtrl;
    private Scene editExpenseScene;

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverviewScene;

    private WebsocketSessionHandler sessionHandler;
    private EventDataHandler dataHandler;

    /**
     * Initializes javafx scenes and their controllers, sets start screen as the currently shown screen
     * @param primaryStage stage
     * @param startScreen a pair of start screen controller and javafx start screen scene
     * @param createParticipant a pair of create participant controller and javafx create participant scene
     * @param editParticipant a pair of edit participant controller and javafx edit participant scene
     * @param eventOverview a pair of event overview controller and javafx event overview scene
     * @param editExpense a pair of edit expense controller and javafx edit expense scene
     * @param addExpense a pair of add expense controller and javafx add expense scene
     */

    public void initialize(Stage primaryStage, Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<CreateParticipantCtrl, Parent> createParticipant,
                           Pair<EditParticipantCtrl, Parent> editParticipant,
                           Pair<EventOverviewCtrl, Parent> eventOverview,
                           Pair<EditExpenseCtrl, Parent> editExpense,
                           Pair<AddExpenseCtrl, Parent> addExpense) {
        this.primaryStage = primaryStage;

        this.startScreenCtrl = startScreen.getKey();
        this.startScreenScene = new Scene(startScreen.getValue());

        this.createParticipantCtrl = createParticipant.getKey();
        this.createParticipantScene = new Scene(createParticipant.getValue());

        this.editParticipantCtrl = editParticipant.getKey();
        this.editParticipantScene = new Scene(editParticipant.getValue());

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpenseScene = new Scene(addExpense.getValue());

        this.editExpenseCtrl = editExpense.getKey();
        this.editExpenseScene = new Scene(editExpense.getValue());

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverviewScene = new Scene(eventOverview.getValue());

        this.dataHandler = new EventDataHandler();

        startWebSocket();

        showStartScreen();

        //showEventOverview(event);

        // showStartScreen() should be used in the final version.
        // Comment out showStartScreen() above and uncomment a scene below to
        // get it to launch as a start screen for debugging reasons.

        //showCreateParticipant(null);
        //showEditParticipant(null);

//        var event = new Event("My Event");
//        var person = new Participant(event, "boaz", "bakhuijzen", null, null, null);
//        var expense = new Expense(person, "My Expense", 12.1);
//        dataHandler = new EventDataHandler(event, null, null);
//        dataHandler.setExpenses(new ArrayList<>());
//        dataHandler.getCreateExpense(expense);
//        showEventOverview(event);

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
        } catch (org.json.JSONException e) {
            // Handle JSON parsing exception
            System.out.println("Failed to parse server response: " + e.getMessage());
        }
    }

    /**
     * Show create participant UI
     * @param event Event, which the participant will belong to
     */
    public void showCreateParticipant(Event event) {
        primaryStage.setTitle("Add participant ui");
        primaryStage.setScene(createParticipantScene);
        primaryStage.setResizable(false);
        createParticipantCtrl.setEvent(event);
    }

    /**
     * Show edit participant ui
     * @param participant Participant that will be edited
     */
    public void showEditParticipant(Participant participant) {
        primaryStage.setTitle("Edit participant ui");
        primaryStage.setScene(editParticipantScene);
        primaryStage.setResizable(false);
        editParticipantCtrl.setParticipant(participant);
    }

    /**
     * Show edit expense ui
     * @param expense expense that will be edited
     */
    public void showEditExpense(Expense expense) {
        primaryStage.setTitle("Edit expense ui");
        primaryStage.setScene(editExpenseScene);
        primaryStage.setResizable(false);
        editExpenseCtrl.setExpense(expense);
    }

    /**
     * Show edit expense ui
     */
    public void showAddExpense() {
        primaryStage.setTitle("Edit expense ui");
        primaryStage.setScene(addExpenseScene);
        primaryStage.setResizable(false);
        addExpenseCtrl.setFields();
    }

    /**
     * Show event overview
     * @param event Event, which will be shown
     */
    public void showEventOverview(Event event) {
        primaryStage.setTitle("Event overview");
        primaryStage.setScene(eventOverviewScene);
        primaryStage.setResizable(false);
        eventOverviewCtrl.setEvent(event);
    }

    /**
     * Start websocket connection
     */
    public void startWebSocket(){
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        sessionHandler = new WebsocketSessionHandler(dataHandler, this);
        stompClient.connectAsync("ws://localhost:8080/v1", sessionHandler);
    }

    /**
     * Standard getter for sessionHandler
     * @return current sessionHandler
     */
    public WebsocketSessionHandler getSessionHandler() {
        return sessionHandler;
    }

    /**
     * Standard setter for sessionHandler
     * @param sessionHandler new sessionHandler to use
     */
    public void setSessionHandler(WebsocketSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    /**
     * Standard getter for dataHandler
     * @return current dataHandler
     */
    public EventDataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Standard setter for dataHandler
     * @param dataHandler new dataHandler to use
     */
    public void setDataHandler(EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
}