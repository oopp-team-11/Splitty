package client.scenes;

import client.interfaces.Translatable;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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

    @Inject
    public DetailedExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void translate(TranslationSupplier translationSupplier) {

    }
}
