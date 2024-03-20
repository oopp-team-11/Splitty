package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static commons.StatusCode.*;

/**
 * Entity sent as an answer for WebSocket requests
 * that holds WebSocket status code, body (received object after processing by the server)
 * and boolean for whether the conflict is unsolvable (client's response may depend on that)
 * @param <T> Class of the object being sent through WebSocket
 */
public class StatusEntity<T> {
    private final T body;
    private final StatusCode statusCode;
    private final boolean unsolvable;

    private StatusEntity(T body, StatusCode statusCode, boolean unsolvable) {
        this.body = body;
        this.statusCode = statusCode;
        this.unsolvable = unsolvable;
    }

    /**
     * Getter for body
     * @return Body of status entity (object processed by the server)
     */
    public T getBody() {
        return body;
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
     * Static method that builds a status entity with the OK status code
     * @param body Object processed by the server
     * @return Status entity holding status code, body, and boolean unsolvable
     * @param <T> Class of the body
     */
    public static <T> StatusEntity<T> ok(T body)
    {
        return new StatusEntity<T>(body, OK, false);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code
     * @param body Object processed by the server
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @return Status entity holding status code, body, and boolean unsolvable
     * @param <T> Class of the body
     */
    public static <T> StatusEntity<T> badRequest(T body, boolean unsolvable)
    {
        return new StatusEntity<T>(body, BAD_REQUEST, unsolvable);
    }

    /**
     * Static method that builds a status entity with the BAD_REQUEST status code and unsolvable = false
     * @param body Object processed by the server
     * @return Status entity holding status code, body, and boolean unsolvable
     * @param <T> Class of the body
     */
    public static <T> StatusEntity<T> badRequest(T body)
    {
        return new StatusEntity<T>(body, BAD_REQUEST, false);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code
     * @param body Object processed by the server
     * @param unsolvable Boolean that indicates whether the conflict is unsolvable (info might be needed by the client)
     * @return Status entity holding status code, body, and boolean unsolvable
     * @param <T> Class of the body
     */
    public static <T> StatusEntity<T> notFound(T body, boolean unsolvable)
    {
        return new StatusEntity<T>(body, NOT_FOUND, unsolvable);
    }

    /**
     * Static method that builds a status entity with the NOT_FOUND status code and unsolvable = false
     * @param body Object processed by the server
     * @return Status entity holding status code, body, and boolean unsolvable
     * @param <T> Class of the body
     */
    public static <T> StatusEntity<T> notFound(T body)
    {
        return new StatusEntity<T>(body, NOT_FOUND, false);
    }

    /**
     * Equals method for StatusEntity
     * @param obj Object that we compare the StatusEntity to
     * @return True/false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof StatusEntity<?> that)) return false;

        return new EqualsBuilder().append(unsolvable, that.unsolvable)
                .append(body, that.body).append(statusCode, that.statusCode).isEquals();
    }

    /**
     * Returns hash code for the Status Entity
     * @return hashed Status Entity
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(body)
                .append(statusCode).append(unsolvable).toHashCode();
    }

    /**
     * toString method for the Status Entity
     * @return string representation of the StatusEntity
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("body", body)
                .append("statusCode", statusCode)
                .append("unsolvable", unsolvable)
                .toString();
    }
}
