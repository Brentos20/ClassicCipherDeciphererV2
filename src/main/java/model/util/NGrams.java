package model.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Contains all the information derived from the CSVs in resources
 */
public class NGrams {

    private HashMap<String, ArrayList<String>> oneGramTable;
    private HashMap<String, ArrayList<String>> diGramTable;
    private HashMap<String, ArrayList<String>> triGramTable;
    private HashMap<String, ArrayList<String>> quadGramTable;

    private final String DELIMITER = ",";

    //Represents total
    private final String TOTAL_NGRAM_COUNT = "****";

    /**
     * Basic constructor that initialises the ngram-table variables with {@link #readCSV(int)}
     */
    public NGrams(){

        oneGramTable = readCSV(1);
        diGramTable = readCSV(2);
        triGramTable = readCSV(3);
        quadGramTable = readCSV(4);

    }

    /**
     * reads and extracts all the information from the a specific CSV
     * @param iD signifies which CSV to extract from
     * @return the {@link HashMap} whose Key = nGram, and Value = a list of stats about nGram
     */
    private HashMap<String, ArrayList<String>> readCSV(int iD){

        String line;
        List<String> takeLine;
        HashMap<String, ArrayList<String>> nGramTable = new HashMap<>();

        try {
            //Initialises using the right CSV using the passed iD
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ngrams" + String.valueOf(iD) + ".csv") ;
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

            //reads all lines in file
            while((line = fileReader.readLine()) != null){

                //First splits every line via a delimiter then passes that into a new arrayList
                takeLine = new ArrayList<>(Arrays.asList(line.split(DELIMITER)));
                //Puts the first value which is the ngram itself as Key, and the rest as Value
                nGramTable.put(takeLine.get(0), (ArrayList<String>) takeLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Removes first line
        nGramTable.remove(String.valueOf(iD) + "-gram");
        return nGramTable;
    }

    /**
     * Takes a {@link String} and finds all the groups of n-letters, without spaces or symbols between them, and returns
     * an {@link ArrayList} of those nGrams
     * @param msg word or sentence that is passed
     * @param n the n in nGrams
     * @return the {@link ArrayList} of nGrams
     */
    public ArrayList<String> getNGrams(String msg, int n){
        ArrayList<String> nGrams = new ArrayList<>();
        StringBuilder tempChars = new StringBuilder();

        //In case word is too small to produce nGrams
        if(msg.length()<n){
            nGrams.add(msg);
            return nGrams;
        }

        for(int i = 0; i < msg.length(); i++){

            //Loop through each potential nGram
            for(int j = i; j < (i+n); j++){

                //Records each letter
                if((i+n-1) < msg.length() && Character.isLetter(msg.charAt(j))){
                    tempChars.append(msg.charAt(j));

                }else{
                    //Starts again if found break in potential nGram
                    tempChars.delete(0,tempChars.length());
                    j=(j+n);
                }
                //Adds nGram when n value is met
                if(tempChars.length() == n){
                    nGrams.add(tempChars.toString());
                    tempChars.delete(0,n);
                }
            }
        }
        return nGrams;
    }

    /**
     * Gets the fitness value for the set of Ngrams
     * @param nGrams An {@link ArrayList} of {@link String}s that represent nGrams
     * @param n The n in Ngram
     * @return The fitness value as a {@link Double}
     */
    public double getFitness(ArrayList<String> nGrams, int n){

        ArrayList<String> line;
        HashMap<String, ArrayList<String>> nGramTable = null;
        double fitness = 0;

        //If an empty list is passed
        if(nGrams.size() == 0){
            return 0;
        }

        //Finds the right nGram through the passed n
        switch (n){
            case 1:
                nGramTable = oneGramTable;
                break;
            case 2:
                nGramTable = diGramTable;
                break;
            case 3:
                nGramTable = triGramTable;
                break;
            case 4:
                nGramTable = quadGramTable;
                break;

            default :
                System.out.println("Wrong int passed through getFitnessOfStringKey");
                break;
        }

        double totalNCount;
        if (nGramTable != null) {
            //This is the total value derived from the CSV as the overall total ngrams of that table
            totalNCount = Double.parseDouble(nGramTable.get(TOTAL_NGRAM_COUNT).get(1));

            for (String nGram: nGrams) {

                if(nGramTable.containsKey(nGram.toUpperCase())){
                    line = nGramTable.get(nGram.toUpperCase());

                    //Fitness = log value of dividing this nGram count by the total nGram count
                    fitness+= Math.log(Double.parseDouble(line.get(1))/totalNCount);
                }else{

                    //In case this nGram was never recorded, fitness given is as low as possible
                    fitness+= Math.log(1/totalNCount);
                }
            }
        }
        return fitness;
    }


    public HashMap<String, ArrayList<String>> getOneGramTable() {
        return oneGramTable;
    }

    public HashMap<String, ArrayList<String>> getDiGramTable() {
        return diGramTable;
    }

    public HashMap<String, ArrayList<String>> getTriGramTable() {
        return triGramTable;
    }

    public HashMap<String, ArrayList<String>> getQuadGramTable() {
        return quadGramTable;
    }


}
