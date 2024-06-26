package client.scenes;

import client.interfaces.Translatable;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Expense;
import commons.Involved;
import commons.Participant;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

/**
 * detailed expense controller
 */
public class DetailedExpenseCtrl implements Translatable {

    // Labels that corresponds to the data
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
    private Label iban;

    @FXML
    private Label bic;

    @FXML
    private TableView<Involved> involvedTableView;

    @FXML
    private TableColumn<Involved, String> participantNameColumn;

    @FXML
    private TableColumn<Involved, Boolean> isSettledColumn;


    // Labels that corresponds to the "namings" of the data

    @FXML
    private Label partialDebtNaming;

    @FXML
    private Label titleNaming;

    @FXML
    private Label amountNaming;

    @FXML
    private Label dateNaming;

    @FXML
    private Label amountOwedNaming;

    @FXML
    private Label paidByNaming;

    @FXML
    private Label ibanNaming;

    @FXML
    private Label bicNaming;

    @FXML
    private Label involvedListNaming;

    // the rest of the scene functionality

    @FXML
    private Button abortBtn;

    @FXML
    private Button editBtn;

    private MainCtrl mainCtrl;

    private Expense expense;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public DetailedExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * std getter
     * @return expense
     */
    public Expense getExpense() {
        return expense;
    }

    /**
     * returns first and last name of participant in the string format
     * @param participant
     * @return string
     */
    public static String participantToString(Participant participant) {
        return participant.getFirstName() + " " + participant.getLastName();
    }

    /**
     * sets up the scene with provided expense or refreshes data on the scene
     * @param expense
     */
    public void setUpOrRefreshData(Expense expense) {
        this.expense = expense;
        title.setText(expense.getTitle());
        amount.setText(Double.toString(expense.getAmount()));
        date.setText(expense.getDate().toString());
        amountOwed.setText(Double.toString(expense.getAmountOwed()));
        paidBy.setText(participantToString(expense.getPaidBy()));
        iban.setText(expense.getPaidBy().getIban());
        bic.setText(expense.getPaidBy().getBic());
        involvedTableView.getItems().clear();

        // if last involved is deleted
        if (expense.getInvolveds() == null || expense.getInvolveds().isEmpty()) {
            goToEventOverview();
        }


        participantNameColumn.setCellValueFactory(obj
                -> new SimpleStringProperty(participantToString(obj.getValue().getParticipant())));
        isSettledColumn.setCellValueFactory(obj -> new SimpleBooleanProperty(obj.getValue().getIsSettled()));
        isSettledColumn.setCellFactory(obj -> new CheckBoxCell());
        involvedTableView.getColumns().getFirst().setVisible(false);
        involvedTableView.getColumns().getFirst().setVisible(true);
        involvedTableView.getItems().setAll(expense.getInvolveds());
    }

    /**
     * onAction of edit button
     */
    public void editBtn() {
        mainCtrl.getSessionHandler().sendInvolveds(expense.getInvolveds(), "update");
        goToEventOverview();
    }


    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.titleNaming, "Title");
        labels.put(this.amountNaming, "Amount");
        labels.put(this.dateNaming, "Date");
        labels.put(this.amountOwedNaming, "Share per person");
        labels.put(this.paidByNaming, "WhoPaidLabel");
        labels.put(this.ibanNaming, "IBAN");
        labels.put(this.bicNaming, "BIC");
        labels.put(this.involvedListNaming, "List of Involved");
        labels.put(this.abortBtn, "Go To Event Overview");
        labels.put(this.editBtn, "Save Partial Debts");
        labels.put(this.partialDebtNaming, "Partial Debt");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled) {
                ((Labeled) key).setText(translation);
            }
            if (key instanceof Button) {
                ((Button) key).setText(translation);
            }
        });

        Map<TableColumn, String> tableColumns = new HashMap<>();
        tableColumns.put(this.participantNameColumn, "Participant");
        tableColumns.put(this.isSettledColumn, "IsSettled");
        tableColumns.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            key.setText(translation);
        });
    }

    /**
     * Method for going back to the event overview
     */
    public void goToEventOverview() {
        mainCtrl.showEventOverview();
    }

    private class CheckBoxCell extends TableCell<Involved, Boolean> {
        final CheckBox checkBox;

        private CheckBoxCell() {
            this.checkBox = new CheckBox();
            this.checkBox.setOnAction(actionEvent -> {
                int row = getTableRow().getIndex();
                Involved inv = involvedTableView.getItems().get(row);
                inv.setIsSettled(checkBox.isSelected());
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                checkBox.setSelected(item);
                setGraphic(checkBox);
            }
        }
    }
}
