package model.ciphers;

public class Caesar extends Cipher {

    private String info = "";
    private int shiftKey;

    public Caesar(String msg, int key){

        super(msg, key);
        this.shiftKey = key1;

    }

    public Caesar(String msg){
        super(msg);
    }

    @Override
    public String encrypt(){

        int alteringValue;

        StringBuilder to_return = new StringBuilder();

        if(shiftKey < 0) shiftKey = 26 + shiftKey;

        for(int i=0; i<textLength; i++) {

            char actualChar = msg.charAt(i);

            if (Character.isLetter(actualChar)) {

                if(Character.isUpperCase(actualChar)){

                    alteringValue = 65;
                }else{
                    alteringValue = 97;
                }

                char alteredChar = (char) ((int)actualChar - alteringValue);
                to_return.append((char) (((((int)alteredChar) + shiftKey) % 26) + alteringValue));

            }else{
                to_return.append(actualChar);
            }

        }
        return to_return.toString();
    }

    @Override
    public String decrypt() {
        shiftKey = -shiftKey;
        return encrypt();
    }

    @Override
    public String[] decryptPossibilities() {

        String[] solutions = new String[26];
        for(int i = 0; i < 26; i++){
            shiftKey = i;
            solutions[i] = decrypt();
        }
        return solutions;
    }
}
