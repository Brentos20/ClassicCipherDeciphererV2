package model.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that contains all the data about the message that can be derived, data is altered when appropriate
 */
public class MsgStats {

    private String DELIMITER = " ";
    private ArrayList<String> msgAsList;
    private String msg;
    private char[] key;

    private LinkedHashMap<Character, Double> letterFreq;
    private LinkedHashMap<String, Double> oneLetterWords;
    private LinkedHashMap<String, Double> twoLetterWords;
    private LinkedHashMap<String, Double> threeLetterWords;
    private LinkedHashMap<String, Double> fourLetterWords;

    private LinkedHashMap<String, Double>  doubleLetters;
    private LinkedHashMap<Character, Double> initialLetters;
    private LinkedHashMap<Character, Double> lastLetters;

    private ArrayList<String> alteredWords;
    private int charCount;



    public MsgStats(String cipherText, char[] key){

        this.key = key;
        msg = cipherText;
        msgAsList = textToList(msg);
        genMsgStats();
        alteredWords = new ArrayList<>();
    }

    /**
     * Copy constructor
     * @param copy
     */
    public MsgStats(MsgStats copy){
        this.key = copy.key;
        this.msg = copy.getMsg();
        this.msgAsList = textToList(msg);
        this.oneLetterWords = new LinkedHashMap<>(copy.getOneLetterWords());
        this.twoLetterWords = new LinkedHashMap<>(copy.getTwoLetterWords());
        this.threeLetterWords = new LinkedHashMap<>(copy.getThreeLetterWords());
        this.fourLetterWords = new LinkedHashMap<>(copy.getFourLetterWords());
        this.alteredWords = new ArrayList<>(copy.getAlteredWords());
    }

    /**
     * Updates every occurrence of the first char with the second char in all appropriate data structures
     * @param oldLetter
     * @param newLetter
     */
    public void UpdateWordLists(char oldLetter, char newLetter) {


        LinkedHashMap<String, Double> oneLetterWords = getOneLetterWords();
        String[] oneLetters = oneLetterWords.keySet().toArray(new String[0]);

        LinkedHashMap<String, Double> twoLetterWords = getTwoLetterWords();
        String[] twoLetters = twoLetterWords.keySet().toArray(new String[0]);

        LinkedHashMap<String, Double> threeLetterWords = getThreeLetterWords();
        String[] threeLetters = threeLetterWords.keySet().toArray(new String[0]);

        LinkedHashMap<String, Double> fourLetterWords = getFourLetterWords();
        String[] fourLetters = fourLetterWords.keySet().toArray(new String[0]);

        //Set the length to be as long as the longest list
        int length = Math.max(oneLetterWords.size(), Math.max(twoLetterWords.size(), Math.max(threeLetterWords.size(), fourLetterWords.size())));
        ArrayList<String> altWordList = new ArrayList<>();

        //Updates class lists and populates altWordList
        for (int i = 0; i < length; i++) {
            String thisWord;

            if (i < oneLetterWords.size() && !(oneLetters[i].indexOf(oldLetter) == -1)) {

                thisWord = oneLetters[i];
                double value = oneLetterWords.get(thisWord);
                String newWord = thisWord.replace(oldLetter, newLetter);

                altWordList.add(newWord);
                oneLetterWords.remove(thisWord);
                oneLetterWords.put(newWord, value);

            }

            if (i < twoLetterWords.size() && !(twoLetters[i].indexOf(oldLetter) == -1)) {

                thisWord = twoLetters[i];
                double value = twoLetterWords.get(thisWord);
                String newWord = thisWord.replace(oldLetter, newLetter);

                altWordList.add(newWord);
                twoLetterWords.remove(thisWord);
                twoLetterWords.put(newWord, value);

            }

            if (i < threeLetterWords.size() && !(threeLetters[i].indexOf(oldLetter) == -1)) {

                thisWord = threeLetters[i];
                double value = threeLetterWords.get(thisWord);
                String newWord = thisWord.replace(oldLetter, newLetter);

                altWordList.add(newWord);
                threeLetterWords.remove(thisWord);
                threeLetterWords.put(newWord, value);

            }

            if (i < fourLetterWords.size() && !(fourLetters[i].indexOf(oldLetter) == -1)) {

                thisWord = fourLetters[i];
                double value = fourLetterWords.get(thisWord);
                String newWord = thisWord.replace(oldLetter, newLetter);

                altWordList.add(newWord);
                fourLetterWords.remove(thisWord);
                fourLetterWords.put(newWord, value);
            }
        }
        //Use sortByValue on all word lists
        setOneLetterWords((LinkedHashMap<String, Double>) sortByValue(oneLetterWords));
        setTwoLetterWords((LinkedHashMap<String, Double>) sortByValue(twoLetterWords));
        setThreeLetterWords((LinkedHashMap<String, Double>) sortByValue(threeLetterWords));
        setFourLetterWords((LinkedHashMap<String, Double>) sortByValue(fourLetterWords));

        updateAlteredWords(altWordList);
    }

