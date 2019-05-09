package model.util;

import model.ciphers.SubKey;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @project ClassicalCipherDecipherer
 */
public class GeneticAlg extends SubSearchAlg {

    public GeneticAlg(SubKey key, MsgStats msgStats, NGrams nGrams, String keyClue, boolean useSearchSpace, boolean trim){
        super(key,msgStats,nGrams,keyClue, useSearchSpace,trim);
    }

    public GeneticAlg(SubKey key, MsgStats msgStats, NGrams nGrams, String keyClue, int mistakeLoc, boolean trim){

        super(key,msgStats,nGrams,keyClue, mistakeLoc, trim);

        //System.out.println("MsgFreq: " + msgFreqList);
        //System.out.println("Key: " + Arrays.toString(this.key));
//        for (ArrayList<Character> charList : searchSpace) {
//            System.out.print(charList.toString()+",");
//        }
//        System.out.println();
    }

    public String decrypt(int popSize, int numbOfGen, int numbOfIter, double muteChance) {

        if(trim)return genResult(trim(startAlg(popSize,numbOfGen,numbOfIter,muteChance)));

        return genResult(startAlg(popSize,numbOfGen,numbOfIter,muteChance));
    }

    public String startAlg(int popSize, int numbOfGen, int numbOfIter, double muteChance){

        ArrayList<char[]> population;
        double bestFitness = Integer.MIN_VALUE;
        double thisFitness = 0.0;
        char[] bestKey = new char[0];
        if(useSearchSpace) bestKey = Arrays.copyOf(key,26);
        char[] thisKey = new char[0];
        Set<char[]> keys;
        Iterator<char[]> iterator;
        int countNoChange = 0;

        for(int i=0; i<numbOfIter; i++){
            population = initialisePop(bestKey,popSize);
            for(int j=0; j<numbOfGen; j++){


//            System.out.println();
//            System.out.println("After initial");
//            for (char[] key : population) {
//                System.out.print(Arrays.toString(key) + ",");
//            }
                population = selectPop(population, popSize);
//            System.out.println();
//            System.out.println("After Select");
//            for (char[] key : population) {
//                System.out.print(Arrays.toString(key) + ",");
//            }
                population = crossOver(population);
//            System.out.println();
//            System.out.println("After Cross");
//            for (char[] key : population) {
//                System.out.print(Arrays.toString(key) + ",");
//            }
                population = mutate(population, muteChance);
//            System.out.println();
//            System.out.println("After Mutate");
//            for (char[] key : population) {
//                System.out.print(Arrays.toString(key) + ",");
//            }
//            System.out.println();

                LinkedHashMap<char[], Double> fitnessMap = getfitnessMap(population);
                keys = fitnessMap.keySet();
                iterator = keys.iterator();
                thisKey = iterator.next();
                thisFitness = fitnessMap.get(thisKey);
                if(thisFitness>bestFitness){
                    bestFitness = thisFitness;
                    bestKey = Arrays.copyOf(thisKey,26);
                    countNoChange = 0;
                }else{
                    countNoChange++;
                    if(countNoChange == 5)break;
                }


//                System.out.println("Best key:      " + Arrays.toString(bestKey));
//                System.out.println("With Fitness:  " + bestFitness);
//                System.out.println("Best key here: " + Arrays.toString(thisKey));
//                System.out.println("Best fit here: " + thisFitness);
//                System.out.println();
            }
            if(countNoChange > numbOfGen*2) break;

           // System.out.println("No Change: " + countNoChange);
        }
        this.key = bestKey;
        return decryptWithKey(bestKey);
    }

    private ArrayList<char[]> mutate(ArrayList<char[]> population, double muteChance) {

        Random random = new Random();

        for (int i = 0; i<population.size(); i++) {
            if(random.nextDouble()%1<=muteChance){
                char[] key = population.get(i);
                population.set(i,swapRandomLett(key));
            }
        }
        return population;
    }

    private char[] swapRandomLett(char[] key) {
        Random random = new Random();

        char oldChar = (char) (random.nextInt(26)+65);
        char newChar = (char) (random.nextInt(26)+65);

        return updateKey(key, oldChar, newChar);

    }

