import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Afina - клас, що наслідується від класу Application
 * бібліотеки JavaFX, що дозволяє створити GUI-додаток.
 */
public class Afina extends Application {

    /**
     * Перевизначений метод, з якого починається виконання програми.
     * Тут відбувається первинне налаштування додатку, наприклад,
     * визначення назви вікна, іконки, тощо.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
        Тут відбувається завантаження ресурсного файлу .fxml
        Це формат розмітки структури GUI, на кшталт HTML.
        Цей файл всередині також використовує інший файл - styles.css
        Це мова стилей, яка використовується у web, а також для задання стилів
        у JavaFX.
         */
        Parent root = FXMLLoader.load(getClass().getResource("afina.fxml"));
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("icon.png"))));
        primaryStage.setTitle("Afina");
        primaryStage.setScene(new Scene(root, 1200, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
