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
import model.ciphers.Cipher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

public class DecryptionController {
    private String cipherText = "";
    private String cipherName = "";
    private Cipher cipher;
    private boolean cipherGiven = false;
    private boolean keysGiven = false;
    private String decipheredText = "";
    static private String DELIMITER = "";
    private NGrams nGrams;
    private WorldLangStats worldLangStats;

    @FXML
    private ScrollPane cipherScrollPane;
    @FXML
    private AnchorPane cipherAnchorPane;
    @FXML
    private ToggleButton rot13Button;
    @FXML
    private ToggleButton railFenceButton;
    @FXML
    private ToggleButton affineButton;
    @FXML
    private ToggleButton caesarButton;
    @FXML
    private ToggleButton atbashButton;
    @FXML
    private ToggleButton simpleSubButton;
    @FXML
    private ToggleButton baconianButton;
    @FXML
    private ToggleButton polybiusButton;
    @FXML
    private ToggleButton nihilistButton;
    @FXML
    private ToggleButton primeButton;
    @FXML
    private ToggleButton vigButton;
    @FXML
    private TextArea encryptedmsgField;
    @FXML
    private TextArea decryptedMsgField;
    @FXML
    private TextField textKeyField;
    @FXML
    private ChoiceBox<Integer> key1ChoiceBox;
    @FXML
    private ChoiceBox<Integer> key2ChoiceBox;
    @FXML
    private Text key1Text;
    @FXML
    private Text key2Text;
    @FXML
    private Button decryptButton;
    @FXML
    private Button quitButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField hintField;

    private ToggleGroup toggleGroup;
    private ObservableList<Integer> caesarList;
    private ObservableList<Integer> affineList1;
    private ObservableList<Integer> affineList2;


    public DecryptionController() {

        nGrams = new NGrams();
        worldLangStats = new WorldLangStats(nGrams);
        caesarList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        affineList1 = FXCollections.observableArrayList(1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25);
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("view/TitleScreen.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            stage.setScene(new Scene(Objects.requireNonNull(root)));
            stage.setTitle("Decryption");
            stage.setResizable(false);
            stage.show();
        });

