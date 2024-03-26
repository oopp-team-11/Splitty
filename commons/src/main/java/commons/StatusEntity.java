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
     * Choose one of the last five parameters and pass null to the rest of those four
     * @param message String message for create/update/delete endpoints
     * @param event Event for event:read endpoint
     * @param eventList EventList for admin endpoints
     * @param participantList ParticipantList for participants:read endpoint
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity ok(String message, Event event,
                                  EventList eventList, ParticipantList participantList, ExpenseList expenseList)
    {
        return new StatusEntity(OK, false, message, event, eventList, participantList, expenseList);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code and unsolvable = false.
     * Choose one of the last five parameters and pass null to the rest of those four
     * @param message String message for create/update/delete endpoints
     * @param event Event for event:read endpoint
     * @param eventList EventList for admin endpoints
     * @param participantList ParticipantList for participants:read endpoint
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(String message, Event event,
                                  EventList eventList, ParticipantList participantList, ExpenseList expenseList)
    {
        return new StatusEntity(BAD_REQUEST, false, message, event, eventList, participantList, expenseList);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code.
     * Choose one of the last five parameters and pass null to the rest of those four
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param message String message for create/update/delete endpoints
     * @param event Event for event:read endpoint
     * @param eventList EventList for admin endpoints
     * @param participantList ParticipantList for participants:read endpoint
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity badRequest(boolean unsolvable, String message, Event event,
                                          EventList eventList, ParticipantList participantList, ExpenseList expenseList)
    {
        return new StatusEntity(BAD_REQUEST, unsolvable, message, event, eventList, participantList, expenseList);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code and unsolvable = false.
     * Choose one of the last five parameters and pass null to the rest of those four
     * @param message String message for create/update/delete endpoints
     * @param event Event for event:read endpoint
     * @param eventList EventList for admin endpoints
     * @param participantList ParticipantList for participants:read endpoint
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(String message, Event event,
                                          EventList eventList, ParticipantList participantList, ExpenseList expenseList)
    {
        return new StatusEntity(NOT_FOUND, false, message, event, eventList, participantList, expenseList);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code.
     * Choose one of the last five parameters and pass null to the rest of those four
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @param message String message for create/update/delete endpoints
     * @param event Event for event:read endpoint
     * @param eventList EventList for admin endpoints
     * @param participantList ParticipantList for participants:read endpoint
     * @param expenseList ExpenseList for expenses:read endpoint
     * @return Status entity holding status code, models, and boolean unsolvable
     */
    public static StatusEntity notFound(boolean unsolvable, String message, Event event,
                                          EventList eventList, ParticipantList participantList, ExpenseList expenseList)
    {
        return new StatusEntity(NOT_FOUND, unsolvable, message, event, eventList, participantList, expenseList);
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