    public ArrayList<String> textToList(String text){

        String[] temp = text.split(DELIMITER);
        msgAsList = new ArrayList<>(temp.length);
        msgAsList.addAll(Arrays.asList(temp));

        return msgAsList;
    }

    private String ListToText(ArrayList<String> list){
       String[] temp = (String[]) list.toArray();
       return String.join(DELIMITER, temp);
    }

    /**
     * Called in constructor, populates the linked hashmaps with appropriate data from the msg
     */
    private void genMsgStats() {

        //Record letter statistics
        HashMap<Character, Double> letterFreq = new HashMap<>(26);
        HashMap<String, Double> doubleLetters = new HashMap<>(26);
        HashMap<Character, Double> initialLetters = new HashMap<>(26);
        HashMap<Character, Double> lastLetters = new HashMap<>(26);

        double letterCount = 0.0;
        double iniLetterCount = 0.0;
        double lastLetterCount = 0.0;
        double doubleLetterCount = 0.0;

        //Record word statistics
        HashMap<String, Double> oneLetterWords = new HashMap<>();
        HashMap<String, Double> twoLetterWords = new HashMap<>();
        HashMap<String, Double> threeLetterWords = new HashMap<>();
        HashMap<String, Double> fourLetterWords = new HashMap<>();

        double oneLetterCount = 0.0;
        double twoLetterCount = 0.0;
        double threeLetterCount = 0.0;
        double fourLetterCount = 0.0;

        for (int i = 0; i<key.length; i++) {

            char letterHere = key[i];

            letterFreq.put(letterHere, 0.0);
            initialLetters.put(letterHere, 0.0);
            lastLetters.put(letterHere, 0.0);
            doubleLetters.put(String.valueOf(letterHere) + String.valueOf(letterHere), 0.0);
        }

        for(String word: msgAsList){

            boolean initialDone = false;
            int wordLength = word.length();

            for(int i = 0; i<wordLength; i++){

                char letterAtLoc = word.charAt(i);

                if(Character.isLetter(letterAtLoc)){

                    //First add to letter freq
                    letterCount++;

                    Double charFreq = ((letterFreq.get(letterAtLoc) + 1));
                    letterFreq.replace(letterAtLoc,charFreq);

                    //Make check to add to first letters
                    if((i==0) && wordLength!=1 || !initialDone && wordLength!=1){

                        iniLetterCount++;

                        charFreq = ((initialLetters.get(letterAtLoc) + 1));
                        initialLetters.replace(letterAtLoc,charFreq);

                        initialDone = true;
                    }
                }
                //Make check to add to last letters
                if(wordLength!=1 && (i==wordLength-1)){

                    if(Character.isLetter(letterAtLoc)){

                        lastLetterCount++;

                        Double charFreq = ((lastLetters.get(letterAtLoc) + 1));
                        lastLetters.replace(letterAtLoc,charFreq);

                    }else{

                        for(int j = wordLength-1; j>0; j--){
                            char letterHere = word.charAt(j);

                            if(Character.isLetter(letterHere)){

                                lastLetterCount++;

                                Double charFreq = ((lastLetters.get(letterHere) + 1));
                                lastLetters.replace(letterHere,charFreq);

                                break;
                            }
                        }
                    }
                }else{
                    //Because the only limit to checking for double letters is if there exists an adjacent letter
                    //Check for double letters here
                    if(wordLength!=1 && Character.isLetter(letterAtLoc) && letterAtLoc == word.charAt(i+1)){

                        String thisDoubleLetter = String.valueOf(letterAtLoc) + String.valueOf(letterAtLoc);
                        doubleLetterCount++;

                        Double charFreq = ((doubleLetters.get(thisDoubleLetter) + 1));
                        doubleLetters.replace(thisDoubleLetter,charFreq);
                    }
                }
            }

            switch(wordLength){

                case 1 :

                    if(Character.isLetter(word.charAt(0))){

                        oneLetterCount++;
                        if(!oneLetterWords.containsKey(word)){
                            oneLetterWords.put(word, 1.0);
                        }else{
                            oneLetterWords.replace(word, oneLetterWords.get(word) + 1);
                        }
                    }

                    break;

                case 2 :

                    twoLetterCount++;
                    if(!twoLetterWords.containsKey(word)){
                        twoLetterWords.put(word, 1.0);
                    }else{
                        twoLetterWords.replace(word, twoLetterWords.get(word) + 1);
                    }
                    break;

                case 3 :

                    threeLetterCount++;
                    if(!threeLetterWords.containsKey(word)){
                        threeLetterWords.put(word, 1.0);
                    }else{
                        threeLetterWords.replace(word, threeLetterWords.get(word) + 1);
                    }
                    break;

                case 4 :

                    fourLetterCount++;
                    if(!fourLetterWords.containsKey(word)){
                        fourLetterWords.put(word, 1.0);
                    }else{
                        fourLetterWords.replace(word, fourLetterWords.get(word) + 1);
                    }
                    break;
            }
        }

        for(int i=0; i<letterFreq.size(); i++){

            char letter = key[i];
            String dLetter = String.valueOf(letter) + String.valueOf(letter);


            //Turn freq lists to percentages
            letterFreq.replace(letter,(letterFreq.get(letter) / letterCount));

            initialLetters.replace(letter,(initialLetters.get(letter) / iniLetterCount));

            doubleLetters.replace(dLetter,doubleLetters.get(dLetter) / doubleLetterCount);

            lastLetters.replace(letter, lastLetters.get(letter) / lastLetterCount);


            //Remove useless data of letters with no frequency
            if(initialLetters.get(letter) == 0){
                initialLetters.remove(letter);
            }
            if(lastLetters.get(letter) == 0){
                lastLetters.remove(letter);
            }
            if(doubleLetters.get(dLetter) == 0){
                doubleLetters.remove(dLetter);
            }
        }

        Set<String> keySet = oneLetterWords.keySet();
        for(String word: keySet){

            oneLetterWords.replace(word, oneLetterWords.get(word)/oneLetterCount);
        }

        keySet = twoLetterWords.keySet();
        for(String word: keySet){

            twoLetterWords.replace(word, twoLetterWords.get(word)/twoLetterCount);
        }

        keySet = threeLetterWords.keySet();
        for(String word: keySet){

            threeLetterWords.replace(word, threeLetterWords.get(word)/threeLetterCount);
        }

        keySet = fourLetterWords.keySet();
        for(String word: keySet){

            fourLetterWords.replace(word, fourLetterWords.get(word)/fourLetterCount);
        }

        //sorts all maps
        this.letterFreq = (LinkedHashMap<Character, Double>) sortByValue(letterFreq);
        this.initialLetters = (LinkedHashMap<Character, Double>) sortByValue(initialLetters);
        this.lastLetters = (LinkedHashMap<Character, Double>) sortByValue(lastLetters);
        this.doubleLetters = (LinkedHashMap<String, Double>) sortByValue(doubleLetters);
        this.oneLetterWords = (LinkedHashMap<String, Double>) sortByValue(oneLetterWords);
        this.twoLetterWords = (LinkedHashMap<String, Double>) sortByValue(twoLetterWords);
        this.threeLetterWords = (LinkedHashMap<String, Double>) sortByValue(threeLetterWords);
        this.fourLetterWords = (LinkedHashMap<String, Double>) sortByValue(fourLetterWords);

        charCount = (int) letterCount;
    }

