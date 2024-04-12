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

import java.util.List;


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
     * @param receivedInvolveds Involved object received from client
     * @return StatusEntity with the result of the operation
     */
    @MessageMapping("/involved:update")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity updateInvolved(List<Involved> receivedInvolveds) {

        if(receivedInvolveds == null) {
            return StatusEntity.badRequest(true, "List of involved is null");
        }

        if (receivedInvolveds.isEmpty()) {
            return StatusEntity.badRequest(true, "List of involved is empty");
        }

        for (Involved inv : receivedInvolveds) {
            if (inv == null) {
                return StatusEntity.badRequest(true, "Involved object is null in the request body");
            }
            if (!involvedRepository.existsById(inv.getId())) {
                return StatusEntity.notFound(true, "One of the involved object not found in database");
            }
        }
        InvolvedList toSend = new InvolvedList();
        for (var inv : receivedInvolveds) {
            Involved involved = involvedRepository.getReferenceById(inv.getId());
            involved.setIsSettled(inv.getIsSettled());
            involved = involvedRepository.save(involved);
            eventLastActivityService.updateLastActivity(inv.getInvitationCode());
            Involved sentInvolved = new Involved(involved.getId(),
                    involved.getIsSettled(), involved.getExpense().getId(),
                    involved.getParticipant().getId(), inv.getInvitationCode());
            toSend.add(sentInvolved);
        }
        template.convertAndSend("/topic/" + toSend.getFirst().getInvitationCode()+ "/involved:update", toSend);




        return StatusEntity.ok("Successfully updated settling of debts");
    }
}