package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TitleScreenController {

    @FXML private AnchorPane anchorPane;
    @FXML private VBox vBox;
    @FXML private Button encryptButton;
    @FXML private Button decryptButton;
    @FXML private Button analysisButton;
    @FXML private Button quitButton;
    @FXML private Text titleText;


    @FXML
    public void initialize(){

        quitButton.setOnAction(event -> {
            Stage stage = (Stage) quitButton.getScene().getWindow();
            stage.close();
        });

        encryptButton.setOnAction(event -> {
            Parent root = null;
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("view/Encryption.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(Objects.requireNonNull(root)));
            stage.setTitle("Encryption");
            stage.setResizable(false);
            stage.show();
        });

        decryptButton.setOnAction(event -> {
            Parent root = null;
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("view/Decryption.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(Objects.requireNonNull(root)));
            stage.setTitle("Decryption");
            stage.setResizable(false);
            stage.show();
        });

        analysisButton.setOnAction(event -> {
            Parent root = null;
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("view/TestSuite.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(Objects.requireNonNull(root)));
            stage.setTitle("Test Suite");
            stage.setResizable(false);
            stage.show();
        });
    }
}
