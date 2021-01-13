package nl.tudelft.oopp.demo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quote {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "text")
    private String quote;

    @Column(name = "author")
    private String author;

    public Quote() {
    }

    /**
     * Create a new Quote instance.
     *
     * @param id Unique identifier as to be used in the database.
     * @param quote Actual text of the quote.
     * @param author Name of the author of the quote.
     */
    public Quote(long id, String quote, String author) {
        this.id = id;
        this.quote = quote;
        this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Quote quote = (Quote) o;

        return id == quote.id;
    }
}
