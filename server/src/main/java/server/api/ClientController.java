package server.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Client;
import server.database.ClientRepository;
@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientRepository repo;

    public ClientController(ClientRepository repo) {
        this.repo = repo;
    }
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    @PutMapping (path = {"", "/"})
    public ResponseEntity<Client> add(@RequestBody Client client) {
        if (isNullOrEmpty(client.getFirstName()) ||
                isNullOrEmpty(client.getLastName()) ||
                isNullOrEmpty(client.getBic()) ||
                isNullOrEmpty(client.getEmail()) ||
                isNullOrEmpty(client.getInvitationCode()) ||
                isNullOrEmpty(client.getIban())) {
            return ResponseEntity.badRequest().build();
        }
        Client saved = repo.save(client);
        return ResponseEntity.ok(saved);
    }
}
