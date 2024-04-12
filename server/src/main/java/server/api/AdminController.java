package server.api;
import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import server.PasswordService;
import server.database.EventRepository;

import server.database.ExpenseRepository;
import server.database.InvolvedRepository;
import server.database.ParticipantRepository;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles the CRUD operations under all /admin endpoints.
 */
@Transactional
@Controller
public class AdminController {
    private final EventRepository eventRepo;
    private final ParticipantRepository participantRepo;
    private final ExpenseRepository expenseRepo;
    private final InvolvedRepository involvedRepo;

    private final SimpMessagingTemplate template;

    private final PasswordService passwordService;

    /**
     * Constructor for the EventController.
     * Constructed automatically by Spring Boot.
     *
     * @param template                 SimpMessagingTemplate
     * @param eventRepo                The EventRepository provided automatically by JPA
     * @param participantRepo
     * @param expenseRepo
     * @param involvedRepository
     * @param passwordService          The PasswordService provided by the server
     */
    @Autowired
    public AdminController(SimpMessagingTemplate template, EventRepository eventRepo,
                           ParticipantRepository participantRepo, ExpenseRepository expenseRepo,
                           InvolvedRepository involvedRepository, PasswordService passwordService) {
        this.template = template;
        this.eventRepo = eventRepo;
        this.participantRepo = participantRepo;
        this.expenseRepo = expenseRepo;
        this.involvedRepo = involvedRepository;
        this.passwordService = passwordService;
    }

    /**
     * Handles read websocket endpoint for event
     *
     * @param receivedEvent requested event
     * @param password contains the admin password, sent as a header
     * @return returns a StatusEntity<Event> body contains Event if status code is OK
     * returns null in body otherwise
     */
    @MessageMapping("/admin/event:dump")
    @SendToUser(value = "/queue/admin/event:dump", broadcast = false)
    public StatusEntity dumpEvent(Event receivedEvent, @Header(name = "passcode") String password) {
        String adminPassword = passwordService.getAdminPassword();

        if (!adminPassword.equals(password)) {
            return StatusEntity.badRequest(true, "Incorrect Password!");
        }

        if(receivedEvent == null)
            return StatusEntity.badRequest(true, "Event is null.");
        if(!eventRepo.existsById(receivedEvent.getId()))
            return StatusEntity.notFound(true, "Event does not exist in the database.");

        Event event = eventRepo.getReferenceById(receivedEvent.getId());

        for (Participant participant : event.getParticipants()) {
            for (Expense expense : participant.getMadeExpenses()) {
                expense.getInvolveds();
                for (Involved involved : expense.getInvolveds())
                    involved.getParticipant();
            }
        }

        return StatusEntity.ok(event);
    }

    /**
     * Handles read websocket endpoint for event
     *
     * @param receivedEvent The event to import
     * @param password contains the admin password, sent as a header
     * @return returns a StatusEntity<String> body containing "Success" if the operation was successful.
     * returns an error message if something went wrong.
     */
    @MessageMapping("/admin/event:import")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity importEvent(Event receivedEvent, @Header(name = "passcode") String password) {
        String adminPassword = passwordService.getAdminPassword();

        if (!adminPassword.equals(password)) {
            return StatusEntity.badRequest(true, "Incorrect Password!");
        }

        if (receivedEvent == null) {
            return StatusEntity.badRequest(true, "Event object cannot be null.");
        }

        boolean eventExists = eventRepo.existsById(receivedEvent.getId());
        if (eventExists) {
            Event currentEvent = eventRepo.getReferenceById(receivedEvent.getId());
            participantRepo.deleteAll(currentEvent.getParticipants());
        }
        Event event = null;

        try {
            HashMap<UUID, Participant> savedParticipants = new HashMap<>();
            event = new Event(receivedEvent.getId(), receivedEvent.getTitle(), receivedEvent.getCreationDate(),
                    receivedEvent.getLastActivity());
            event = eventRepo.save(event);
            for (Participant receivedParticipant : receivedEvent.getParticipants()) {
                Participant participant = new Participant(event,
                        receivedParticipant.getFirstName(), receivedParticipant.getLastName(),
                        receivedParticipant.getIban(), receivedParticipant.getBic());
                participant = participantRepo.save(participant);
                savedParticipants.put(receivedParticipant.getId(), participant);
            }
            for (Participant receivedParticipant : receivedEvent.getParticipants()) {
                for (Expense receivedExpense : receivedParticipant.getMadeExpenses()) {
                    Participant participant = savedParticipants.get(receivedParticipant.getId());
                    Expense expense = new Expense(participant, receivedExpense.getTitle(),
                            receivedExpense.getAmount(), receivedExpense.getDate(), new InvolvedList());
                    expense = expenseRepo.save(expense);
                    for (Involved receivedInvolved : receivedExpense.getInvolveds()) {
                        Participant involvedParticipant = savedParticipants.get(receivedInvolved.getParticipant().getId());
                        Involved involved = new Involved(receivedInvolved.getIsSettled(), expense, involvedParticipant);
                        involvedRepo.save(involved);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            return StatusEntity.badRequest(true, "Request body contains null entity");
        }

        if (!eventExists)
            template.convertAndSend("/topic/admin/event:create", event);
        else
            template.convertAndSend("/topic/admin/event:update", event);
        return StatusEntity.ok("Event has been imported.");

    }
}
