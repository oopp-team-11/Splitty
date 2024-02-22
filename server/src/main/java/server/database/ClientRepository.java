package server.database;


import org.springframework.data.jpa.repository.JpaRepository;
import commons.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {}

