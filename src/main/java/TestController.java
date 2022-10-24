import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class TestController {
    public class Vec2 {
        public double x;
        public double y;
    }

    public AnchorPane g_pane;
    public AnchorPane controlPane;
    public Pane viewPane;

    public TextField putKeyTextfield;
    public TextField putValueTextfield;
    public Button putButton;

    public TextField removeKeyTextfield;
    public TextField removeValueTextfield;
    public Button removeButton;

    public TextField replaceKeyTextfield;
    public TextField replaceValueTextfield;
    public Button replaceButton;

    public Button clearButton;
    public CheckBox accessOrderCheck;

    private TestFXNode pickedNode;
    private Vec2 mousePreviousPos;
    private Vec2 globalScale;
    private final LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
    private final LinkedList<TestFXNode> mapList = new LinkedList<>();

    private List<Node> listy;

    public void initialize() {
        globalScale = new Vec2();
        globalScale.x = 1;
        globalScale.y = 1;
        mousePreviousPos = new Vec2();
        controlPane.toFront();
    }

    public void onPutButtonAction(ActionEvent event) {
        final double OFFSET = 30.f;
        String key = putKeyTextfield.getText();
        String value = putValueTextfield.getText();

        if (key.equals("") || value.equals("")) {
            showError("Input is invalid", "No Key or Value values are found");
        } else {
            if(hashMap.get(key) == null){
                TestFXNode newNode = new TestFXNode();
                newNode.key.setText(key);
                newNode.value.setText(value);
                newNode.grid.setScaleX(globalScale.x);
                newNode.grid.setScaleY(globalScale.y);

                if (viewPane.getChildren().size() > 0) {
                    TestFXNode prev = mapList.getLast();
                    newNode.setLayoutX(prev.getLayoutX() + (prev.grid.getWidth() + OFFSET) * globalScale.x);
                    newNode.setLayoutY(prev.getLayoutY());
                    bind(prev, newNode);
                } else {
                    newNode.setLayoutX(100.0);
                    newNode.setLayoutY(200.0);
                }

                mapList.add(newNode);
                viewPane.getChildren().add(newNode);
            } else {
                for(TestFXNode node: mapList){
                    if(node.key.getText().equals(key)){
                        if(accessOrderCheck.isSelected()){
                            moveToMapListBack(node);
                        }
                        node.value.setText(value);
                        break;
                    }
                }
            }

            hashMap.put(key, value);
        }

        putKeyTextfield.clear();
        putValueTextfield.clear();
    }

    public void onRemoveButtonAction(ActionEvent event) {
        String key = removeKeyTextfield.getText();
        String value = removeValueTextfield.getText();

        if (key.equals("")) {
            showError("Input is invalid", "No Key value are found");
        } else{
            String ret;
            if(value.isBlank()){
                if(hashMap.remove(key) == null) return;
            } else {
                if(!hashMap.remove(key, value)) return;
            }

            for(TestFXNode node: mapList){
                if(node.key.getText().equals(key)){
                    removeFromMapList(node);
                    viewPane.getChildren().remove(node);
                    break;
                }
            }
        }

        removeKeyTextfield.clear();
        removeValueTextfield.clear();
    }

    public void onReplaceButtonAction(ActionEvent event) {
        String key = replaceKeyTextfield.getText();
        String value = replaceValueTextfield.getText();

        if (key.equals("") || value.equals("")) {
            showError("Input is invalid", "No Key or Value values are found");
        } else {
            if(hashMap.get(key) != null) {
                hashMap.replace(key, value);

                for(TestFXNode node: mapList){
                    if(node.key.getText().equals(key)){
                        if(accessOrderCheck.isSelected()){
                            moveToMapListBack(node);
                        }
                        node.value.setText(value);
                        break;
                    }
                }
            }
        }

        replaceKeyTextfield.clear();
        replaceValueTextfield.clear();
    }

    public void onClearButtonAction(ActionEvent event) {
        mapList.clear();
        hashMap.clear();
        viewPane.getChildren().clear();
    }

    public void onViewPaneMousePressed(MouseEvent event) {
        for (TestFXNode node : mapList) {
            double pointX = (event.getX() - (node.getLayoutX() + node.grid.getWidth() / 2.0)) / globalScale.x + node.grid.getWidth() / 2.0;
            double pointY = (event.getY() - (node.getLayoutY() + node.grid.getHeight() / 2.0)) / globalScale.y + node.grid.getHeight() / 2.0;
            if (node.grid.contains(pointX, pointY)){
                pickedNode = node;
                pickedNode.toFront();
            }
        }

        mousePreviousPos.x = event.getX();
        mousePreviousPos.y = event.getY();
    }

    public void onViewPaneMouseReleased(MouseEvent event) {
        pickedNode = null;
    }

    public void onViewPaneMouseMoved(MouseEvent event){
        mousePreviousPos.x = event.getX();
        mousePreviousPos.y = event.getY();
    }

    public void onViewPaneMouseDragged(MouseEvent event) {
        if (pickedNode != null) {
            moveNode(pickedNode, event.getX() - mousePreviousPos.x, event.getY() - mousePreviousPos.y);

            int index = mapList.indexOf(pickedNode);
            if (index > 0) {
                bind(mapList.get(index - 1), pickedNode);
            }
            if (index < mapList.size() - 1) {
                bind(pickedNode, mapList.get(index + 1));
            }
        } else {
            for (Node node : viewPane.getChildren()) {
                TestFXNode testNode = (TestFXNode) node;
                moveNode(testNode, event.getX() - mousePreviousPos.x, event.getY() - mousePreviousPos.y);
            }
        }

        mousePreviousPos.x = event.getX();
        mousePreviousPos.y = event.getY();
    }

    public void onViewPaneMouseScroll(ScrollEvent event){
        double scaleFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;

        globalScale.x *= scaleFactor;
        globalScale.y *= scaleFactor;

        for (int index = 0; index < mapList.size(); index++) {
            TestFXNode node = mapList.get(index);
            node.grid.setScaleX(node.grid.getScaleX() * scaleFactor);
            node.grid.setScaleY(node.grid.getScaleY() * scaleFactor);

            double xMove = node.getLayoutX() - event.getX();
            double yMove = node.getLayoutY() - event.getY();

            node.setLayoutX(event.getX() + xMove * scaleFactor);
            node.setLayoutY(event.getY() + yMove * scaleFactor);

            if(index > 0){
                bind(mapList.get(index - 1), node);
            }

        }
    }

    private void moveToMapListBack(TestFXNode node){
        removeFromMapList(node);
        bind(mapList.getLast(), node);
        mapList.add(node);
        node.resetLine();
    }

    private void removeFromMapList(TestFXNode node) {
        int index = mapList.indexOf(node);
        if (index > 0 && index < mapList.size() - 1) {
            TestFXNode prev = mapList.get(index - 1);

            prev.line.setEndX(prev.line.getEndX() + node.line.getEndX());
            prev.line.setEndY(prev.line.getEndY() + node.line.getEndY());
        }

        mapList.remove(node);
    }

    private void bind(TestFXNode prev, TestFXNode next){
        prev.line.setStartX(prev.grid.getWidth() * (prev.grid.getScaleX() / 2.0 + 0.5));
        prev.line.setStartY(prev.grid.getHeight() * (prev.grid.getScaleY() / 2.0 + 0.5));
        prev.line.setEndX(next.getLayoutX() - prev.getLayoutX() + next.grid.getWidth() * (-next.grid.getScaleX() / 2.0 + 0.5));
        prev.line.setEndY(next.getLayoutY() - prev.getLayoutY() + next.grid.getHeight() * (-next.grid.getScaleX() / 2.0 + 0.5));
    }

    private void moveNode(TestFXNode node, double dx, double dy){
        node.setLayoutX(node.getLayoutX() + dx);
        node.setLayoutY(node.getLayoutY() + dy);
    }

    private void showError(String head, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(head);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
