package model.ciphers;

import model.util.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that contains the encrypting and decrypting capabilities of The Simple Substitution Cipher
 */
public class SimpleSubstitution extends Cipher {

    private String cipherText;
    private SubKey key;
    private String encryptKey;
    private String initialKey;
    private boolean keyGiven = false;

    private NGrams nGrams;

    private LinkedHashMap<Character, Double> letterFreq;

    private LinkedList<Boolean> caps;

    private WorldLangStats worldLangStats;
    private MsgStats msgStats;

    private int numLettersToTake = 10;
    private final double certThresh = 0.95;
    private final double diffThresh = 0.15;
    private double reachThresh = 3;

    //In case of single letters and mistake occurring
    private boolean mistake = false;
    private MsgStats oriMsgStats;
    private double oriReachThresh = 2.5;
    private String oriCipherText;
    private int mistakeLoc = 25;
    private boolean useSearch;

    /**
     * Constructor called for decryption
     * @param cipherText to be encrypted
     * @param nGrams contains the data of the nGrams
     * @param worldLangStats contains the data that represents English language stats
     */
    public SimpleSubstitution(String cipherText, NGrams nGrams, WorldLangStats worldLangStats, boolean useSearch) {
        super(cipherText);
        oriCipherText = cipherText;
        this.cipherText = oriCipherText.toUpperCase();
        this.nGrams = nGrams;
        initialKey = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        msgStats = new MsgStats(this.cipherText,new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'});
        oriMsgStats = msgStats;
        letterFreq = this.msgStats.getLetterFreq();
        this.worldLangStats = worldLangStats;
        key = new SubKey(this.cipherText,oriCipherText);
        this.caps = key.getCaps();
        this.useSearch = useSearch;
    }

    /**
     * Constructor called for encryption
     * @param plainText The msg to be encrypted
     * @param key The key that that decides the encryption
     */
    public SimpleSubstitution(String plainText, String key, boolean useSearch){

        super(plainText);
        cipherText = plainText;
        encryptKey = key;
        keyGiven = true;
        this.useSearch = useSearch;
    }

    @Override
    public String encrypt() {
        String msg = cipherText;
        int length = msg.length();
        char letter;
        StringBuilder encryptedMsg = new StringBuilder();

        if(!isValidKey(encryptKey) && encryptKey.length()>0) return "Key passed was not valid, please send 26 unique characters";

        if(encryptKey.length()==0) encryptKey = randomKey();

        //Encryption process retains capitals and non-letter symbols
        for(int i = 0; i<length; i++){
            letter = msg.charAt(i);
            if(Character.isLetter(letter)){

                if(Character.isUpperCase(letter)){

                    encryptedMsg.append(Character.toUpperCase(encryptKey.charAt(letter-65)));
                }else{
                    encryptedMsg.append(encryptKey.charAt(letter - 97));
                }
            }else{
                encryptedMsg.append(letter);
            }

        }
        return encryptedMsg.toString();
    }

    /**
     * Checks whether the passed key for encryption is valid
     */
    boolean isValidKey(String thisKey){

        char letter;
        int length = thisKey.length();
        String key = thisKey.toLowerCase();
        if(length != 26) return false;
        for(int i = 0; i<length; i++){
            letter = key.charAt(i);
            if(key.indexOf(letter) != key.lastIndexOf(letter) || !Character.isLetter(letter)){
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a random key using {@link Math}.random()
     * @return A 26 letter key of randomly positioned unique letters
     */
    private String randomKey() {
        String oldKey = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer newKey = new StringBuffer();

        while(oldKey.length()>0) {
            int loc = (int) (Math.random() * oldKey.length());
            newKey.append(oldKey.charAt(loc));
            oldKey = oldKey.substring(0,loc) + oldKey.substring(loc+1);
        }
        return newKey.toString();
    }

    /**
     * Decrypts cipherText by updating/reusing {@link #encryptKey} and {@link #encrypt()}
     * @return Uses {@link #encrypt()} to return the decrypted message
     */
    private String decryptWithKey() {

        String thisKey = encryptKey.toLowerCase();
        char[] sortedKey = new char[26];
        StringBuilder newKey = new StringBuilder();

        for(int i=0; i<encryptKey.length(); i++){
            char thisLetter = thisKey.charAt(i);
            sortedKey[thisLetter-97] = (char) (i+97);
        }

        for (char letter : sortedKey) {
            newKey.append(letter);
        }
        encryptKey = newKey.toString();

        //encryptKey has been altered so as to decrypt the message when encrypt() is called
        return encrypt();
    }

    /**
     *
     * @return
     */
    @Override
    public String decrypt() {


        //If a key is given
        if(keyGiven){
            if(isValidKey(encryptKey)){

                return decryptWithKey();

            }else{
                return "Please provide the key used or allow the program decrypt blind";
            }
        }

        //Initialise lists
        ArrayList<Character> worldFreqList = new ArrayList<>(worldLangStats.getLetterFreq().keySet());
        ArrayList<Character> msgFreqList = new ArrayList<>(letterFreq.keySet());


        //Check if text is too small
        if(msgStats.getCharCount()<150) return "Msg too small to decrypt";

        cipherText = blindDecryption(msgFreqList,worldFreqList);

        if(mistake){

           // Check if possible to attempt decryption again but switching the single letters
            if(msgStats.getOneLetterWords().size()>=2){
                attemptOtherSingleLetter(msgFreqList,worldFreqList);
            }

            System.out.println("Ori: " + key.getOriCipherText());
            System.out.println(key.getKey());


            if(useSearch){
                GeneticAlg geneticAlg = new GeneticAlg(key,msgStats,nGrams,"", mistakeLoc, true);
                return geneticAlg.decrypt(500, 20, 100, 0.9);
            }

            //SimAnneal simAnneal = new SimAnneal(key,msgStats,nGrams, "", true,true);
            //return simAnneal.decrypt(10000,0.0001,1, 1);
            //SubHillClimb subHillClimb = new SubHillClimb(key,  msgStats,  worldLangStats,  nGrams, caps, msgFreqList,mistakeLoc);
            //return subHillClimb.decrypt();
            //GeneticAlg geneticAlg = new GeneticAlg(key,msgStats,nGrams,"", mistakeLoc, true);
            //            //return geneticAlg.decrypt(3000, 20, 100, 0.7);
            return cipherText;
        }else{
            return cipherText;
        }
    }

    /*private void useHint() {

        int hintLength = hint.length();
        ArrayList<Character> hintLetters = new ArrayList<>();
        StringBuilder hintCode = new StringBuilder();
        Integer[] numbOfEachLetter = new Integer[hintLength];

        //Populate lists for use
        for(int i=0; i<hintLength; i++){

            char thisLetter = hint.charAt(i);

            if(!hintLetters.contains(thisLetter)){

                hintLetters.add(thisLetter);
                int letterLoc = hintLetters.indexOf(thisLetter);
                hintCode.append(letterLoc);
                numbOfEachLetter[letterLoc] = 1;

            }else{

                int letterLoc = hintLetters.indexOf(thisLetter);
                hintCode.append(letterLoc);
                numbOfEachLetter[letterLoc] = numbOfEachLetter[letterLoc] + 1;
            }
        }

        System.out.println("Hint letters: " + hintLetters);
        System.out.println("Hintcode:     " + hintCode);
        System.out.println("NumbOfEachLet " + Arrays.toString(numbOfEachLetter));

    }*/


    /**
     * Attempts blindDecryption by switching around the single letters
     * @param msgFreqList
     * @param worldFreqList
     */
    private void attemptOtherSingleLetter(ArrayList<Character> msgFreqList, ArrayList<Character> worldFreqList) {

        //Retain relevant variables
        double firstFitness = nGrams.getFitness(nGrams.getNGrams(cipherText,4),4);
        SubKey firstKey = new SubKey(key);
        MsgStats firstStats = new MsgStats(msgStats);

        Object[] singleLetters = firstStats.getOneLetterWords().keySet().toArray();
        int locOfOldLetter;
        Character oldSingleLetter;

        //Searches for location of previously added single letter, if 'i' was added first, then the new key will add 'a' first
        oldSingleLetter = singleLetters[0].toString().charAt(0);
        locOfOldLetter = firstKey.getKey().indexOf(oldSingleLetter);
        System.out.println(locOfOldLetter);



        //Redo variables to their originals
        reachThresh = oriReachThresh;
        cipherText = oriCipherText.toUpperCase();
        key = new SubKey(cipherText, oriCipherText);

        Character oldLetter = key.getKey().charAt(locOfOldLetter);
        Character newLetter;
        if(oldSingleLetter == 'a'){
            newLetter = 'i';
        }else{
            newLetter ='a';
        }
        System.out.println(newLetter);

        key.updateKey(String.valueOf(oldLetter),String.valueOf(newLetter), false);
        msgStats = new MsgStats(key.getCipherText(), key.getKeyAsArray());
        String newText = blindDecryption(msgFreqList,worldFreqList);

        if(firstFitness>nGrams.getFitness(nGrams.getNGrams(newText,4),4)){
            key = new SubKey(firstKey);
            msgStats = new MsgStats(firstStats);
        }
    }

    /**
     * The start of the cryptanalysis process, using the letter frequency lists as a base
     * @param msgFreqList letter frequency statistics on the cipherText
     * @param worldFreqList letter frequency statistics representing English language
     * @return
     */
    String blindDecryption(ArrayList<Character> msgFreqList, ArrayList<Character> worldFreqList){

        int count = 0;

        do{

            //Finds next letter to replace
            char oldLetter = findNextToReplace(key.getReplacedLetters(), msgFreqList);

            //Check if letter even appears in msg, if so direct decryption starts
            if(msgStats.getLetterFreq().get(oldLetter)>0){
                System.out.println("Old letter: " + oldLetter);

                //Makes list of possible letters
                ArrayList<Character> posLetters = findPosLetters(oldLetter);

                //Cross check through wordLists
                crossCheckLettersToLists(oldLetter,posLetters, msgFreqList);

            }else{
                addRemaining(worldFreqList,msgFreqList);
            }

            count++;

        }while(!key.getKey().toLowerCase().equals(key.getKey()));


        return key.getCipherText();
    }

    /**
     * Works out the fitness value of a word, by treating it as its own ngram, therefore giving a value towards the
     * frequency of a word existing in the data
     * @param word
     * @return
     */
    double getWordFitness(String word){

        ArrayList<String> line;
        HashMap<String, ArrayList<String>> nGramTable;
        double fitness = 0;
        int length = word.length();
        double totalNCount = 0;
        //This variable when altered will point to the right total value from the Ngram table
        int loc = 12;

        if(length == 0){
            return totalNCount;
        }

        nGramTable = getNgramByLength(length);


        loc -=length;
        if (nGramTable != null) {


            totalNCount = Double.parseDouble(nGramTable.get("****").get(loc));

            if(nGramTable.containsKey(word.toUpperCase())){

                line = nGramTable.get(word.toUpperCase());

                fitness+= Math.log(Double.parseDouble(line.get(loc))/totalNCount);

            }else{

                fitness+= Math.log(1/totalNCount);
            }
        }
        return fitness;
    }

    /**
     * Extracts the fitness of a partially completed word by only testing the fitness of letters added which are
     * signified as lowercase, lowercase letters are assumed to exist in the passed {@link String}
     * @param partialWord A partially decrypted word
     * @return
     */
    double getPartialWordFitness(String partialWord){

        //Iterate through each potential nGram and fill the variables for the formulas to find the spot in the nGram list
        // formula: startLoc + wordLengthLoc + nGramLoc
        // formula: (12 - nGramLength) + [0 + 1 + 2...(wordLength - nGramLength)] + (i-1)

        int wordLength = partialWord.length();
        int nGramLoc;
        int nGramLength = 0;
        StringBuilder nGram = new StringBuilder();
        HashMap<String, ArrayList<String>> nGramTable;
        double fitness = 0;

        for(int i = 0; i<wordLength+1; i++){

            int startLoc = 12;
            int wordLengthLoc = 0;
            nGramLoc = i-1;
            Character thisLetter = ' ';

            if(i!=wordLength){
                thisLetter = partialWord.charAt(i);
            }
            if(i!=wordLength && Character.isLowerCase(partialWord.charAt(i))){

                nGramLength++;
                nGram.append(thisLetter);

            }else{

                if(nGramLength>0){

                    startLoc = startLoc - nGramLength;
                    for(int j = 0; j<(wordLength-nGramLength)+1; j++){
                        wordLengthLoc+=j;
                    }

                    nGramTable = getNgramByLength(nGramLength);
                    int finalLoc = startLoc + wordLengthLoc + nGramLoc;

                    String thisNGram = nGram.toString();

                    double totalNCount = Double.parseDouble(nGramTable.get("****").get(finalLoc));

                    if(nGramTable.containsKey(thisNGram.toUpperCase())){

                        ArrayList<String> line = nGramTable.get(thisNGram.toUpperCase());
                        fitness+= Math.log(Double.parseDouble(line.get(finalLoc))/totalNCount);

                    }else{

                        fitness+= Math.log(1/totalNCount);
                    }
                }
                nGramLength = 0;
                nGram = new StringBuilder();
            }
        }
        return fitness;
    }

    /**
     * Finds the right nGram table by the length passed
     * @param nGramLength length nGram or value of n
     * @return
     */
    private HashMap<String, ArrayList<String>> getNgramByLength(int nGramLength) {

        HashMap<String, ArrayList<String>> nGramTable = new HashMap<>();

        switch (nGramLength){
            case 1:
                nGramTable = nGrams.getOneGramTable();
                break;
            case 2:
                nGramTable = nGrams.getDiGramTable();
                break;
            case 3:
                nGramTable = nGrams.getTriGramTable();
                break;
            case 4:
                nGramTable = nGrams.getQuadGramTable();
                break;
            default :
                System.out.println("Wrong int passed through getFitnessOfStringKey");
                break;
        }
        return nGramTable;
    }

    /**
     * Creates a list of possible letters that can replace the passed character
     * @param oldLetter
     * @return
     */
    private ArrayList<Character> findPosLetters(char oldLetter){

        ArrayList<Character> worldLetterFreq = new ArrayList<>(worldLangStats.getLetterFreq().keySet());
        String dOldLetter = String.valueOf(oldLetter) + String.valueOf(oldLetter);

        boolean isSingle = false;
        boolean isDouble = false;
        boolean isInitial = false;
        boolean isLast = false;

        //Check what groups oldLetter belongs in
        if(msgStats.getOneLetterWords().containsKey(Character.toString(oldLetter))) isSingle = true;
        if(msgStats.getDoubleLetters().containsKey(dOldLetter)) isDouble = true;
        if(msgStats.getInitialLetters().containsKey(oldLetter)) isInitial = true;
        if(msgStats.getLastLetters().containsKey(oldLetter)) isLast = true;

        double oldLetterFreq = msgStats.getLetterFreq().get(oldLetter);
        HashMap<Character, Double> posLettersCerts = new HashMap<>();
        int count = 0;
        double freqCertainty;
        double newLetterFreq;
        ArrayList<Character> lettersToCons = new ArrayList<>();
        ArrayList<Character> posLetters;

        int missedLetterPos = 0;
        //Finds position to start looking for possible letters, considers position of the first missed letter
        do{
            int start = -1;
            for(Character thisLetter : worldLetterFreq){

                start++;

                //Record position of first missed letter
                if(!key.getAddedLetters().contains(Character.toLowerCase(thisLetter)) && missedLetterPos == 0){

                    missedLetterPos = start;
                }

                double letterFreq = worldLangStats.getLetterFreq().get(thisLetter);

                if(letterFreq<oldLetterFreq) break;
            }

            //Alters range according to limiters to avoid errors
            if(start < (numLettersToTake/2)) {
                start = 0;
            }else{

                start = start - (numLettersToTake/2);

                if(missedLetterPos>start){
                    start = missedLetterPos;
                }
            }

            //Find first few letters with the highest freq not yet added, and record freqCertainty value,
            // amount decided by lettersToTake's value
            for (int i=start; i<worldLetterFreq.size(); i++) {

                Character letter = worldLetterFreq.get(i);
                if (!key.getAddedLetters().contains(Character.toLowerCase(letter))) {

                    count++;
                    newLetterFreq = worldLangStats.getLetterFreq().get(letter);

                    //freqCertainty is initialised as a fraction displaying how close the new letter's frequency matches
                    // the old letter's frequency
                    if(oldLetterFreq < newLetterFreq){
                        freqCertainty = 1 + ((newLetterFreq-oldLetterFreq)/oldLetterFreq);
                    }else{
                        freqCertainty = (newLetterFreq/oldLetterFreq);
                    }

                    posLettersCerts.put(Character.toLowerCase(letter),freqCertainty);

                    if(count==numLettersToTake) break;
                }
            }
            if(posLettersCerts.isEmpty()) System.out.println("Couldn't find letter to add");

            posLettersCerts = (HashMap<Character, Double>) sortByValue(posLettersCerts);
            posLetters = new ArrayList<>(posLettersCerts.keySet());
//            System.out.println("Pos letters before: " + posLetters);

            lettersToCons.clear();
            if(isInitial){

//                System.out.println("World initial: " + worldLangStats.getInitialLetters());
//                System.out.println("Msg initial: " + msgStats.getInitialLetters());

                lettersToCons = addIniOrLastToCons(oldLetter,worldLangStats.getInitialLetters(),msgStats.getInitialLetters(),lettersToCons);
//                System.out.println("lettersToCons after initial letters run: " + lettersToCons);
            }

            if(isLast){
//                System.out.println("World last: " + worldLangStats.getLastLetters());
//                System.out.println("Msg last: " + msgStats.getLastLetters());

                lettersToCons = addIniOrLastToCons(oldLetter,worldLangStats.getLastLetters(),msgStats.getLastLetters(),lettersToCons);
//                System.out.println("lettersToCons after last letters run: " + lettersToCons);
            }

            if(isDouble){
//                System.out.println("World doubles: " + worldLangStats.getDoubleLetters());
//                System.out.println("Msg doubles: " + msgStats.getDoubleLetters());
                lettersToCons = addDoublesToCons(dOldLetter,msgStats,lettersToCons);
//                System.out.println("lettersToCons after double letters run: " + lettersToCons);
            }

            // if not, loop through each character and check through fail states, deleting letters when appropriate
            ArrayList<Character> temp = new ArrayList<>(posLetters);
            int length = temp.size();
//            System.out.println("LettersToCons after state checks " + lettersToCons);

            if(!lettersToCons.isEmpty()){

                for(int i = 0; i<length; i++){

                    char thisLetter = temp.get(i);

                    if(!lettersToCons.contains(thisLetter)){

                        posLetters.remove(posLetters.indexOf(thisLetter));
                    }
                }
            }

            //Check isSingle
//            if(isSingle){
//                posLetters.clear();
//                posLetters.add('a');
//                posLetters.add('i');
//            }
//            System.out.println("Pos letters after: " + posLetters);

            if(posLetters.isEmpty()){
                numLettersToTake++;
                reachThresh += 0.5;
            }

        }while(posLetters.isEmpty());

//
//        System.out.println("Letters to consider at this time: " + lettersToCons);
        int pos = oldLetter - 65;

        key.updateLettersToCons(posLetters, pos);

        reachThresh+= 0.1;

        return posLetters;
    }

    /**
     * Updates the wordlists with the posLetters in order to find the best fit letter
     * @param oldLetter
     * @param posLetters
     * @param msgFreqList
     */
    private void crossCheckLettersToLists(char oldLetter, ArrayList<Character> posLetters,
                                          ArrayList<Character> msgFreqList) {

        int pos = oldLetter - 65;
        ArrayList<Character> lettersToCons = key.getLettersToCons().get(pos);
        LinkedHashMap<Character, Double> letterFitnessMap = new LinkedHashMap<>(lettersToCons.size());
        char tempLetter = oldLetter;
        boolean mistakeHere = false;


        //Cycle through each possible letter
        for(char thisLetter: lettersToCons){
            ArrayList<String> wordList = new ArrayList<>();
            ArrayList<String> partialWordList = new ArrayList<>();
            double thisFitness = 0.0;

            //Update the wordlists with that letter
            msgStats.UpdateWordLists(tempLetter, thisLetter);
            letterFitnessMap.put(thisLetter, 0.0);

            ArrayList<String> alteredWords = msgStats.getAlteredWords();


            //Make list of complete words and partially complete words
            for (String thisWord : alteredWords) {

                if (thisWord.equals(thisWord.toLowerCase())) {

                    wordList.add(thisWord);
                } else {
                    partialWordList.add(thisWord);
                }
            }

            //If contains complete words, record fitness for the letter being placed
            int wordListLength = wordList.size();
            for (String word : wordList) {

                thisFitness += getWordFitness(word);
            }

            //Does the same for incomplete words
            int partialWordListLength = partialWordList.size();
            for (String word : partialWordList) {

                thisFitness += getPartialWordFitness(word);
            }

            letterFitnessMap.replace(thisLetter, (thisFitness/(wordListLength + partialWordListLength)));

            //Check if a mistake may have occurred
            if(wordListLength==0 && partialWordListLength==0) mistakeHere = true;

            //moves the current letter as temp so as to be replaced by the next letter in the loop
            tempLetter = thisLetter;
        }

//        System.out.println((letterFitnessMap.get(posLetters.get(0))));
        if((letterFitnessMap.get(posLetters.get(0))!=0.0)){

            letterFitnessMap = (LinkedHashMap<Character, Double>) sortByValue(letterFitnessMap);
            posLetters = new ArrayList<>(letterFitnessMap.keySet());

        }
        //Last check to avoid duplicate letters in key
//        System.out.println(posLetters);
        for (int i = 0; i<posLetters.size(); i++) {

            Character letter = posLetters.get(i);
            if(key.getAddedLetters().contains(letter)){
                posLetters.remove(letter);
                letterFitnessMap.remove(letter);
            }

        }
//        System.out.println("letter Fitness: " + letterFitnessMap);
        msgStats.UpdateWordLists(tempLetter, posLetters.get(0));
        key.updateKey(String.valueOf(oldLetter),String.valueOf(posLetters.get(0)), false);

        if(letterFitnessMap.get(posLetters.get(0))<-20 || letterFitnessMap.size()>1 &&
                      (letterFitnessMap.get(posLetters.get(0)) - letterFitnessMap.get(posLetters.get(1))) < 0.25){

            mistakeHere = true;

        }

        if(mistakeHere && !mistake){
            mistake = true;
            mistakeLoc = msgFreqList.indexOf(oldLetter);
        }

//        System.out.println("New Letter: " + posLetters.get(0)+ "\n");
    }

    /**
     * Add remaining letters that don't appear in cipherText
     * @param worldFreqList
     * @param msgFreqList
     */
    private void addRemaining(ArrayList<Character> worldFreqList, ArrayList<Character> msgFreqList){

        StringBuffer lettersToReplace = new StringBuffer();
        StringBuffer lettersToAdd = new StringBuffer();

        for(int i = 0; i<key.getKey().length(); i++){
            char thisLetter = key.getKey().charAt(i);
            if(Character.isUpperCase(thisLetter))lettersToReplace.append(thisLetter);
        }

        for(int i = msgFreqList.size()-1; i>=0; i--){

            Character thisLetter = worldFreqList.get(i);

//            System.out.println(key.getAddedLetters());
            if(!key.getAddedLetters().contains(Character.toLowerCase(thisLetter))){

                lettersToAdd.append(Character.toLowerCase(thisLetter));
            }

            if(lettersToAdd.length() == lettersToReplace.length()) break;
        }

        key.updateKey(lettersToReplace.toString(),lettersToAdd.toString(),false);
    }

    /**
     * Used in {@link #blindDecryption(ArrayList, ArrayList)} to find possible letters found in the doubleLetterList
     * @param dOldLetter
     * @param msgStats
     * @param lettersToCons
     * @return
     */
    private ArrayList<Character> addDoublesToCons(String dOldLetter, MsgStats msgStats, ArrayList<Character> lettersToCons){

        //Find start position
        String[] worldDoubles = (String[]) worldLangStats.getDoubleLetters().keySet().toArray(new String[0]);
        Double oldDLetterFreq = msgStats.getDoubleLetters().get(dOldLetter);
        ArrayList<Character> dLettersToCons = new ArrayList<>();
        int start = -1;
        for(String dLetter : worldDoubles){

            start++;
            double dLetterFreq = (double) worldLangStats.getDoubleLetters().get(dLetter);
            if(dLetterFreq<oldDLetterFreq) break;

        }

        //Adjust start and range using the reachThreshold
        int reach = (int) (msgStats.getDoubleLetters().size()*reachThresh);
        if(start < (reach/2)) {
            start = 0;
            reach = reach - ((reach/2)-start);
        }else{
            start = start - (reach/2);
            reach = reach + (reach/2);
        }
        if(reach > worldDoubles.length) reach = worldDoubles.length ;

        //Add letters to be considered
        for(int i=start; i<reach; i++) {

            char doubleLetter = Character.toLowerCase(worldDoubles[i].charAt(0));

            if (lettersToCons.contains(doubleLetter)) dLettersToCons.add(doubleLetter);

        }

        ArrayList<Character> toReturn = new ArrayList<>();
        for (Character thisLetter : lettersToCons) {

            if (dLettersToCons.contains(thisLetter)) toReturn.add(thisLetter);
        }
        return toReturn;
    }

    /**
     * Used in {@link #blindDecryption(ArrayList, ArrayList)} to find initial or last letters as lettersToCons,
     * cross check with given lists
     * @param oldLetter letter to be replaced
     * @param worldCharStats Statistics about either last letters or initial letters in world stats
     * @param msgCharStats Statistics about either last letters or initial letters cipherText
     * @param lettersToCons The list of possible letters to replace oldLetter
     * @return
     */
    private ArrayList<Character> addIniOrLastToCons(char oldLetter, LinkedHashMap<Character,
                                                            Double> worldCharStats, LinkedHashMap<Character,
                                                            Double> msgCharStats, ArrayList<Character> lettersToCons){

        //Find start position
        Character[] worldLetters = worldCharStats.keySet().toArray(new Character[0]);
        Double oldDLetterFreq = msgCharStats.get(oldLetter);
        int start = -1;
        for(char letter : worldLetters){

            start++;
            double letterFreq = worldCharStats.get(letter);
            if(letterFreq<oldDLetterFreq) break;

        }

        //Adjust start and range using the reachThresh value
        int reach = (int) (reachThresh * (msgCharStats.size()));
        if(start < (reach/2)) {
            start = 0;
            reach = reach - ((reach/2)-start);
        }else{
            start = start - (reach/2);
            reach = reach + (reach/2);
        }
        if(reach > worldLetters.length) reach = worldLetters.length ;

        //Add letters to be considered made here
        ArrayList<Character> aLettersTobeCons = new ArrayList<>();
        for(int i=start; i<reach; i++){

            char thisLetter = Character.toLowerCase(worldLetters[i]);

            if(!lettersToCons.isEmpty()){

                if(lettersToCons.contains(thisLetter)){
                    aLettersTobeCons.add(thisLetter);
                }
            }else{
                aLettersTobeCons.add(thisLetter);
            }

        }
        ArrayList<Character> toReturn = new ArrayList<>();

        if(!lettersToCons.isEmpty()){

            int length = lettersToCons.size();
            for(int i = 0; i<length; i++){

                char thisLetter = lettersToCons.get(i);

                if(aLettersTobeCons.contains(thisLetter)) toReturn.add(thisLetter);
            }
        }else{
            return aLettersTobeCons;
        }

        return toReturn;
    }

    /**
     * Used in {@link #blindDecryption(ArrayList, ArrayList)} to find the next letter that needs replacing
     * @param movedLetters
     * @param msgFreqList
     * @return
     */
    private char findNextToReplace(ArrayList<Character> movedLetters, ArrayList<Character> msgFreqList){

        //Find position of next letter to possibly replace
        int pos = 0;
        if(!movedLetters.isEmpty()){

            for(int i = 0; i<msgFreqList.size(); i++){
                if(!movedLetters.contains(msgFreqList.get(i))){
                    pos = i;
                    break;
                }
            }
        }

        //Find altered words with only one missing letter
        ArrayList<String> alteredWords = msgStats.getAlteredWords();
        for (String thisWord : alteredWords) {

            int upperCaseCount = 0;
            char posLetter = ' ';
            int posLetterLoc = 25;

            //Once a singular unaltered letter is discovered, mark down its location
            //e.g. "thWs" = word, record location of 'W'
            for (int i = 0; i < thisWord.length(); i++) {

                char thisLetter = thisWord.charAt(i);
                if (Character.isUpperCase(thisLetter)) {

                    upperCaseCount++;
                    posLetter = thisLetter;
                }


                if (upperCaseCount > 1){
                    posLetter =  ' ';
                    break;
                }
            }
            //Compare each singled out letter to output the earliest location of a letter that can potentially complete a word(s)
            if(posLetter!= ' ' && msgFreqList.indexOf(posLetter) < posLetterLoc){
                posLetterLoc = msgFreqList.indexOf(posLetter);
            }

            //if found a word with one letter left to change, change pos value if within a range of 5 of original pos
            if(posLetterLoc<25 && Math.abs(posLetterLoc-pos) <= reachThresh){
                pos = posLetterLoc;
                break;
            }
        }
        return msgFreqList.get(pos);
    }

    /**
     * uses the tracked caps to generate the message as it originally was
     * @param cipherText encrypted message
     * @return
     */
    private String genResult(String cipherText) {

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

    @Override
    public String[] decryptPossibilities() {

        String[] temp = new String[1];
        temp[0] = decrypt();
        return temp;
    }

    public SubKey getKey() {
        return key;
    }

    public NGrams getnGrams() {
        return nGrams;
    }

    public LinkedList<Boolean> getCaps() {
        return caps;
    }

    public WorldLangStats getWorldLangStats() {
        return worldLangStats;
    }

    public MsgStats getMsgStats() {
        return msgStats;
    }

    public int getMistakeLoc() {
        return mistakeLoc;
    }

    public LinkedHashMap<Character, Double> getLetterFreq() {
        return letterFreq;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

}