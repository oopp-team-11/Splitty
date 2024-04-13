package server.api;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import server.EventLastActivityService;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.InvolvedRepository;
import server.database.ParticipantRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that represents the participant controller
 */
@Transactional
@Controller
public class ExpenseController {
    private final EventRepository eventRepository;
    private final ExpenseRepository expenseRepository;
    private final ParticipantRepository participantRepository;
    private final InvolvedRepository involvedRepository;
    private final SimpMessagingTemplate template;

    private final EventLastActivityService eventLastActivityService;

    /**
     * Constructor
     * @param eventRepository event repository
     * @param expenseRepository expense repository
     * @param participantRepository participant repository
     * @param involvedRepository involved repository
     * @param template SimpMessagingTemplate
     * @param eventLastActivityService EventLastActivityService
     */
    @Autowired
    public ExpenseController(EventRepository eventRepository,
                             ExpenseRepository expenseRepository,
                             ParticipantRepository participantRepository,
                             InvolvedRepository involvedRepository,
                             SimpMessagingTemplate template,
                             EventLastActivityService eventLastActivityService) {
        this.eventRepository = eventRepository;
        this.expenseRepository = expenseRepository;
        this.participantRepository = participantRepository;
        this.involvedRepository = involvedRepository;
        this.template = template;
        this.eventLastActivityService = eventLastActivityService;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Evaluates whether the received Expense object has all the correct field values
     *
     * @param receivedExpense received Expense object
     * @return Returns a statusEntity with an error message, if it is a bad request
     * Returns an OK status with null body otherwise
     */
    public StatusEntity isExpenseBadRequest(Expense receivedExpense) {
        if(receivedExpense == null)
            return StatusEntity.badRequest(true, "Expense object not found in message body");
        if (isNullOrEmpty(receivedExpense.getTitle()))
            return StatusEntity.badRequest(true, "Expense title should not be empty");
        if (receivedExpense.getAmount() <= 0)
            return StatusEntity.badRequest(true, "Amount should be positive");
        if (receivedExpense.getPaidById() == null)
            return StatusEntity.badRequest(true, "Id of participant who paid should be provided");
        if (!participantRepository.existsById(receivedExpense.getPaidById()))
            return StatusEntity.notFound(true, "Provided participant who paid for the expense does not exist");
        if (receivedExpense.getInvitationCode() == null)
            return StatusEntity.badRequest(true, "InvitationCode of event should be provided");
        if (receivedExpense.getDate() == null)
            return StatusEntity.badRequest(true, "Date should be provided");
        return isInvolvedBadRequest(receivedExpense);
    }

    private StatusEntity isInvolvedBadRequest(Expense receivedExpense) {
        if (receivedExpense.getInvolveds() == null || receivedExpense.getInvolveds().isEmpty())
            return StatusEntity.badRequest(true, "Expense should involve a participant");
        Set<UUID> participantSet = new HashSet<>(
                receivedExpense.getInvolveds().stream().map(Involved::getParticipantId).toList()
        );
        if(participantSet.size() != receivedExpense.getInvolveds().size())
            return StatusEntity.badRequest(true, "Expense cannot involve duplicates of participants");
        var participantsFound = participantSet.stream().filter(participantRepository::existsById).toList();
        if(participantsFound.size() != participantSet.size())
            return StatusEntity.notFound(true, "Involved participant not found");
        return StatusEntity.ok((String) null);
    }

    /**
     * Evaluates whether the received existing Expense object has the correct field values
     *
     * @param receivedExpense received Expense object
     * @return Returns a statusEntity with an error message, if it is a bad request
     * Returns an OK status with null body otherwise
     */
    public StatusEntity isExistingExpenseBadRequest(Expense receivedExpense) {
        if(receivedExpense.getId() == null)
            return StatusEntity.badRequest(true, "Expense ID should be provided");
        if(!expenseRepository.existsById(receivedExpense.getId()))
            return StatusEntity.notFound(true, "Expense with provided ID does not exist");
        return isInvolvedBadRequest(receivedExpense);
    }

    /**
     * Handles the /topic/expense:delete endpoint messages
     *
     * @param receivedExpense The received expense object
     * @return returns a StatusEntity with an error message
     */
    @MessageMapping("/expense:create")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity createExpense(Expense receivedExpense)
    {
        StatusEntity badRequest = isExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;

        Participant paidBy = participantRepository.getReferenceById(receivedExpense.getPaidById());

        Expense expense = new Expense(paidBy, receivedExpense.getTitle(), receivedExpense.getAmount(),
                receivedExpense.getDate(), receivedExpense.getInvolveds());

        //hibernate and jackson behave funky when used together
        for (Involved involved : receivedExpense.getInvolveds()) {
            involved.setExpense(expense);
        }

        eventLastActivityService.updateLastActivity(receivedExpense.getInvitationCode());

        expense = expenseRepository.save(expense);

        List<Involved> involveds = new InvolvedList();
        for(Involved involved : expense.getInvolveds())
        {
            Involved thisInvolved = new Involved(involved.getId(), involved.getIsSettled(),
                    expense.getId(), involved.getParticipant().getId(), receivedExpense.getInvitationCode());
            involveds.add(thisInvolved);
        }
        Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(), paidBy.getId(),
                receivedExpense.getInvitationCode(), expense.getDate(), involveds);
        sentExpense.setAmountOwed(expense.getAmount() / sentExpense.getInvolveds().size());

        template.convertAndSend("/topic/" + sentExpense.getInvitationCode() + "/expense:create",
                sentExpense);

        return StatusEntity.ok("Expense was successfully created");
    }

