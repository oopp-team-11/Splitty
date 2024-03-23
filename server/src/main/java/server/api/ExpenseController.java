package server.api;

import commons.Expense;
import commons.StatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import server.database.ExpenseRepository;

import java.security.Principal;
import java.util.UUID;

/**
 * Class that represents the participant controller
 */
@Controller
public class ExpenseController {
    private final ExpenseRepository expenseRepository;
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor
     * @param expenseRepository expense repository
     * @param template SimpMessagingTemplate
     */
    public ExpenseController(ExpenseRepository expenseRepository,
                             SimpMessagingTemplate template) {
        this.expenseRepository = expenseRepository;
        this.template = template;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Handles create websocket endpoint for expense
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/expense:create")
    public void createExpense(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Expense.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Payload should be an expense", true));
            return;
        }

        Expense receivedExpense = (Expense) payload;

        if (isNullOrEmpty(receivedExpense.getTitle())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Title should not be empty"));
            return;
        }
        if (receivedExpense.getPaidBy() == null) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Did not receive who made the expense"));
            return;
        }
        if (receivedExpense.getAmount() <= 0) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Amount should be positive"));
            return;
        }

        expenseRepository.save(receivedExpense);

        template.convertAndSend("/topic/"+receivedExpense.getId(), receivedExpense);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("expense:create " + receivedExpense.getId()));
    }

    /**
     * Handles read websocket endpoint for expense
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/expense:read")
    public void readExpense(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != UUID.class) {
            template.convertAndSendToUser(principal.getName(), "/queue/reply",
                    StatusEntity.badRequest("Payload should be a UUID", true));
            return;
        }

        UUID invitationCode = (UUID) payload;

        if(!expenseRepository.existsById(invitationCode))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Participant not found", true));
            return;
        }

        Expense expense = expenseRepository.getReferenceById(invitationCode);

        template.convertAndSend("/topic/"+invitationCode, expense);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("expense:read " + expense.getId()));
    }

    /**
     * Handles update websocket endpoint for participant
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/expense:update")
    public void updateExpense(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Expense.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Payload should be an expense", true));
            return;
        }

        Expense receivedExpense = (Expense) payload;

        if (isNullOrEmpty(receivedExpense.getTitle())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Title should not be empty"));
            return;
        }
        if (receivedExpense.getPaidBy() == null) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Did not receive who made the expense"));
            return;
        }
        if (receivedExpense.getAmount() <= 0) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Amount should be positive"));
            return;
        }

        if(!expenseRepository.existsById(receivedExpense.getId()))
        {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.notFound("Participant not found", true));
            return;
        }

        Expense expense = expenseRepository.getReferenceById(receivedExpense.getId());
        expense.setAmount(receivedExpense.getAmount());
        expense.setTitle(receivedExpense.getTitle());
        expense.setPaidById(receivedExpense.getPaidById());
        expenseRepository.save(expense);

        template.convertAndSend("/topic/"+receivedExpense.getId(), expense);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("expense:update " + expense.getId()));
    }

    /**
     * Handles delete websocket endpoint for expense
     * @param principal connection data about user
     * @param payload content of a websocket message
     */
    @MessageMapping("/expense:delete")
    public void deleteExpense(Principal principal, @Payload Object payload)
    {
        if(payload.getClass() != Expense.class) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Payload should be an expense", true));
            return;
        }

        Expense receivedExpense = (Expense) payload;

        if (isNullOrEmpty(receivedExpense.getTitle())) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Title should not be empty"));
            return;
        }
        if (receivedExpense.getPaidBy() == null) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Did not receive who made the expense"));
            return;
        }
        if (receivedExpense.getAmount() <= 0) {
            template.convertAndSendToUser(principal.getName(),"/queue/reply",
                    StatusEntity.badRequest("Amount should be positive"));
            return;
        }

        Expense expense = expenseRepository.getReferenceById(receivedExpense.getId());
        expenseRepository.delete(expense);

        template.convertAndSend("/topic/"+receivedExpense.getId(), expense);
        template.convertAndSendToUser(principal.getName(), "/queue/reply",
                StatusEntity.ok("expense:delete " + expense.getId()));
    }
}
