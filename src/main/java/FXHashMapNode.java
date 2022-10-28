import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;

public class FXHashMapNode extends Pane {

    GridPane grid;
    Label key;
    Label value;
    Line line;

    public FXHashMapNode() {
        super();
        grid  = new GridPane();
        line  = new Line();
        key   = new Label();
        value = new Label();

        grid.setGridLinesVisible(true);
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        grid.getRowConstraints().add(new RowConstraints(70));

        grid.setStyle("-fx-background-color: #ebcb8b; -fx-font-size: 18");
        grid.add(key, 0, 0);
        grid.add(value, 1, 0);

        getChildren().addAll(line, grid);
        setMaxSize(100, 100);
    }

    public void resetLine(){
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(0);
        line.setEndY(0);
    }
}
