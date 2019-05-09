package model.util;

import model.ciphers.SubKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * @project ClassicalCipherDecipherer
 */
public class SimAnneal extends SubSearchAlg{
    private double startTemp;
    private double coolRate;
    private int maxIter;
    private String bestText;

    public SimAnneal(SubKey key, MsgStats msgStats, NGrams nGrams, String keyClue, boolean useSearchSpace, boolean trim){
        super(key,msgStats,nGrams,keyClue,useSearchSpace,trim);
        System.out.println(searchSpace);
    }
    public SimAnneal(SubKey key, MsgStats msgStats, NGrams nGrams, String keyClue, int mistakeLoc, boolean trim){
        super(key,msgStats,nGrams,keyClue, mistakeLoc, trim);
    }
    public String decrypt(int startTemp, double coolRate, int maxIter, int strategy){

        this.startTemp = startTemp;
        this.coolRate = coolRate;
        this.maxIter = maxIter;

        switch (strategy){
            case 1:
                if(trim) return genResult(trim(fixPosErrors(simAnnealDecryptWithSearchSpace())));
                return genResult(simAnnealDecryptWithSearchSpace());
            case 2:
                if(trim) return genResult(trim(fixPosErrors(simAnnealDecryptViaIndWords())));
                return genResult(simAnnealDecryptViaIndWords());
            case 3:
                if(trim) return genResult(trim(fixPosErrors(simAnnealDecryptRand())));
                return genResult(simAnnealDecryptRand());
            default:
                if(trim) return genResult(trim(fixPosErrors(simAnnealDecryptWithSearchSpace())));
                return genResult(simAnnealDecryptWithSearchSpace());
        }
    }

    public String simAnnealDecryptViaIndWords(){
        String[] textAsArray = cipherText.replaceAll("[^a-z ]", "").split("\\s+");
        ArrayList<String> sizedWords = getWordsOfSize(3,textAsArray,10);
        ArrayList<String> largestWords = getLargestWords(textAsArray, 10);
        sizedWords.addAll(largestWords);

        ArrayList<char[]> usedKeys = new ArrayList<>();
        char[] bestKey = new char[26];
        char[] currKey = Arrays.copyOf(key,26);
        usedKeys.add(currKey);
        for (String sizedWord : sizedWords) {

            char[] thisKey;
            double bestFitness = Integer.MIN_VALUE;
            double currFitness = getFitnessOfKey(currKey,4);
            double thisFitness;
            double temp = startTemp;
            String wordToDecrypt = sizedWord;

            while(temp>1){
                do {
                    thisKey = (randKeyFromWord(Arrays.copyOf(currKey,26),wordToDecrypt));
                }while(usedKeys.contains(thisKey));
                usedKeys.add(thisKey);

                wordToDecrypt = decryptWordWithKey(wordToDecrypt,thisKey);
                thisFitness = getFitnessOfWord(wordToDecrypt);
                if(thisFitness>currFitness){
                    currFitness = thisFitness;
                    currKey = thisKey;

                    if(currFitness>bestFitness){
                        bestFitness = currFitness;
                        bestKey = Arrays.copyOf(currKey,26);
                    }
                }else if(Math.exp(-(currFitness-thisFitness)/temp) > Math.random()){
                    currFitness = thisFitness;
                    currKey = Arrays.copyOf(thisKey,26);
                }
                temp -= temp*coolRate;
            }
        }
        return decryptWithKey(bestKey);
    }

    private ArrayList<String> getWordsOfSize(int size, String[] textAsList, int numbOfWords) {

        ArrayList<String> sizedWords = new ArrayList<>();
        for (String word : textAsList) {
            if(word.length()==size && !sizedWords.contains(word)) sizedWords.add(word);
            if(sizedWords.size()==numbOfWords) break;
        }
        return sizedWords;
    }

    private String decryptWordWithKey(String wordToDecrypt, char[] thisKey) {
        StringBuilder decryptedMsg = new StringBuilder();
        char letter;
        for(int i = 0; i<wordToDecrypt.length(); i++){
            letter = wordToDecrypt.charAt(i);
            if(Character.isLetter(letter)){

                if(Character.isUpperCase(letter)){

                    decryptedMsg.append(thisKey[letter-65]);
                }else{
                    decryptedMsg.append(Character.toLowerCase(thisKey[letter - 97]));
                }
            }else{
                decryptedMsg.append(letter);
            }
        }
        return decryptedMsg.toString();
    }

    private double getFitnessOfWord(String wordToDecrypt) {
        int nGramLength = 4;
        if(wordToDecrypt.length()<4){
            nGramLength = wordToDecrypt.length();
        }
        return nGrams.getFitness(nGrams.getNGrams(wordToDecrypt,nGramLength),nGramLength);
    }

    private char[] randKeyFromWord(char[] currKey, String largestWord) {
        Random random = new Random();
        int locInFreq;
        int locInKey;
        char oldLetter;
        char newLetter;

        do{
            oldLetter = Character.toLowerCase(largestWord.charAt(random.nextInt(largestWord.length())));
            locInFreq = msgFreqList.indexOf(Character.toUpperCase(oldLetter));
            locInKey = keyAsString.indexOf(oldLetter);
        }while(searchSpace.get(locInKey).size()<2 || locInFreq<mistakeLoc);

        do {
            //Find possible new letter using the searchSpace
            newLetter = searchSpace.get(locInKey).get(random.nextInt(searchSpace.get(locInKey).size()));
        }while(newLetter==oldLetter);

        return updateKey(currKey,oldLetter,newLetter);
    }

