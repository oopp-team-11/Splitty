package client.utils;

import commons.Event;

import java.util.List;
import java.util.UUID;

/**
 * Class that handles the messages from server and adjust data on client
 */
public class AdminDataHandler {
    private List<Event> events;
    private WebsocketSessionHandler sessionHandler;

    /**
     * empty constructor
     */
    public AdminDataHandler() {
    }

    /**
     * default constructor
     * @param events
     * @param sessionHandler
     */
    public AdminDataHandler(List<Event> events, WebsocketSessionHandler sessionHandler) {
        this.events = events;
        this.sessionHandler = sessionHandler;
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
        this.events = events;
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
     * @param event
     * @return
     */
    private boolean containsById(Event event) {
        for (var e : events) {
            if (e.getId().equals(event.getId())) {
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
     * @param e
     * @return
     */
    private Event getEventById(Event e) {
        for (var event : events) {
            if (event.getId().equals(e.getId())) {
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
