package model.ciphers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * @project ClassicalCipherDecipherer
 */
public class OneTimePad extends Cipher {

    private ArrayList<Character> alphabet = new ArrayList<>(
            Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));

    public OneTimePad(String msg) {
        super(msg);
    }

    @Override
    public String encrypt() {
        ArrayList<char[]> deadKeys = new ArrayList<>();
        StringBuilder encrypted = new StringBuilder();
        char[] thisKey;
        char thisLetter;
        int altvalue;

        for (int i = 0; i <textLength; i++) {
            thisLetter = msg.charAt(i);
            if(Character.isLetter(thisLetter)){
                do{
                    thisKey = randomKey();
                }while(deadKeys.contains(thisKey));
                deadKeys.add(thisKey);

                if(Character.isUpperCase(thisLetter)){
                    altvalue = 65;
                }else{
                    altvalue = 97;
                }

                encrypted.append(thisKey[thisLetter-altvalue]);
            }else{
                encrypted.append(thisLetter);
            }

        }

        return  encrypted.toString();
    }

    private char[] randomKey() {
        ArrayList<Character> alpha = new ArrayList<>((Collection<? extends Character>) alphabet.clone());
        char[] newKey = new char[26];
        int pos = 0;
        Random random = new Random();

        while(alpha.size()>0) {
            int loc = random.nextInt(alpha.size());
            newKey[pos] = alpha.get(loc);
            alpha.remove(loc);
            pos++;
        }
        return newKey;
    }
    @Override
    public String decrypt() {
        return null;
    }

    @Override
    public String[] decryptPossibilities() {
        return new String[0];
    }
}
