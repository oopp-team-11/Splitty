package server.database;


import commons.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import commons.Client;

public interface InitRepository extends JpaRepository<Client, Long> {}

