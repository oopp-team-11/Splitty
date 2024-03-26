package client.utils;

import commons.Event;

import java.util.List;

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

    public void getCreateEvent(Event receivedEvent) {

    }

    public void getUpdateEvent(Event receivedEvent) {

    }

    public void getDeleteEvent(Event receivedEvent) {

    }
}
