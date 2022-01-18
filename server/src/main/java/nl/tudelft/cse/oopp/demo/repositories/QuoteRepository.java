package nl.tudelft.cse.oopp.demo.repositories;

import nl.tudelft.cse.oopp.demo.entities.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, Long> {}
