package controller;

import model.ciphers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.util.*;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @project ClassicalCipherDecipherer
 */
public class TestSuiteController {
    public ChoiceBox<Integer> keyNumbChoiceBox;
    @FXML
    private ScrollPane cipherScrollPane;
    @FXML
    private ToggleButton hillClimbButton;
    @FXML
    private ToggleButton simAnnealButton;
    @FXML
    private ToggleButton geneticAlgButton;
    @FXML
    private TextArea msgField;
    @FXML
    private TextArea decryptedMsgField;
    @FXML
    private Button quitButton;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox searchSpaceCheckBox;
    @FXML
    private Text keyText;
    @FXML
    private CheckBox trimCheckBox;
    @FXML
    private Text paramText;
    @FXML
    private TextField paramOneField;
    @FXML
    private TextField paramTwoField;
    @FXML
    private TextField paramThreeField;
    @FXML
    private Text paramOneText;
    @FXML
    private Text paramTwoText;
    @FXML
    private Text paramThreeText;
    @FXML
    private Text key1Text11;
    @FXML
    private Text key1Text111;
    @FXML
    private Text key1Text31;
    @FXML
    private Button startButton;
    @FXML
    private TextField timeTakenField;
    @FXML
    private TextField accField;
    @FXML
    private TextField keyLettersFoundField;

    private ToggleGroup toggleGroup;
    private ObservableList<Integer>  numbKeyLetters;
    private SimpleSubstitution simpleSub;
    private String algName;
    private boolean algGiven = false;
    private boolean learnedSearchSpace = false;
    private boolean learnedTrim = false;

