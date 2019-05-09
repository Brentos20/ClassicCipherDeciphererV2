package controller;

import model.ciphers.*;
import model.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ciphers.*;

import java.io.IOException;
import java.util.Objects;

public class EncryptionController {

    @FXML private ScrollPane cipherScrollPane;
    @FXML private AnchorPane cipherAnchorPane;
    @FXML private ToggleButton rot13Button;
    @FXML private ToggleButton railFenceButton;
    @FXML private ToggleButton affineButton;
    @FXML private ToggleButton caesarButton;
    @FXML private ToggleButton atbashButton;
    @FXML private ToggleButton simpleSubButton;
    @FXML private ToggleButton baconianButton;
    @FXML private ToggleButton polybiusButton;
    @FXML private ToggleButton nihilistButton;
    @FXML private ToggleButton primeButton;
    @FXML private ToggleButton vigButton;
    @FXML private ToggleButton padButton;
    @FXML private TextArea msgField;
    @FXML private TextArea encryptedMsgField;
    @FXML private TextField textKeyField;
    @FXML private ChoiceBox<Integer> key1ChoiceBox;
    @FXML private ChoiceBox<Integer> key2ChoiceBox;
    @FXML private Text key1Text;
    @FXML private Text key2Text;
    @FXML private Button encryptButton;
    @FXML private Button quitButton;
    @FXML private Button backButton;

    private ToggleGroup toggleGroup;
    private ObservableList<Integer> caesarList;
    private ObservableList<Integer> affineList1;
    private ObservableList<Integer> affineList2;
    private String cipherName;
    private Cipher cipher;



    public EncryptionController() {
        caesarList = FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25);
        affineList1 = FXCollections.observableArrayList(1,3,5,7,9,11,15,17,19,21,23,25);
        affineList2 = FXCollections.observableArrayList(caesarList);
        cipherName = "";
    }

    @FXML
    public void initialize() {

        quitButton.setOnAction(event -> {
            Stage stage = (Stage) quitButton.getScene().getWindow();
            stage.close();
        });

        backButton.setOnAction(event -> {
            Parent root = null;
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("view/TitleScreen.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(Objects.requireNonNull(root)));
            stage.setTitle("Encryption");
            stage.setResizable(false);
            stage.show();
        });


        genToggleGroup();
    }

    private void genToggleGroup() {

        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(caesarButton);
        toggleGroup.getToggles().add(rot13Button);
        toggleGroup.getToggles().add(atbashButton);
        toggleGroup.getToggles().add(affineButton);
        toggleGroup.getToggles().add(railFenceButton);
        toggleGroup.getToggles().add(simpleSubButton);
        toggleGroup.getToggles().add(baconianButton);
        toggleGroup.getToggles().add(polybiusButton);
        toggleGroup.getToggles().add(nihilistButton);
        toggleGroup.getToggles().add(primeButton);
        toggleGroup.getToggles().add(vigButton);
        toggleGroup.getToggles().add(padButton);
    }

    @FXML
    void encryptHandler(ActionEvent event) {

        if(!msgField.getText().equals("[]")) {

            String msg = msgField.getText();
            boolean inputValid = true;
            switch (cipherName) {
                case "caesar":
                    cipher = new Caesar(msg, key1ChoiceBox.getValue());
                    break;
                case "atbash":
                    cipher = new Atbash(msg);
                    break;
                case "rot13":
                    cipher = new Rot13(msg);
                    break;
                case "affine":
                    cipher = new Affine(msg, key1ChoiceBox.getValue(), key2ChoiceBox.getValue());
                    break;
                case "railFence":
                    cipher = new RailFence(msg, key1ChoiceBox.getValue());
                    break;
                case "simpleSub":
                    cipher = new SimpleSubstitution(msg, textKeyField.getText(),false);
                    break;
                case "baconian":
                    cipher = new Baconian(msg);
                    break;
                case "polybius":
                    cipher = new Polybius(msg);
                    break;
                case "nihilist":
                    cipher = new Nihilist(msg, textKeyField.getText());
                    break;
                case "prime":
                    cipher = new PrimeCipher(msg);
                    break;
                case "vig":
                    cipher = new Nihilist(msg);
                    break;
                case "pad":
                    cipher = new OneTimePad(msg);
                    break;
                default:
                    inputValid = false;
                    break;
            }
            if(inputValid)encryptedMsgField.setText(cipher.encrypt());


        }else{
            msgField.promptTextProperty().setValue("You need to type in a message for me to work with here");
        }
    }

    @FXML
    void cipherHandler(ActionEvent event) {

        ToggleButton pressedButton = (ToggleButton) event.getSource();

        if(pressedButton.equals(caesarButton) || pressedButton.equals(railFenceButton)){

            resetItems();
            key1ChoiceBox.getItems().addAll(caesarList);
            key1ChoiceBox.setValue(6);
            key2Text.strikethroughProperty().setValue(true);
        }

        if(pressedButton.equals(rot13Button)         ||
                pressedButton.equals(atbashButton)   ||
                pressedButton.equals(simpleSubButton)||
                pressedButton.equals(baconianButton) ||
                pressedButton.equals(polybiusButton) ||
                pressedButton.equals(nihilistButton) ||
                pressedButton.equals(primeButton)    ||
                pressedButton.equals(vigButton)      ||
                pressedButton.equals(padButton)){

            resetItems();
            key1Text.strikethroughProperty().setValue(true);
            key2Text.strikethroughProperty().setValue(true);
        }

        if(pressedButton.equals(affineButton)){
            resetItems();
            key1ChoiceBox.getItems().addAll(affineList1);
            key1ChoiceBox.setValue(9);
            key2ChoiceBox.setValue(10);
            key2ChoiceBox.getItems().addAll(affineList2);
        }

        String temp = pressedButton.toString();
        cipherName = temp.substring(temp.indexOf("=")+1, temp.lastIndexOf("Button"));

    }

    private void resetItems(){
        msgField.promptTextProperty().setValue("Write your message here");
        key1ChoiceBox.getItems().clear();
        key2ChoiceBox.getItems().clear();
        key1Text.strikethroughProperty().setValue(false);
        key2Text.strikethroughProperty().setValue(false);

    }

}