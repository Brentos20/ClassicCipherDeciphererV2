package model.ciphers;

import model.util.NGrams;

import java.sql.SQLOutput;
import java.util.*;

public class Nihilist extends Cipher {

    private String key = "";
    private Character[][] nihilMatrix;
    private HashMap<Character, Integer> nihilMapLetterToNumb;
    private HashMap<Integer, Character> nihilMapNumbToLetter;
    private boolean keyGiven;
    private HashSet<Integer> involedNihilSums;
    private ArrayList<Object> cipherTextAsListWithPunc;
    private ArrayList<Integer> cipherTextAsList;

    public Nihilist(String msg) {
        super(msg.toLowerCase());
        initialiseDatastructures();
    }

    public Nihilist(String msg, String key){
        super(msg.toLowerCase());

        //Checks if the key sent through is valid
        if(key.length()>0){
            this.key = key.toLowerCase();
            keyGiven = true;
        }
        initialiseDatastructures();
    }

    private void initialiseDatastructures() {
        nihilMatrix = new Character[6][6];
        nihilMapLetterToNumb = new HashMap<>();
        nihilMapNumbToLetter = new HashMap<>();
        char letter;
        int numb;
        int alteringValue = 97;


        for (int i = 1; i < 6; i++) {
            for (int j = 1; j < 6; j++) {

                //To skip j
                if(i==2 && j==5) alteringValue++;

                letter = (char) (((i-1)*5)+(j-1) + alteringValue);
                numb = Integer.valueOf(i + Integer.toString(j));

                nihilMatrix[i][j] = letter;
                nihilMapLetterToNumb.put(letter, numb);
                nihilMapNumbToLetter.put(numb, letter);
            }

        }
        //Give j the same value as i for ease of encryption
        nihilMapLetterToNumb.put('j',24);

    }

    @Override
    public String encrypt() {

        StringBuilder to_return = new StringBuilder();
        int keyPos = 0;
        int keyNumb = 0;
        int keyLength;
        ArrayList<Integer> keyAsNumbList = keyToNumbList();

        keyLength = keyAsNumbList.size();

        if(keyLength==0) return "Please input letters in the text-key area to act as a key";
        if(keyLength>15) return "key is too large, limit is 15 characters";

        for(int i = 0; i < textLength; i++){

            char letter = msg.charAt(i);

            if(Character.isLetter(letter)){
                int letterNumb = nihilMapLetterToNumb.get(letter);

                keyNumb = keyAsNumbList.get(keyPos);

                //Encrypt with the key layer
                to_return.append((letterNumb + keyNumb));

                keyPos++;
                if(keyPos==keyLength) keyPos = 0;
            }else{
                if(Character.isDigit(letter)) to_return.append("**");
                else to_return.append(letter);
            }
        }
        return to_return.toString();
    }

    @Override
    public String decrypt() {

        if(keyGiven) return informedDecrypt();
        else return blindDecrypt();
    }

    private String informedDecrypt() {

        StringBuilder to_return = new StringBuilder();
        ArrayList<Integer> keyAsNumbList = keyToNumbList();
        int keyLength = keyAsNumbList.size();
        System.out.println(keyAsNumbList);
        String encryptNumb;
        String decryptNumb;
        int keyPos = 0;
        for(int i = 0; i<textLength; i++){
            //In case of spaces and special characters
            if(Character.isDigit(msg.charAt(i))){

                //Remove first layer of encryption using the known key
                encryptNumb = msg.substring(i, i+2);
                if(encryptNumb.equals("10")||encryptNumb.equals("11")){
                    encryptNumb =  msg.substring(i, i+3);
                    i++;
                }
                decryptNumb = String.valueOf(Integer.parseInt(encryptNumb) - keyAsNumbList.get(keyPos));
                keyPos++;
                if(keyPos==keyLength) keyPos = 0;


                to_return.append(nihilMatrix[Integer.parseInt(decryptNumb.substring(0,1))][Integer.parseInt(decryptNumb.substring(1))]);
                i++;
            }else{
                to_return.append(msg.charAt(i));
            }
        }
        return to_return.toString();
    }

    private String informedDecrypt(String key){
        this.key = key;
        return informedDecrypt();
    }