    private ArrayList<char[]> crossOver(ArrayList<char[]> population) {
        Random random = new Random();
        int crossPoint;
        int popSize = population.size();
        ArrayList<char[]> newGen = new ArrayList<>();
        Pair<char[],char[]> siblings;

        for (int i = 0; i <popSize; i+=2) {
            crossPoint = random.nextInt(26);
            if(i+1==popSize)break;
            siblings = getChildren(crossPoint, population.get(i), population.get(i + 1));
            newGen.add(siblings.getValue0());
            newGen.add(siblings.getValue1());

        }
        return newGen;
    }

    private Pair<char[],char[]> getChildren(int crossPoint, char[] parentOne, char[] parentTwo) {

        Random random = new Random();
        char[] childOne = new char[26];
        ArrayList<Character> childOneList = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
        ArrayList<Integer> cOMissed = new ArrayList<>();
        char[] childTwo = new char[26];
        ArrayList<Character> childTwoList = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
        ArrayList<Integer> cTMissed = new ArrayList<>();

        for(int i=0; i<26; i++){

            char parentOneLett = parentOne[i];
            char parentTwoLett = parentTwo[i];

            if(i>crossPoint){
                if(childOneList.contains(parentOneLett)){
                    childOne[i] = parentOneLett;
                    childOneList.remove(childOneList.indexOf(parentOneLett));
                }else{
                    cOMissed.add(i);
                }
                if(childTwoList.contains(parentTwoLett)){
                    childTwo[i] = parentTwoLett;
                    childTwoList.remove(childTwoList.indexOf(parentTwoLett));
                }else{
                    cTMissed.add(i);
                }
            }else{
                if(childOneList.contains(parentTwoLett)){
                    childOne[i] = parentTwoLett;
                    childOneList.remove(childOneList.indexOf(parentTwoLett));
                }else{
                    cOMissed.add(i);
                }
                if(childTwoList.contains(parentOneLett)){
                    childTwo[i] = parentOneLett;
                    childTwoList.remove(childTwoList.indexOf(parentOneLett));
                }else{
                    cTMissed.add(i);
                }
            }
        }

//        System.out.println("Before fix");
//        System.out.println("Child one:      " + Arrays.toString(childOne));
//        System.out.println("Child one List: " + childOneList.toString());
//        System.out.println("C1 Missed pos:  " + cOMissed.toString());
//        System.out.println("Child Two:      " + Arrays.toString(childTwo));
//        System.out.println("Child Two List: " + childTwoList.toString());
//        System.out.println("C2 Missed pos:  " + cTMissed.toString());

        int randPos;
        while(!cOMissed.isEmpty() || !cTMissed.isEmpty()){
            if(!cOMissed.isEmpty()){
                randPos = random.nextInt(childOneList.size());
                childOne[cOMissed.get(0)] = childOneList.get(randPos);
                cOMissed.remove(0);
                childOneList.remove(childOneList.get(randPos));
            }
            if(!cTMissed.isEmpty()){
                randPos = random.nextInt(childTwoList.size());
                childTwo[cTMissed.get(0)] = childTwoList.get(randPos);
                cTMissed.remove(0);
                childTwoList.remove(childTwoList.get(randPos));
            }
        }
//        System.out.println("After fix");
//        System.out.println("Child one:      " + Arrays.toString(childOne));
//        System.out.println("Child one List: " + childOneList.toString());
//        System.out.println("C1 Missed pos:  " + cOMissed.toString());
//        System.out.println("Child Two:      " + Arrays.toString(childTwo));
//        System.out.println("Child Two List: " + childTwoList.toString());
//        System.out.println("C2 Missed pos:  " + cTMissed.toString());

        return new Pair<>(childOne, childTwo);
    }

    private ArrayList<char[]> selectPop(ArrayList<char[]> population, int popSize) {

        int newSize = popSize/2;
        if(newSize%2!=0) newSize +=1;

        ArrayList<char[]> newPop = new ArrayList<>(newSize);
        LinkedHashMap<char[], Double> fitnessMap = getfitnessMap(population);
        Set<char[]> keys = fitnessMap.keySet();

        int count = 0;
        for (char[] key : keys) {
            newPop.add(key);
            count++;
            if(count==newSize) break;
        }
        return newPop;
    }

