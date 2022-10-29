import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Клас, що представляє собою контроллер. Тобто такий клас, логіку якого
 * використовує елемент заданий у .fxml файлі.
 */
public class BorderPaneController {

    /*
    Ці змінні зберігають у собі елементи, які є дочірніми для елементу,
    контроллером якого є цей клас. Це дозволить нам взаємодіяти з ними.
    Наприклад, читати текст з TextBox, тощо.
     */

    public Button putButton;
    public Button removeButton;
    public Button replaceButton;
    public Button clearButton;
    public Button saveButton;
    public Button loadButton;

    public TextField putKey;
    public TextField putValue;
    public TextField removeKey;
    public TextField replaceKey;
    public TextField replaceValue;

    public CheckBox accessOrderCheck;

    public Pane viewPane;

    // Змінна, що збергіє відображення пари ключ-значення (далі: нода) з якої йде взаємодія.
    private FXHashMapNode pickedNode;
    // Змінна, що зберігає попередню позицію мишки.
    private Vec2 mousePreviousPos;
    // Змінна, що зберігає значення global scale.
    private Vec2 globalScale;
    // Хешмапа, яка зберігає пари ключ-значення.
    private final LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
    // Список, що зберігає всі ноди.
    private final LinkedList<FXHashMapNode> mapList = new LinkedList<>();

    /**
     * Клас, що дозволяє описувати координати, двомірний вектор, тощо.
     */
    public static class Vec2 {
        public double x;
        public double y;
    }

    /**
     * Метод ініціалізації деяких значення для додатку.
     */
    public void initialize() {
        globalScale = new Vec2();
        globalScale.x = 1;
        globalScale.y = 1;
        mousePreviousPos = new Vec2();
        viewPane.toBack();
    }