    private String blindDecrypt(){
        NGrams nGrams = new NGrams();
        genCipherTextToList();
        HashMap<Integer, ArrayList<ArrayList<Character>>> allPosKeyLengthsAndChars = getAllPosKeyLengthsAndChars();

        ArrayList<String> allPosKeys = new ArrayList<>();
        ArrayList<ArrayList<Character>> posKeyHere = null;
        for(int i=0; i<=15; i++){
            if(allPosKeyLengthsAndChars.containsKey(i)){
                posKeyHere = allPosKeyLengthsAndChars.get(i);
                break;
            }
        }

        if(posKeyHere==null) return "could not find message, possibly wrong key or cipher was inputed";
        allPosKeys = getAllPosKeys(posKeyHere, allPosKeys);
        double fitness = Integer.MIN_VALUE;
        String msg = "";

        for (String allPosKey : allPosKeys) {
            String posMsg = informedDecrypt(allPosKey);

            double fitnessHere = nGrams.getFitness(nGrams.getNGrams(posMsg, 3), 3);
            if(fitnessHere>fitness){
                fitness = fitnessHere;
                msg = posMsg;
            }
        }
        return msg;
    }

    private ArrayList<String> getAllPosKeys(ArrayList<ArrayList<Character>> posKeyHere, ArrayList<String> allPosKeys) {

        allPosKeys.add("");
        int oriSize;
        int newSize;
        int startHere = 0;
        ArrayList<String> temp;
                for (ArrayList<Character> charsHere : posKeyHere) {
                    oriSize = allPosKeys.size();

                    if(charsHere.size()>1){
                        newSize = oriSize*charsHere.size();
                        temp = new ArrayList<>(allPosKeys);
                        while(allPosKeys.size()!=newSize){
                            allPosKeys.addAll(temp);
                        }
                    }
                    for (Character aCharsHere : charsHere) {
                        for (int i=startHere; i<startHere+oriSize;) {

                            allPosKeys.set(i, allPosKeys.get(i) + aCharsHere);
                            i++;
                        }
                        startHere+=oriSize;
                    }
                    startHere = 0;
                    for (String allPosKey : allPosKeys) {
                        System.out.println(allPosKey);
                    }
                    System.out.println("\n");
                }
        System.out.println(allPosKeys);
        return allPosKeys;
    }

    private HashMap<Integer,ArrayList<ArrayList<Character>>> getAllPosKeyLengthsAndChars() {
        HashMap<Integer,ArrayList<Character>> nihilSumMap = new HashMap<>();
        HashMap<Integer,ArrayList<ArrayList<Character>>> posKeyLengthsAndChars = new HashMap<>();
        int start = 0;
        int startNumb;
        int posKeyLength = 1;

        for (Integer nihilSum : involedNihilSums) {
            nihilSumMap.put(nihilSum,createInvolvedLettersList(nihilSum));
            System.out.println();
        }

        int keyIter = 0;
        startNumb = cipherTextAsList.get(keyIter);

        ArrayList<Character> startChars = new ArrayList<>(nihilSumMap.get(startNumb));
        ArrayList<Character> posKeyCharsHere;
        ArrayList<ArrayList<Character>> allPosCharsForEachIter = new ArrayList<>();

        for(int i = start; i<cipherTextAsList.size(); i+=posKeyLength){
            System.out.println("Start Chars: " + startChars + " at posKeyLength: " + posKeyLength);
            posKeyCharsHere = nihilSumMap.get(cipherTextAsList.get(i));
            System.out.println("Letters to compare: " + posKeyCharsHere);
            startChars.retainAll(posKeyCharsHere);
            System.out.println("Start Chars: " + startChars + " at posKeyLength: " + posKeyLength);
            System.out.println("\n");

            if(i!=start || startChars.isEmpty()){

                if(!startChars.isEmpty()){
                    start = i + posKeyLength;
                }else{
                    allPosCharsForEachIter.clear();
                    keyIter = 0;
                    start = keyIter;
                    startNumb = cipherTextAsList.get(keyIter);
                    posKeyLength++;
                    i = start;
                    startChars = new ArrayList<>(nihilSumMap.get(startNumb));
                }
            }
            if(i+posKeyLength>=cipherTextAsList.size()){
                System.out.println("Possible key length: " + posKeyLength);
                System.out.println("Possible chars " + startChars);
                ArrayList<Character> posCharsHere = new ArrayList<>(startChars);
                allPosCharsForEachIter.add(posCharsHere);
                System.out.println("Added posCharsHere: " + posCharsHere);
                System.out.println("\n");

                if(keyIter+1==posKeyLength){
                    posKeyLengthsAndChars.put(posKeyLength, allPosCharsForEachIter);
                    allPosCharsForEachIter = new ArrayList<>();
                    start = 0;
                    posKeyLength++;
                    i = start;
                    keyIter = 0;
                }else{
                    keyIter ++;
                    startNumb = cipherTextAsList.get(keyIter);
                    start = keyIter;
                    i = start;
                }
                startChars = new ArrayList<>(nihilSumMap.get(startNumb));
            }
            if(posKeyLength>15) break;
        }

        for(int i = 0; i<=15; i++){
            if(posKeyLengthsAndChars.containsKey(i)){
                ArrayList<ArrayList<Character>> arrayLists = posKeyLengthsAndChars.get(i);
                System.out.println(arrayLists);
            }
        }
        return posKeyLengthsAndChars;
    }

