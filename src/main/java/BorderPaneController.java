import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class BorderPaneController {

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

    private FXHashMapNode pickedNode;
    private Vec2 mousePreviousPos;
    private Vec2 globalScale;
    private final LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
    private final LinkedList<FXHashMapNode> mapList = new LinkedList<>();

    public static class Vec2 {
        public double x;
        public double y;
    }

    public void initialize() {
        globalScale = new Vec2();
        globalScale.x = 1;
        globalScale.y = 1;
        mousePreviousPos = new Vec2();
        viewPane.toBack();
    }

    public void handleMouseClickedPutButton(MouseEvent mouseEvent) {

        final double OFFSET = 30.f;

        String key   = putKey.getText();
        String value = putValue.getText();

        if (key.equals("") || value.equals("")) {
            showErrorDialogIfFieldsAreEmpty();
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

    public void handleMouseClickedRemoveButton(MouseEvent mouseEvent) {

        String key = removeKey.getText();

        if (key.equals("")) {
            showErrorDialogIfFieldsAreEmpty();
            return;
        }

        if (hashMap.remove(key) == null) {
            return;
        }

        for (FXHashMapNode node: mapList) {
            if (node.getKey().getText().equals(key)) {
                removeNodeFromMapList(node);
                viewPane.getChildren().remove(node);
                break;
            }
        }

        removeKey.clear();
    }

    public void handleMouseClickedReplaceButton(MouseEvent mouseEvent) {

        String key   = replaceKey.getText();
        String value = replaceValue.getText();

        if (key.equals("") || value.equals("")) {
            showErrorDialogIfFieldsAreEmpty();
            return;
        }

        if (hashMap.get(key) != null) {
            hashMap.replace(key, value);

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

        replaceKey.clear();
        replaceValue.clear();
    }

    public void handleMouseClickedClearButton(MouseEvent mouseEvent) {
        mapList.clear();
        hashMap.clear();
        viewPane.getChildren().clear();
    }

    public void handleMouseClickedSaveButton(MouseEvent mouseEvent) {

        try {
            FileWriter saveFile = new FileWriter("saved.txt");

            for (String key: hashMap.keySet()) {
                saveFile.write(key + " " + hashMap.get(key) + "\n");
            }

            saveFile.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("File saved :>");
            alert.setContentText("saved.txt created!");
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error :<");
            alert.setHeaderText("Ooops, there was an error!!");
            alert.setContentText("Cannot create saved.txt!");
            alert.showAndWait();
        }
    }

    public void handleMouseClickedLoadButton(MouseEvent mouseEvent) {

        handleMouseClickedClearButton(mouseEvent);

        final double OFFSET = 30.f;

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error :<");
            alert.setHeaderText("Ooops, there was an error!!");
            alert.setContentText("Saved file not found!");
            alert.showAndWait();
        }

    }

    public void showErrorDialogIfFieldsAreEmpty() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error :<");
        alert.setHeaderText("Ooops, there was an error!!");
        alert.setContentText("Field 'Key' or 'Value' is empty!");
        alert.showAndWait();
    }

    private void bindNodes(FXHashMapNode prev, FXHashMapNode next) {
        prev.getLine().setVisible(true);
        prev.getLine().setStartX(prev.getGridWidth()  * (prev.getGrid().getScaleX() / 2.0 + 0.5));
        prev.getLine().setStartY(prev.getGridHeight() * (prev.getGrid().getScaleY() / 2.0 + 0.5));
        prev.getLine().setEndX(next.getLayoutX() - prev.getLayoutX() + next.getGridWidth()  * (-next.getGrid().getScaleX() / 2.0 + 0.5));
        prev.getLine().setEndY(next.getLayoutY() - prev.getLayoutY() + next.getGridHeight() * (-next.getGrid().getScaleX() / 2.0 + 0.5));
    }

    private void moveNode(FXHashMapNode node, double dx, double dy){
        node.setLayoutX(node.getLayoutX() + dx);
        node.setLayoutY(node.getLayoutY() + dy);
    }

    private void moveNodeToMapListBack(FXHashMapNode node){
        removeNodeFromMapList(node);
        bindNodes(mapList.getLast(), node);
        mapList.add(node);
        node.resetLine();
    }

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

    public void onViewPaneMouseMoved(MouseEvent mouseEvent) {
        mousePreviousPos.x = mouseEvent.getX();
        mousePreviousPos.y = mouseEvent.getY();
    }

    public void onViewPaneMousePressed(MouseEvent mouseEvent) {
        if(!mouseEvent.isSecondaryButtonDown()) return;

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

    public void onViewPaneMouseReleased(MouseEvent mouseEvent) {
        pickedNode = null;
    }

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

            if(index > 0){
                bindNodes(mapList.get(index - 1), node);
            }

        }
    }
}