    /**
     * Handles read expenses websocket endpoint
     *
     * @param invitationCode invitationCode of parent Event
     * @return StatusEntity containing a list of all Event's expenses in body
     */
    @MessageMapping("/expenses:read")
    @SendToUser(value = "/queue/expenses:read", broadcast = false)
    public StatusEntity readExpenses(UUID invitationCode)
    {
        if(invitationCode == null)
            return StatusEntity.badRequest(true, (ExpenseList) null);
        if(!eventRepository.existsById(invitationCode))
            return StatusEntity.notFound(true, (ExpenseList) null);

        Event event = eventRepository.getReferenceById(invitationCode);
        List<Participant> participants = event.getParticipants();
        ExpenseList expenses = new ExpenseList();

        for (Participant participant : participants) {
            List<Expense> participantExpenses = participant.getMadeExpenses();
            for (Expense expense : participantExpenses) {
                List<Involved> involveds = new InvolvedList();
                for(Involved involved : expense.getInvolveds())
                {
                    Involved thisInvolved = new Involved(involved.getId(), involved.getIsSettled(),
                            expense.getId(), involved.getParticipant().getId(), invitationCode);
                    involveds.add(thisInvolved);
                }
                Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount()
                        , participant.getId(), invitationCode, expense.getDate(), involveds);
                sentExpense.setAmountOwed(sentExpense.getAmount()/sentExpense.getInvolveds().size());

                expenses.add(sentExpense);
            }
        }

        return StatusEntity.ok(expenses);
    }

    /**
     * Handles the /topic/expense:update endpoint messages
     *
     * @param receivedExpense The received expense object
     * @return returns a StatusEntity with an error message
     */
    @MessageMapping("/expense:update")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity updateExpense(Expense receivedExpense)
    {
        StatusEntity badRequest = isExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;
        badRequest = isExistingExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;

        Expense expense = expenseRepository.getReferenceById(receivedExpense.getId());

        Involved testInvolved = expense.getInvolveds().getFirst();

        var oldAmountOwed = expense.getAmount() / expense.getInvolveds().size();
        var newAmountOwed = receivedExpense.getAmount() / receivedExpense.getInvolveds().size();

        HashSet<UUID> participantIds = expense.getInvolveds().stream()
                .map(Involved::getParticipant)
                .map(Participant::getId)
                .collect(Collectors.toCollection(HashSet::new));

        HashSet<UUID> newParticipantIds = new HashSet<>();
        for (Involved involved : receivedExpense.getInvolveds()) {
            newParticipantIds.add(involved.getParticipantId());
            if (!participantIds.contains(involved.getParticipantId())) {
                involved.setExpense(expense);
                expense.getInvolveds().add(involved);
            }
        }
        expense.getInvolveds().removeIf(involved -> !newParticipantIds.contains(involved.getParticipant().getId()));

        if(newAmountOwed != oldAmountOwed)
        {
            expense.setAmountOwed(newAmountOwed);
            for(Involved involved : expense.getInvolveds())
                involved.setIsSettled(false);
        }

        Participant newPaidBy = participantRepository.getReferenceById(receivedExpense.getPaidById());
        expense.setPaidBy(newPaidBy);
        expense.setAmount(receivedExpense.getAmount());
        expense.setTitle(receivedExpense.getTitle());

        expense = expenseRepository.save(expense);

        eventLastActivityService.updateLastActivity(receivedExpense.getInvitationCode());

        List<Involved> involveds = new InvolvedList();
        for(Involved involved : expense.getInvolveds())
        {
            Involved thisInvolved = new Involved(involved.getId(), involved.getIsSettled(),
                    expense.getId(), involved.getParticipant().getId(), receivedExpense.getInvitationCode());
            involveds.add(thisInvolved);
        }

        Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(),
                newPaidBy.getId(), receivedExpense.getInvitationCode(),
                expense.getDate(), involveds);
        sentExpense.setAmountOwed(newAmountOwed);

        template.convertAndSend("/topic/" + sentExpense.getInvitationCode() + "/expense:update", sentExpense);
        return StatusEntity.ok("Expense was successfully updated");
    }

    /**
     * Handles the /topic/expense:delete endpoint messages
     *
     * @param receivedExpense The received expense object
     * @return returns a StatusEntity with an error message
     */
    @MessageMapping("/expense:delete")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity deleteExpense(Expense receivedExpense)
    {
        if (receivedExpense == null)
            return StatusEntity.badRequest(false, "Expense object not found in message body");

        StatusEntity badRequest = isExistingExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;


        Expense expense = expenseRepository.getReferenceById(receivedExpense.getId());
        eventLastActivityService.updateLastActivity(receivedExpense.getInvitationCode());
        expenseRepository.delete(expense);

        template.convertAndSend("/topic/" + receivedExpense.getInvitationCode() + "/expense:delete",
                receivedExpense);
        return StatusEntity.ok("Expense was successfully deleted");
    }
}
