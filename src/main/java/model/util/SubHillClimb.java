package model.util;

import model.ciphers.SubKey;

import java.util.*;
import java.util.stream.Collectors;

public class SubHillClimb {

    private String key;
    private String cipherText;
    private ArrayList<ArrayList<Character>> lettersToCons;
    private MsgStats msgStats;
    private WorldLangStats worldLangStats;
    private NGrams nGrams;
    private ArrayList<String> deadKeys;
    private LinkedList<Boolean> caps;
    private double bestFitness;
    private ArrayList<Character> msgFreqList;
    private int mistakeLoc;
    private int numbOfLoops = 0;
    private int numbOfRestarts = 0;
    private boolean useTrim;
    private ArrayList<Character> alphabet = new ArrayList<>(
            Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));



    public SubHillClimb(SubKey key, MsgStats msgStats, WorldLangStats worldLangStats, NGrams nGrams, LinkedList<Boolean> caps, ArrayList<Character> msgFreqList, int mistakeLoc){

        this.key = key.getKey();
        this.cipherText = key.getCipherText();
        this.lettersToCons = key.getLettersToCons();
        this.msgStats = msgStats;
        this.worldLangStats = worldLangStats;
        this.nGrams = nGrams;
        this.caps = caps;
        this.msgFreqList = msgFreqList;
        this.mistakeLoc = mistakeLoc;
        deadKeys = new ArrayList<>();
    }

    private int useKeyclue(String keyClue) {

        char newChar;
        char thisChar;
        int posOfChar;
        for(int i = 0; i<keyClue.length(); i++){
            thisChar = msgFreqList.get(i);
            newChar = Character.toLowerCase(keyClue.charAt(i));
            posOfChar = thisChar-65;
            lettersToCons.set(posOfChar, new ArrayList<>());
            lettersToCons.get(posOfChar).add(newChar);

            swapLettersInString(thisChar,newChar,cipherText);
            swapLettersInString(thisChar,newChar,key);
            for (int j=0; j<lettersToCons.size(); j++) {
                if(lettersToCons.get(j).contains(newChar) && j!=posOfChar) {
                    lettersToCons.get(j).remove(lettersToCons.get(j).indexOf(newChar));
                }
            }
        }
        return keyClue.length();
    }

    SubHillClimb(String cipherText){
        this.cipherText = cipherText;
        deadKeys = new ArrayList<>();

    }

    public void setParams(int numbOfLoops, int numbOfRestarts, boolean useTrim, boolean useCustSearchSpace, String keyClue){
        this.numbOfLoops = numbOfLoops;
        this.numbOfRestarts = numbOfRestarts;
        this.useTrim = useTrim;
        if(!useCustSearchSpace) genFullSearchSpace();
        if(keyClue.length()>0) useKeyclue(keyClue);

    }

    private void genFullSearchSpace() {
        this.lettersToCons.clear();
        this.lettersToCons = new ArrayList<>();
        for (int i = 0; i <26; i++) {
            this.lettersToCons.add(new ArrayList<>((Collection<? extends Character>) alphabet.clone()));
        }
    }

    public String decrypt(){

        for(int i=0; i<numbOfRestarts; i++)controlledHillClimb();

        if(useTrim) cipherText = trim(cipherText);

        return GenResult(cipherText);
    }

    private void findLastMistakes() {
        ArrayList<String> lastTen = getlastTen();
        ArrayList<Character> triedLetters = new ArrayList<>(26);


        for (String word: lastTen) {

            for(int i = 0; i<word.length(); i++){
                Character thisLetter = word.charAt(i);

                if(!triedLetters.contains(thisLetter) && Character.isLetter(thisLetter)){
                    for(int j = 0; j < key.length(); j++){
                        Character otherLetter = key.charAt(j);

                        String tempText = swapLettersInString(thisLetter,otherLetter,cipherText);
                        double tempFitness = getStringFitness(tempText);
                        if(tempFitness > bestFitness){
                            cipherText = tempText;
                            bestFitness = tempFitness;
                            key = swapLettersInString(thisLetter,otherLetter,key);
                        }
                    }
                    triedLetters.add(thisLetter);
                }
            }
        }
    }

    private ArrayList<String> getlastTen() {
        LinkedHashMap<String,Double> wordsAndFitness = new LinkedHashMap<>();

        ArrayList<String> textAsList = msgStats.textToList(cipherText);

        for (String word: textAsList) {
            wordsAndFitness.put(word,getStringFitness(word));
        }

        wordsAndFitness = (LinkedHashMap<String, Double>) sortByValue(wordsAndFitness);
        ArrayList<String> keySet = new ArrayList<>(wordsAndFitness.keySet());
        ArrayList<String> lastTen = new ArrayList<>(10);

        for(int i = 0; i<15; i++){
            lastTen.add(keySet.get(i));
        }

        return lastTen;
    }

    private void controlledHillClimb() {
        String parentKey = key;
        String parentText = cipherText;
        double parentFitness = getStringFitness(parentText);
        Random random = new Random();


        int count = numbOfLoops;
        while(count>0){

            //Pick random location in msgFreqKeyset
            int randomLocInList = 0;
            do{
                randomLocInList = random.nextInt(26);
            }while(randomLocInList<mistakeLoc);

            //Find letter to replace in msgFreqList
            char oldLetter = Character.toLowerCase(msgFreqList.get(randomLocInList));

            //Find location of letter in key which is the same for letterToCons
            int randomLoc = parentKey.indexOf(oldLetter);

            //Find the location of the corresponding letter in letterToCons
            int oldLettLocInList = lettersToCons.get(randomLoc).indexOf(oldLetter);
            int listSize = lettersToCons.get(randomLoc).size();

            //Check if the letters in the list can be altered
            if(listSize> 1){

                int locOfNewLetter;
                do{
                    //Randomly pick a location in that list that isn't where the old letter is located
                    locOfNewLetter = random.nextInt(listSize);

                }while(locOfNewLetter==oldLettLocInList );

                //Grab new letter in location chosen
                char newLetter = lettersToCons.get(randomLoc).get(locOfNewLetter);

                String tempKey = swapLettersInString(oldLetter, newLetter, parentKey);
                //Check if this key has already been recorded as dead before going any further
                if(!deadKeys.contains(tempKey)){

                    String tempText = swapLettersInString(oldLetter,newLetter,parentText);
                    double tempFitness = getStringFitness(tempText);

                    //Check if thisFitness is better than parentFitness, switch if so
                    if(tempFitness>parentFitness){
                        parentKey = tempKey;
                        parentText = tempText;
                        parentFitness = tempFitness;

                    }else{
                        deadKeys.add(tempKey);
                        count--;
                    }
                }else{
                    count--;
                }
            }
        }
        key = parentKey;
        cipherText = parentText;
        bestFitness = parentFitness;
    }

    private double getWordFitness(String word){

        return nGrams.getFitness(nGrams.getNGrams(word,2),2);

    }

    private double getStringFitness(String text){

        return nGrams.getFitness(nGrams.getNGrams(text, 4), 4);
    }

    private String switchLettersInString(char letter1, char letter2, String text){
        String newString;
        newString = text.replace(letter1, '*');
        newString = newString.replace(letter2, letter1);
        newString = newString.replace('*', letter2);

        return newString;
    }

    private String GenResult(String cipherText) {

        StringBuffer resultMsg = new StringBuffer();
        int length = cipherText.length();
        for(int i = 0; i<length; i++){
            char thisLetter = cipherText.charAt(i);

            if(Character.isLetter(thisLetter) && caps.get(i)){
                resultMsg.append(Character.toUpperCase(thisLetter));
            }else{
                resultMsg.append(thisLetter);
            }
        }
        return resultMsg.toString();
    }
    protected String trim(String cipherText){

        String temp;
        String to_return = cipherText;
        double bestFitness = getStringFitness(cipherText);
        double thisFitnes;
        char charOne;

        char[] tempKey  = new char[26];
        for (int i = 0; i<26; i++) {
            tempKey[i] = alphabet.get(i);
        }

        MsgStats stats = new MsgStats(cipherText,tempKey);
        ArrayList<Character> lettFreq = new ArrayList<>(stats.getLetterFreq().keySet());
        boolean improved;
        do{
            improved = false;
            for (int i=25; i>=0; i--) {

                charOne = lettFreq.get(i);
                for (Character charTwo : alphabet) {
                    temp = swapLettersInString(charOne,charTwo,cipherText);
                    thisFitnes = getStringFitness(temp);
                    if(thisFitnes>bestFitness){
                        bestFitness = thisFitnes;
                        to_return = temp;
                        Collections.swap(lettFreq,i,lettFreq.indexOf(charTwo));
                        improved=true;
                    }
                }
            }

        }while(improved);

        this.bestFitness = bestFitness;
        return to_return;
    }

    private String swapLettersInString(char letter1, char letter2, String text){
        char[] chars = text.toCharArray();
        StringBuilder newText = new StringBuilder();
        char thisLetter;
        letter1 = Character.toLowerCase(letter1);
        letter2 = Character.toLowerCase(letter2);

        for (char letter : chars) {
            thisLetter = Character.toLowerCase(letter);
            if (Character.isLetter(thisLetter)) {
                if (thisLetter == letter1) {
                    newText.append(letter2);
                } else {
                    if (thisLetter == letter2) {
                        newText.append(letter1);
                    } else {
                        newText.append(thisLetter);
                    }
                }
            } else {
                newText.append(thisLetter);
            }
        }
        return newText.toString();
    }

    //Method used to simplify the use of the map.entry method for ordering maps
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public String getKey() {
        return key;
    }
}
