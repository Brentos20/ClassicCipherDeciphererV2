package model.ciphers;

public class RailFence extends Cipher{
    private String info = "";
    private int key;


    public RailFence(String msg){

        super(msg);

    }
    public RailFence(String msg, int key){

        super(msg, key);
        this.key = key1;

    }


    @Override
    public String encrypt(){
        StringBuilder to_return = new StringBuilder();
        int hopLength = (key-1)*2;
        int smallHop = 0;
        int pos;

        for(int i = 0; i < key; i++){

            pos = i;

            if(i > 0){
                smallHop+= 2;
            }

            while(pos < textLength){

                to_return.append(msg.charAt(pos));
                pos += hopLength;

                if((pos - smallHop) < textLength && i > 0 && i != key-1) {

                    to_return.append(msg.charAt(pos - smallHop));
                }
            }
        }
        return to_return.toString();
    }

    @Override
    public String decrypt() {
        if(key == 1){
            return msg;
        }
        int hopLength = (key-1)*2;
        int smallHop = 0;
        int posInGrid;
        int posInText = 0;
        String[] textArray = new String[textLength];
        StringBuilder to_return = new StringBuilder();
        for(int i = 0; i<key; i++){
            posInGrid = i;

            if(i > 0){
                smallHop+= 2;
            }

            while(posInGrid < textLength){
                textArray[posInGrid] = String.valueOf(msg.charAt(posInText));
                posInGrid += hopLength;
                posInText++;

                if((posInGrid - smallHop) < textLength && i > 0 && i != key-1) {
                    textArray[posInGrid - smallHop] = String.valueOf(msg.charAt(posInText));
                    posInText++;
                }
            }
        }
        for (String letter: textArray) {
            to_return.append(letter);

        }
        return to_return.toString();
    }

    @Override
    public String[] decryptPossibilities() {

        String[] to_return = new String[textLength-1];

        for(int i=1; i<textLength; i++){
            key = i;
            to_return[i-1] = decrypt();
        }

        return to_return;
    }
}
