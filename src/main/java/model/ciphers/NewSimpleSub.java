package model.ciphers;

import model.util.MsgStats;
import model.util.NGrams;
import model.util.WorldLangStats;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @project ClassicalCipherDecipherer
 */
public class NewSimpleSub extends Cipher {

    private MsgStats msgStats;
    private String oriCipherText;
    private String encryptKey;
    private boolean keyGiven;
    private LinkedList<Object> caps;
    private NGrams nGrams;
    private char[] key;
    private WorldLangStats worldLangStats;

    NewSimpleSub(String cipherText, NGrams nGrams, WorldLangStats worldLangStats) {
        super(cipherText);
        genCaps(msg);
        oriCipherText = msg;
        this.nGrams = nGrams;
        key = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        msgStats = new MsgStats(msg, key);
        this.worldLangStats = worldLangStats;
    }
    public NewSimpleSub(String plainText, String key){

        super(plainText);
        msg = plainText;
        oriCipherText = msg;
        encryptKey = key;
        keyGiven = true;
    }

    @Override
    public String encrypt() {
        return null;
    }

    @Override
    public String decrypt() {

        LinkedHashMap<Character, Double> wLetterFreq = worldLangStats.getLetterFreq();
        LinkedHashMap<Character, Double> letterFreq = msgStats.getLetterFreq();
        genStartKeyAndMsg(wLetterFreq,letterFreq);
        ArrayList<ArrayList<Character>> searchSpace = genSearchSpace(letterFreq);
       // SimAnneal simAnneal = new SimAnneal(msg,key,nGrams,searchSpace, false);
        //System.out.println(msg);
       // return simAnneal.simAnnealDecryptViaIndWords();
        return null;
    }

    private ArrayList<ArrayList<Character>> genSearchSpace(LinkedHashMap<Character, Double> letterFreq) {

        ArrayList<Character> lettFreqList = new ArrayList<>(letterFreq.keySet());
        ArrayList<ArrayList<Character>> searchSpace = new ArrayList<>();

        int searchCounter;
        int backwardLoc = 0;
        for(int i = 0 ; i<key.length; i++){
            searchCounter = 5;
            ArrayList<Character> searchSpaceHere = new ArrayList<>();

            int index = lettFreqList.indexOf(key[i]);

            for(int j=index; j>=0; j--){
                searchSpaceHere.add(lettFreqList.get(j));
                searchCounter--;
                if(searchCounter==2){
                    backwardLoc = j-1;
                    break;
                }
            }
            for(int j=(index+1); j<(index+6) && j<key.length; j++){
                searchSpaceHere.add(lettFreqList.get(j));
                searchCounter--;
                if(searchCounter==0) break;
            }
            if(searchSpaceHere.size()<5){
                for(int j=backwardLoc; j>=0; j--){
                    searchSpaceHere.add(lettFreqList.get(j));
                    searchCounter--;
                    if(searchCounter==0) break;
                }
            }
            searchSpace.add(searchSpaceHere);
        }
        return searchSpace;
    }

    private void genStartKeyAndMsg(LinkedHashMap<Character, Double> wLetterFreq,
                             LinkedHashMap<Character, Double> mLetterFreq) {

        Set<Character> wLetterInFreqOrder = wLetterFreq.keySet();
        Set<Character> mLetterInFreqOrder = mLetterFreq.keySet();
        Iterator<Character> mLetterInFreqOrderIter = mLetterInFreqOrder.iterator();

        System.out.println(wLetterInFreqOrder);
        System.out.println(mLetterInFreqOrder);
        for (Character wChar : wLetterInFreqOrder) {

            Character oldChar = key[wChar-65];
            Character newChar = mLetterInFreqOrderIter.next();

            updateKey(oldChar, newChar);
        }

        System.out.println("\n");
        StringBuilder sysOut = new StringBuilder();
        for(int i = 0; i<key.length; i++){
            sysOut.append(key[i] + ", ");
        }
        System.out.println(oriCipherText);
        System.out.println(sysOut.toString().substring(0,sysOut.length()-2));
        updateMsgWithKey();

    }

    private void updateMsgWithKey() {

        StringBuilder newMsg = new StringBuilder();
        ArrayList<Character> keyAsList = new ArrayList<>();
        for (char c : key) {
            keyAsList.add(c);
        }

        char thisChar;
        for(int i = 0; i<oriCipherText.length(); i++){

            thisChar = oriCipherText.charAt(i);
            if(Character.isLetter(thisChar)){

                newMsg.append((char) (keyAsList.indexOf(thisChar)+65));
            }else{
                newMsg.append(thisChar);
            }

        }
        msg = newMsg.toString();
    }

    private void updateKey(Character oldChar, Character newChar) {

        oldChar = Character.toUpperCase(oldChar);
        newChar = Character.toUpperCase(newChar);
        boolean replacedNew = false;
        boolean replacedOld = false;
        char thisChar;
        for (int i = 0; i<key.length; i++) {
            thisChar = key[i];

            if(thisChar==oldChar && !replacedOld){
                key[i] = newChar;
                replacedOld = true;
            }else{
                if(thisChar==newChar && !replacedNew) {
                    key[i] = oldChar;
                    replacedNew = true;
                }
            }
            if(replacedOld && replacedNew) break;
        }
    }

    @Override
    public String[] decryptPossibilities() {
        return new String[0];
    }

    /**
     * Makes a list that records the position of each capital letter in the original text
     * @param msg
     * @return
     */
    private void genCaps(String msg){

        caps = new LinkedList<>();
        char letter;

        for(int i=0; i<msg.length(); i++){
            letter = msg.charAt(i);
            if(Character.isLetter(letter) && Character.isUpperCase(letter)){

                caps.add(true);

            }else{
                caps.add(false);
            }
        }

        this.msg = msg.toUpperCase();
    }

    private LinkedHashMap<Character, Double> getLetterFreq(){

        LinkedHashMap<Character, Double> lettFreq = new LinkedHashMap<>();
        Character thisChar;
        double charFreq;
        for (char c : key) {
            thisChar = c;
            lettFreq.put(thisChar, 0.0);
        }

        for(int i = 0; i<msg.length(); i++){
            thisChar = msg.charAt(i);

            if(Character.isLetter(thisChar)){
                charFreq = ((lettFreq.get(thisChar) + 1));
                lettFreq.replace(thisChar,charFreq);
            }
        }

        return (LinkedHashMap<Character, Double>) sortByValue(lettFreq);

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

}
