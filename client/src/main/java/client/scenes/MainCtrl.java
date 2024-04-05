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

import client.utils.AdminDataHandler;
import client.utils.EventDataHandler;
import client.utils.FileSystemUtils;
import client.utils.TranslationSupplier;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private String serverIp;
    private AdminPanelCtrl adminPanelCtrl;
    private Scene adminPanelScene;
    private AdminDataHandler adminDataHandler;

    private TranslationSupplier translationSupplier;
    private LinkedHashMap<String, Locale> availableLanguages;

    /**
     * Initializes javafx scenes and their controllers, sets start screen as the currently shown screen
     * @param primaryStage stage
     * @param startScreen a pair of start screen controller and javafx start screen scene
     * @param createParticipant a pair of create participant controller and javafx create participant scene
     * @param editParticipant a pair of edit participant controller and javafx edit participant scene
     * @param eventOverview a pair of event overview controller and javafx event overview scene
     * @param editExpense a pair of edit expense controller and javafx edit expense scene
     * @param addExpense a pair of add expense controller and javafx add expense scene
     * @param adminPanel a pair of admin panel controller and javafx admin panel scene
     */

    public void initialize(Stage primaryStage, Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<CreateParticipantCtrl, Parent> createParticipant,
                           Pair<EditParticipantCtrl, Parent> editParticipant,
                           Pair<EventOverviewCtrl, Parent> eventOverview,
                           Pair<EditExpenseCtrl, Parent> editExpense,
                           Pair<AddExpenseCtrl, Parent> addExpense,
                           Pair<AdminPanelCtrl, Parent> adminPanel) {
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

        this.adminPanelCtrl = adminPanel.getKey();
        this.adminPanelScene = new Scene(adminPanel.getValue());

        this.adminDataHandler = new AdminDataHandler();

        setAvailableLanguagesFromFiles();

        // Needs to be before the start websocket method
        setServerIp();

        setTranslationSupplier();

        startWebSocket();

        showStartScreen();

        primaryStage.show();
    }

    private void setAvailableLanguagesFromFiles() {
        File locales = new File("locales");
        List<String> listOfLocalesNames = new ArrayList<>(Arrays.stream(Objects.requireNonNull(locales.listFiles()))
                .map(File::getName).map(string -> string.replace(".json", "")).toList());
        listOfLocalesNames.add("en");

        HashMap<String, Locale> localeHashMap = new HashMap<>();
        listOfLocalesNames.forEach(localeName ->
                localeHashMap.put(localeName, new Locale.Builder().setLanguage(localeName).build()));

        this.availableLanguages = localeHashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(Locale::getDisplayLanguage)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * std getter
     * @return admin data handler
     */
    public AdminDataHandler getAdminDataHandler() {
        return adminDataHandler;
    }

    /**
     * std setter
     * @param adminDataHandler
     */
    public void setAdminDataHandler(AdminDataHandler adminDataHandler) {
        this.adminDataHandler = adminDataHandler;
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
        startScreenCtrl.translate(this.translationSupplier);
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
        createParticipantCtrl.translate(this.translationSupplier);
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
        editParticipantCtrl.translate(this.translationSupplier);
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
        editExpenseCtrl.translate(this.translationSupplier);
    }

    /**
     * Show edit expense ui
     */
    public void showAddExpense() {
        primaryStage.setTitle("Edit expense ui");
        primaryStage.setScene(addExpenseScene);
        primaryStage.setResizable(false);
        addExpenseCtrl.setFields();
        addExpenseCtrl.translate(this.translationSupplier);
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
        eventOverviewCtrl.translate(this.translationSupplier);
    }

    /**
     * shows admin panel
     */
    public void showAdminPanel() {
        primaryStage.setTitle("Admin Panel");
        primaryStage.setScene(adminPanelScene);
        primaryStage.setResizable(false);
        adminPanelCtrl.makeSetUp();
    }

    /**
     * Start websocket connection
     */
    public void startWebSocket(){
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        MappingJackson2MessageConverter jackson2MessageConverter = new MappingJackson2MessageConverter();
        jackson2MessageConverter.getObjectMapper().findAndRegisterModules();
        stompClient.setMessageConverter(jackson2MessageConverter);

        sessionHandler = new WebsocketSessionHandler(dataHandler, adminDataHandler, this);
        stompClient.connectAsync("ws://" + this.serverIp + "/v1", sessionHandler);
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

    private void setServerIp(){
        FileSystemUtils utils = new FileSystemUtils();
        try {
            this.serverIp = utils.getServerIP("client-config.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Did not find client config file." + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for setting the correct language in all scenes
     */
    public void setTranslationSupplier(){
        FileSystemUtils utils = new FileSystemUtils();
        try {
            this.translationSupplier = utils.getTranslationSupplier("client-config.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Did not find client config file." + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Standard Getter for server ip
     * @return String of serverIp
     */
    public String getServerIp(){
        return this.serverIp;
    }

    /**
     * Method for updating data in scenes
     */
    public void refreshEventData() {
        if(primaryStage.getTitle().equals("Event overview")){
            eventOverviewCtrl.refreshEventData();
        }
    }

    /**
     * Method for updating data in scenes
     */
    public void refreshParticipantsData() {
        if(primaryStage.getTitle().equals("Event overview")){
            eventOverviewCtrl.refreshParticipantsData();
        }
    }

    /**
     * Method for updating data in scenes
     */
    public void refreshExpensesData() {
        if(primaryStage.getTitle().equals("Event overview")){
            eventOverviewCtrl.refreshExpensesData();
        }
    }

    /**
     * Getter for available languages
     * @return available languages in a linked hashmap
     */
    public LinkedHashMap<String, Locale> getAvailableLanguages() {
        return availableLanguages;
    }

    /**
     * Getter for the translation Supplier
     * @return current translationSupplier
     */
    public TranslationSupplier getTranslationSupplier() {
        return translationSupplier;
    }
}