package javafxtest;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;

public class TestFXNode extends Pane {
    GridPane grid;
    Label key;
    Label value;
    Line line;

    public TestFXNode() {
        super();
        grid = new GridPane();
        line = new Line();
        key = new Label();
        value = new Label();


        grid.setGridLinesVisible(true);
        grid.getColumnConstraints().add(new ColumnConstraints(60));
        grid.getColumnConstraints().add(new ColumnConstraints(60));
        grid.getRowConstraints().add(new RowConstraints(30));

        grid.setStyle("-fx-background-color: #A0A0A0");
        grid.add(key, 0, 0);
        grid.add(value, 1, 0);

        getChildren().addAll(line, grid);
        setMaxSize(60, 60);
    }

    public void resetLine(){
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(0);
        line.setEndY(0);
    }
}
