package model.ciphers;

public class Affine extends Cipher {

    private String info = "";
    private int firstKey;
    private int secondKey;
    private static int[] coPrimes = {1,3,5,7,9,11,15,17,19,21,23,25};

    public Affine(String msg, int key1, int key2){

        super(msg, key1, key2);
        this.firstKey = key1;
        this.secondKey = key2;
    }

    public Affine(String msg){

        super(msg);
    }

    @Override
    public String encrypt(){

        int alteringValue;
        StringBuilder to_return = new StringBuilder();
        char alteredChar;

        for(int i = 0; i < textLength; i++){

            char actualChar = msg.charAt(i);

            if(Character.isLetter(actualChar)){

                //Change the altering value to keep capitals
                if(Character.isUpperCase(actualChar)){

                    alteringValue = 65;
                }else{

                    alteringValue = 97;
                }

                alteredChar = (char) ((int)actualChar - alteringValue);
                to_return.append( (char) ((((firstKey * (int)alteredChar) + secondKey) % 26) + alteringValue));

            }else{
                to_return.append(actualChar);
            }

        }
        return to_return.toString();
    }

    @Override
    public String decrypt() {
        int alteringValue;
        StringBuilder to_return = new StringBuilder();
        char alteredChar;
        int letterPos;
        int inverseKey = modularInverse(firstKey);

        for(int i = 0; i < textLength; i++){

            char actualChar = msg.charAt(i);

            if(Character.isLetter(actualChar)){

                //Change the altering value to keep capitals
                if(Character.isUpperCase(actualChar)){

                    alteringValue = 65;
                }else{

                    alteringValue = 97;
                }

                alteredChar = (char) ((int)actualChar - alteringValue);
                letterPos = (int) alteredChar;
                while((letterPos - secondKey) < 0){
                    letterPos+= 26;
                }
                to_return.append( (char) (((((letterPos - secondKey)*inverseKey) % 26) + alteringValue)));

            }else{
                to_return.append(actualChar);
            }

        }
        return to_return.toString();
    }

    private int modularInverse(int numb){
        for(int i = 1; i<=26; i++){
            if(((numb*i) % 26) == 1){
                return i;
            }
        }
        return 0;
    }

    @Override
    public String[] decryptPossibilities() {
        String[] to_return = new String[12*26];
        int pos = 0;
        for(int i = 0; i < coPrimes.length; i++){
            for(int j = 0; j < 26; j++){
                setAffineKeys(coPrimes[i],j);
                to_return[pos] = decrypt();
                pos++;
            }
        }
        return to_return;
    }

    private void setAffineKeys(int newKey1, int newkey2) {
        firstKey = newKey1;
        secondKey = newkey2;
    }

    private static boolean isCoPrime(int firstKey){
        for (int numb: coPrimes) {
            if(numb == firstKey) return true;
        }
        return false;
    }

}
