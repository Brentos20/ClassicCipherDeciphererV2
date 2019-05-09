package model.util;

import model.ciphers.SubKey;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @project ClassicalCipherDecipherer
 */
public abstract class SubSearchAlg {

    protected boolean trim;
    protected String cipherText;
    protected char[] key;
    protected String keyAsString;
    protected MsgStats msgStats;
    protected NGrams nGrams;
    protected ArrayList<ArrayList<Character>> searchSpace;
    protected boolean useSearchSpace;
    protected LinkedList<Boolean> caps;
    protected ArrayList<Character> msgFreqList;
    protected int mistakeLoc = 0;
    protected ArrayList<Character> alphabet = new ArrayList<>(
            Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));


    SubSearchAlg(SubKey key, MsgStats msgStats, NGrams nGrams, String keyClue, boolean useSearchSpace, boolean trim){
        this.useSearchSpace = useSearchSpace;
        this.cipherText = key.getOriCipherText();
        this.key = key.getKeyAsArray();
        checkKey();
        this.keyAsString = key.getKey();
        System.out.println(keyAsString);
        this.msgStats = msgStats;
        this.nGrams = nGrams;
        this.caps = key.getCaps();
        this.msgFreqList = new ArrayList<>(msgStats.getLetterFreq().keySet());
        if(!useSearchSpace){
            genFullSearchSpace();
            this.mistakeLoc = useKeyclue(keyClue);
        }
        this.trim = trim;
        System.out.println(searchSpace);

    }

    private void checkKey() {
        ArrayList<Character> alpha = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
        ArrayList<Integer> missSpots = new ArrayList<>(26);
        char thisLetter;
        Random rand = new Random();
        int alphaSpot;

        for (int i=0; i<26; i++) {
            if(alpha.contains(key[i])){
                alpha.remove(alpha.indexOf(key[i]));
            }else{
                missSpots.add(i);
            }
        }
        for (Integer missSpot : missSpots) {
            alphaSpot = rand.nextInt(alpha.size());
            key[missSpot] = alpha.get(alphaSpot);
            alpha.remove(alphaSpot);
        }
    }

    SubSearchAlg(SubKey key, MsgStats msgStats, NGrams nGrams, String keyClue, int mistakeLoc, boolean trim){

        this(key,msgStats,nGrams,keyClue, true, trim);
        searchSpace = key.getLettersToCons();
        useSearchSpace = true;
        fillMissingSpots();
        this.mistakeLoc = useKeyclue(keyClue);
        if(mistakeLoc>this.mistakeLoc) this.mistakeLoc = mistakeLoc;
        System.out.println(searchSpace);
    }

    private void fillMissingSpots() {

        ArrayList<Character> listHere;

        for (int i=0; i<26; i++) {
            listHere = searchSpace.get(i);
            if(listHere.isEmpty()){
                searchSpace.set(i,new ArrayList<>((Collection<? extends Character>) alphabet.clone()));
            }
        }
        System.out.println(searchSpace);
    }

    protected void genFullSearchSpace() {
        searchSpace = new ArrayList<>();
        for (int i = 0; i <26; i++) {
            searchSpace.add(new ArrayList<>((Collection<? extends Character>) alphabet.clone()));
        }
    }

    protected int useKeyclue(String keyClue) {

        char newChar;
        char thisChar;
        int posOfChar;
        for(int i = 0; i<keyClue.length(); i++){
            thisChar = msgFreqList.get(i);
            newChar = Character.toLowerCase(keyClue.charAt(i));
            posOfChar = thisChar-65;
            searchSpace.set(posOfChar, new ArrayList<>());
            searchSpace.get(posOfChar).add(newChar);
            for (int j=0; j<searchSpace.size(); j++) {
                if(searchSpace.get(j).contains(newChar) && j!=posOfChar) {
                    searchSpace.get(j).remove(searchSpace.get(j).indexOf(newChar));
                }
                if(key[j]==newChar && j!=posOfChar){
                    key[j] = searchSpace.get(j).get(0);
                }
            }
            if(key[posOfChar]!=newChar){
                key[posOfChar] = newChar;
            }
        }
        return keyClue.length();
    }

    protected Double getFitnessOfKey(char[] key, int n) {
        String oriCipherText = decryptWithKey(key).toUpperCase();
        return nGrams.getFitness(nGrams.getNGrams(oriCipherText,n),n);
    }
    protected String decryptWithKey(char[] key) {

        StringBuilder decryptedMsg = new StringBuilder();
        char letter;
        for(int i = 0; i<cipherText.length(); i++){
            letter = cipherText.charAt(i);
            if(Character.isLetter(letter)){

                if(Character.isUpperCase(letter)){

                    decryptedMsg.append(Character.toUpperCase(key[letter-65]));
                }else{
                    decryptedMsg.append(Character.toLowerCase(key[letter - 97]));
                }
            }else{
                decryptedMsg.append(letter);
            }
        }
        return decryptedMsg.toString();
    }
    protected char[] updateKey(char[] key, char oldChar, char newChar) {

        oldChar = Character.toLowerCase(oldChar);
        newChar = Character.toLowerCase(newChar);
        boolean replacedNew = false;
        boolean replacedOld = false;
        char thisChar;
        char[] newKey = new char[26];

        for (int i = 0; i<26; i++) {
            if(replacedNew&&replacedOld){
                while(i<26){
                    newKey[i] = key[i];
                    i++;
                }
                break;
            }
            thisChar = key[i];

            if(thisChar==oldChar && !replacedOld){
                newKey[i] = newChar;
                replacedOld = true;
            }else{
                if(thisChar==newChar && !replacedNew) {
                    newKey[i] = oldChar;
                    replacedNew = true;
                }else{
                    newKey[i] = thisChar;
                }
            }
        }
        return newKey;
    }
    protected String genResult(String decipheredText) {

        StringBuilder resultMsg = new StringBuilder();
        int length = decipheredText.length();
        for(int i = 0; i<length; i++){
            char thisLetter = decipheredText.charAt(i);

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
        double bestFitness = getStringFitness(cipherText,4);
        double thisFitnes;

        boolean improved;
        do{
            improved = false;
            for (Character charOne : alphabet) {
                for (Character charTwo : alphabet) {
                    temp = swapLettersInString(charOne,charTwo,cipherText);
                    thisFitnes = getStringFitness(temp,4);
                    if(thisFitnes>bestFitness){
                        bestFitness = thisFitnes;
                        to_return = temp;
                        improved=true;
                    }
                }
            }

        }while(improved);

        return to_return;
    }
    /**
     * Takes 10 words with the lowest fitness in the text and replace each letter with every other letter in alphabet
     * until a higher fitness is found
     */
    protected String fixPosErrors(String cipherText) {
        ArrayList<String> textAsList = msgStats.textToList(cipherText);
        ArrayList<String> wordsToIter = getRandWords(getlastTen(textAsList), cipherText);
        //Keeps track of tried letters in order to not waste time getting the same result
        ArrayList<Character> triedLetters = new ArrayList<>(26);

        char[] key = Arrays.copyOf(this.key, 26);
        char thisLetter;
        char otherLetter;
        double bestFitness = getStringFitness(cipherText,3);
        String tempText;
        String decipheredText = cipherText;
        for (String word: wordsToIter) {

            for(int i = 0; i<word.length(); i++){
                thisLetter = word.charAt(i);

                if(!triedLetters.contains(thisLetter) && Character.isLetter(thisLetter)){
                    for(int j = 0; j < 26; j++){
                        otherLetter = key[j];

                        tempText = swapLettersInString(thisLetter,otherLetter,cipherText);
                        double tempFitness = getStringFitness(tempText,3);
                        if(tempFitness > bestFitness){
                            decipheredText = tempText;
                            bestFitness = tempFitness;
                            key = updateKey(key,thisLetter,otherLetter);
                        }
                    }
                    triedLetters.add(thisLetter);
                }
            }
        }
        return decipheredText;
    }
    /**
     * Finds the 10 words with the lowest fitness ratings
     * @return An {@link ArrayList} of words with the lowest fitness
     * @param textAsList
     */
    private ArrayList<String> getlastTen(ArrayList<String> textAsList) {
        LinkedHashMap<String,Double> wordsAndFitness = new LinkedHashMap<>();

        for (String word: textAsList) {
            wordsAndFitness.put(word,getStringFitness(word,3));
        }

        wordsAndFitness = (LinkedHashMap<String, Double>) sortByValue(wordsAndFitness);
        ArrayList<String> keySet = new ArrayList<>(wordsAndFitness.keySet());
        ArrayList<String> lastTen = new ArrayList<>(10);

        for(int i = 0; i<10; i++){
            lastTen.add(keySet.get(i));
        }

        return lastTen;
    }

    private ArrayList<String> getRandWords(ArrayList<String> randWords, String cipherText) {

        Random random = new Random();
        ArrayList<String> textAsList = msgStats.textToList(cipherText);
        int randLoc;
        int range = textAsList.size();
        String randWord;

        for(int i=0; i<10; i++){
            do{
                randLoc = random.nextInt(range);
                randWord = textAsList.get(randLoc);
            }while(randWords.contains(randWord));
            randWords.add(randWord);

        }
        return randWords;
    }

    protected double getStringFitness(String text,int  n){

        return nGrams.getFitness(nGrams.getNGrams(text, n), n);
    }

    protected String swapLettersInString(char letter1, char letter2, String text){
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
    protected static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
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

    public char[] getKey() {
        return key;
    }
}
