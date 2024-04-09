package server.api;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import server.EventLastActivityService;
import server.database.InvolvedRepository;


/**
 * Class that represents the involved controller
 */
@Transactional
@Controller
public class InvolvedController {
    private final InvolvedRepository involvedRepository;
    private final SimpMessagingTemplate template;

    private final EventLastActivityService eventLastActivityService;

    /**
     * Constructor for the InvolvedController
     * @param involvedRepository involved repository
     * @param template SimpMessagingTemplate
     * @param eventLastActivityService EventLastActivityService
     */
    @Autowired
    public InvolvedController(InvolvedRepository involvedRepository,
                              SimpMessagingTemplate template,
                              EventLastActivityService eventLastActivityService) {
        this.involvedRepository = involvedRepository;
        this.template = template;
        this.eventLastActivityService = eventLastActivityService;
    }

    /**
     * Handles update websocket endpoint for involved entity
     * @param receivedInvolved Involved object received from client
     * @return StatusEntity with the result of the operation
     */
    @MessageMapping("/involved:update")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity updateInvolved(Involved receivedInvolved) {

        if(receivedInvolved == null) {
            return StatusEntity.badRequest(true, "Involved object not found in request body");
        }

        if(!involvedRepository.existsById(receivedInvolved.getId())) {
            return StatusEntity.notFound(true, "Involved object not found in database");
        }

        Involved involved = involvedRepository.getReferenceById(receivedInvolved.getId());
        involved.setIsSettled(receivedInvolved.getIsSettled());

        involved = involvedRepository.save(involved);

        eventLastActivityService.updateLastActivity(receivedInvolved.getParticipant().getEventId());

        Involved sentInvolved = new Involved(involved.getId(),
                involved.getIsSettled(), involved.getExpense().getId(),
                involved.getParticipant().getId(), involved.getInvitationCode());


        template.convertAndSend("/topic/" + sentInvolved.getId() + "/involved:update", sentInvolved);
        return StatusEntity.ok("involved:update " + sentInvolved.getId());
    }
}