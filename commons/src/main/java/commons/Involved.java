package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Involved {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
