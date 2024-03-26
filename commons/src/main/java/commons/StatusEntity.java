package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static commons.StatusEntity.StatusCode.*;

/**
 * Entity sent as an answer for WebSocket requests
 * that holds WebSocket status code, body (received object after processing by the server)
 * and boolean for whether the conflict is unsolvable (client's response may depend on that)
 */
public class StatusEntity {

    /**
     * Status codes for WebSocket requests
     */
    public enum StatusCode {
        OK,
        BAD_REQUEST,
        NOT_FOUND
    }

    private StatusCode statusCode;
    private boolean unsolvable;
    private String message;
    private Event event;
    private EventList eventList;
    private ParticipantList participantList;
    private ExpenseList expenseList;

    private StatusEntity(StatusCode statusCode, boolean unsolvable, String message, Event event,
                         EventList eventList, ParticipantList participantList, ExpenseList expenseList) {
        this.statusCode = statusCode;
        this.unsolvable = unsolvable;
        this.message = message;
        this.event = event;
        this.eventList = eventList;
        this.participantList = participantList;
        this.expenseList = expenseList;
    }

    /**
     * Empty constructor for deserialization
     */
    public StatusEntity() {
    }

    /**
     * Getter for status code
     * @return Status code
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Getter for unsolvable
     * @return Boolean unsolvable
     */
    public boolean isUnsolvable() {
        return unsolvable;
    }

    /**
     * Static method that builds a status entity with the OK status code.
     * @param message String message for create/update/delete endpoints
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity ok(String message)
    {
        return new StatusEntity(OK, false, message, null, null, null, null);
    }

    /**
     * Static method that builds a status entity with the OK status code.
     * @param event Event for event:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity ok(Event event)
    {
        return new StatusEntity(OK, false, null, event, null, null, null);
    }

    /**
     * Static method that builds a status entity with the OK status code.
     * @param eventList EventList for admin endpoints
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity ok(EventList eventList)
    {
        return new StatusEntity(OK, false, null, null, eventList, null, null);
    }

    /**
     * Static method that builds a status entity with the OK status code.
     * @param participantList ParticipantList for participants:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity ok(ParticipantList participantList)
    {
        return new StatusEntity(OK, false, null, null, null, participantList, null);
    }

    /**
     * Static method that builds a status entity with the OK status code.
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity ok(ExpenseList expenseList)
    {
        return new StatusEntity(OK, false, null, null, null, null, expenseList);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param message String message for create/update/delete endpoints
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(boolean unsolvable, String message)
    {
        return new StatusEntity(BAD_REQUEST, unsolvable, message, null, null, null, null);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param event Event for event:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(boolean unsolvable, Event event)
    {
        return new StatusEntity(BAD_REQUEST, unsolvable, null, event, null, null, null);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param eventList EventList for admin endpoints
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(boolean unsolvable, EventList eventList)
    {
        return new StatusEntity(BAD_REQUEST, unsolvable, null, null, eventList, null, null);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param participantList ParticipantList for participants:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(boolean unsolvable, ParticipantList participantList)
    {
        return new StatusEntity(BAD_REQUEST, unsolvable, null, null, null, participantList, null);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(boolean unsolvable, ExpenseList expenseList)
    {
        return new StatusEntity(BAD_REQUEST, unsolvable, null, null, null, null, expenseList);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param message String message for create/update/delete endpoints
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(boolean unsolvable, String message)
    {
        return new StatusEntity(NOT_FOUND, unsolvable, message, null, null, null, null);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param event Event for event:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(boolean unsolvable, Event event)
    {
        return new StatusEntity(NOT_FOUND, unsolvable, null, event, null, null, null);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param eventList EventList for admin endpoints
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(boolean unsolvable, EventList eventList)
    {
        return new StatusEntity(NOT_FOUND, unsolvable, null, null, eventList, null, null);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param participantList ParticipantList for participants:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(boolean unsolvable, ParticipantList participantList)
    {
        return new StatusEntity(NOT_FOUND, unsolvable, null, null, null, participantList, null);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code.
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(boolean unsolvable, ExpenseList expenseList)
    {
        return new StatusEntity(NOT_FOUND, unsolvable, null, null, null, null, expenseList);
    }

    /**
     * Getter for message
     * @return Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for event
     * @return Event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Getter for eventList
     * @return eventList
     */
    public EventList getEventList() {
        return eventList;
    }

    /**
     * Getter for participantList
     * @return participantList
     */
    public ParticipantList getParticipantList() {
        return participantList;
    }

    /**
     * Getter for expenseList
     * @return expenseList
     */
    public ExpenseList getExpenseList() {
        return expenseList;
    }

    /**
     * Equals method for StatusEntity
     * @param obj Object that we compare the StatusEntity to
     * @return true/false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof StatusEntity that)) return false;

        return new EqualsBuilder().append(unsolvable, that.unsolvable).append(statusCode, that.statusCode)
                .append(message, that.message).append(event, that.event).append(eventList, that.eventList)
                .append(participantList, that.participantList).append(expenseList, that.expenseList).isEquals();
    }

    /**
     * Returns hash code for the Status Entity
     * @return hashed Status Entity
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(statusCode).append(unsolvable)
                .append(message).append(event).append(eventList).append(participantList)
                .append(expenseList).toHashCode();
    }

    /**
     * toString method for the Status Entity
     * @return string representation of the StatusEntity
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("statusCode", statusCode)
                .append("unsolvable", unsolvable)
                .append("message", message)
                .append("event", event)
                .append("eventList", eventList)
                .append("participantList", participantList)
                .append("expenseList", expenseList)
                .toString();
    }
}
