import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;

/**
 * Клас, що представляє собою відображення пари ключ-значення
 * у додатку.
 */
public class FXHashMapNode extends Pane {

    private final int width = 200, height = 70;
    private GridPane grid;
    private Label key;
    private Label value;
    private Line line;

    /**
     * Констуктор, що задає базові налаштування, наприклад, розмір, стиль, тощо
     */
    public FXHashMapNode() {
        super();
        grid  = new GridPane();
        line = new Line();
        key   = new Label();
        value = new Label();

        line.setVisible(false);

        grid.setGridLinesVisible(true);
        grid.getColumnConstraints().add(new ColumnConstraints(width / 2.f));
        grid.getColumnConstraints().add(new ColumnConstraints(width / 2.f));
        grid.getRowConstraints().add(new RowConstraints(height));

        grid.setStyle("-fx-background-color: #ebcb8b; -fx-font-size: 18");
        grid.add(key, 0, 0);
        grid.add(value, 1, 0);

        getChildren().addAll(line, grid);
        setMaxSize(100, 70);
    }

    public int getGridWidth() {
        return width;
    }

    public int getGridHeight() {
        return height;
    }

    public GridPane getGrid() {
        return grid;
    }

    public Label getKey() {
        return key;
    }

    public Label getValue() {
        return value;
    }

    public Line getLine() {
        return line;
    }

    public void resetLine(){
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(0);
        line.setEndY(0);
    }
}
