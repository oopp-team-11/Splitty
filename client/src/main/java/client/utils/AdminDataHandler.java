package client.utils;

import commons.Event;
import javafx.application.Platform;

import java.io.File;
import java.util.List;

/**
 * Class that handles the messages from server and adjust data on client
 */
public class AdminDataHandler {
    private List<Event> events;
    private WebsocketSessionHandler sessionHandler;
    private String passcode;
    private File jsonDumpDir;

    /**
     * empty constructor
     */
    public AdminDataHandler() {
    }

    /**
     * default constructor
     * @param events List of all events
     * @param sessionHandler websocket session handler
     * @param passcode client's passcode
     * @param jsonDumpDir jsonDump directory
     */
    public AdminDataHandler(List<Event> events, WebsocketSessionHandler sessionHandler, String passcode,
                            File jsonDumpDir) {
        this.events = events;
        this.sessionHandler = sessionHandler;
        this.passcode = passcode;
        this.jsonDumpDir = jsonDumpDir;
    }

    /**
     * Getter for directory of jsonDump
     *
     * @return returns a File representing a directory for dumping json
     */
    public File getJsonDumpDir() {
        return jsonDumpDir;
    }

    /**
     * Setter for directory of jsonDump
     *
     * @param jsonDumpDir the chosen directory to save dump
     */
    public void setJsonDumpDir(File jsonDumpDir) {
        this.jsonDumpDir = jsonDumpDir;
    }

    /**
     * Getter for passcode
     * @return passcode
     */
    public String getPasscode() {
        return passcode;
    }

    /**
     * Setter for passcode
     * @param passcode passcode we want to set
     */
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    /**
     * std getter
     * @return list of events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * std setter
     * @param events
     */
    public void setEvents(List<Event> events) {
        boolean refresh = this.events != null;
        this.events = events;
        if (refresh)
            Platform.runLater(() -> sessionHandler.getMainCtrl().refreshAdminData());
        else {
            sessionHandler.subscribeToAdmin(passcode);
            Platform.runLater(() -> sessionHandler.getMainCtrl().showAdminPanel());
        }
    }

    /**
     * Sets admin local data to null
     */
    public void setDataToNull() {
        events = null;
        passcode = null;
        jsonDumpDir = null;
    }

    /**
     * std getter
     * @return websocket session handler
     */
    public WebsocketSessionHandler getSessionHandler() {
        return sessionHandler;
    }

    /**
     * std setter
     * @param sessionHandler
     */
    public void setSessionHandler(WebsocketSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    /**
     * checks if event is in the list of events on the client-side by the id
     * @param receivedEvent
     * @return
     */
    private boolean containsById(Event receivedEvent) {
        for (var event : events) {
            if (event.getId().equals(receivedEvent.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * handles the creation of the event
     * @param receivedEvent
     */
    public void getCreateEvent(Event receivedEvent) {
        if (containsById(receivedEvent)) {
            sessionHandler.sendReadEvents(passcode);
            return;
        }
        events.add(receivedEvent);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshAdminData());
    }

    /**
     * gets event by id from list of events on the client side
     * @param receivedEvent
     * @return
     */
    private Event getEventById(Event receivedEvent) {
        for (var event : events) {
            if (event.getId().equals(receivedEvent.getId())) {
                return event;
            }
        }
        return null;
    }


    private static void updateEvent(Event toUpdate, Event fromUpdate) {
        toUpdate.setTitle(fromUpdate.getTitle());
        toUpdate.setLastActivity(fromUpdate.getLastActivity());
        toUpdate.setCreationDate(fromUpdate.getCreationDate());
    }
    /**
     * handles the update of the event
     * @param receivedEvent
     */
    public void getUpdateEvent(Event receivedEvent) {
        Event localEvent = getEventById(receivedEvent);
        if (localEvent == null) {
            sessionHandler.sendReadEvents(passcode);
            return;
        }
        updateEvent(localEvent, receivedEvent);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshAdminData());
    }

    /**
     * handles the deletion of the event
     * @param receivedEvent
     */
    public void getDeleteEvent(Event receivedEvent) {
        Event localEvent = getEventById(receivedEvent);
        if (localEvent == null) {
            sessionHandler.sendReadEvents(passcode);
            return;
        }
        events.remove(localEvent);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshAdminData());
    }
}
