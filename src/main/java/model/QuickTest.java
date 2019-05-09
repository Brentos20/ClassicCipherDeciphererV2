package model;

import model.ciphers.Nihilist;
import model.ciphers.SimpleSubstitution;
import model.util.NGrams;
import model.util.WorldLangStats;

import java.util.*;

/**
 * @project ClassicalCipherDecipherer
 */
public class QuickTest {

    public static void main(String[] args){
        //Nihilist nihilist = new Nihilist("Crytography has been through numerous phases of evolution. Early ciphers in cryptography were designed to allow encryption and decryption to take place by hand, while those which are developed and used today are only possible due to the high computational performance of modern machines (i.e the computer you are using right now). The major eras which have shaped cryptography are listed below.", "childishgambino");
        //System.out.println(nihilist.encrypt());
        //nihilist.genComboList();
        //75 38 42 75 86 75 45 86 83 37
        //h  e  l  l  o  w  o  r  l  d
        //w  h  a  t  w  h  a  t  w  h
        //0  1  2  3  4  5  6  7  8  9
        //nihilistStats();
        //Nihilist nihilist = new Nihilist("2665787548468534573486 353576 46283857 75376677684434 6557564876476867 663735863865 4553 277567655867486547. 3954655365 45365956495566 4864 276697586645545435685767 75397329 3858664633652738 7768 2454556566 39763664656756486767 245638 45293785775755564657 7768 57344946 4955543637 2386 35356648, 6546486229 6866576526 8435484657 246539 452975585456464726 356648 58663945 5858573476 227427 58666567 5858745748555437 257727 6867 783638 47553647 56575446775635775847563562 4939854456536423574649 4744 566528398556 5422453548664956 (47.39 753739 5657544677563975 884768 357329 6986475533 7436465678 465776). 753739 7534464574 27664477 6546484437 47547437 545523594848 266578665858656533465566 357549 444767752938 5538534584.");
        //String msg = nihilist.decrypt();
        //System.out.println(msg);

//        String msg = "By so delight of showing neither believe he present. Deal sigh up in shew away when. Pursuit express no or prepare replied. Wholly formed old latter future but way she. Day her likewise smallest expenses judgment building man carriage gay. Considered introduced themselves mr to discretion at. Means among saw hopes for. Death mirth in oh learn he equal on. ";

//        String key = "abcdefghijklmnopqrstuvwxyz";
//        key = new StringBuilder(key).reverse().toString();
        //SimpleSubstitution simSub = new SimpleSubstitution(msg,new StringBuilder(key).reverse().toString());
        //System.out.println(simSub.encrypt());

//        String cipherText = "Yb hl wvortsg lu hsldrmt mvrgsvi yvorvev sv kivhvmg. Wvzo hrts fk rm hsvd zdzb dsvm. Kfihfrg vckivhh ml li kivkziv ivkorvw. Dsloob ulinvw low ozggvi ufgfiv yfg dzb hsv. Wzb svi orpvdrhv hnzoovhg vckvmhvh qfwtnvmg yfrowrmt nzm xziirztv tzb. Xlmhrwvivw rmgilwfxvw gsvnhvoevh ni gl wrhxivgrlm zg. Nvzmh znlmt hzd slkvh uli. Wvzgs nrigs rm ls ovzim sv vjfzo lm.";
//        SimAnneal simAnneal = new SimAnneal(cipherText, nGrams);
//        String randKey = simAnneal.randStringKey();
//
//        String solvedMsg = simAnneal.decryptWithStringKey(key);
//        String notSoSolved = simAnneal.decryptWithStringKey(randKey);
//        double bestFit =  simAnneal.getFitnessOfStringKey(key);
//        double notsoFit = simAnneal.getFitnessOfStringKey(randKey);
//        System.out.println("Fitness of solved: " + bestFit);
//        System.out.println("Fitness of notso: " + notsoFit);
//

//        PrimeCipher pC = new PrimeCipher("able./");
//        String cipherText = pC.encrypt();
//        System.out.println(cipherText);
//        pC = new PrimeCipher(cipherText);
//        System.out.println(pC.decrypt());

       // NGrams nGrams = new NGrams();
       // WorldLangStats worldLangStats = new WorldLangStats(nGrams);
       // String text =       "His having within saw become ask passed misery giving. Recommend questions get too fulfilled. He fact in we case miss sake. Entrance be throwing he do blessing up. Hearts warmth in genius do garden advice mr it garret. Collected preserved are middleton dependent residence but him how. Handsome weddings yet mrs you has carriage packages. Preferred joy agreement put continual elsewhere delivered now. Mrs exercise felicity had men speaking met. Rich deal mrs part led pure will but. Talking chamber as shewing an it minutes. Trees fully of blind do. Exquisite favourite at do extensive listening. Improve up musical welcome he. Gay attended vicinity prepared now diverted. Esteems it ye sending reached as. Longer lively her design settle tastes advice mrs off who. Was certainty remaining engrossed applauded sir how discovery. Settled opinion how enjoyed greater joy adapted too shy. Now properly surprise expenses interest nor replying she she. Bore tall nay many many time yet less. Doubtful for answered one fat indulged margaret sir shutters together. Ladies so in wholly around whence in at. Warmth he up giving oppose if. Impossible is dissimilar entreaties oh on terminated. Earnest studied article country ten respect showing had. But required offering him elegance son improved informed. Feet evil to hold long he open knew an no. Apartments occasional boisterous as solicitude to introduced. Or fifteen covered we enjoyed demesne is in prepare. In stimulated my everything it literature. Greatly explain attempt perhaps in feeling he. House men taste bed not drawn joy. Through enquire however do equally herself at. Greatly way old may you present improve. Wishing the feeling village him musical. ";
       // String cipherText = "Oxs ocgxel yxaoxe scy ptudjt csq ncsstr jxstkf lxgxel. Ktudjjter zitsaxdes lta add miwmxwwtr. Ot mcua xe yt ucst jxss scqt. Teakceut pt aokdyxel ot rd pwtssxel in. Otckas yckjao xe ltexis rd lckrte crgxut jk xa lckkta. Udwwtuatr nktstkgtr ckt jxrrwtade rtntertea ktsxrteut pia oxj ody. Ocersdjt ytrrxels fta jks fdi ocs uckkxclt ncuqclts. Nktmtkktr hdf clkttjtea nia udeaxeicw twstyotkt rtwxgtktr edy. Jks tbtkuxst mtwxuxaf ocr jte sntcqxel jta. Kxuo rtcw jks ncka wtr nikt yxww pia. Acwqxel uocjptk cs sotyxel ce xa jxeiats. Aktts miwwf dm pwxer rd. Tbzixsxat mcgdikxat ca rd tbatesxgt wxsatexel. Xjnkdgt in jisxucw ytwudjt ot. Lcf caatertr gxuxexaf nktncktr edy rxgtkatr.";
        //SimpleSubstitution simpleSubstitution = new SimpleSubstitution(text, "");
        //System.out.println(simpleSubstitution.encrypt());
       // SimpleSubstitution simpleSub = new SimpleSubstitution(cipherText,nGrams,worldLangStats,"");
       // System.out.println(simpleSub.decrypt());

        //A[v, k, x, j, q],B[w, v, k, x, j, q],C[r, l, c, f, g, w, y, v, k, x],D[s, r, l, d, c, m, f, p, g],E[r, l, d, c, f, p, g],F[t, o, n, s, r, l],G[l, f, g, w, y, v, k, x, j, q],H[f, g, w, y, v, k, x, j, q, z],I[],J[x, j, q],K[j, q],L[f, w, y, v, k, x, j, q],M[l, c, f, g, w, y, v, k, x, j],N[r, l, d, c, m, f, p, g],O[e, t, a, o, i, n, s, r, h, l],P[q],Q[i, s, r, l, d, c, u, m, f, p],R[f, g, w, y, v, k, x, j, q],S[t, a, i, s, r, h, l, d, c, u],T[t, o, s, r, l, c],U[a, s, r, l, d, c, u, m, f, p],V[f, x],W[k, x, j, q],X[r, l, c, f, p, g],Y[r, l, c, f, g, w, y, b, v, k],Z[r, l, d, c, u, m, f, p, g, w],
        //
//        SimpleSubstitution simpleSub = new SimpleSubstitution(cipherText,nGrams,worldLangStats,"");
//        System.out.println(simpleSub.decrypt());
//        double start = System.nanoTime();
//        NewSimpleSub newSimpleSub = new NewSimpleSub(cipherText,nGrams,worldLangStats);
//        double end = System.nanoTime();


//        System.out.println("Time taken " + (end-start));

//        text = "One spliff a daya keep the evil awaya";
//        String key = "excuse me";
//        Vigenere vigenere = new Vigenere(text,key);
//        String encrypted = vigenere.encrypt();
//        System.out.println(encrypted);
//        vigenere = new Vigenere(encrypted,key);
//        System.out.println(vigenere.decrypt());

        //GeneticAlg geneticAlg = new GeneticAlg(cipherText,nGrams,1000,1000,0.1);
        //geneticAlg.startAlg();

        //geneticAlg.startAlg();


        for (double i = 100000; i>1;) {



            System.out.println("Chance: " + Math.exp((-0.5/i)));
            System.out.println("temp: " + i);
            i = i - (i*0.001);
        }
    }
    static void nihilistStats(){
        Nihilist nihilist = new Nihilist("Hello World", "What");
        //System.out.println(nihilist.encrypt());

        HashMap<Character, Integer> nihilMap = nihilist.getNihilMapLetterToNumb();
        Character[][] nihilMAtrix = nihilist.getNihilMatrix();


        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int sum = 0;
        ArrayList<Integer> sumList = new ArrayList<>();
        ArrayList<Integer> numbList = new ArrayList<>();
        ArrayList<Integer> invalidNumbList = new ArrayList<>();
        for (int i = 1; i<6; i++) {
            for(int j = 1; j<6; j++){
                numbList.add(Integer.valueOf(i + Integer.toString(j)));
            }
        }

        for(int i = 22; i<111; i++){
            invalidNumbList.add(i);
        }
        for (Integer integer : numbList) {
            for (Integer integer1 : numbList) {
                sum = integer + integer1;
                if(sum<lowest)lowest = sum;
                if(sum>highest) highest = sum;
                if(!sumList.contains(sum)) sumList.add(sum);
                if(invalidNumbList.contains(sum)) invalidNumbList.remove(invalidNumbList.indexOf(sum));
            }
        }
        System.out.println("Highest combo: " + highest);
        System.out.println("Lowest combo: " + lowest);

        Collections.sort(sumList);
        System.out.println("----------------All nihilist number combos----------------------------------------------");
        for (Integer numb : sumList) {
            System.out.print(numb + ", ");
        }
        System.out.println("\n");
        System.out.println("----------------All invalid numbers that don't occur in nihilist-------------------------");
        for (Integer invalid : invalidNumbList) {
            System.out.print(invalid + ", ");
        }
        System.out.println("\n");
    }
}
