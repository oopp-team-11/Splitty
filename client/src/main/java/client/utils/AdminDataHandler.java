package client.utils;

import commons.Event;
import javafx.application.Platform;

import java.util.List;

/**
 * Class that handles the messages from server and adjust data on client
 */
public class AdminDataHandler {
    private List<Event> events;
    private WebsocketSessionHandler sessionHandler;
    private String passcode;

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
     */
    public AdminDataHandler(List<Event> events, WebsocketSessionHandler sessionHandler, String passcode) {
        this.events = events;
        this.sessionHandler = sessionHandler;
        this.passcode = passcode;
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
            // TODO: logic of refetching
            // sessionHandler.refreshAllEvents();
            // TODO: logic of pop-up
            return;
        }
        events.add(receivedEvent);
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
        if (!containsById(receivedEvent)) {
            // TODO: logic of refetching
            // sessionHandler.refreshAllEvents();
            // TODO: logic of pop-up
            return;
        }
        updateEvent(getEventById(receivedEvent), receivedEvent);
    }

    /**
     * handles the deletion of the event
     * @param receivedEvent
     */
    public void getDeleteEvent(Event receivedEvent) {
        if (!containsById(receivedEvent)) {
            // TODO: logic of refetching
            // sessionHandler.refreshAllEvents();
            // TODO: logic of pop-up
            return;
        }
        events.remove(getEventById(receivedEvent));
    }
}