    /**
     * Метод, що обробляє подію натискання на кнопку "Put".
     * Додає пару ключ-значення у хешмапу.
     * Обробляє можливі помилки, якщо поле "key" або/та "value" пусті.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void handleMouseClickedPutButton(MouseEvent mouseEvent) {

        final double OFFSET = 30.f;

        String key   = putKey.getText();
        String value = putValue.getText();

        if (key.equals("") || value.equals("")) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Error :<",
                    "Ooops, there was an error!!",
                    "Field 'Key' or 'Value' is empty!"
            );
            return;
        }

        if (hashMap.get(key) == null) {
            FXHashMapNode newNode = new FXHashMapNode();
            newNode.getKey().setText(key);
            newNode.getValue().setText(value);
            newNode.getGrid().setScaleX(globalScale.x);
            newNode.getGrid().setScaleY(globalScale.y);

            if (viewPane.getChildren().size() > 0) {
                FXHashMapNode prev = mapList.getLast();
                newNode.setLayoutX(prev.getLayoutX() + (prev.getGridWidth() + OFFSET) * globalScale.x);
                newNode.setLayoutY(prev.getLayoutY());
                bindNodes(prev, newNode);
            } else {
                newNode.setLayoutX(100.0);
                newNode.setLayoutY(200.0);
            }

            mapList.add(newNode);
            viewPane.getChildren().add(newNode);
        } else {
            for (FXHashMapNode node: mapList) {
                if (node.getKey().getText().equals(key)) {
                    if(accessOrderCheck.isSelected()){
                        moveNodeToMapListBack(node);
                    }
                    node.getValue().setText(value);
                    break;
                }
            }
        }

        hashMap.put(key, value);

        putKey.clear();
        putValue.clear();
    }

    /**
     * Метод, що обробляє подію натискання кнопки "Remove".
     * Видаляє пару ключ-значення з хешмапи.
     * Обробляє помилик, якщо поля пусті, або такого ключа немає у хешмапі.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void handleMouseClickedRemoveButton(MouseEvent mouseEvent) {

        String key = removeKey.getText();

        removeKey.clear();

        if (key.equals("")) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Error :<",
                    "Ooops, there was an error!!",
                    "Field 'Key' is empty!"
            );
            return;
        }

        if (hashMap.remove(key) == null) {
            showDialog(
                    Alert.AlertType.INFORMATION,
                    "Info",
                    "Ooops, there was a non-existent key!!",
                    "You cannot remove a non-existent key."
            );
            return;
        }

        for (FXHashMapNode node: mapList) {
            if (node.getKey().getText().equals(key)) {
                removeNodeFromMapList(node);
                viewPane.getChildren().remove(node);
                break;
            }
        }
    }

    /**
     * Метод, що обробляє подію натискання на кнопку "Replace".
     * Замінює значення з ключом "key" (якщо воно є) на "value".
     * Обробляє помилки, якщо поля пусті, а також якщо пара з ключом
     * "key" не існує.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void handleMouseClickedReplaceButton(MouseEvent mouseEvent) {

        String key   = replaceKey.getText();
        String value = replaceValue.getText();

        replaceKey.clear();
        replaceValue.clear();

        if (key.equals("") || value.equals("")) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Error :<",
                    "Ooops, there was an error!!",
                    "Field 'Key' or 'Value' is empty!"
            );
            return;
        }

        if (hashMap.get(key) == null) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Error :<",
                    "Ooops, there was an error!!",
                    "You cannot replace a non-existent key."
            );
            return;
        }

        hashMap.replace(key, value);

        for (FXHashMapNode node: mapList) {
            if (node.getKey().getText().equals(key)) {
                if (accessOrderCheck.isSelected()) {
                    moveNodeToMapListBack(node);
                }
                node.getValue().setText(value);
                break;
            }
        }
    }

    /**
     * Метод, що обробляє подію натискання на кнопку "Clear".
     * Видаляє всі елементи хешмапи та ноди відображення.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void handleMouseClickedClearButton(MouseEvent mouseEvent) {
        mapList.clear();
        hashMap.clear();
        viewPane.getChildren().clear();
    }

    /**
     * Метод, який обробляє подію натискання на кнопку "Save".
     * Зберігає хешмапу до файла saved.txt, якщо хешмапа не пуста.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void handleMouseClickedSaveButton(MouseEvent mouseEvent) {

        if (hashMap.isEmpty()) {
            showDialog(
                    Alert.AlertType.INFORMATION,
                    "Info",
                    "HashMap is empty!",
                    "Cannot save empty HashMap!"
            );
            return;
        }

        try {
            FileWriter saveFile = new FileWriter("saved.txt");

            for (String key: hashMap.keySet()) {
                saveFile.write(key + " " + hashMap.get(key) + "\n");
            }

            saveFile.close();

            showDialog(
                    Alert.AlertType.INFORMATION,
                    "Info",
                    "File saved :>",
                    "saved.txt created!"
            );

        } catch (IOException e) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Error :<",
                    "Ooops, there was an error!!",
                    "Cannot create saved.txt!"
            );
        }
    }

    /**
     * Метод, що обробляє подію натискання на кнопку "Load".
     * Завантажує хешмапу з файла saved.txt, якщо він існує.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void handleMouseClickedLoadButton(MouseEvent mouseEvent) {

        handleMouseClickedClearButton(mouseEvent);

        final double OFFSET = 30.f;

        hashMap.clear();

        try {
            Scanner saveFile = new Scanner(new FileReader("saved.txt"));

            while (saveFile.hasNext()) {
                String key   = saveFile.next();
                String value = saveFile.next();
                hashMap.put(key, value);

                FXHashMapNode newNode = new FXHashMapNode();
                newNode.getKey().setText(key);
                newNode.getValue().setText(value);
                newNode.getGrid().setScaleX(globalScale.x);
                newNode.getGrid().setScaleY(globalScale.y);

                if (viewPane.getChildren().size() > 0) {
                    FXHashMapNode prev = mapList.getLast();
                    newNode.setLayoutX(prev.getLayoutX() + (prev.getGridWidth() + OFFSET) * globalScale.x);
                    newNode.setLayoutY(prev.getLayoutY());
                    bindNodes(prev, newNode);
                } else {
                    newNode.setLayoutX(100.0);
                    newNode.setLayoutY(200.0);
                }

                mapList.add(newNode);
                viewPane.getChildren().add(newNode);
            }

        } catch (FileNotFoundException e) {
            showDialog(
                    Alert.AlertType.ERROR,
                    "Error :<",
                    "Ooops, there was an error!!",
                    "Saved file not found!"
            );
        }

    }

    /**
     * Метод, що створює діалог з користувачом. Використовуємо для
     * виводу певної інформації, помилок або попередженнь.
     * @param type - тип діалогу (ERROR, INFORMATION, ...).
     * @param title - заголовок.
     * @param headerText - текст у розділі header.
     * @param contentText - основний текст.
     */
    private void showDialog(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Метод, що пов'язує лінією дві ноди.
     * @param prev - перша нода.
     * @param next - друга нода.
     */
    private void bindNodes(FXHashMapNode prev, FXHashMapNode next) {
        prev.getLine().setVisible(true);
        prev.getLine().setStartX(prev.getGridWidth()  * (prev.getGrid().getScaleX() / 2.0 + 0.5));
        prev.getLine().setStartY(prev.getGridHeight() * (prev.getGrid().getScaleY() / 2.0 + 0.5));
        prev.getLine().setEndX(next.getLayoutX() - prev.getLayoutX() + next.getGridWidth()  * (-next.getGrid().getScaleX() / 2.0 + 0.5));
        prev.getLine().setEndY(next.getLayoutY() - prev.getLayoutY() + next.getGridHeight() * (-next.getGrid().getScaleX() / 2.0 + 0.5));
    }

    /**
     * Метод, для переміщення ноди.
     * @param node - нода для переміщення.
     * @param dx - координата х.
     * @param dy - координата у.
     */
    private void moveNode(FXHashMapNode node, double dx, double dy){
        node.setLayoutX(node.getLayoutX() + dx);
        node.setLayoutY(node.getLayoutY() + dy);
    }

    /**
     * Метод, що рухає ноду у списку. Потрібно для Access Order.
     * @param node - нода для переміщення.
     */
    private void moveNodeToMapListBack(FXHashMapNode node){
        removeNodeFromMapList(node);
        bindNodes(mapList.getLast(), node);
        mapList.add(node);
        node.resetLine();
    }

    /**
     * Метод, що видаляє ноду.
     * @param node - нода для видалення.
     */
    private void removeNodeFromMapList(FXHashMapNode node) {

        int index = mapList.indexOf(node);

        if (index > 0 ) {
            FXHashMapNode prev = mapList.get(index - 1);
            if(index < mapList.size() - 1){
                bindNodes(prev, mapList.get(index + 1));
            } else {
                prev.getLine().setVisible(false);
            }

        }

        mapList.remove(node);
    }

    /**
     * Метод, що обробляє рух миші з натисненою кнопкою на панелі ViewPane.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void onViewPaneMouseDragged(MouseEvent mouseEvent) {

        if (pickedNode != null) {
            moveNode(pickedNode, mouseEvent.getX() - mousePreviousPos.x, mouseEvent.getY() - mousePreviousPos.y);

            int index = mapList.indexOf(pickedNode);
            if (index > 0) {
                bindNodes(mapList.get(index - 1), pickedNode);
            }
            if (index < mapList.size() - 1) {
                bindNodes(pickedNode, mapList.get(index + 1));
            }
        } else {
            for (Node node : viewPane.getChildren()) {
                FXHashMapNode testNode = (FXHashMapNode) node;
                moveNode(testNode, mouseEvent.getX() - mousePreviousPos.x, mouseEvent.getY() - mousePreviousPos.y);
            }
        }

        mousePreviousPos.x = mouseEvent.getX();
        mousePreviousPos.y = mouseEvent.getY();
    }

    /**
     * Метод, що обробляє рух мишки на панелі ViewPane.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void onViewPaneMouseMoved(MouseEvent mouseEvent) {
        mousePreviousPos.x = mouseEvent.getX();
        mousePreviousPos.y = mouseEvent.getY();
    }

    /**
     * Метод, що обробляє подію натискання кнопки миши на панелі ViewPane.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void onViewPaneMousePressed(MouseEvent mouseEvent) {

        if (!mouseEvent.isSecondaryButtonDown()) return;

        for (FXHashMapNode node : mapList) {
            double pointX = (mouseEvent.getX() - (node.getLayoutX() + node.getGridWidth()  / 2.0))
                    / globalScale.x + node.getGridWidth()  / 2.0;
            double pointY = (mouseEvent.getY() - (node.getLayoutY() + node.getGridHeight() / 2.0))
                    / globalScale.y + node.getGridHeight() / 2.0;
            if (node.getGrid().contains(pointX, pointY)) {
                pickedNode = node;
                pickedNode.toFront();
            }
        }

        mousePreviousPos.x = mouseEvent.getX();
        mousePreviousPos.y = mouseEvent.getY();
    }

    /**
     * Метод, що обробляє подію відпускання кнопки мишки на панелі ViewPane.
     * @param mouseEvent - об'єкт, який зберігає подію мишки.
     */
    public void onViewPaneMouseReleased(MouseEvent mouseEvent) {
        pickedNode = null;
    }

    /**
     * Метод, що обробляє подію скроллу (пролистування, середня кнопка мишки)
     * на панелі ViewPane.
     * @param scrollEvent - об'єкт, який зберігає подію мишки.
     */
    public void onViewPaneMouseScroll(ScrollEvent scrollEvent) {

        double scaleFactor = scrollEvent.getDeltaY() > 0 ? 1.05 : 0.95;

        globalScale.x *= scaleFactor;
        globalScale.y *= scaleFactor;

        for (int index = 0; index < mapList.size(); index++) {
            FXHashMapNode node = mapList.get(index);
            node.getGrid().setScaleX(node.getGrid().getScaleX() * scaleFactor);
            node.getGrid().setScaleY(node.getGrid().getScaleY() * scaleFactor);

            double xMove = node.getLayoutX() - scrollEvent.getX();
            double yMove = node.getLayoutY() - scrollEvent.getY();

            node.setLayoutX(scrollEvent.getX() + xMove * scaleFactor);
            node.setLayoutY(scrollEvent.getY() + yMove * scaleFactor);

            if (index > 0)  {
                bindNodes(mapList.get(index - 1), node);
            }

        }
    }
}
