package model.ciphers;

/**
 * Simple cipher class that employs Caesar at shift value 13
 */
public class Rot13 extends Cipher {

    private int key = 13;

    public Rot13(String msg){
        super(msg);
    }


    @Override
    public String encrypt() {
        Caesar caesar = new Caesar(msg, key);
        return caesar.encrypt();
    }

    @Override
    public String decrypt() {
        Caesar caesar = new Caesar(msg, key);
        return  caesar.decrypt();
    }

    @Override
    public String[] decryptPossibilities() {

        String[] to_return = new String[1];
        to_return[0] = decrypt();
        return to_return;
    }
}
