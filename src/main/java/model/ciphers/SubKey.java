package model.ciphers;

import java.util.*;

/**
 * Class that stores the key for the Simple substitution cipher and additional functionality for the decryption process
 */
public class SubKey {

    private String key;
    private ArrayList<Character> addedLetters;
    private ArrayList<Character> replacedLetters;
    private ArrayList<String> msgAsList;
    private String cipherText;
    private String oriCipherText;
    private ArrayList<ArrayList<Character>> lettersToCons;
    private String DELIMITER = " ";
    private LinkedList<Boolean> caps;

    SubKey(String cipherText, String oriCipherText){

        this.cipherText = cipherText;
        this.oriCipherText = oriCipherText;
        this.msgAsList = textToList(cipherText);
        initialiseKey();
        addedLetters = new ArrayList<>();
        replacedLetters = new ArrayList<>();
        initialiseCons();
        genMsgTrackCaps();
    }

    //Copy Constructor
    SubKey(SubKey copy){
        this.cipherText = copy.cipherText;
        this.oriCipherText = copy.oriCipherText;
        this.msgAsList = new ArrayList<>(copy.getMsgAsList());
        this.key = copy.getKey();
        this.addedLetters = new ArrayList<>(copy.getAddedLetters());
        this.replacedLetters = new ArrayList<>(copy.getReplacedLetters());
        this.lettersToCons = new ArrayList<>(copy.getLettersToCons());
        this.DELIMITER = copy.DELIMITER;

    }

    private ArrayList<String> textToList(String text){

        String[] temp = text.split(DELIMITER);
        msgAsList = new ArrayList<>(temp.length);
        msgAsList.addAll(Arrays.asList(temp));

        return msgAsList;
    }

    /**
     * Merely initialises {@link #lettersToCons}'s individual {@link ArrayList}s
     */
    private void initialiseCons(){
        lettersToCons = new ArrayList<>();
        for(int i = 0; i<26; i++) lettersToCons.add(new ArrayList<>());
    }

    /**
     * Creates generic A - Z key
     */
    private void initialiseKey(){

        key = "";
        StringBuilder keyBuilder = new StringBuilder(key);

        for(int i = 65; i<91; i++){
            keyBuilder.append((char) (i));
        }
        key = keyBuilder.toString();
    }


    /**
     * Updates {@link #lettersToCons}
     */
    void updateLettersToCons(ArrayList<Character> letters, int pos){

        ArrayList<Character> lettersRecorded = lettersToCons.get(pos);
        for (Character letter : letters) {

            char thisLetter = letter;

            if (!lettersRecorded.contains(thisLetter)) lettersRecorded.add(thisLetter);
        }
        lettersToCons.set(pos, lettersRecorded);
    }

    /**
     * Updates {@link #key} with the new letters replacing the old ones, and vice versa if mistake=true
     * @param oldLetters letters to replace
     * @param newLetters letters to replace with
     * @param mistake In case the decryption process detects a mistake
     */
    void updateKey(String oldLetters, String newLetters, boolean mistake){

        String oLs;
        String nLs;

        if(!mistake){
            nLs = newLetters.toLowerCase();
            oLs = oldLetters.toUpperCase();

        }else{
            nLs = newLetters.toUpperCase();
            oLs = oldLetters.toLowerCase();
        }

        for(int i=0; i<oldLetters.length(); i++){

            char nL = nLs.charAt(i);
            char oL = oLs.charAt(i);


            key = key.replace(oL, nL);

            updateAddedLetters(nL,mistake);
            updateReplacedLetters(oL);
            updateCipherText(nL,oL);
        }

        //System.out.println("Key after update: " + key);

    }

    /**
     * Updates {@link #addedLetters} depending on if a mistake occurred during decryption
     * @param letter Letter to be added or removed from {@link #addedLetters}
     * @param mistake If true it removes letter, if false it adds letter
     */
    private void updateAddedLetters(char letter, boolean mistake){

        if(!mistake){

            //check if letter isn't already recorded
            if(!addedLetters.contains(letter)){

                addedLetters.add(letter);

            }else{

                System.out.println("Attempted add " + letter + "  failed because it is already recorded");
            }
        }else{

            if(addedLetters.contains(letter)){

                addedLetters.remove(letter);

            }else{

                System.out.println("Attempted to remove " + letter + " that wasn't recorded");
            }
        }
    }

    private void updateReplacedLetters(char letter){
        replacedLetters.add(letter);
    }

    /**
     * Updates {@link #cipherText} which includes the {@link #msgAsList}
     */
    private void updateCipherText(char newLetter, char oldLetter){

        cipherText = cipherText.replace(oldLetter,newLetter);
        msgAsList = textToList(cipherText);
    }

    public ArrayList<Character> getReplacedLetters() {
        return replacedLetters;
    }

    public String getKey() {
        return key;
    }

    public char[] getKeyAsArray() {
        char[] keyArr = new char[26];

        for(int i = 0; i<keyArr.length; i++){
            keyArr[i] = key.charAt(i);
        }
        return keyArr;
    }

    public ArrayList<Character> getAddedLetters() {
        return addedLetters;
    }

    public ArrayList<String> getMsgAsList() {
        return msgAsList;
    }

    public ArrayList<ArrayList<Character>> getLettersToCons() {
        return lettersToCons;
    }

    public String getCipherText() {
        return cipherText;
    }

    private String ListToText(ArrayList<String> list){
        String[] temp = (String[]) list.toArray();
        return String.join(" ", temp);
    }
    /**
     * Makes a list that records the position of each capital letter in the original text
     */
    private void genMsgTrackCaps(){

        caps = new LinkedList<>();
        char letter;

        for(int i=0; i<oriCipherText.length(); i++){
            letter = oriCipherText.charAt(i);
            if(Character.isLetter(letter) && Character.isUpperCase(letter)){

                caps.add(true);

            }else{
                caps.add(false);
            }
        }
    }
    public LinkedList<Boolean> getCaps() {
        return caps;
    }

    public String getOriCipherText() {
        return oriCipherText;
    }
}
