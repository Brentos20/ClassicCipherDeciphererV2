package model.ciphers;

import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.util.ArrayList;
import java.util.List;




/**
 * @project ClassicalCipherDecipherer
 */
public class Baconian extends Cipher{

    //Map that stores the entries for replacing each character in this cipher
    private List<Pair<Character,String>> baconList;

    public Baconian(String msg){
        super(msg);
        baconList = new ArrayList<>();
        baconList.add(new Pair<>('a',"aaaaa"));
        baconList.add(new Pair<>('b',"aaaab"));
        baconList.add(new Pair<>('c',"aaaba"));
        baconList.add(new Pair<>('d',"aaabb"));
        baconList.add(new Pair<>('e',"aabaa"));
        baconList.add(new Pair<>('f',"aabab"));
        baconList.add(new Pair<>('g',"aabba"));
        baconList.add(new Pair<>('h',"aabbb"));
        baconList.add(new Pair<>('i',"abaaa"));
        baconList.add(new Pair<>('j',"abaab"));
        baconList.add(new Pair<>('k',"ababa"));
        baconList.add(new Pair<>('l',"ababb"));
        baconList.add(new Pair<>('m',"abbaa"));
        baconList.add(new Pair<>('n',"abbab"));
        baconList.add(new Pair<>('o',"abbba"));
        baconList.add(new Pair<>('p',"abbbb"));
        baconList.add(new Pair<>('q',"baaaa"));
        baconList.add(new Pair<>('r',"baaab"));
        baconList.add(new Pair<>('s',"baaba"));
        baconList.add(new Pair<>('t',"baabb"));
        baconList.add(new Pair<>('u',"babaa"));
        baconList.add(new Pair<>('v',"babab"));
        baconList.add(new Pair<>('w',"babba"));
        baconList.add(new Pair<>('x',"babbb"));
        baconList.add(new Pair<>('y',"bbaaa"));
        baconList.add(new Pair<>('z',"bbaab"));
    }

    @Override
    public String encrypt() {
        StringBuilder to_return = new StringBuilder();
        int alteringValue;
        boolean isCap;

        for(int i = 0; i < textLength; i++) {
            char letter = msg.charAt(i);

            if (Character.isLetter(letter)) {

                if(Character.isUpperCase(letter)) {
                    alteringValue = 65;
                    isCap = true;

                }else{
                    alteringValue = 97;
                    isCap = false;
                }

                //replace the letter with the corresponding string
                if(isCap)to_return.append((baconList.get(letter-alteringValue).getValue1()).toUpperCase());
                else to_return.append((baconList.get(letter-alteringValue).getValue1()));


            }else{
                to_return.append(letter);
            }

        }

        return to_return.toString();
    }


    @Override
    public String decrypt() {
        StringBuilder to_return = new StringBuilder();
        int alteringValue;
        char letter;
        for(int i = 0; i < textLength; i++) {
            letter = msg.charAt(i);

            if (Character.isLetter(letter)) {

                if(Character.isUpperCase(letter)) {

                    alteringValue = 65;

                }else{

                    alteringValue = 97;

                }
                //replace the letter with the corresponding string
                to_return.append(convertToLetter(msg.substring(i,i+5),alteringValue));
                i+=4;

            }else{
                to_return.append(letter);
            }

        }
        return to_return.toString();
    }

    private char convertToLetter(String substring, int alteringValue) {
        boolean isCap = (alteringValue==65);
        int pos = 0;
        if(isCap) substring = substring.toLowerCase();

        for(int i = 4; i >= 0; i--){
            if(substring.charAt(i) == 'a') continue;
            if(substring.charAt(i)== 'b'){
                switch (i){
                    case 4:
                        pos+=1;
                        break;
                    case 3:
                        pos+=2;
                        break;
                    case 2:
                        pos+=4;
                        break;
                    case 1:
                        pos+=8;
                        break;
                    case 0:
                        pos+=16;
                        break;
                }
            }
        }
        char to_return = (char)(pos + alteringValue);
        if(isCap) return Character.toUpperCase(to_return);
        return to_return;

    }

    @Override
    public String[] decryptPossibilities() {
        return new String[]{decrypt()};
    }


}