    public ArrayList<String> getMsgAsList() {
        return msgAsList;
    }

    public String getMsg() {
        return msg;
    }

    public LinkedHashMap<Character, Double> getLetterFreq() {
        return letterFreq;
    }

    public LinkedHashMap<String, Double> getDoubleLetters() {
        return doubleLetters;
    }

    public LinkedHashMap<Character, Double> getInitialLetters() {
        return initialLetters;
    }

    public LinkedHashMap<Character, Double> getLastLetters() {
        return lastLetters;
    }

    public LinkedHashMap<String, Double> getOneLetterWords() {
        return oneLetterWords;
    }

    public LinkedHashMap<String, Double> getTwoLetterWords() {
        return twoLetterWords;
    }

    public LinkedHashMap<String, Double> getThreeLetterWords() {
        return threeLetterWords;
    }

    public LinkedHashMap<String, Double> getFourLetterWords() {
        return fourLetterWords;
    }

    public void setOneLetterWords(LinkedHashMap<String, Double> oneLetterWords) {
        this.oneLetterWords = oneLetterWords;
    }

    public void setTwoLetterWords(LinkedHashMap<String, Double> twoLetterWords) {
        this.twoLetterWords = twoLetterWords;
    }

    public void setThreeLetterWords(LinkedHashMap<String, Double> threeLetterWords) {
        this.threeLetterWords = threeLetterWords;
    }

    public void setFourLetterWords(LinkedHashMap<String, Double> fourLetterWords) {
        this.fourLetterWords = fourLetterWords;
    }

    //Method used to simplify the use of the map.entry method for ordering maps
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public ArrayList<String> getAlteredWords() {
        return alteredWords;
    }

    public void updateAlteredWords(ArrayList<String> alteredWords) {

        this.alteredWords = alteredWords;
    }

    public int getCharCount() {
        return charCount;
    }
}
