package client.scenes;

import client.interfaces.Translatable;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Expense;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class DetailedExpenseCtrl implements Translatable {

    @FXML
    private Label title;

    @FXML
    private Label amount;

    @FXML
    private Label date;

    @FXML
    private Label amountOwed;

    @FXML
    private Label paidBy;

    @FXML
    private Label bankingDetails;

    @FXML
    private ListView<String> involvedList;

    @FXML
    private Button abortBtn;

    @FXML
    private Button editBtn;

    private MainCtrl mainCtrl;

    private Expense expense;

    /**
     * std getter
     * @return expense
     */
    public Expense getExpense() {
        return expense;
    }

    /**
     * sets up the scene
     * @param expense
     */
    public void setExpense(Expense expense) {
        this.expense = expense;

    }

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public DetailedExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.title, "Title");
        labels.put(this.amount, "Amount");
        labels.put(this.date, "Date");
        labels.put(this.amountOwed, "Share per person");
        labels.put(this.paidBy, "Paid by");
        labels.put(this.bankingDetails, "Banking Details");
        labels.put(this.involvedList, "List of Involved");
        labels.put(this.abortBtn, "Cancel");
        labels.put(this.editBtn, "Edit");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            ((Labeled) key).setText(translation.replaceAll("\"", ""));
        });
    }

    /**
     * Method for going back to the event overview
     */
    public void goToEventOverview() {
        mainCtrl.showEventOverview();
    }
}