    private ArrayList<String> getLargestWords(String[] textAsList, int numbOfWords) {

        ArrayList<String> largestWords = new ArrayList<>();
        String lWord;
        for (String word : textAsList) {
            if(largestWords.size()==numbOfWords){
                for (int i = 0; i<numbOfWords; i++) {
                    lWord = largestWords.get(i);
                    if(word.length()>lWord.length()){
                        largestWords.set(i,word);
                        break;
                    }
                }
            }else{
                largestWords.add(word);
            }
        }
        return largestWords;
    }

    public String simAnnealDecryptWithSearchSpace(){
        ArrayList<String> usedKeys = new ArrayList<>();
        char[] bestKey = new char[26];
        char[] currKey =  Arrays.copyOf(key,26);
        bestText = decryptWithKey(currKey);
        usedKeys.add(Arrays.toString(currKey));
        char[] thisKey;
        double currFitness = getFitnessOfKey(currKey,4);
        double bestFitness = currFitness;
        double thisFitness;
        double temp = startTemp;
        Random random = new Random();
        int deadLoops = 0;

        for (int i = 0; i < maxIter; i++) {

            while(temp>1){
                do {
                    thisKey = (randKeyThroughSearchSpace(Arrays.copyOf(currKey,26)));
                    deadLoops++;
                    if(deadLoops==1000){
                        break;
                    }
                }while(usedKeys.contains(Arrays.toString(thisKey)));
                if(deadLoops==1000) {
                    break;
                }
                deadLoops = 0;
                usedKeys.add(Arrays.toString(thisKey));

                thisFitness = getFitnessOfKey(thisKey,4);

                if(thisFitness>currFitness){
                    currFitness = thisFitness;
                    currKey = Arrays.copyOf(thisKey,26);

                    if(currFitness>bestFitness){

                        bestText = decryptWithKey(currKey);
                        bestFitness = currFitness;
                        bestKey = Arrays.copyOf(currKey,26);
                    }

                }else {

                    if(Math.exp(((thisFitness-currFitness)/temp)) > random.nextDouble()){

                        currKey = Arrays.copyOf(thisKey,26);
                    }
                }
                temp = temp - temp*coolRate;
            }
            usedKeys.clear();

        }
        return bestText;
    }

    private char[] randKeyThroughSearchSpace(char[] currKey) {

        Random random = new Random();
        int randLocInFreq;
        int randLoc;
        char oldLetter;
        char newLetter;
        char[] key = new char[26];

        int amountOfChanges = random.nextInt(5)+1;

        for(int i=0; i<amountOfChanges; i++){
            do{
                randLocInFreq = random.nextInt(26);
                randLoc = keyAsString.indexOf(Character.toLowerCase(msgFreqList.get(randLocInFreq)));
            }while(searchSpace.get(randLoc).size()<2 || randLocInFreq<mistakeLoc);

            oldLetter = keyAsString.charAt(randLoc);

            //Find possible new letter using the searchSpace
            do{
                newLetter = searchSpace.get(randLoc).get(random.nextInt(searchSpace.get(randLoc).size()));
            }while(oldLetter==newLetter);

            key = updateKey(currKey,oldLetter,newLetter);
        }
        return key;
    }

    public String simAnnealDecryptRand(){
        ArrayList<String> usedKeys = new ArrayList<>();
        char[] bestKey = new char[26];
        char[] currKey = randKey();
        usedKeys.add(Arrays.toString(currKey));
        bestText = decryptWithKey(currKey);
        char[] thisKey;
        double bestFitness = Integer.MIN_VALUE;
        double currFitness = getFitnessOfKey(currKey,4);
        double thisFitness;
        double temp = startTemp;
        Random random = new Random();
        int deadLoops = 0;

        while(temp>1){
            do {
                thisKey = randKey();
                deadLoops++;
                if(deadLoops==1000){
                    break;
                }
            }while(usedKeys.contains(Arrays.toString(thisKey)));
            if(deadLoops==1000) {
                break;
            }
            deadLoops = 0;
            usedKeys.add(Arrays.toString(thisKey));

            thisFitness = getFitnessOfKey(thisKey,4);
            if(thisFitness>currFitness){
                currFitness = thisFitness;
                currKey = thisKey;

                if(currFitness>bestFitness){
                    bestText = decryptWithKey(currKey);
                    bestFitness = currFitness;
                    bestKey = currKey;
                }
            }else {
                if(Math.exp(((thisFitness-currFitness)/temp)) > random.nextDouble()) {
                    currFitness = thisFitness;
                }
            }
            temp = temp - temp*coolRate;
        }
        return decryptWithKey(bestKey);
    }

    private char[] randKey() {
        char[] randKey = new char[26];
        ArrayList<Character> alphaCopy = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
        Random random = new Random();
        int randPos;
        for (int i = 0; i <26; i++) {
            randPos = random.nextInt(alphaCopy.size());
            randKey[i] = alphaCopy.get(randPos);
            alphaCopy.remove(randPos);
        }
        return randKey;
    }

}
