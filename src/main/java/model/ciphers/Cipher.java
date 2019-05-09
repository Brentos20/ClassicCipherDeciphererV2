package model.ciphers;

/**
 * Parent class of all cipher classes, contains basic variables, and methods for setting them
 */
public abstract class Cipher {

    protected String msg;
    protected int textLength;
    protected int key1;
    protected int key2;

    /**
     * Constructor for ciphers where a key hasn't been established, or be used by child classes for specific
     * configurations
     * @param msg The {@link String} message, not specified as encrypted or decrypted
     */
    Cipher(String msg){
        this.msg = msg;
        textLength = msg.length();

    }
    /**
     * Constructor for ciphers where one key has been established, or be used by child classes for specific
     * configurations
     * @param msg The {@link String} message, not specified as encrypted or decrypted
     */
    Cipher(String msg, int key1){
        this.msg = msg;
        textLength = msg.length();
        this.key1 = key1;
    }
    /**
     * Constructor for ciphers where two keys has been established, or be used by child classes for specific
     * configurations
     * @param msg The {@link String} message, not specified as encrypted or decrypted
     */
    Cipher(String msg, int key1, int key2){
        this.msg = msg;
        textLength = msg.length();
        this.key1 = key1;
        this.key2 = key2;
    }

    /**
     * Contains the method for encrypting a message through the specific cipher's method
     * @return alters the {@link #msg} to become encrypted, treated as cipherText
     */
    public abstract String encrypt();

    /**
     * Contains the method for decrypting a message through the specific cipher's method
     * @return alters the {@link #msg} to become decrypted, treated as plainText
     */
    public abstract String decrypt();

    public abstract String[] decryptPossibilities();

}