    public TestSuiteController(){

        numbKeyLetters = FXCollections.observableArrayList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);
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
            stage.setTitle("TestSuite");
            stage.setResizable(false);
            stage.show();
        });

        keyNumbChoiceBox.setItems(numbKeyLetters);
        keyNumbChoiceBox.setValue(0);
        keyNumbChoiceBox.setOnAction(event -> msgField.setPromptText("Here you have chosen " + keyNumbChoiceBox.getValue() + " key-letters to be already known by the algorithm" ));
        resetItems();
        genToggleGroup();
    }

    @FXML
    void testHandler(ActionEvent event){

        String alg;

        if(!hillClimbButton.isSelected() && !simAnnealButton.isSelected() && !geneticAlgButton.isSelected()){
            decryptedMsgField.setPromptText("Please choose an algorithm to be tested on");
        }
        else{
            if(msgField.getText().isEmpty()) decryptedMsgField.setPromptText("Please add the text you want the algorthm to be tested on");
            else if(charCount(msgField.getText())<150) decryptedMsgField.setPromptText("Please provide at least 150 characters to be tested on");
            else{
                Toggle selectedToggle = toggleGroup.getSelectedToggle();
                 alg = selectedToggle.toString();
                 alg = alg.substring(alg.indexOf("=")+1, alg.indexOf(","));

                 if(hasValidParams(alg)){
                     startTest(alg);
                 }
            }
        }
    }

    private int charCount(String msg){
        int count = 0;
        for (int i = 0; i <msg.length(); i++) {
            if(Character.isLetter(msg.charAt(i))) count++;
        }
        return count;
    }

    private void startTest(String alg) {

        String oriText = msgField.getText();
        NGrams nGrams = new NGrams();
        String msg = msgField.getText();
        SimpleSubstitution simpleSub = new SimpleSubstitution(msg,"",false);
        String cipherText = simpleSub.encrypt();
        String keyAsString = simpleSub.getEncryptKey();
        char[] encryptKey = keyAsString.toCharArray();
        simpleSub = new SimpleSubstitution(cipherText,nGrams,new WorldLangStats(nGrams),false);
        cipherText = simpleSub.decrypt();
        System.out.println(simpleSub.getLetterFreq().keySet());
        ArrayList<Character> msgFreqList = new ArrayList<>(simpleSub.getLetterFreq().keySet());

        //Results params
        long startTime;
        long endTime;
        String decryptedText = "";

        boolean useTrim = trimCheckBox.isSelected();
        boolean useCustSearchSpace = searchSpaceCheckBox.isSelected();
        String givenKeyLetts = genKeyClue(msgFreqList,encryptKey);


        startTime= System.nanoTime();
        switch(alg){
            case "hillClimbButton":
                SubHillClimb subHillClimb = new SubHillClimb(simpleSub.getKey(),
                        simpleSub.getMsgStats(),
                        simpleSub.getWorldLangStats(),
                        nGrams, simpleSub.getCaps(),
                        msgFreqList,simpleSub.getMistakeLoc());
                subHillClimb.setParams(Integer.valueOf(paramOneField.getText()),
                        Integer.valueOf(paramTwoField.getText()),useTrim,useCustSearchSpace, givenKeyLetts);
                decryptedText = subHillClimb.decrypt();
                decryptedMsgField.setText(decryptedText);
                break;
            case "simAnnealButton":
                SimAnneal simAnneal;
                if(useCustSearchSpace){
                    simAnneal = new SimAnneal(simpleSub.getKey(),
                            simpleSub.getMsgStats(), nGrams,
                            givenKeyLetts, simpleSub.getMistakeLoc(),useTrim);

                    decryptedText = simAnneal.decrypt(Integer.valueOf(paramOneField.getText()),
                            Double.valueOf(paramTwoField.getText()),
                            Integer.valueOf(paramThreeField.getText()),1);
                    decryptedMsgField.setText(decryptedText);

                }else{
                    simAnneal = new SimAnneal(simpleSub.getKey(),
                            simpleSub.getMsgStats(),nGrams,
                            givenKeyLetts, useCustSearchSpace, useTrim);

                    decryptedText = simAnneal.decrypt(Integer.valueOf(paramOneField.getText()),
                            Double.valueOf(paramTwoField.getText()),
                            Integer.valueOf(paramThreeField.getText()),1);
                    decryptedMsgField.setText(decryptedText);
                }
                break;
            case "geneticAlgButton":
                GeneticAlg geneticAlg;
                if(useCustSearchSpace){
                    geneticAlg = new GeneticAlg(simpleSub.getKey(),simpleSub.getMsgStats(),nGrams, givenKeyLetts,simpleSub.getMistakeLoc(),useTrim);
                    decryptedText = geneticAlg.decrypt(Integer.valueOf(paramOneField.getText()),Integer.valueOf(paramTwoField.getText()),100,Double.valueOf(paramThreeField.getText()));
                    decryptedMsgField.setText(decryptedText);
                }else{
                    geneticAlg = new GeneticAlg(simpleSub.getKey(),simpleSub.getMsgStats(),nGrams, givenKeyLetts,useCustSearchSpace,useTrim);
                    decryptedText = geneticAlg.decrypt(Integer.valueOf(paramOneField.getText()),Integer.valueOf(paramTwoField.getText()),100,Double.valueOf(paramThreeField.getText()));
                    decryptedMsgField.setText(decryptedText);
                }
                break;
        }
        endTime = System.nanoTime();
        genResults(endTime,startTime,decryptedText,oriText);
    }

    private void genResults(long endTime, long startTime, String decryptedText, String oriText) {
        double timeTaken = ((endTime-startTime)/1000000);
        if(timeTaken>=1000){
            timeTaken = Math.round((timeTaken/1000) * 100d) / 100d;
            timeTakenField.setText(timeTaken+ "s");
        }else{
            timeTaken = Math.round((timeTaken) * 100d) / 100d;
            timeTakenField.setText(timeTaken + "ms");
        }

        double oriCharCount = oriText.length();
        double accCharCount = 0;
        ArrayList<Character> keyLetters = new ArrayList<>(26);
        ArrayList<Character> misKeyLetters = new ArrayList<>(26);
        int keyLettsfound;
        int keyletterNotpresent = 26;
        char dThisChar;
        char oThisChar;
        for (int i = 0; i <oriCharCount; i++) {
            dThisChar = Character.toLowerCase(decryptedText.charAt(i));
             oThisChar = Character.toLowerCase(oriText.charAt(i));
            if(dThisChar==oThisChar){
                accCharCount++;

                if(Character.isLetter(dThisChar) && !keyLetters.contains(dThisChar)){
                    keyLetters.add(dThisChar);
                    keyletterNotpresent--;
                }
            }else{
                if(Character.isLetter(dThisChar) && !misKeyLetters.contains(dThisChar)) {
                    misKeyLetters.add(dThisChar);
                    keyletterNotpresent--;
                }
            }
        }
        keyLettsfound = keyLetters.size()+keyletterNotpresent;
        accField.setText((Math.round(((accCharCount/oriCharCount)*100) * 100d) / 100d) + "%");

        keyLettersFoundField.setText(String.valueOf(keyLettsfound));
    }

    private String genKeyClue(ArrayList<Character> msgFreqList, char[] encryptKey) {

        int keyLetters = keyNumbChoiceBox.getValue();
        StringBuilder keyClue = new StringBuilder();

        for (int i = 0; i < keyLetters; i++) {
            char keyLetter = Character.toLowerCase(msgFreqList.get(i));
            for (int j = 0; j < encryptKey.length; j++) {
                if(encryptKey[j]==keyLetter){
                    keyClue.append((char) (j+97));
                    break;
                }
            }
        }
        return keyClue.toString();
    }


    private boolean hasValidParams(String alg) {

        switch(alg){
            case "hillClimbButton":
                return checkHillClimbParams();
            case "simAnnealButton":
                return checkSimAnnealParams();
            case "geneticAlgButton":
                return checkGenAlgParams();
        }
        return false;
    }

    private boolean checkGenAlgParams() {
        if(paramOneField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please give a population size e.g. 1000");
            return false;
        }else{
            if(!isInteger(paramOneField.getText(),1)){
                decryptedMsgField.setPromptText("Please give a valid number as a population size");
                return false;
            }
        }
        if(paramTwoField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please set the number of generations e.g. 100");
            return false;
        }else{
            if(!isInteger(paramTwoField.getText(),2)){
                decryptedMsgField.setPromptText("Please give a valid number generations");
                return false;
            }
        }
        if(paramThreeField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please set the mutation rate equal or less than 1.0, e.g. 0.3");
            return false;
        }else{
            if(!isvalidRate(paramThreeField.getText())){
                decryptedMsgField.setPromptText("Please set a valid number for the mutation rate equal or less than 1.0, e.g. 0.3");
                return false;
            }
        }
        return true;
    }

    private boolean checkSimAnnealParams() {
        if(paramOneField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please give a starting temp e.g. 10000");
            return false;
        }else{
            if(!isInteger(paramOneField.getText(),1)){
                decryptedMsgField.setPromptText("Please give a valid number as a starting temp");
                return false;
            }
        }
        if(paramTwoField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please give a cooling rate equal or less than 1.0, e.g. 0.01");
            return false;
        }else{
            if(!isvalidRate(paramTwoField.getText())){
                decryptedMsgField.setPromptText("Please give a valid cooling rate equal or less than 1.0, e.g. 0.01");
                return false;
            }
        }
        if(paramThreeField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please set the maximum iterations e.g. 5");
            return false;
        }else{
            if(!isInteger(paramThreeField.getText(),3)){
                decryptedMsgField.setPromptText("Please give a valid number as a maximum iteration");
                return false;
            }
        }
        return true;
    }

    private boolean checkHillClimbParams() {
        if(paramOneField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please set the number of rounds");
            return false;
        }
        if(!isInteger(paramOneField.getText(),1)) {
            decryptedMsgField.setPromptText("Please give a valid number e.g. 10");
            return false;
        }
        if(paramTwoField.getText().isEmpty()){
            decryptedMsgField.setPromptText("Please set number of restarts");
            return false;
        }
        if(isInteger(paramTwoField.getText(),2))return true;
        else {
            decryptedMsgField.setPromptText("Please set valid number of restarts e.g. 5");
            return false;
        }
    }

    private boolean isInteger(String string, int param) {
        try {
            if(string.contains(".")) {
                string = string.substring(0,string.indexOf("."));
            }
            Integer.valueOf(string);
            switch(param){
                case 1:
                    paramOneField.setText(string);
                    break;
                case 2:
                    paramTwoField.setText(string);
                    break;
                case 3:
                    paramThreeField.setText(string);
                    break;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isvalidRate(String string) {
        try {
            Double.valueOf(string);
            return Double.valueOf(string) <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void infoHandler(ActionEvent event){
        String temp = event.getSource().toString();
        temp = temp.substring(temp.indexOf("=")+1, temp.indexOf(","));
        switch (temp){
            case "searchSpaceCheckBox":
                if(!learnedSearchSpace){
                    msgField.setPromptText("This will set up a custom search-space imperative for a problem of this size");
                    learnedSearchSpace = true;
                }else{
                    if(searchSpaceCheckBox.isSelected())msgField.setPromptText("A custom search-space will be used");
                    else msgField.setPromptText("The maximum search space will be used");
                }
                break;
            case "trimCheckBox":
                if(!learnedTrim){
                    msgField.setPromptText("This adds an exhaustive search to the end of the algorithm, in an attempt to escape a local optima");
                    learnedTrim = true;
                }else{
                    if(trimCheckBox.isSelected())msgField.setPromptText("Will use an exhaustive search");
                    else msgField.setPromptText("Will not use an exhaustive search");
                }

                break;
        }
    }

    @FXML
    void algHandler(ActionEvent event){
        ToggleButton pressedButton = (ToggleButton) event.getSource();

        for(Toggle button : toggleGroup.getToggles()){
            if(!button.equals(pressedButton)) button.setSelected(false);
        }

        if(!pressedButton.isSelected()){
            resetItems();
            algGiven = false;
        }else{
            //Change environment based on pressed button
            if(pressedButton.equals(hillClimbButton)){
                resetItems();
                paramText.setText("Hill climb Params");
                paramOneText.setText("Numb of Rounds:");
                paramOneField.setVisible(true);
                paramTwoText.setText("Numb of Restarts:");
                paramTwoField.setVisible(true);
            }
            if(pressedButton.equals(simAnnealButton)){
                resetItems();
                paramText.setText("Sim Anneal Params");
                paramOneText.setText("Temp:");
                paramTwoText.setText("Cool Rate:");
                paramThreeText.setText("Max Iteration:");
                paramOneField.setVisible(true);
                paramTwoField.setVisible(true);
                paramThreeField.setVisible(true);
            }
            if(pressedButton.equals(geneticAlgButton)){
                resetItems();
                paramText.setText("Genetic Alg Params");
                paramOneText.setText("Pop size:");
                paramTwoText.setText("Numb of Gen");
                paramThreeText.setText("Mutation Rate:");
                paramOneField.setVisible(true);
                paramTwoField.setVisible(true);
                paramThreeField.setVisible(true);
            }
        }
        //Record name of button for identification later
        String temp = pressedButton.toString();
        algName = temp.substring(temp.indexOf("=") + 1, temp.lastIndexOf("Button"));
        algGiven = true;

    }
    private void genToggleGroup() {
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(hillClimbButton);
        toggleGroup.getToggles().add(simAnnealButton);
        toggleGroup.getToggles().add(geneticAlgButton);
    }
    void resetItems(){
        paramOneText.setText("");
        paramOneField.clear();
        paramOneField.setVisible(false);
        paramTwoText.setText("");
        paramTwoField.clear();
        paramTwoField.setVisible(false);
        paramThreeText.setText("");
        paramThreeField.clear();
        paramThreeField.setVisible(false);
    }

}
