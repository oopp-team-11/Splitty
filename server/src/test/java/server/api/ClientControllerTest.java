package server.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Client;
public class ClientControllerTest {
    private TestClientRepository repo;
    private ClientController sut;

    @BeforeEach
    public void setup() {
        repo = new TestClientRepository();
        sut = new ClientController(repo);
    }
}
