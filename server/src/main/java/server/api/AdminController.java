package server.api;
import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;
import server.EventLastActivityService;
import server.PasswordService;
import server.database.EventRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import server.database.ExpenseRepository;
import server.database.InvolvedRepository;
import server.database.ParticipantRepository;

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

    private final Map<UUID, List<DeferredResult<ResponseEntity<Map<UUID, String>>>>> deferredResults;

    private final SimpMessagingTemplate template;

    private final PasswordService passwordService;

    private final EventLastActivityService eventLastActivityService;

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
     * @param eventLastActivityService The EventLastActivityService provided by the server
     */
    @Autowired
    public AdminController(SimpMessagingTemplate template, EventRepository eventRepo,
                           ParticipantRepository participantRepo, ExpenseRepository expenseRepo,
                           InvolvedRepository involvedRepository, PasswordService passwordService,
                           EventLastActivityService eventLastActivityService) {
        this.template = template;
        this.eventRepo = eventRepo;
        this.participantRepo = participantRepo;
        this.expenseRepo = expenseRepo;
        this.involvedRepo = involvedRepository;
        this.passwordService = passwordService;
        this.deferredResults = new ConcurrentHashMap<>();
        this.eventLastActivityService = eventLastActivityService;
    }

    /**
     * Handles read websocket endpoint for event
     *
     * @param invitationCode invitationCode of the requested event
     * @param password contains the admin password, sent as a header
     * @return returns a StatusEntity<Event> body contains Event if status code is OK
     * returns null in body otherwise
     */
    @MessageMapping("/admin/event:dump")
    @SendToUser(value = "/queue/admin/event:dump", broadcast = false)
    public StatusEntity dumpEvent(UUID invitationCode, @Header(name = "passcode") String password) {
        String adminPassword = passwordService.getAdminPassword();

        if (!adminPassword.equals(password)) {
            return StatusEntity.badRequest(true, "Incorrect Password!");
        }

        if(invitationCode == null)
            return StatusEntity.badRequest(true, (Event) null);
        if(!eventRepo.existsById(invitationCode))
            return StatusEntity.notFound(true, (Event) null);

        Event event = eventRepo.getReferenceById(invitationCode);
        for (Participant participant : event.getParticipants()) {
            for (Expense expense : participant.getMadeExpenses()) {
                for (Involved involved : expense.getInvolveds()) {
                    // create new involved with expense and participant equal to null to avoid cyclic reference
                    var pId = involved.getParticipantId();
                    var eId = involved.getExpenseId();
                    var iId = involved.getId();
                    var isSettled = involved.getIsSettled();
                    var invitationCodeId = involved.getInvitationCode();
                    involved = new Involved(iId, isSettled, eId, pId, invitationCodeId);
                }
            }
        }

        // for debugging
//        ObjectMapper ob = new ObjectMapper().findAndRegisterModules();
//        String eventString = null;
//        try {
//            eventString = ob.writeValueAsString(event);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(eventString);


        return StatusEntity.ok(event);
    }

    /**
     * Handles read websocket endpoint for event
     *
     * @param event The event to import
     * @param password contains the admin password, sent as a header
     * @return returns a StatusEntity<String> body containing "Success" if the operation was successful.
     * returns an error message if something went wrong.
     */
    @MessageMapping("/admin/event:import")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity importEvent(Event event, @Header(name = "passcode") String password) {
        String adminPassword = passwordService.getAdminPassword();

        if (!adminPassword.equals(password)) {
            return StatusEntity.badRequest(true, "Incorrect Password!");
        }

        if (event == null || eventRepo.existsById(event.getId())) {
            return StatusEntity.badRequest(true, "Event already exists");
        }

        try {
            eventRepo.save(event);
            participantRepo.saveAll(event.getParticipants());
            for (Participant participant : event.getParticipants()) {
                for (Expense expense : participant.getMadeExpenses()) {
                    expenseRepo.save(expense);
                    for (Involved involved : expense.getInvolveds()) {
                        var involvedParticipant = participantRepo.getReferenceById(involved.getParticipantId());
                        involved.setParticipant(involvedParticipant);
                        involved.setExpense(expense);
                        involvedRepo.save(involved);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            return StatusEntity.badRequest(true, "Request body contains null entity");
        }

        return StatusEntity.ok("Success");
    }
}
