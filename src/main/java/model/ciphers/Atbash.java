package model.ciphers;

public class Atbash extends Cipher{

    private String info = "";

    public Atbash(String msg){

        super(msg);

    }

    @Override
    public String encrypt(){

        StringBuilder to_return = new StringBuilder();

        for(int i = 0; i < textLength; i++) {
            char letter = msg.charAt(i);

            if (Character.isLetter(letter)) {

                if (Character.toLowerCase(letter) - 97 <= 13){
                    to_return.append((char)(letter + (25 - ((Character.toLowerCase(letter) - 97)*2))));
                }else{
                    to_return.append((char)(letter - (25 - ('z' - Character.toLowerCase(letter))*2)));
                }

            } else {
                to_return.append(letter);
            }
        }
        return to_return.toString();
    }

    @Override
    public String decrypt() {
       return encrypt();
    }

    @Override
    public String[] decryptPossibilities() {
        String[] to_return = new String[1];
        to_return[0] = decrypt();
        return to_return;
    }

}
