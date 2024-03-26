package server.api;

import commons.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.UUID;

/**
 * Class that represents the participant controller
 */
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
    public StatusEntity isExpenseBadRequest(Expense receivedExpense) {
        if(receivedExpense == null)
            return StatusEntity.badRequest(true, "Expense object not found in message body");
        if (isNullOrEmpty(receivedExpense.getTitle()))
            return StatusEntity.badRequest(true, "Expense title should not be empty");
        if (receivedExpense.getAmount() <= 0)
            return StatusEntity.badRequest(true, "Amount should be positive");
        if (receivedExpense.getPaidById() == null)
            return StatusEntity.badRequest(true, "Id of participant who paid should be provided");
        if (receivedExpense.getInvitationCode() == null)
            return StatusEntity.badRequest(true, "InvitationCode of event should be provided");
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
        return StatusEntity.ok((String) null);
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
        if (!participantRepository.existsById(receivedExpense.getPaidById()))
            return StatusEntity.notFound(false, "Provided participant who paid for the expense does not exist");

        Participant paidBy = participantRepository.getReferenceById(receivedExpense.getPaidById());
        Expense expense = new Expense(paidBy, receivedExpense.getTitle(), receivedExpense.getAmount());
        paidBy.addExpense(expense);

        UUID invitationCode = receivedExpense.getInvitationCode();
        receivedExpense = expenseRepository.save(receivedExpense);
        participantRepository.save(paidBy);

        receivedExpense.setPaidById(paidBy.getId());
        receivedExpense.setInvitationCode(invitationCode);

        template.convertAndSend("/topic/" + invitationCode + "/expense:create",
                receivedExpense);
        return StatusEntity.ok("expense:create " + receivedExpense.getId());
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
                expense.setInvitationCode(invitationCode);
                expense.setPaidById(participant.getId());
                expenses.add(expense);
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
        expense.setAmount(receivedExpense.getAmount());
        expense.setTitle(receivedExpense.getTitle());
        expense = expenseRepository.save(expense);

        expense.setInvitationCode(receivedExpense.getInvitationCode());
        expense.setPaidById(expense.getPaidBy().getId());

        template.convertAndSend("/topic/" + expense.getInvitationCode() + "/expense:update", expense);
        return StatusEntity.ok("expense:update " + expense.getId());
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
        expenseRepository.delete(expense);

        template.convertAndSend("/topic/" + receivedExpense.getInvitationCode() + "/expense:delete",
                receivedExpense);
        return StatusEntity.ok("expense:delete " + expense.getId());
    }
}
