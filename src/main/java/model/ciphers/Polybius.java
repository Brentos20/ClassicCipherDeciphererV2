package model.ciphers;

import java.util.Vector;

/**
 * @project ClassicalCipherDecipherer
 */
public class Polybius extends Cipher {

    private Character[][] polyMatrix;

    public Polybius(String msg) {
        super(msg);
        initialiseMatrix();
    }

    private void initialiseMatrix() {
        polyMatrix = new Character[7][7];
        int numbs = 0;
        char letter;

        //Creates 6x6 matrix starts at 1 for ease of relation to Polybius matrix
        for(int i = 1; i<7; i++){
            for(int j = 1; j<7; j++){

                if(!(i==6) && !(i>4 && j>=3) ){
                    letter = (char) (((i-1)*6)+(j-1) + 97);

                    polyMatrix[i][j] = letter;
                }else{
                    polyMatrix[i][j] = Character.forDigit(numbs,10) ;
                    numbs++;
                }


            }
        }
    }

    @Override
    public String encrypt() {
        StringBuilder to_return = new StringBuilder();
        msg = msg.toLowerCase();
        char letter;
        int letterPos;
        int row;
        int col;

        for(int i = 0; i<textLength; i++){
            letter = msg.charAt(i);
            if(Character.isLetterOrDigit(letter)){

                //So as to find the right position in matrix
                if(Character.isLetter(letter)) letterPos = (int) msg.charAt(i) - 97;
                else letterPos = (26 + Character.digit(letter,10));

                //Exploiting 6x6 structure to find the values of row and column
                row = (letterPos / 6) +1;
                col = (letterPos % 6) +1;
                //System.out.println(((char)(letterPos + 97))+": " + letterPos + " " + " q: " + row + " r: " + col);

                to_return.append(row).append(col);
            }else{
                to_return.append(letter);
            }
        }
        return to_return.toString();

    }

    @Override
    public String decrypt() {
        StringBuilder to_return = new StringBuilder();
        String number;
        for(int i=0; i < textLength; i++){
            if(Character.isDigit(msg.charAt(i))){
                number = msg.substring(i,i+2);
                to_return.append(polyMatrix[Integer.valueOf(number.substring(0,1))][Integer.valueOf(number.substring(1))]);
                i++;
            }else{
                to_return.append(msg.charAt(i));
            }
        }
        return to_return.toString();
    }

    @Override
    public String[] decryptPossibilities() {
        return new String[]{decrypt()};
    }
}
