package client.scenes;

import client.interfaces.Translatable;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
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

    public void goToEventOverview() {

    }
}