    private LinkedHashMap<char[], Double> getfitnessMap(ArrayList<char[]> population) {
        LinkedHashMap<char[],Double> fitnessMap = new LinkedHashMap<>();
        for (char[] key : population) {
            fitnessMap.put(key,getFitnessOfKey(key,3));
        }

        fitnessMap = (LinkedHashMap<char[], Double>) sortByRevValue(fitnessMap);
        return fitnessMap;
    }

    private ArrayList<char[]> initialisePop(char[] startKey, int popSize) {

        ArrayList<char[]> population = new ArrayList<>(popSize);
        if(!useSearchSpace){
            for (int i = 0; i < popSize; i++) {
                population.add(randKey(population));
            }
        }else{
            for (int i = 0; i < popSize/2; i++) {
                population.add(randKeyFromSearchSpace(population));
                population.add(randIterKeyFromSearchSpace(population, startKey));
            }
        }
        return population;
    }

    private char[] randIterKeyFromSearchSpace(ArrayList<char[]> population, char[] startKey) {

        Random random = new Random();
        int randLocInFreq;
        int locInKey;
        char oldLetter;
        char newLetter;
        char[] key = new char[26];

        int amountOfChanges = random.nextInt(11)+1;

        do {
            for(int i=0; i<amountOfChanges; i++){
                do{
                    randLocInFreq = random.nextInt(26);
                    oldLetter = Character.toLowerCase(msgFreqList.get(randLocInFreq));
                    locInKey = keyAsString.indexOf(oldLetter);

                }while(searchSpace.get(locInKey).size()<1 || randLocInFreq<mistakeLoc);

                //Find possible new letter using the searchSpace
                newLetter = searchSpace.get(locInKey).get(random.nextInt(searchSpace.get(locInKey).size()));
                key = updateKey(startKey,oldLetter,newLetter);
            }

        }while(population.contains(key));
        return key;
    }

    private char[] randKeyFromSearchSpace(ArrayList<char[]> population) {

        Random rand = new Random();
        char[] key = new char[26];

        ArrayList<Character> alphabetHere;
        ArrayList<Character> charsHere;
        char charHere;
        ArrayList<Integer> missPos = new ArrayList<>();
        ArrayList<Character> trackChars = new ArrayList<>();
        int posOfChar;
        do{
            alphabetHere = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
            for (int i = 0; i<26; i++) {

                //Grab list of possible chars
                charsHere = searchSpace.get(i);
                if(charsHere.size()>0){
                    do{
                        //Find random valid char
                        posOfChar = rand.nextInt(charsHere.size());
                        charHere = searchSpace.get(i).get(posOfChar);

                        //If not already added char
                        //System.out.println("Alphabet here: " + alphabetHere.toString());
                        if(alphabetHere.contains(charHere)) {
                            key[i] = charHere;
                            alphabetHere.remove(alphabetHere.indexOf(charHere));
                            break;
                        }
                        trackChars.add(key[i]);
                    }while(trackChars.size()<charsHere.size());

                    //If no valid letter was found
                    if(trackChars.size()==charsHere.size()){
                        missPos.add(i);
                    }
                }else{
                    missPos.add(i);
                }
                trackChars.clear();
            }

            //Loop through and fill through all empty key positions
            while(!missPos.isEmpty()){
                posOfChar = rand.nextInt(alphabetHere.size());
                key[missPos.get(0)] = alphabetHere.get(posOfChar);
                missPos.remove(0);
                alphabetHere.remove(posOfChar);
            }
        }while(population.contains(key));
        return key;
    }

    //Update to allow random key using search space
    private char[] randKey(ArrayList<char[]> population) {

        Random rand = new Random();
        char[] key = new char[26];

        ArrayList<Character> chars = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
        int pos;
        do{
            for (int i = 0; i <26; i++) {

                pos = rand.nextInt(chars.size());
                key[i] = chars.get(pos);
                chars.remove(pos);

            }
        }while(population.contains(key));
        return key;
    }

    //Method used to simplify the use of the map.entry method for ordering maps
    protected static <K, V extends Comparable<? super V>> Map<K, V> sortByRevValue(Map<K, V> map) {
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
