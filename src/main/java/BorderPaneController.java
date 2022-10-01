import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.LinkedHashMap;

public class BorderPaneController {

    public Button putButton;
    public Button removeButton;
    public Button replaceButton;
    public Button defaultButton;
    public Button clearButton;

    public TextField putKey;
    public TextField putValue;
    public TextField removeKey;
    public TextField removeValue;
    public TextField replaceKey;
    public TextField replaceValue;

    private final LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

    public void handleMouseClickedPutButton(MouseEvent mouseEvent) {
        String key   = putKey.getText();
        String value = putValue.getText();

        if (key.equals("") || value.equals("")) {
            showErrorDialogIfFieldsAreEmpty();
        } else {
            hashMap.put(key, value);
        }
    }

    public void handleMouseClickedRemoveButton(MouseEvent mouseEvent) {
        String key   = removeKey.getText();
        String value = removeValue.getText();

        if (key.equals("") || value.equals("")) {
            showErrorDialogIfFieldsAreEmpty();
        } else {
            hashMap.put(key, value);
        }
    }

    public void handleMouseClickedReplaceButton(MouseEvent mouseEvent) {
        String key   = replaceKey.getText();
        String value = replaceValue.getText();

        if (key.equals("") || value.equals("")) {
            showErrorDialogIfFieldsAreEmpty();
        } else {
            hashMap.put(key, value);
        }
    }

    public void handleMouseClickedClearButton(MouseEvent mouseEvent) {
        hashMap.clear();
    }

    public void handleMouseClickedDefaultButton(MouseEvent mouseEvent) {
        hashMap.put("Ilon", "Mask");
        hashMap.put("Coca", "Cola");
    }

    public void showErrorDialogIfFieldsAreEmpty() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error :<");
        alert.setHeaderText("Ooops, there was an error!!");
        alert.setContentText("Field 'Key' or 'Value' is empty!");
        alert.showAndWait();
    }

    public void debugPrintItems() {
        for (String item: hashMap.keySet()) {
            System.out.println(hashMap.get(item));
        }
    }
}
