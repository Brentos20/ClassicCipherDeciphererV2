package model.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Statistics that are derived from the CSVs in resources, treated at stats that best represents written English
 * Source used google books
 */
public class WorldLangStats {

    private LinkedHashMap twoLetterWords;
    private LinkedHashMap threeLetterWords;
    private LinkedHashMap fourLetterWords;

    private LinkedHashMap<String, Double>  doubleLetters;
    private LinkedHashMap<Character, Double> initialLetters;
    private LinkedHashMap<Character, Double> lastLetters;
    private LinkedHashMap<Character, Double> worldLetterFreq;

    /**
     * Calling constructor will fully populate class's data structures
     * @param nGrams
     */
    public WorldLangStats(NGrams nGrams){

        twoLetterWords = GenWordList(nGrams.getDiGramTable());
        threeLetterWords = GenWordList(nGrams.getTriGramTable());
        fourLetterWords = GenWordList(nGrams.getQuadGramTable());
        GenDoubleLetters(nGrams);
        GenWorldLetterFreq(nGrams.getOneGramTable());
        GenInitialLetters(nGrams.getOneGramTable());
        GenLastLetters(nGrams.getOneGramTable());
    }

    /**
     * Generates the list of (possible) words for the specific map
     * @param nGramTable Holds the data to be extracted from
     * @return {@link HashMap} where the Key = word and Value = the word stats
     */
    private LinkedHashMap GenWordList(HashMap<String, ArrayList<String>> nGramTable) {


        Map<String, Double> temp = new LinkedHashMap<>();
        LinkedHashMap wordList;
        Set<String> keys = nGramTable.keySet();

        for (String key : keys) {

            temp.put(key, Double.parseDouble(nGramTable.get(key).get(2)));

        }

        wordList = (LinkedHashMap) sortByValue(temp);

        return wordList;
    }

    /**
     * Generates the {@link HashMap} that stores the letter frequency
     * @param oneGrameTable
     */
    private void GenWorldLetterFreq(HashMap<String, ArrayList<String>> oneGrameTable){

        Set<String> letters = oneGrameTable.keySet();
        HashMap WLF = new HashMap<>(26);
        for (String letter : letters) {

            Double value = Double.parseDouble(oneGrameTable.get(letter).get(1))/Double.parseDouble(oneGrameTable.get("****").get(1));
            WLF.put(letter.charAt(0), value);
        }

        WLF.remove('*');

        worldLetterFreq = (LinkedHashMap<Character, Double>) sortByValue(WLF);
    }

    /**
     * Generates the {@link HashMap} that stores the double letters
     * @param nGrams
     */
    private void GenDoubleLetters(NGrams nGrams){

        HashMap<String, ArrayList<String>> diGramTable = nGrams.getDiGramTable();

        Set<String> diGrams = diGramTable.keySet();
        doubleLetters = new LinkedHashMap<>();
        double count = 0.0;

        for (String diGram: diGrams) {

            if(diGram.charAt(0)==diGram.charAt(1) && !diGram.equals("****")){

                Double value = Double.parseDouble(diGramTable.get(diGram).get(1));
                count += value;
                doubleLetters.put(diGram, value);
            }
        }

        Set<String> dLetters = doubleLetters.keySet();


        for(String dLetter: dLetters){

            if(!(dLetter.equals("****"))){

                double freq = doubleLetters.get(dLetter);
                doubleLetters.replace(dLetter, (freq/count));
            }
        }
        doubleLetters = (LinkedHashMap<String, Double>) sortByValue(doubleLetters);
    }

    /**
     * Generates the {@link HashMap} that stores the initial letters of any word
     * @param oneGrameTable
     */
    private void GenInitialLetters(HashMap<String, ArrayList<String>> oneGrameTable){

        Set<String> letters = oneGrameTable.keySet();
        initialLetters = new LinkedHashMap();
        for (String letter : letters) {

            Double value = Double.parseDouble(oneGrameTable.get(letter).get(56))/Double.parseDouble(oneGrameTable.get("****").get(56));
            initialLetters.put(letter.charAt(0), value);
        }
        initialLetters.remove('*');
        initialLetters = (LinkedHashMap<Character, Double>) sortByValue(initialLetters);
    }

    /**
     * Generates the {@link HashMap} that stores the last letters of any word
     * @param oneGrameTable
     */
    private void GenLastLetters(HashMap<String, ArrayList<String>> oneGrameTable){


        Set<String> letters = oneGrameTable.keySet();
        lastLetters = new LinkedHashMap();
        for (String letter : letters) {

            Double value = Double.parseDouble(oneGrameTable.get(letter).get(73))/Double.parseDouble(oneGrameTable.get("****").get(73));
            lastLetters.put(letter.charAt(0), value);
        }
        lastLetters.remove('*');
        lastLetters = (LinkedHashMap<Character, Double>) sortByValue(lastLetters);
    }

    //Sorts map via the value from largest to smallest
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

    public LinkedHashMap getTwoLetterWords() {
        return twoLetterWords;
    }

    public LinkedHashMap getThreeLetterWords() {
        return threeLetterWords;
    }

    public LinkedHashMap getFourLetterWords() {
        return fourLetterWords;
    }

    public LinkedHashMap getDoubleLetters() {
        return doubleLetters;
    }

    public LinkedHashMap<Character, Double> getLetterFreq() {
        return worldLetterFreq;
    }

    public LinkedHashMap<Character, Double> getLastLetters() {
        return lastLetters;
    }

    public LinkedHashMap<Character, Double> getInitialLetters() {
        return initialLetters;
    }
}
