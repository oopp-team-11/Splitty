package server.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import commons.Client;
import server.database.ClientRepository;
@RestController
@RequestMapping("/init_client")
public class ClientController {
    private final ClientRepository repo;

    public ClientController(ClientRepository repo) {
        this.repo = repo;
    }
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    @PostMapping("/init_client")
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
