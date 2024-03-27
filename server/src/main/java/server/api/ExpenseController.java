package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class that represents the participant controller
 */
@Transactional
@Controller
public class ExpenseController {
    private final EventRepository eventRepository;
    private final ExpenseRepository expenseRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate template;

    /**
     * Constructor
     * @param eventRepository event repository
     * @param expenseRepository expense repository
     * @param participantRepository participant repository
     * @param template SimpMessagingTemplate
     */
    public ExpenseController(EventRepository eventRepository,
                             ExpenseRepository expenseRepository,
                             ParticipantRepository participantRepository,
                             SimpMessagingTemplate template) {
        this.eventRepository = eventRepository;
        this.expenseRepository = expenseRepository;
        this.participantRepository = participantRepository;
        this.template = template;
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
    public StatusEntity<String> isExpenseBadRequest(Expense receivedExpense) {
        if(receivedExpense == null)
            return StatusEntity.badRequest("Expense object not found in message body", true);
        if (isNullOrEmpty(receivedExpense.getTitle()))
            return StatusEntity.badRequest("Expense title should not be empty", true);
        if (receivedExpense.getAmount() <= 0)
            return StatusEntity.badRequest("Amount should be positive", true);
        if (receivedExpense.getPaidById() == null)
            return StatusEntity.badRequest("Id of participant who paid should be provided", true);
        if (receivedExpense.getInvitationCode() == null)
            return StatusEntity.badRequest("InvitationCode of event should be provided", true);
        return StatusEntity.ok(null);
    }

    /**
     * Evaluates whether the received existing Expense object has the correct field values
     *
     * @param receivedExpense received Expense object
     * @return Returns a statusEntity with an error message, if it is a bad request
     * Returns an OK status with null body otherwise
     */
    public StatusEntity<String> isExistingExpenseBadRequest(Expense receivedExpense) {
        if(receivedExpense.getId() == null)
            return StatusEntity.badRequest("Expense ID should be provided", true);
        if(!expenseRepository.existsById(receivedExpense.getId()))
            return StatusEntity.notFound("Expense with provided ID does not exist", true);
        return StatusEntity.ok(null);
    }

    /**
     * Handles the /topic/expense:delete endpoint messages
     *
     * @param receivedExpense The received expense object
     * @return returns a StatusEntity with an error message
     */
    @MessageMapping("/expense:create")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity<String> createExpense(Expense receivedExpense)
    {
        StatusEntity<String> badRequest = isExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;
        if (!participantRepository.existsById(receivedExpense.getPaidById()))
            return StatusEntity.notFound("Provided participant who paid for the expense does not exist");

        Participant paidBy = participantRepository.getReferenceById(receivedExpense.getPaidById());
        Expense expense = new Expense(paidBy, receivedExpense.getTitle(), receivedExpense.getAmount());
        paidBy.addExpense(expense);

        expense = expenseRepository.save(expense);
        paidBy = participantRepository.save(paidBy);

        Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(), paidBy.getId(),
                receivedExpense.getInvitationCode());
        template.convertAndSend("/topic/" + sentExpense.getInvitationCode() + "/expense:create",
                sentExpense);
        return StatusEntity.ok("expense:create " + sentExpense.getId());
    }

    /**
     * Handles read expenses websocket endpoint
     *
     * @param invitationCode invitationCode of parent Event
     * @return StatusEntity containing a list of all Event's expenses in body
     */
    @MessageMapping("/expenses:read")
    @SendToUser(value = "/queue/expenses:read", broadcast = false)
    public StatusEntity<List<Expense>> readExpenses(UUID invitationCode)
    {
        if(invitationCode == null)
            return StatusEntity.badRequest(null, true);
        if(!eventRepository.existsById(invitationCode))
            return StatusEntity.notFound(null, true);

        Event event = eventRepository.getReferenceById(invitationCode);
        List<Participant> participants = event.getParticipants();
        List<Expense> expenses = new ArrayList<>();

        for (Participant participant : participants) {
            List<Expense> participantExpenses = participant.getMadeExpenses();
            for (Expense expense : participantExpenses) {
                Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount()
                        , participant.getId(), invitationCode);
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
    public StatusEntity<String> updateExpense(Expense receivedExpense)
    {
        StatusEntity<String> badRequest = isExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;
        badRequest = isExistingExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;

        Expense expense = expenseRepository.getReferenceById(receivedExpense.getId());
        expense.setAmount(receivedExpense.getAmount());
        expense.setTitle(receivedExpense.getTitle());
        expense = expenseRepository.save(expense);

        Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(),
                receivedExpense.getPaidById(), receivedExpense.getInvitationCode());
        template.convertAndSend("/topic/" + sentExpense.getInvitationCode() + "/expense:update", sentExpense);
        return StatusEntity.ok("expense:update " + sentExpense.getId());
    }

    /**
     * Handles the /topic/expense:delete endpoint messages
     *
     * @param receivedExpense The received expense object
     * @return returns a StatusEntity with an error message
     */
    @MessageMapping("/expense:delete")
    @SendToUser(value = "/queue/reply", broadcast = false)
    public StatusEntity<String> deleteExpense(Expense receivedExpense)
    {
        if (receivedExpense == null)
            return StatusEntity.badRequest("Expense object not found in message body");

        StatusEntity<String> badRequest = isExistingExpenseBadRequest(receivedExpense);
        if (badRequest.isUnsolvable())
            return badRequest;

        Expense expense = expenseRepository.getReferenceById(receivedExpense.getId());
        expenseRepository.delete(expense);

        Expense sentExpense = new Expense(expense.getId(), expense.getTitle(), expense.getAmount(),
                receivedExpense.getPaidById(), receivedExpense.getInvitationCode());
        template.convertAndSend("/topic/" + sentExpense.getInvitationCode() + "/expense:delete",
                sentExpense);
        return StatusEntity.ok("expense:delete " + sentExpense.getId());
    }
}
