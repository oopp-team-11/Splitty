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
}