        genToggleGroup();
    }

    @FXML
    void cipherHandler(ActionEvent event) {
        ToggleButton pressedButton = (ToggleButton) event.getSource();


        //Unpress any other buttons
        for (Toggle button : toggleGroup.getToggles()) {

            if (!button.equals(pressedButton)) button.setSelected(false);
        }

        if (!pressedButton.isSelected()) {

            resetItems();
            cipherGiven = false;

        } else {

            if (pressedButton.equals(caesarButton) || pressedButton.equals(railFenceButton)) {

                resetItems();
                key1ChoiceBox.getItems().addAll(caesarList);
                key2Text.strikethroughProperty().setValue(true);
            }

            if (pressedButton.equals(rot13Button) ||
                    pressedButton.equals(atbashButton) ||
                    pressedButton.equals(simpleSubButton) ||
                    pressedButton.equals(polybiusButton) ||
                    pressedButton.equals(nihilistButton)) {

                resetItems();
                key1Text.strikethroughProperty().setValue(true);
                key2Text.strikethroughProperty().setValue(true);
            }

            if (pressedButton.equals(affineButton)) {
                resetItems();
                key1ChoiceBox.getItems().addAll(affineList1);
                key2ChoiceBox.getItems().addAll(affineList2);
            }

            //Record name of button for identification later
            String temp = pressedButton.toString();
            cipherName = temp.substring(temp.indexOf("=") + 1, temp.lastIndexOf("Button"));
            cipherGiven = true;
        }

    }

    @FXML
    void decryptHandler(ActionEvent event) {

        if (key1ChoiceBox.getValue() == null && textKeyField.getText().isEmpty()) {
            keysGiven = false;

        } else {
            keysGiven = true;
        }

        if (!encryptedmsgField.getText().equals("[]")) {
            cipherText = encryptedmsgField.getText();
            setCipher();
            decryptedMsgField.setText(decryptMsg());

        } else {
            decryptedMsgField.setText("You need to give me something to work with");
        }
    }

    private String decryptMsg() {
        String[] solutions;
        DELIMITER = " ";
        String[] testLine;
        ArrayList<String> quadgrams;
        String testWord;
        int pos = -1;
        double solFitness = 0;
        double bestFitness = 0;


        //Check through info provided
        if (cipherGiven) {
            if (keysGiven) {
                return cipher.decrypt();
            }
            solutions = cipher.decryptPossibilities();
        } else {
            solutions = allSimplePossibilities();
        }

        System.out.println("solutions: " + solutions.length);
        for (int i = 0; i < solutions.length; i++) {

            testLine = solutions[i].split(DELIMITER);
            ArrayList<String> takeLine = new ArrayList<>(testLine.length);
            takeLine.addAll(Arrays.asList(testLine));

            for (String word : takeLine) {

                //Take each word individually
                testWord = word;

                //Record every quadgram that exists, TODO include position in word
                quadgrams = nGrams.getNGrams(testWord, 4);

                solFitness += nGrams.getFitness(quadgrams, 4);
            }

            if (solFitness > bestFitness || bestFitness == 0) {
                bestFitness = solFitness;
                pos = i;
            }

            solFitness = 0;
        }

        decipheredText = String.join(DELIMITER, solutions[pos]);
        return decipheredText;
    }

    private void setCipher() {

        switch (cipherName) {
            case "caesar":
                if (keysGiven) {
                    cipher = new Caesar(cipherText, key1ChoiceBox.getValue());
                } else cipher = new Caesar(cipherText);
                break;
            case "atbash":
                cipher = new Atbash(cipherText);
                break;
            case "rot13":
                cipher = new Rot13(cipherText);
                break;
            case "affine":
                if (keysGiven) {
                    cipher = new Affine(cipherText, key1ChoiceBox.getValue(), key2ChoiceBox.getValue());
                } else cipher = new Affine(cipherText);
                break;
            case "railFence":
                if (keysGiven) {
                    cipher = new RailFence(cipherText, key1ChoiceBox.getValue());
                } else cipher = new RailFence(cipherText);
                break;
            case "simpleSub":
                if (textKeyField.getText().length() > 0) {
                    cipher = new SimpleSubstitution(cipherText, textKeyField.getText(), true);
                    keysGiven = true;
                } else cipher = new SimpleSubstitution(cipherText, nGrams, worldLangStats,true);
                break;
            case "baconian":
                cipher = new Baconian(cipherText);
                break;
            case "polybius":
                cipher = new Polybius(cipherText);
                break;
            case "nihilist":
                if (!textKeyField.getText().isEmpty()) {
                    cipher = new Nihilist(cipherText, textKeyField.getText());
                    keysGiven = true;
                } else cipher = new Nihilist(cipherText);
                break;
            case "prime":
                cipher = new PrimeCipher(cipherText);
                break;
            case "vig":
                if(!textKeyField.getText().isEmpty()){
                    cipher = new Vigenere(cipherText,textKeyField.getText());
                    keysGiven = true;
                }else cipher = new Vigenere(cipherText);
            default:
                cipherGiven = false;
        }
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
    }

    void resetItems() {
        key1ChoiceBox.getItems().clear();
        key2ChoiceBox.getItems().clear();
        key1Text.strikethroughProperty().setValue(false);
        key2Text.strikethroughProperty().setValue(false);

    }

    private boolean isBacon() {
        //Check if string contains any instance of a letter that isn't a or b
        if (!Pattern.matches("[\\dC-Zc-z]+", cipherText)) {
            //Return true if confirmed to be decrypted via bacon
            return  cipherText.contains("a") && cipherText.contains("b") && cipherText.length()>=5
                    || cipherText.contains("a") && cipherText.length()>=5;
        } else {
            return false;
        }
    }
    //TODO if will always detect poly
    //
    private boolean isPoly() {
        int num1;
        int num2;

        //Check if it contains no letters
        for (int i = 0; i < cipherText.length(); i++) {
            if (Character.isDigit(cipherText.charAt(i))) {

                num1 = Integer.parseInt(cipherText.substring(i, i + 1));
                num2 = Integer.parseInt(cipherText.substring(i + 1, i + 2));
                i++;
                if (num1 > 5 || num2 > 5 || num2 == 0) return false;
            }
        }
        return true;
    }


    private boolean isNihil() {

        int numb;

        for (int i = 0; i < cipherText.length(); i++) {
            if (Character.isDigit(cipherText.charAt(i))) {

                numb = Integer.parseInt(cipherText.substring(i, i + 2));
                i++;
                if(numb == 10 ||  numb == 11 && cipherText.charAt(i+2)=='0'){
                    numb = Integer.parseInt(cipherText.substring(i-1, i + 2));
                }

                if (numb < 21 || numb == 31 ||  numb == 41 || numb == 51 ||
                        numb == 61 || numb == 71 || numb == 81 || numb == 91 || numb == 101) return false;
            }
        }
        return true;
    }

    private String[] allSimplePossibilities() {

        ArrayList<String> list = new ArrayList<>();

        if (isBacon()) return new Baconian(cipherText).decryptPossibilities();
        //TODO Check if it contains at least 2 numbers too
        if (!Pattern.matches((".*[A-Za-z].*"), cipherText)) {
            if (!isPoly()) return new Nihilist(cipherText).decryptPossibilities();
            if (!isNihil()) return new Polybius(cipherText).decryptPossibilities();
            else {
                Collections.addAll(list, new Polybius(cipherText).decryptPossibilities());
                Collections.addAll(list, new Nihilist(cipherText).decryptPossibilities());
                return list.toArray(new String[0]);
            }
        }

        cipher = new Caesar(cipherText);
        Collections.addAll(list, cipher.decryptPossibilities());
        cipher = new Affine(cipherText);
        Collections.addAll(list, cipher.decryptPossibilities());
        cipher = new Atbash(cipherText);
        Collections.addAll(list, cipher.decryptPossibilities());
        cipher = new RailFence(cipherText);
        Collections.addAll(list, cipher.decryptPossibilities());

        return list.toArray(new String[0]);
    }
}
