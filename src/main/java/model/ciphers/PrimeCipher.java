package model.ciphers;

import model.util.NGrams;

import java.util.ArrayList;

/**
 * @project ClassicalCipherDecipherer
 */
public class PrimeCipher extends Cipher {

    String[] key;

    public PrimeCipher(String msg){
        super(msg.toLowerCase());
        key = new String[]{"2", "3", "5", "7", "11", "13", "17", "19", "23", "29", "31", "37", "41", "43", "47",
                "53", "59", "61", "67", "71", "73", "79", "83", "89", "97", "101"};
    }

    @Override
    public String encrypt() {

        StringBuilder to_return = new StringBuilder();
        char thisLetter;
        String primeLetter;
        for(int i=0; i<textLength; i++){
            thisLetter = msg.charAt(i);

            if(Character.isLetter(thisLetter)){
                primeLetter = key[thisLetter-97];
                to_return.append(primeLetter);
            }else{
                to_return.append(thisLetter);
            }
        }
        return to_return.toString();
    }

    @Override
    public String decrypt() {
        NGrams nGrams = new NGrams();
        ArrayList<String> possibleTexts = genPossibleTexts();

        double thisFitness;
        double bestFitness = Integer.MIN_VALUE;
        String msg = "";
        int nGramLength = 4;
        if(possibleTexts.get(0).length()<4) nGramLength = 2;
        for (String possibleText : possibleTexts) {
            thisFitness = nGrams.getFitness(nGrams.getNGrams(possibleText, nGramLength), nGramLength);
            if(thisFitness!=0 && thisFitness>bestFitness){
                bestFitness = thisFitness;
                msg = possibleText;
            }
        }
        return msg;
    }

    private ArrayList<String> genPossibleTexts() {
        //a=0="2", b=1="3", c=2="5", d=3="7", e=4="11", f=5="13", g=6="17", h=7="19", i=8="23", j=9="29", k=10="31", l=11="37", m=12="41", n=13="43", o=14="47",
        //p=15="53", q=16="59", r=17="61", s=18="67", t=19="71", u=20="73", v=21="79", w=22="83", x=23="89", y=24="97", z=25="101"
        ArrayList<String> posTextsList = new ArrayList<>();
        posTextsList.add("");
        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(0);

        ArrayList<Integer> possLetters;

        for (int i = 0; i < textLength; i++) {
            char letterHere = msg.charAt(i);
            //Since the letters at this moment are in order their indexes reflect so
            possLetters = getLetterPos(i);
            int posLettersSize = possLetters.size();
            for (int j = 0; j<indexes.size(); j++) {
                if(indexes.get(j)==i){
                    char thisLetter;
                    int numHere;
                    String thisText = posTextsList.get(j);
                    int indexHere = indexes.get(j);
                    int numLength;

                    if(posLettersSize==2){
                        numHere = possLetters.get(1);
                        numLength = key[numHere].length();
                        thisLetter = (char) (numHere+97);
                        posTextsList.add(thisText+thisLetter);
                        indexes.add(indexHere+numLength);
                    }

                    if(posLettersSize>0){
                        numHere = possLetters.get(0);
                        if(numHere==26){
                            thisLetter = letterHere;
                            numLength = 1;
                        }
                        else{
                            thisLetter = (char) (numHere+97);
                            numLength = key[numHere].length();
                        }
                        posTextsList.set(j,thisText+thisLetter);
                        indexes.set(j,indexHere+numLength);
                    }else{
                        posTextsList.set(j,thisText+letterHere);
                        indexes.set(j,indexHere+1);
                    }
                }
            }
            System.out.println(posTextsList);
        }
        return posTextsList;
    }

    //Outputs list of either size 1 or 2, that contains the key indexes of the possible letters to append or
    //that there is a non letter character indicated by a list with the singular value 26
    private ArrayList<Integer> getLetterPos(int i) {
        char thisNumb;
        char nextNumb;
        ArrayList<Integer> positions = new ArrayList<>();
        thisNumb = msg.charAt(i);
        boolean isLastDigit = (i+1==textLength);
        if (Character.isDigit(thisNumb)) {
            switch (thisNumb) {
                case '1':
                    if(!isLastDigit){
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '1':
                                positions.add(4);
                                break;
                            case '3':
                                positions.add(5);
                                break;
                            case '7':
                                positions.add(6);
                                break;
                            case '9':
                                positions.add(7);
                                break;
                            case '0':
                                positions.add(25);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '2':
                    positions.add(0);
                    if (!isLastDigit) {
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '3':
                                positions.add(8);
                                break;
                            case '9':
                                positions.add(9);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '3':
                    positions.add(1);
                    if (!isLastDigit) {
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '1':
                                positions.add(10);
                                break;
                            case '7':
                                positions.add(11);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '4':
                    if(!isLastDigit){
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '1':
                                positions.add(12);
                                break;
                            case '3':
                                positions.add(13);
                                break;
                            case '7':
                                positions.add(14);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '5':
                    positions.add(2);
                    if (!isLastDigit) {
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '3':
                                positions.add(15);
                                break;
                            case '9':
                                positions.add(16);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '6':
                    if(!isLastDigit){
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '1':
                                positions.add(17);
                                break;
                            case '7':
                                positions.add(18);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '7':
                    positions.add(3);
                    if (!isLastDigit) {
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '1':
                                positions.add(19);
                                break;
                            case '3':
                                positions.add(20);
                                break;
                            case '9':
                                positions.add(21);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '8':
                    if(!isLastDigit){
                        nextNumb = msg.charAt(i + 1);
                        switch (nextNumb) {
                            case '3':
                                positions.add(22);
                                break;
                            case '9':
                                positions.add(23);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case '9':
                    positions.add(24);
                    break;
                default:
                    positions.add(27);
                    break;
            }
        }else{
            positions.add(26);
        }
        return positions;
    }

    @Override
    public String[] decryptPossibilities() {
        return new String[0];
    }
}
