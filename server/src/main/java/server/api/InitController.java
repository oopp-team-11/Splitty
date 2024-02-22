package server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import commons.Client;
import server.database.InitRepository;
@RestController
@RequestMapping("/init_client")
public class InitController {
    private final InitRepository repo;

    public InitController(InitRepository repo) {
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
