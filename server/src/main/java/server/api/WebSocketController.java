package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class WebSocketController {

    private final EventRepository repo;
    private final ParticipantRepository participantRepository;

    public WebSocketController(EventRepository repo, ParticipantRepository participantRepository) {
        this.repo = repo;
        this.participantRepository = participantRepository;
    }

    /**
     * Checks whether a provided String is null or empty.
     * @param string The String to be checked.
     * @return Returns a boolean.
     */
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Handles the messages from server
     *
     * @param headers Message headers
     * @param payload Updated object
     */
    @MessageMapping("/event")
    public void incomingMessage(StompHeaders headers, Object payload){
        String modelType = headers.getFirst("model");
        String methodType = headers.getFirst("method");
        switch (modelType) {
            case "Event" -> {
                Event receivedEvent = (Event) payload;
                ResponseEntity<Event> response = receiveEvent(methodType, receivedEvent);
            }
            case "Participant" -> {
                Participant receivedParticipant = (Participant) payload;
                ResponseEntity<Participant> response = receiveParticipant(methodType, receivedParticipant);
            }
            case "Expense" -> {
                Expense receivedExpense = (Expense) payload;
                ResponseEntity<Expense> response = receiveExpense(methodType, receivedExpense);
            }
            case null, default -> System.out.println("Model type not specified in the message headers");
        }
    }

    public ResponseEntity<Event> receiveEvent(String methodType, Event receivedEvent){
        switch(methodType){
            case "create" -> {
                if (isNullOrEmpty(receivedEvent.getTitle())) {
                    return ResponseEntity.badRequest().build();
                }
                Event event = new Event(receivedEvent.getTitle());
                event.setCreationDate(LocalDateTime.now());
                event.setLastActivity(event.getCreationDate());
                repo.save(event);
                return ResponseEntity.ok(event);
            }
            case "update" -> {
                if (receivedEvent.getId() == null) {
                    return ResponseEntity.badRequest().build();
                }
                Optional<Event> event = repo.findById(receivedEvent.getId());
                return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
            }
            case "delete" -> {
                if (receivedEvent.getId() == null) {
                    return ResponseEntity.badRequest().build();
                }
                if (!repo.existsById(receivedEvent.getId())) {
                    return ResponseEntity.notFound().build();
                }
                Event event = repo.getReferenceById(receivedEvent.getId());
                repo.delete(event);
                return ResponseEntity.ok(event);
            }
            case null, default -> {return ResponseEntity.badRequest().build();}
        }
    }

    public ResponseEntity<Participant> receiveParticipant(String methodType, Participant receivedParticipant){
        switch(methodType){
            case "create" -> {
                if (!repo.existsById(receivedParticipant.getEvent().getId())) {
                    return ResponseEntity.notFound().build();
                }
                if (isNullOrEmpty(receivedParticipant.getFirstName())
                        || isNullOrEmpty(receivedParticipant.getLastName())) {
                    return ResponseEntity.badRequest().build();
                }
                Event event = repo.getReferenceById(receivedParticipant.getEvent().getId());
                Participant participant = new Participant(event,
                        receivedParticipant.getFirstName(),
                        receivedParticipant.getLastName(),
                        receivedParticipant.getEmail(),
                        receivedParticipant.getIban(),
                        receivedParticipant.getBic()
                );
                repo.save(event); //saves participant reference in event model
                participantRepository.save(participant);
                return ResponseEntity.created(URI.create("/participants/" + participant.getId())).body(participant);
            }
            case "update" -> {
                if (!participantRepository.existsById(receivedParticipant.getId())) {
                    return ResponseEntity.notFound().build();
                }
                if (isNullOrEmpty(receivedParticipant.getFirstName())
                        || isNullOrEmpty(receivedParticipant.getLastName())) {
                    return ResponseEntity.badRequest().build();
                }
                Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
                participant.setFirstName(receivedParticipant.getFirstName());
                participant.setLastName(receivedParticipant.getLastName());
                participant.setEmail(receivedParticipant.getEmail());
                participant.setIban(receivedParticipant.getIban());
                participant.setBic(receivedParticipant.getBic());
                participantRepository.save(participant);
                return ResponseEntity.ok(participant);
            }
            case "delete" -> {
                if (!participantRepository.existsById(receivedParticipant.getId())) {
                    return ResponseEntity.notFound().build();
                }
                Participant participant = participantRepository.getReferenceById(receivedParticipant.getId());
                //automatically removes participant from Event and its expenses due to CascadeType.REMOVE
                participantRepository.delete(participant);
                return ResponseEntity.ok(participant);
            }
            case null, default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    //TODO: Expense controller / Expense repository without it we cannot do anything
    public ResponseEntity<Expense> receiveExpense(String methodType, Expense receivedExpense){
        switch(methodType){
            case "create" -> {
                return ResponseEntity.status(501).build();
            }
            case "update" -> {
                return ResponseEntity.status(501).build();
            }
            case "delete" -> {
                return ResponseEntity.status(501).build();
            }
            case null, default -> {return ResponseEntity.status(501).build();}
        }
    }
}
