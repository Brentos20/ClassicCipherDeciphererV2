package model.ciphers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @project ClassicalCipherDecipherer
 */
public class Vigenere extends Cipher {


    private boolean hasHey = true;
    private HashMap<Character,Integer> rows;
    private HashMap<Character,Integer> columns;
    private char[][] tableau;
    private String key;
    private String capMsg;


    public Vigenere(String msg, String key){
        super(msg);
        capMsg = msg.toUpperCase();
        genTableau();
        this.key = genKey(key);
        hasHey = true;

    }

    private String genKey(String key) {
        return key.replaceAll(" ", "").toUpperCase();
    }

    private void genTableau() {
        rows = new HashMap<>();
        columns = new HashMap<>();
        tableau = new char[26][26];
        for(int i = 0; i<26; i++){
            rows.put((char)(i+65), i);
            columns.put((char)(i+65), i);
        }
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                tableau[i][j] = (char)(((i+j)%26)+65);
            }
        }
    }

    public Vigenere(String msg) {
        super(msg);
        genTableau();
        capMsg = msg.toUpperCase();
    }

    @Override
    public String encrypt() {

        int keyPos = 0;
        int keyLength = key.length();
        int altValue = 0;
        char thisLett;
        char keyLett;
        StringBuilder encryptedMsg = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            thisLett = capMsg.charAt(i);

            if(Character.isLetter(thisLett)){

                keyLett = key.charAt(keyPos);

                if(!(msg.charAt(i)==thisLett)){
                    altValue = 32;
                }
                encryptedMsg.append((char)(tableau[rows.get(thisLett)][columns.get(keyLett)]+altValue));
                altValue = 0;
                keyPos++;
                if(keyPos==keyLength) keyPos = 0;
            }else{
                encryptedMsg.append(thisLett);
            }
        }
        return  encryptedMsg.toString();
    }

    @Override
    public String decrypt() {
        if(hasHey){
            return informedDecrypt();
        }
        else return "Please input key used, blind decryption has not been implemented for this key :( ";
    }

    private String informedDecrypt() {

        int keyPos = 0;
        int keyLength = key.length();
        int altValue = 0;
        char thisLett;
        char keyLett;
        StringBuilder dencryptedMsg = new StringBuilder();
        ArrayList<Character> rowList = new ArrayList<>(rows.keySet());

        for (int i = 0; i < textLength; i++) {
            thisLett = capMsg.charAt(i);

            if(Character.isLetter(thisLett)){

                if(!(msg.charAt(i)==thisLett)){
                    altValue = 32;
                }
                for (int j = 0; j <26; j++) {

                    if(tableau[rows.get((char)(j+65))][columns.get(key.charAt(keyPos))]==thisLett){
                        dencryptedMsg.append((char)(rowList.get(j)+altValue));
                        break;
                    }
                }
                altValue = 0;
                keyPos++;
                if(keyPos==keyLength) keyPos = 0;
            }else{
                dencryptedMsg.append(thisLett);
            }
        }
        return dencryptedMsg.toString();
    }

    @Override
    public String[] decryptPossibilities() {
        return new String[0];
    }

    public HashMap<Character, Integer> getRows() {
        return rows;
    }

    public HashMap<Character, Integer> getColumns() {
        return columns;
    }

    public char[][] getTableau() {
        return tableau;
    }
}
