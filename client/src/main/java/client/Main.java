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
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;
import client.scenes.StartScreenCtrl;
import client.scenes.MainCtrl;

/**
 * The Main class for the client.
 * Launches the application, loads the scenes from fxml files and pairs them with the corresponding controllers,
 * and calls the MainCtrl.initialize() method
 */
public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * The client main function. Launches the app, calls start(Stage)
     * @param args CLI arguments
     * @throws URISyntaxException If parsing a URI fails
     * @throws IOException If I/O fails
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
        var threads = Thread.getAllStackTraces().keySet();
        var pollingThread = threads.stream()
                .filter(thread -> thread.getName().equals("Polling thread"))
                .toList();
        if (!pollingThread.isEmpty()) {
            pollingThread.forEach(Thread::interrupt);
        }
    }

    /**
     * Javafx function called by launch() that uses dependency injection to load scenes and main controller.
     * Calls initialize(Stage, scene1, scene2, ...)
     * @param primaryStage primary stage
     * @throws IOException if I/O fails
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        var startScreen = FXML.load(StartScreenCtrl.class, "client", "scenes", "StartScreen.fxml");
        var createParticipant = FXML.load(CreateParticipantCtrl.class, "client", "scenes", "CreateParticipant.fxml");
        var editParticipant = FXML.load(EditParticipantCtrl.class, "client", "scenes", "EditParticipant.fxml");
        var eventOverview = FXML.load(EventOverviewCtrl.class, "client", "scenes", "EventOverview.fxml");
        var editExpense = FXML.load(EditExpenseCtrl.class, "client", "scenes", "EditExpense.fxml");
        var addExpense = FXML.load(AddExpenseCtrl.class, "client", "scenes", "AddExpense.fxml");
        var adminPanel = FXML.load(AdminPanelCtrl.class, "client", "scenes", "AdminPanel.fxml");
        var detailedExpense = FXML.load(DetailedExpenseCtrl.class, "client", "scenes", "DetailedExpense.fxml");
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, startScreen,
                createParticipant, editParticipant,
                eventOverview,
                editExpense, addExpense, adminPanel, detailedExpense);
    }
}