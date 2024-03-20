package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static commons.StatusCode.*;

public class StatusEntity<T> {
    private T body;
    private StatusCode statusCode;
    private boolean unsolvable;

    private StatusEntity(T body, StatusCode statusCode, boolean unsolvable) {
        this.body = body;
        this.statusCode = statusCode;
        this.unsolvable = unsolvable;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public boolean isUnsolvable() {
        return unsolvable;
    }

    public StatusEntity<T> ok(T body)
    {
        return new StatusEntity<T>(body, OK, false);
    }

    public StatusEntity<T> badRequest(T body, boolean unsolvable)
    {
        return new StatusEntity<T>(body, BAD_REQUEST, unsolvable);
    }

    public StatusEntity<T> badRequest(T body)
    {
        return new StatusEntity<T>(body, BAD_REQUEST, false);
    }

    public StatusEntity<T> notFound(T body, boolean unsolvable)
    {
        return new StatusEntity<T>(body, NOT_FOUND, unsolvable);
    }

    public StatusEntity<T> notFound(T body)
    {
        return new StatusEntity<T>(body, NOT_FOUND, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StatusEntity<?> that)) return false;

        return new EqualsBuilder().append(unsolvable, that.unsolvable)
                .append(body, that.body).append(statusCode, that.statusCode).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(body)
                .append(statusCode).append(unsolvable).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("body", body)
                .append("statusCode", statusCode)
                .append("unsolvable", unsolvable)
                .toString();
    }
}