    public void genCipherTextToList(){
        cipherTextAsListWithPunc = new ArrayList<>();
        cipherTextAsList = new ArrayList<>();
        involedNihilSums = new HashSet<>();
        int msgLength = msg.length();
        char letter1;
        char letter2;
        String numbAsStr;
        int numb;

        for(int i=0; i<msgLength; i++){
            letter1 = msg.charAt(i);
            //In case of ending up in the last index due to non-numeric symbols
            if(i == (msgLength-1)){
                cipherTextAsListWithPunc.add(letter1);
                break;
            }
            letter2 = msg.charAt(i+1);

            if(Character.isDigit(letter1) && Character.isDigit(letter2)){

                numbAsStr = String.valueOf(letter1) + String.valueOf(letter2);

                //In case of the few 3 digit numbers
                if(letter1=='1'){
                    numbAsStr += msg.substring(i+2,i+3);
                    i++;
                }
                numb = Integer.parseInt(numbAsStr);
                cipherTextAsListWithPunc.add(numb);
                cipherTextAsList.add(numb);
                involedNihilSums.add(numb);
                i++;
            }else{
                cipherTextAsListWithPunc.add(letter1);
            }
        }
    }

    /**
     * Creates an arraylist of letters that could have been involved in the creation of the passed number through
     * encryption
     * @param nihilSum an integer between 22 and 110
     * @return ArrayList of letters
     */
    public ArrayList<Character> createInvolvedLettersList(int nihilSum){

        HashSet<Character> lettersWithoutDupes = new HashSet();

        Set<Integer> nihilNumbs = nihilMapNumbToLetter.keySet();

        for (Integer numb1 : nihilNumbs) {
            for (Integer numb2 : nihilNumbs) {
                if(numb1 + numb2 == nihilSum){
                    lettersWithoutDupes.add(nihilMapNumbToLetter.get(numb1));
                    lettersWithoutDupes.add(nihilMapNumbToLetter.get(numb2));
                }
            }
        }
        System.out.println(nihilSum);
        for (Character lettersWithoutDupe : lettersWithoutDupes) {
            System.out.print(lettersWithoutDupe + "=" + nihilMapLetterToNumb.get(lettersWithoutDupe) + ", ");

        }
        System.out.println("\n");
        return new ArrayList(lettersWithoutDupes);
    }

    private ArrayList<Integer> keyToNumbList() {

        ArrayList<Integer> keyAsNumbList = new ArrayList<>();

        for(int i = 0; i < key.length(); i++){

            char letter = key.charAt(i);
            if(Character.isLetter(letter)) keyAsNumbList.add(nihilMapLetterToNumb.get(letter));
        }
        return keyAsNumbList;
    }

    public Character[][] getNihilMatrix() {
        return nihilMatrix;
    }

    public HashMap<Character, Integer> getNihilMapLetterToNumb() {
        return nihilMapLetterToNumb;
    }

    public ArrayList<Object> getCipherTextAsListWithPunc() {
        return cipherTextAsListWithPunc;
    }
    @Override
    public String[] decryptPossibilities() {
        String[] to_return = new String[1];
        to_return[0] = blindDecrypt();
        return to_return;
    }
}
