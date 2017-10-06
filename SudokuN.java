/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Tommi
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class SudokuN {
    private static ArrayList<HashSet<Integer>> estoLista;
    private static ArrayList<HashSet<Integer>> hakuLista;
    private static ArrayList<HashSet<Integer>> loydetytRivi;
    private static ArrayList<HashSet<Integer>> loydetytSarake;
    private static ArrayList<HashSet<Integer>> loydetytRuutu;
    private static ArrayList<HashSet<Integer>> mahdolliset;
    private static int laskuri;
    private static int SIZE;
    private static ArrayList<String> merkisto;
    private static boolean omaMerkisto;
    private static HashSet<Integer> rivit;
    private static HashSet<Integer> arvot; 

    
    private static void combinationUtil(int arr[], int data[], int start,
                                int end, int index, int r)
    {
        // Current combination is ready to be printed, print it
        if (index == r)
        {
           HashSet<Integer> ts = new HashSet<Integer>();
            for (int j=0; j<r; j++) ts.add(data[j]);

            hakuLista.add(ts);
            return;
        }
 
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    private static void asetaMahdolliset(int[][] sudoku) {
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
 }  
//Pointing Pairs, Pointing Triples
    private static boolean lukitseRivi(int[][] sudoku) {
        //tulostaSudoku(sudoku);
        //tulostaVaihtoehdot(sudoku);
        boolean paluu = false;
        
        //vaakaan
        for (int ruutuY = 0 ; ruutuY < SIZE*SIZE ; ruutuY=ruutuY+SIZE) {
            for (int ruutuX = 0 ; ruutuX < SIZE*SIZE ; ruutuX=ruutuX+SIZE) {
                for (int ruudunRivi = 0 ; ruudunRivi < SIZE ; ruudunRivi++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sy = 0 ; sy < SIZE ; sy++) {
                        for (int sx = 0 ; sx < SIZE ; sx++) {
                            if (sy == ruudunRivi) {
                                omat.addAll(mahdolliset.get((ruutuY+sy)*SIZE*SIZE + ruutuX + sx));
                            } else {
                                muut.addAll(mahdolliset.get((ruutuY+sy)*SIZE*SIZE + ruutuX + sx));
                            }
                        }
                    }
                    //System.out.println("Vaaka: "+ruutuY+":"+ruutuX+" " +ruudunRivi +" - " + omat.toString()+muut.toString());
                   
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int y = ruutuY+ruudunRivi;
                        for (int luku : omat) {
                            for (int x = 0 ; x < SIZE*SIZE; x++) {
                                if ( x < ruutuX || x >= ruutuX + SIZE ) {
                                    if (sudoku[y][x] == 0 && !estoLista.get(y*SIZE*SIZE + x).contains(luku) && mahdolliset.get(y*SIZE*SIZE + x).contains(luku)) {
                                        //System.out.println("LukitseVaaka " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE + x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE + x).remove(luku);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //pystyyn
        for (int ruutuY = 0 ; ruutuY < SIZE*SIZE ; ruutuY=ruutuY+SIZE) {
            for (int ruutuX = 0 ; ruutuX < SIZE*SIZE ; ruutuX=ruutuX+SIZE) {
                for (int ruudunSarake = 0 ; ruudunSarake < SIZE ; ruudunSarake++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sy = 0 ; sy < SIZE ; sy++) {
                        for (int sx = 0 ; sx < SIZE ; sx++) {
                            if (sx == ruudunSarake) {
                                omat.addAll(mahdolliset.get((ruutuY+sy)*SIZE*SIZE + ruutuX + sx));
                            } else {
                                muut.addAll(mahdolliset.get((ruutuY+sy)*SIZE*SIZE + ruutuX + sx));
                            }
                        }
                    }
                    //System.out.println("Pysty: "+ruutuY+":"+ruutuX+" " +ruudunSarake +" - " + omat.toString()+muut.toString());
                
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int x = ruutuX+ruudunSarake;
                        for (int luku : omat) {
                            for (int y = 0 ; y < SIZE*SIZE; y++) {
                                if ( y < ruutuY || y >= ruutuY + SIZE ) {
                                    if (sudoku[y][x] == 0 && !estoLista.get(y*SIZE*SIZE + x).contains(luku) && mahdolliset.get(y*SIZE*SIZE + x).contains(luku)) {
                                        //System.out.println("LukitsePysty " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE + x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE + x).remove(luku);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }
    
    //Box Line Reduction
    private static boolean lukitseRuutu(int[][] sudoku) {
        boolean paluu = false;

        //rivi
        for (int rivi = 0 ; rivi < SIZE*SIZE ; rivi++) {
            for (int vaakaRuutu = 0 ; vaakaRuutu < SIZE*SIZE ; vaakaRuutu=vaakaRuutu + SIZE) {
                HashSet<Integer> muut = new HashSet<Integer>();
                HashSet<Integer> omat = new HashSet<Integer>();
                for (int sisus = 0 ; sisus < SIZE*SIZE ; sisus ++) {
                    if (sisus >= vaakaRuutu && sisus < vaakaRuutu + SIZE) {
                        omat.addAll(mahdolliset.get(rivi*SIZE*SIZE+sisus));
                    } else {
                        muut.addAll(mahdolliset.get(rivi*SIZE*SIZE+sisus));
                    }
                    
                }
                //System.out.println("Omat"+rivi+" "+vaakaRuutu +omat.toString()+muut.toString());
                omat.removeAll(muut);
                if (!omat.isEmpty()) {
                    int alkuY = (rivi/SIZE) * SIZE;
                    int alkuX = vaakaRuutu;
                    for (int luku : omat) {
                        for (int y = alkuY ; y < alkuY+SIZE; y++) {
                            for (int x = alkuX ; x < alkuX+SIZE; x++) {
                                if ( y != rivi ) {
                                    if (sudoku[y][x] == 0 && mahdolliset.get(y*SIZE*SIZE + x).contains(luku) && !estoLista.get(y*SIZE*SIZE + x).contains(luku)) {
                                        //System.out.println("LukitseRivi " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE + x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE + x).remove(luku);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // sarake
        for (int sarake = 0 ; sarake < SIZE*SIZE ; sarake++) {
            for (int pystyRuutu = 0 ; pystyRuutu < SIZE*SIZE ; pystyRuutu=pystyRuutu + SIZE) {
                HashSet<Integer> muut = new HashSet<Integer>();
                HashSet<Integer> omat = new HashSet<Integer>();
                for (int sisus = 0 ; sisus < SIZE*SIZE ; sisus ++) {
                    if (sisus >= pystyRuutu && sisus < pystyRuutu + SIZE) {
                        omat.addAll(mahdolliset.get(sisus*SIZE*SIZE + sarake));
                    } else {
                        muut.addAll(mahdolliset.get(sisus*SIZE*SIZE + sarake));
                    }
                    
                }
                omat.removeAll(muut);
                if (!omat.isEmpty()) {
                    int alkuY = pystyRuutu;
                    int alkuX = (sarake/SIZE)*SIZE;
                    for (int luku : omat) {
                        for (int y = alkuY ; y < alkuY+SIZE; y++) {
                            for (int x = alkuX ; x < alkuX+SIZE; x++) {
                                if ( x != sarake ) {
                                    if (sudoku[y][x] == 0  && mahdolliset.get(y*SIZE*SIZE + x).contains(luku) && !estoLista.get(y*SIZE*SIZE + x).contains(luku)) {
                                        //System.out.println("LukitseSarake " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE + x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE + x).remove(luku);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }    
    
    private static boolean piiloMonikotRivi(int mista, int mihin, int[][] sudoku) {
        boolean paluu = false;
        int estoja = 0;
        for (int montako = mista ; montako < mihin+1 ; montako++) {
            for (int rivi = 0; rivi < SIZE*SIZE; rivi++) {
                HashSet<Integer> rivinarvot = new HashSet<Integer>();
                ArrayList<Integer> kaikkiVaihtoehdot = new ArrayList<Integer>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    kaikkiVaihtoehdot.addAll(mahdolliset.get(rivi*SIZE*SIZE+i));
                }
                int[] kertaa = new int[SIZE*SIZE+1];
                for (int luku : kaikkiVaihtoehdot) {
                    kertaa[luku]++;
                }
                for (int i = 1 ; i <= SIZE*SIZE ; i++) {
                    if (kertaa[i] > 0 && kertaa[i] <= montako) {
                        rivinarvot.add(i);
                    }
                }

                int raja = 200;
                if (montako > 3 ) raja = 28;
                if (montako > 5 ) raja = 22;
                if (montako > 7 ) raja = 18;
                if (rivinarvot.size() > montako && rivinarvot.size() < raja) {
                    // Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                    // int lista[] = new int[rivinarvot.size()];
                    // for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                    //     lista[i] = kokolista[i];
                    // }
                    int[] lista = rivinarvot.stream().mapToInt(Number::intValue).toArray(); 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (HashSet<Integer> ts : hakuLista) {
                        
                        int loytynytpiilo = 0;
                        ArrayList<Integer> loytyneetpiilopaikat = new ArrayList<Integer>();
                        for (int sarake = 0 ; sarake < SIZE*SIZE ;sarake++) {
                            if (!Collections.disjoint(mahdolliset.get(rivi*SIZE*SIZE+sarake), ts)) {
                                loytynytpiilo++;
                                loytyneetpiilopaikat.add(sarake);
                            }
                        }
                        if (loytynytpiilo == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(rivit);
                            estot.removeAll(ts);
                            for (int r : loytyneetpiilopaikat) {

                                if (!Collections.disjoint(mahdolliset.get(SIZE*SIZE*rivi+r),estot)) {
                                    estoLista.get(SIZE*SIZE*rivi+r).addAll(estot);
                                    mahdolliset.get(SIZE*SIZE*rivi+r).removeAll(estot);
                                    //System.out.println(montako+" Esta PiiloRivi "+(SIZE*SIZE*rivi+r)+" "+loytyneetpiilopaikat.toString()+ts.toString());
                                    estoja++;
                                    paluu = true; 
                                }    
                            }
                        }
                    }
                }
            }
        }
        // if (paluu) System.out.println(" piilomonikko rivi " +estoja);
        return paluu;
    }

    private static boolean nakuMonikotRivi(int mista, int mihin, int[][] sudoku) {
        boolean paluu = false;
        int estoja = 0;
        for (int montako = mista ; montako < mihin+1 ; montako++) {
            for (int rivi = 0; rivi < SIZE*SIZE; rivi++) {
                HashSet<Integer> ruudut = new HashSet<Integer>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    int s = mahdolliset.get(rivi*SIZE*SIZE+i).size();
                    if (s != 0 && s <= montako ){
                        ruudut.add(i);
                    }
                }
                int raja = 200;
                if (montako > 3 ) raja = 28;
                if (montako > 5 ) raja = 22;
                if (montako > 7 ) raja = 18;
                if (ruudut.size() > montako && ruudut.size() < raja) {
                    int[] lista = ruudut.stream().mapToInt(Number::intValue).toArray();
                    // System.out.println(ruudut +" "+Arrays.toString(lista));
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);

                    for (HashSet<Integer> ts : hakuLista) {
                        HashSet<Integer> nakumonikko = new HashSet<Integer>();
                        for (int c : ts) {
                            nakumonikko.addAll(mahdolliset.get(rivi*SIZE*SIZE+c));
                        }
                        if (nakumonikko.size() == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(rivit);
                            estot.removeAll(ts);
                            //System.out.println(loytynyt+" "+rivi+" "+estot.toString()+ts.toString());
                            for (int r : estot) {
                                if (sudoku[rivi][r] == 0 ) {
                                    for (int luku : nakumonikko) {
                                        if (mahdolliset.get(SIZE*SIZE*rivi+r).contains(luku)) {
                                            //System.out.println(montako+" Naku mon EstaR " +(SIZE*SIZE*rivi+r)+" "+(luku));
                                            estoLista.get(SIZE*SIZE*rivi+r).add(luku);
                                            mahdolliset.get(SIZE*SIZE*rivi+r).remove(luku);
                                            estoja++;
                                            paluu = true; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (paluu) System.out.println(" nakumonikko rivi " +estoja);
        return paluu;
    }

    private static boolean piiloMonikotSarake(int mista, int mihin, int[][] sudoku) {
        boolean paluu = false;
        int estoja = 0;
        for (int montako = mista ; montako < mihin+1 ; montako++) {
            for (int sarake = 0; sarake < SIZE*SIZE; sarake++) {
                HashSet<Integer> rivinarvot = new HashSet<Integer>();
                ArrayList<Integer> kaikkiVaihtoehdot = new ArrayList<Integer>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    kaikkiVaihtoehdot.addAll(mahdolliset.get(i*SIZE*SIZE + sarake));
                }
                int[] kertaa = new int[SIZE*SIZE+1];
                for (int luku : kaikkiVaihtoehdot) {
                    kertaa[luku]++;
                }
                for (int i = 1 ; i <= SIZE*SIZE ; i++) {
                    if (kertaa[i] > 0 && kertaa[i] <= montako) {
                        rivinarvot.add(i);
                    }
                }
                int raja = 200;
                if (montako > 3 ) raja = 28;
                if (montako > 5 ) raja = 22;
                if (montako > 7 ) raja = 18;
                if (rivinarvot.size() > montako && rivinarvot.size() < raja) {
                    int[] lista = rivinarvot.stream().mapToInt(Number::intValue).toArray(); 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (HashSet<Integer> ts : hakuLista) {
                        
                        int loytynytpiilo = 0;
                        ArrayList<Integer> loytyneetpiilopaikat = new ArrayList<Integer>();
                        for (int rivi = 0 ; rivi < SIZE*SIZE ; rivi++) {
                            if (!Collections.disjoint(mahdolliset.get(rivi*SIZE*SIZE+sarake), ts)) {
                                loytynytpiilo++;
                                loytyneetpiilopaikat.add(rivi);
                            }
                        }
                        if (loytynytpiilo == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(arvot);
                            estot.removeAll(ts); 
                            for (int r : loytyneetpiilopaikat) {
                                if (!Collections.disjoint(mahdolliset.get(SIZE*SIZE*r+sarake),estot)) {
                                    estoLista.get(SIZE*SIZE*r + sarake).addAll(estot);
                                    mahdolliset.get(SIZE*SIZE*r + sarake).removeAll(estot);
                                    //System.out.println(montako+" Esta PiiloSarake "+(SIZE*SIZE*r+sarake)+" "+loytyneetpiilopaikat.toString()+ts.toString());
                                    estoja++;
                                    paluu = true; 
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (paluu) System.out.println(" piilomonikko sarake " +estoja);
        return paluu;
    }
    
    private static boolean nakuMonikotSarake(int mista, int mihin, int[][] sudoku) {
        boolean paluu = false;
        int estoja = 0;
        for (int montako = mista ; montako < mihin+1 ; montako++) {
            for (int sarake = 0; sarake < SIZE*SIZE; sarake++) {
                HashSet<Integer> ruudut = new HashSet<Integer>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    int s = mahdolliset.get(i*SIZE*SIZE+sarake).size(); 
                    if ( s != 0 && s <= montako ){
                        ruudut.add(i);
                    }
                }
                int raja = 200;
                if (montako > 3 ) raja = 28;
                if (montako > 5 ) raja = 22;
                if (montako > 7 ) raja = 18;
                if (ruudut.size() > montako && ruudut.size() < raja) {
                    int[] lista = ruudut.stream().mapToInt(Number::intValue).toArray();
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                    for (HashSet<Integer> ts : hakuLista) {
                        HashSet<Integer> nakumonikko = new HashSet<Integer>();
                        for (int c : ts) {
                            nakumonikko.addAll(mahdolliset.get(c*SIZE*SIZE+sarake));
                        }
                        if (nakumonikko.size() == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(rivit);
                            estot.removeAll(ts);
                            for (int r : estot) {
                                if (sudoku[r][sarake] == 0 ) {
                                    for (int luku : nakumonikko) {
                                        if (mahdolliset.get(SIZE*SIZE*r+sarake).contains(luku)) {
                                            //System.out.println(montako+" Naku mon EstaS " +(SIZE*SIZE*r+sarake)+" "+(luku));
                                            estoLista.get(SIZE*SIZE*r+sarake).add(luku);
                                            mahdolliset.get(SIZE*SIZE*r+sarake).remove(luku);
                                            estoja++;
                                            paluu = true; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (paluu) System.out.println(" nakumonikko sarake " +estoja);
        return paluu;
    }    

    private static boolean piiloMonikotRuutu(int mista, int mihin, int[][] sudoku) {
        boolean paluu = false;
        int estoja = 0;
        for (int montako = mista ; montako < mihin+1 ; montako++) {
            for ( int rivi = 0 ;rivi < SIZE*SIZE ; rivi=rivi+SIZE) {
                for ( int sarake = 0 ;sarake < SIZE*SIZE ; sarake=sarake+SIZE) {
                    int vaaka = (rivi/SIZE)*SIZE;
                    int pysty = (sarake/SIZE)*SIZE;
                    HashSet<Integer> rivinarvot = new HashSet<Integer>();
                    ArrayList<Integer> kaikkiVaihtoehdot = new ArrayList<Integer>();
                    for (int y = vaaka ; y < vaaka+SIZE; y++) {
                        for (int x = pysty ; x < pysty+SIZE; x++) {
                            kaikkiVaihtoehdot.addAll(mahdolliset.get(SIZE*SIZE*y+x));
                        }
                    }
                    int[] kertaa = new int[SIZE*SIZE+1];
                    for (int luku : kaikkiVaihtoehdot) {
                        kertaa[luku]++;
                    }
                    for (int i = 1 ; i <= SIZE*SIZE ; i++) {
                        if (kertaa[i] > 0 && kertaa[i] <= montako) {
                            rivinarvot.add(i);
                        }
                    }
                    int raja = 200;
                    if (montako > 3 ) raja = 28;
                    if (montako > 5 ) raja = 22;
                    if (montako > 7 ) raja = 18;
                    if (rivinarvot.size() > montako && rivinarvot.size() < raja) {
                        int[] lista = rivinarvot.stream().mapToInt(Number::intValue).toArray();
                        int vaihtoehto[] = new int[montako];
                        hakuLista = new ArrayList<HashSet<Integer>>();
                        combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                        for (HashSet<Integer> ts : hakuLista) {

                            int loytynytpiilo = 0;
                            ArrayList<Integer> loytyneetpiilopaikat = new ArrayList<Integer>();
                            for (int y = 0 ; y < SIZE ;y++) {
                                for (int x = 0 ; x < SIZE ;x++) {
                                    int paikka = (rivi+y)*SIZE*SIZE+sarake+x;
                                    if (!Collections.disjoint(mahdolliset.get(paikka), ts)) {
                                        loytynytpiilo++;
                                        loytyneetpiilopaikat.add(paikka);
                                    }
                                }
                            }
                            if (loytynytpiilo == montako) {
                                HashSet<Integer> estot = new HashSet<Integer>(arvot);
                                estot.removeAll(ts);
                                //System.out.println(montako+" EstaPiiloX 0" +" "+estot);
                                for (int r : loytyneetpiilopaikat) {
                                    if (!Collections.disjoint(mahdolliset.get(r), estot)) {
                                        estoLista.get(r).addAll(estot);
                                        mahdolliset.get(r).removeAll(estot);
                                        //System.out.println(montako+" Esta PiiloX " +r+" "+loytyneetpiilopaikat.toString()+ts.toString());
                                        estoja++;
                                        paluu = true; 
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (paluu) System.out.println(" piilomonikko ruutu " +estoja);
        return paluu;
    }       
   
    private static boolean nakuMonikotRuutu(int mista, int mihin, int[][] sudoku) {
        boolean paluu = false;
        int estoja = 0;
        for (int montako = mista ; montako < mihin+1 ; montako++) {
            for ( int rivi = 0 ;rivi < SIZE*SIZE ; rivi=rivi+SIZE) {
                for ( int sarake = 0 ;sarake < SIZE*SIZE ; sarake=sarake+SIZE) {
                    int vaaka = (rivi/SIZE)*SIZE;
                    int pysty = (sarake/SIZE)*SIZE;
                    HashSet<Integer> ruudut = new HashSet<Integer>();
                    HashSet<Integer> alue = new HashSet<Integer>();
                    
                    for (int y = vaaka ; y < vaaka+SIZE; y++) {
                        for (int x = pysty ; x < pysty+SIZE; x++) {
                            int s = mahdolliset.get(y*SIZE*SIZE+x).size(); 
                            if ( s != 0 && s <= montako ){
                                ruudut.add(y*SIZE*SIZE+x);
                            }    
                            alue.add(y*SIZE*SIZE+x);
                        }
                    }
                    int raja = 200;
                    if (montako > 3 ) raja = 28;
                    if (montako > 5 ) raja = 22;
                    if (montako > 7 ) raja = 18;
                    if (ruudut.size() > montako && ruudut.size() < raja) {
                        int[] lista = ruudut.stream().mapToInt(Number::intValue).toArray();
                        int vaihtoehto[] = new int[montako];
                        hakuLista = new ArrayList<HashSet<Integer>>();
                        combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                        for (HashSet<Integer> ts : hakuLista) {
                            HashSet<Integer> nakumonikko = new HashSet<Integer>();
                            for (int c : ts) {
                                nakumonikko.addAll(mahdolliset.get(c));
                            }
                            if (nakumonikko.size() == montako) {
                                HashSet<Integer> estot = new HashSet<Integer>(alue);
                                estot.removeAll(ts);
                                //System.out.println(nakumonikko+""+ts+""+estot);
                                for (int r : estot) {
                                    if (sudoku[r/(SIZE*SIZE)][r%(SIZE*SIZE)] == 0) {
                                        for (int luku : nakumonikko) {
                                            if (mahdolliset.get(r).contains(luku)) {
                                                //System.out.println(montako+" Naku mon EstaX " +r+" "+(luku));
                                                estoLista.get(r).add(luku);
                                                mahdolliset.get(r).remove(luku);
                                                estoja++;
                                                paluu = true; 
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (paluu) System.out.println(" nakumonikko ruutu " +estoja);
        return paluu;
    }
    
        private static boolean jellyfishVaaka(int[][] sudoku) {
        boolean paluu = false;
        for (int numero = 1 ; numero <=(SIZE*SIZE) ; numero++) {
            ArrayList<Integer> xrivit  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiSarakkeet = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetRivit = new ArrayList<HashSet<Integer>>();
            for (int rivi = 0 ; rivi < (SIZE*SIZE) ; rivi ++) {
                HashSet<Integer> sarakkeet = new HashSet<>();
                for (int i = 0 ; i < (SIZE*SIZE) ; i++) {
                    if (mahdolliset.get(rivi*SIZE*SIZE+i).contains(numero)) {
                        sarakkeet.add(i);
                    }
                }
                if (sarakkeet.size() == 2 || sarakkeet.size() == 3 || sarakkeet.size() == 4) {
                    xrivit.add(rivi);
                    kaikkiSarakkeet.addAll(sarakkeet);
                    loytyneetRivit.add(sarakkeet);
                }
            }
            if (xrivit.size() == 4 && kaikkiSarakkeet.size() == 4) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatRivit = new HashSet<Integer>();
                blokattavatRivit.addAll(rivit);
                blokattavatRivit.removeAll(xrivit);
                for ( int y : blokattavatRivit) {
                    for (int x : kaikkiSarakkeet) {
                        if (sudoku[y][x] == 0) {
                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                //System.out.println("Vaaka Jellyfish vaaka esta " + numero +" "+(SIZE*SIZE*y+x));
                                estoLista.get(SIZE*SIZE*y+x).add(numero);
                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                paluu=true;
                            }
                        }
                    }
                    
                }
            } else if (xrivit.size() > 4 ) {
                //System.out.println(numero+ " " +rivit+loytyneetRivit.toString());
                for (int r1 = 0 ; r1 < loytyneetRivit.size()-3 ; r1++) {
                    for (int r2 = r1+1 ; r2 < loytyneetRivit.size()-2 ; r2++) {
                        for (int r3 = r2+1 ; r3 < loytyneetRivit.size()-1 ; r3++) {
                            for (int r4 = r3+1 ; r4 < loytyneetRivit.size() ; r4++) {
                                HashSet<Integer> apu = new HashSet<Integer>();                        
                                for (int a : loytyneetRivit.get(r1)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetRivit.get(r2)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetRivit.get(r3)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetRivit.get(r4)) {
                                    apu.add(a);
                                }

                                if (apu.size() == 4) {
                                   HashSet<Integer> blokattavatRivit = new HashSet<Integer>();
                                   blokattavatRivit.addAll(rivit);
                                    blokattavatRivit.remove(xrivit.get(r1));
                                    blokattavatRivit.remove(xrivit.get(r2));
                                    blokattavatRivit.remove(xrivit.get(r3));
                                    blokattavatRivit.remove(xrivit.get(r4));
                                    HashSet<Integer> apu2 = new HashSet<Integer>();
                                    apu2.addAll(loytyneetRivit.get(r1));
                                    apu2.addAll(loytyneetRivit.get(r2));
                                    apu2.addAll(loytyneetRivit.get(r3));
                                    apu2.addAll(loytyneetRivit.get(r4));

                                    for ( int y : blokattavatRivit) {
                                        for (int x : apu2) {
                                            if (sudoku[y][x] == 0) {
                                                if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                                    //System.out.println("Vaaka Jellyfish Piilo esta " + numero +" "+((SIZE*SIZE)*y+x));
                                                    estoLista.get((SIZE*SIZE)*y+x).add(numero);
                                                    mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                                    paluu=true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }    
    
    private static boolean jellyfishPysty(int[][] sudoku) {
        boolean paluu = false;
        
        for (int numero = 1 ; numero <=SIZE*SIZE ; numero++) {
            ArrayList<Integer> sarakkeet  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiRivit = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetSarakkeet = new ArrayList<HashSet<Integer>>();
            for (int sarake = 0 ; sarake < (SIZE*SIZE) ; sarake ++) {
                HashSet<Integer> xrivit = new HashSet<>();
                for (int i = 0 ; i < (SIZE*SIZE) ; i++) {
                    if (mahdolliset.get(i*SIZE*SIZE+sarake).contains(numero)) {
                        xrivit.add(i);
                    }
                }
                if (xrivit.size() == 2 || xrivit.size() == 3 || xrivit.size() == 4) {
                    sarakkeet.add(sarake);
                    kaikkiRivit.addAll(xrivit);
                    loytyneetSarakkeet.add(xrivit);
                }
            }
            if (sarakkeet.size() == 4 && kaikkiRivit.size() == 4) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>();
                blokattavatSarakkeet.addAll(rivit);
                blokattavatSarakkeet.removeAll(sarakkeet);
                for ( int x : blokattavatSarakkeet) {
                    for (int y : kaikkiRivit) {
                        if (sudoku[y][x] == 0) {
                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                //System.out.println("Pysty Jellyfish  pysty esta " + numero +" "+(SIZE*SIZE*y+x));
                                estoLista.get(SIZE*SIZE*y+x).add(numero);
                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                paluu=true;
                            }
                        }
                    }
                    
                }
            } else if (sarakkeet.size() > 4 ) {
                //System.out.println(numero+ "P " +sarakkeet+loytyneetSarakkeet.toString());
                for (int s1 = 0 ; s1 < loytyneetSarakkeet.size()-3 ; s1++) {
                    for (int s2 = s1+1 ; s2 < loytyneetSarakkeet.size()-2 ; s2++) {
                        for (int s3 = s2+1 ; s3 < loytyneetSarakkeet.size()-1 ; s3++) {
                            for (int s4 = s3+1 ; s4 < loytyneetSarakkeet.size() ; s4++) {
                                HashSet<Integer> apu = new HashSet<Integer>();                        
                                for (int a : loytyneetSarakkeet.get(s1)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetSarakkeet.get(s2)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetSarakkeet.get(s3)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetSarakkeet.get(s4)) {
                                    apu.add(a);
                                }
                                if (apu.size() == 4) {
                                   HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>();
                                   blokattavatSarakkeet.addAll(rivit);
                                    blokattavatSarakkeet.remove(sarakkeet.get(s1));
                                    blokattavatSarakkeet.remove(sarakkeet.get(s2));
                                    blokattavatSarakkeet.remove(sarakkeet.get(s3));
                                    blokattavatSarakkeet.remove(sarakkeet.get(s4));
                                    HashSet<Integer> apu2 = new HashSet<Integer>();
                                    apu2.addAll(loytyneetSarakkeet.get(s1));
                                    apu2.addAll(loytyneetSarakkeet.get(s2));
                                    apu2.addAll(loytyneetSarakkeet.get(s3));
                                    apu2.addAll(loytyneetSarakkeet.get(s4));

                                    for ( int x : blokattavatSarakkeet) {
                                        for (int y : apu2) {
                                            if (sudoku[y][x] == 0) {
                                                if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                                    //System.out.println("Pysty Jellyfish Piilo esta " + numero +" "+((SIZE*SIZE)*y+x));
                                                    estoLista.get((SIZE*SIZE)*y+x).add(numero);
                                                    mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                                    paluu=true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }     

    private static boolean swordfishVaaka(int[][] sudoku) {
        boolean paluu = false;
        
        for (int numero = 1 ; numero <=(SIZE*SIZE) ; numero++) {
            ArrayList<Integer> xrivit  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiSarakkeet = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetRivit = new ArrayList<HashSet<Integer>>();
            for (int rivi = 0 ; rivi < (SIZE*SIZE) ; rivi ++) {
                HashSet<Integer> sarakkeet = new HashSet<>();
                boolean seuraava = false;
                for (int i = 0 ; i < (SIZE*SIZE) ; i++) {
                    if (mahdolliset.get(rivi*(SIZE*SIZE)+i).contains(numero)) {
                        sarakkeet.add(i);
                        if (mahdolliset.get(rivi*(SIZE*SIZE)+i).size() > 3) {
                            seuraava = true;
                        }
                    }
                }
                if (seuraava) continue;
                if (sarakkeet.size() == 2 || sarakkeet.size() == 3) {
                    xrivit.add(rivi);
                    kaikkiSarakkeet.addAll(sarakkeet);
                    loytyneetRivit.add(sarakkeet);
                }
            }
            if (xrivit.size() == 3 && kaikkiSarakkeet.size() == 3) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatRivit = new HashSet<Integer>();
                blokattavatRivit.addAll(rivit);
                blokattavatRivit.removeAll(xrivit);
                for ( int y : blokattavatRivit) {
                    for (int x : kaikkiSarakkeet) {
                        if (sudoku[y][x] == 0) {
                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                //System.out.println("Vaaka Swordfish esta " + numero +" "+(SIZE*SIZE*y+x));
                                estoLista.get(SIZE*SIZE*y+x).add(numero);
                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                paluu = true;
                            }
                        }
                    }
                    
                }
            } else if (xrivit.size() > 3 ) {
                //System.out.println(numero+ " " +rivit+loytyneetRivit.toString());
                for (int r1 = 0 ; r1 < loytyneetRivit.size()-2 ; r1++) {
                    for (int r2 = r1+1 ; r2 < loytyneetRivit.size()-1 ; r2++) {
                        for (int r3 = r2+1 ; r3 < loytyneetRivit.size() ; r3++) {
                            HashSet<Integer> apu = new HashSet<Integer>();                        
                            for (int a : loytyneetRivit.get(r1)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetRivit.get(r2)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetRivit.get(r3)) {
                                apu.add(a);
                            }
                            
                            if (apu.size() == 3) {
                               HashSet<Integer> blokattavatRivit = new HashSet<Integer>();
                               blokattavatRivit.addAll(rivit);
                                blokattavatRivit.remove(xrivit.get(r1));
                                blokattavatRivit.remove(xrivit.get(r2));
                                blokattavatRivit.remove(xrivit.get(r3));
                                HashSet<Integer> apu2 = new HashSet<Integer>();
                                apu2.addAll(loytyneetRivit.get(r1));
                                apu2.addAll(loytyneetRivit.get(r2));
                                apu2.addAll(loytyneetRivit.get(r3));

                                for ( int y : blokattavatRivit) {
                                    for (int x : apu2) {
                                        if (sudoku[y][x] == 0) {
                                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                                //System.out.println("Vaaka Swordfish Piilo esta " + numero +" "+(SIZE*SIZE*y+x));
                                                estoLista.get((SIZE*SIZE)*y+x).add(numero);
                                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                                paluu = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }
    private static boolean swordfishPysty(int[][] sudoku) {
        boolean paluu = false;
        for (int numero = 1 ; numero <=(SIZE*SIZE) ; numero++) {
            ArrayList<Integer> sarakkeet  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiRivit = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetSarakkeet = new ArrayList<HashSet<Integer>>();
            for (int sarake = 0 ; sarake < (SIZE*SIZE) ; sarake ++) {
                HashSet<Integer> rivit = new HashSet<>();
                boolean seuraava = false;
                for (int i = 0 ; i < (SIZE*SIZE) ; i++) {
                    if (mahdolliset.get(i*(SIZE*SIZE)+sarake).contains(numero)) {
                        rivit.add(i);
                        if (mahdolliset.get(i*(SIZE*SIZE)+sarake).size() > 3) {
                            seuraava = true;
                        }
                    }
                }
                if (seuraava) continue;
                if (rivit.size() == 2 || rivit.size() == 3) {
                    sarakkeet.add(sarake);
                    kaikkiRivit.addAll(rivit);
                    loytyneetSarakkeet.add(rivit);
                }
            }
            if (sarakkeet.size() == 3 && kaikkiRivit.size() == 3) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>();
                blokattavatSarakkeet.addAll(rivit);
                blokattavatSarakkeet.removeAll(sarakkeet);
                for ( int x : blokattavatSarakkeet) {
                    for (int y : kaikkiRivit) {
                        if (sudoku[y][x] == 0) {
                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                //System.out.println("Pysty Swordfish esta " + numero +" "+(SIZE*SIZE*y+x));
                                estoLista.get(SIZE*SIZE*y+x).add(numero);
                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                paluu = true;
                            }
                        }
                    }
                    
                }
            } else if (sarakkeet.size() > 3 ) {
                //System.out.println(numero+ "P " +sarakkeet+loytyneetSarakkeet.toString());
                for (int s1 = 0 ; s1 < loytyneetSarakkeet.size()-2 ; s1++) {
                    for (int s2 = s1+1 ; s2 < loytyneetSarakkeet.size()-1 ; s2++) {
                        for (int s3 = s2+1 ; s3 < loytyneetSarakkeet.size() ; s3++) {
                            HashSet<Integer> apu = new HashSet<Integer>();                        
                            for (int a : loytyneetSarakkeet.get(s1)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetSarakkeet.get(s2)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetSarakkeet.get(s3)) {
                                apu.add(a);
                            }
                            if (apu.size() == 3) {
                                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>();
                                blokattavatSarakkeet.addAll(rivit);
                                blokattavatSarakkeet.remove(sarakkeet.get(s1));
                                blokattavatSarakkeet.remove(sarakkeet.get(s2));
                                blokattavatSarakkeet.remove(sarakkeet.get(s3));
                                HashSet<Integer> apu2 = new HashSet<Integer>();
                                apu2.addAll(loytyneetSarakkeet.get(s1));
                                apu2.addAll(loytyneetSarakkeet.get(s2));
                                apu2.addAll(loytyneetSarakkeet.get(s3));

                                for ( int x : blokattavatSarakkeet) {
                                    for (int y : apu2) {
                                        if (sudoku[y][x] == 0) {
                                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                                //System.out.println("Pysty swordfish Piilo esta " + numero +" "+(SIZE*SIZE*y+x));
                                                estoLista.get((SIZE*SIZE)*y+x).add(numero);
                                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                                paluu = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }    

    private static boolean xWingVaaka(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        boolean paluu = false;
        for (int numero = 1 ; numero <=SIZE*SIZE ; numero++) {
            ArrayList<Integer> xrivit  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiSarakkeet = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetParit = new ArrayList<HashSet<Integer>>();
            for (int rivi = 0 ; rivi < SIZE*SIZE ; rivi ++) {
                HashSet<Integer> sarakkeet = new HashSet<>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    if (mahdolliset.get(rivi*SIZE*SIZE+i).contains(numero)) {
                        sarakkeet.add(i);
                    }
                }
                //if (seuraava) continue;
                if (sarakkeet.size() == 2 ) {
                    xrivit.add(rivi);
                    kaikkiSarakkeet.addAll(sarakkeet);
                    loytyneetParit.add(sarakkeet);
                } 
            }
            if (xrivit.size() == 2 && kaikkiSarakkeet.size() == 2 ) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatRivit = new HashSet<Integer>();
                blokattavatRivit.addAll(rivit);
                blokattavatRivit.removeAll(xrivit);
                for ( int y : blokattavatRivit) {
                    for (int x : kaikkiSarakkeet) {
                        if (sudoku[y][x] == 0) {
                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                //System.out.println("Vaaka xwing esta " + numero +" "+(SIZE*SIZE*y+x));
                                estoLista.get(SIZE*SIZE*y+x).add(numero);
                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                paluu = true;
                            }
                        }
                    }
                    
                }
            } else if (xrivit.size() > 2 ) {
                //System.out.println(numero+ " " +rivit+loytyneetParit.toString());
                for (int i = 0 ; i < loytyneetParit.size()-1 ; i++) {
                    for (int j = i+1 ; j < loytyneetParit.size() ; j++) {
                        if(loytyneetParit.get(i).containsAll(loytyneetParit.get(j))) {   
                            HashSet<Integer> blokattavatRivit = new HashSet<Integer>();
                            blokattavatRivit.addAll(rivit);
                            blokattavatRivit.remove(xrivit.get(i));
                            blokattavatRivit.remove(xrivit.get(j));
                            for ( int y : blokattavatRivit) {
                                for (int x : loytyneetParit.get(i)) {
                                    if (sudoku[y][x] == 0) {
                                        if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                            //System.out.println("Vaaka xwing Piilo esta " + numero +" "+(SIZE*SIZE*y+x));
                                            estoLista.get(SIZE*SIZE*y+x).add(numero);
                                            mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                            paluu = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } 
        }
        return paluu;
    }
    private static boolean xWingPysty(int[][] sudoku) {
        boolean paluu = false;
        for (int numero = 1 ; numero <=SIZE*SIZE ; numero++) {
            ArrayList<Integer> sarakkeet  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiRivit = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetParit = new ArrayList<HashSet<Integer>>();
            for (int sarake = 0 ; sarake < SIZE*SIZE ; sarake ++) {
                HashSet<Integer> xrivit = new HashSet<>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    if (mahdolliset.get(i*SIZE*SIZE+sarake).contains(numero)) {
                        xrivit.add(i);
                    }
                }
                if (xrivit.size() == 2 ) {
                    sarakkeet.add(sarake);
                    kaikkiRivit.addAll(xrivit);
                    loytyneetParit.add(xrivit);
                }
            }
            if (sarakkeet.size() == 2 && kaikkiRivit.size() == 2 ) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>();
                blokattavatSarakkeet.addAll(rivit);
                blokattavatSarakkeet.removeAll(sarakkeet);
                for ( int x : blokattavatSarakkeet) {
                    for (int y : kaikkiRivit) {
                        if (sudoku[y][x] == 0) {
                            if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                //System.out.println("Pysty xwing esta " + numero +" "+(SIZE*SIZE*y+x));
                                estoLista.get(SIZE*SIZE*y+x).add(numero);
                                mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                paluu = true;
                            }
                        }
                    }
                    
                }
            } else if (sarakkeet.size() > 2 ) {
                //System.out.println(numero+ "P " +sarakkeet+loytyneetSarakkeet.toString());
                for (int i = 0 ; i < loytyneetParit.size()-1 ; i++) {
                    for (int j = i+1 ; j < loytyneetParit.size() ; j++) {
                        if(loytyneetParit.get(i).containsAll(loytyneetParit.get(j))) {
                            HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>();
                            blokattavatSarakkeet.addAll(rivit);
                            blokattavatSarakkeet.remove(sarakkeet.get(i));
                            blokattavatSarakkeet.remove(sarakkeet.get(j));
                            for ( int x : blokattavatSarakkeet) {
                                for (int y : loytyneetParit.get(i)) {
                                    if (sudoku[y][x] == 0) {
                                        if (mahdolliset.get(SIZE*SIZE*y+x).contains(numero)) {
                                            //System.out.println("Pysty xwing Piilo esta " + numero +" "+(SIZE*SIZE*y+x));
                                            estoLista.get(SIZE*SIZE*y+x).add(numero);
                                            mahdolliset.get(SIZE*SIZE*y+x).remove(numero);
                                            paluu = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }

    private static boolean nishio(HashSet<Integer> paikat, int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        int [][] kopio = new int[SIZE*SIZE][SIZE*SIZE];
        ArrayList<HashSet<Integer>> m = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y <  SIZE*SIZE ; y++) {
            for (int x = 0 ; x <  SIZE*SIZE; x++) {
                m.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int mahdollisuuksia = 4 ; mahdollisuuksia < SIZE+1 ; mahdollisuuksia++) {
            System.out.print("n");
            
            for (int i : paikat) {
                if (m.get(i).size() == mahdollisuuksia) {
                    for (int n : m.get(i)) {
                        for (int y = 0 ; y < SIZE*SIZE ; y++) {
                            for (int x = 0 ; x < SIZE*SIZE; x++) {
                                kopio[y][x] = sudoku[y][x];
                            }
                        }
                        kopio[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = n;
                        
                        loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                        loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                        loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                        for (int j = 0 ; j < SIZE*SIZE ; j++) {
                            HashSet<Integer> hs1 = new HashSet<Integer>();
                            loydetytRivi.add(hs1);
                            HashSet<Integer> hs2 = new HashSet<Integer>();
                            loydetytSarake.add(hs2);
                            HashSet<Integer> hs3 = new HashSet<Integer>();
                            loydetytRuutu.add(hs3);
                        }

                        for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                            AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),kopio[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                        }
                        mahdolliset = new ArrayList<HashSet<Integer>>();
                        asetaMahdolliset(kopio);
                        
                        while (!etsiVarmat(paikat, kopio).isEmpty()){};
                        if (sudokuValmis(kopio)) {
                            for (int y = 0 ; y < SIZE*SIZE ; y++) {
                            for (int x = 0 ; x < SIZE*SIZE; x++) {
                                sudoku[y][x] = kopio[y][x];
                            }
                            }
                            //System.out.println("Nishio: touchdown " +i +" " +n);
                            System.out.print("X");
                            return true;
                        }
                        
                        //tulostaVaihtoehdot(kopio);
                        for ( int a : paikat) {
                            if (kopio[a/(SIZE*SIZE)][a%(SIZE*SIZE)] == 0) {
                                if (vaihtoehdot(a/(SIZE*SIZE), a%(SIZE*SIZE), kopio).isEmpty()) {
                                    //System.out.println("Nishio: "+i +" " + n +" a:"+a);
                                    System.out.print("N");
                                    estoLista.get(i).add(n);
                                    loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                                    loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                                    loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                                    for (int j = 0 ; j < SIZE*SIZE ; j++) {
                                        HashSet<Integer> hs1 = new HashSet<Integer>();
                                        loydetytRivi.add(hs1);
                                        HashSet<Integer> hs2 = new HashSet<Integer>();
                                        loydetytSarake.add(hs2);
                                        HashSet<Integer> hs3 = new HashSet<Integer>();
                                        loydetytRuutu.add(hs3);
                                    }

                                    for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                                        AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudoku[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                    kopio[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = 0; 
                }
            }
            
        }
        loydetytRivi = new ArrayList<HashSet<Integer>>(); 
        loydetytSarake = new ArrayList<HashSet<Integer>>(); 
        loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
        for (int j = 0 ; j < SIZE*SIZE ; j++) {
            HashSet<Integer> hs1 = new HashSet<Integer>();
            loydetytRivi.add(hs1);
            HashSet<Integer> hs2 = new HashSet<Integer>();
            loydetytSarake.add(hs2);
            HashSet<Integer> hs3 = new HashSet<Integer>();
            loydetytRuutu.add(hs3);
        }

        for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
            HashSet<Integer> hs = new HashSet<Integer>();
            AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudoku[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
        }        
        return false;
    }

    private static boolean exclusion3(HashSet<Integer> paikat, int[][] sudoku) {
        // tulostaSudoku(sudoku);
        //tulostaVaihtoehdot(sudoku);
        
        System.out.print("-e");
        boolean paluu = false;
        int [][] sudokuA = new int[SIZE*SIZE][SIZE*SIZE];
        int [][] sudokuB = new int[SIZE*SIZE][SIZE*SIZE];
        int [][] sudokuC = new int[SIZE*SIZE][SIZE*SIZE];
        ArrayList<HashSet<Integer>> mahdollisetOrg = new ArrayList<HashSet<Integer>>();
        for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
            mahdollisetOrg.add(mahdolliset.get(j));
        }
        int count = 0;
        int checkpoint = 0;
        for (int i : paikat) {
            checkpoint++;
            boolean info = false;
            ArrayList<HashSet<Integer>> mahdollisetA = new ArrayList<HashSet<Integer>>();
            ArrayList<HashSet<Integer>> mahdollisetB = new ArrayList<HashSet<Integer>>();
            ArrayList<HashSet<Integer>> mahdollisetC = new ArrayList<HashSet<Integer>>();
            if (mahdollisetOrg.get(i).size() == 3) {
                for (int y = 0 ; y < SIZE*SIZE ; y++) {
                    for (int x = 0 ; x < SIZE*SIZE; x++) {
                        sudokuA[y][x] = sudoku[y][x];
                        sudokuB[y][x] = sudoku[y][x];
                        sudokuC[y][x] = sudoku[y][x];
                    }
                }
                if (SIZE > 6) System.out.println(" Exclusion3 "+checkpoint+"/"  +paikat.size());
                sudokuA[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)mahdollisetOrg.get(i).toArray()[0];
                loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                for (int j = 0 ; j < SIZE*SIZE ; j++) {
                    HashSet<Integer> hs1 = new HashSet<Integer>();
                    loydetytRivi.add(hs1);
                    HashSet<Integer> hs2 = new HashSet<Integer>();
                    loydetytSarake.add(hs2);
                    HashSet<Integer> hs3 = new HashSet<Integer>();
                    loydetytRuutu.add(hs3);
                }

                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudokuA[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                }
                
                while (!etsiVarmat(paikat,sudokuA).isEmpty()){};
                if (sudokuValmis(sudokuA)) {
                    for (int y = 0 ; y < (SIZE*SIZE) ; y++) {
                        for (int x = 0 ; x < (SIZE*SIZE); x++) {
                            sudoku[y][x] = sudokuA[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                mahdolliset = new ArrayList<HashSet<Integer>>();
                asetaMahdolliset(sudokuA);
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    mahdollisetA.add(mahdolliset.get(j));
                }
                // tulostaVaihtoehdot(sudokuA);
                sudokuB[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)mahdollisetOrg.get(i).toArray()[1];
                loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                for (int j = 0 ; j < SIZE*SIZE ; j++) {
                    HashSet<Integer> hs1 = new HashSet<Integer>();
                    loydetytRivi.add(hs1);
                    HashSet<Integer> hs2 = new HashSet<Integer>();
                    loydetytSarake.add(hs2);
                    HashSet<Integer> hs3 = new HashSet<Integer>();
                    loydetytRuutu.add(hs3);
                }

                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudokuB[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                }
                while (!etsiVarmat(paikat, sudokuB).isEmpty()){};
                if (sudokuValmis(sudokuB)) {
                    for (int y = 0 ; y < (SIZE*SIZE) ; y++) {
                        for (int x = 0 ; x < (SIZE*SIZE); x++) {
                            sudoku[y][x] = sudokuB[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                mahdolliset = new ArrayList<HashSet<Integer>>();
                asetaMahdolliset(sudokuB);
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    mahdollisetB.add(mahdolliset.get(j));
                }

                sudokuC[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)mahdollisetOrg.get(i).toArray()[2];
                loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                for (int j = 0 ; j < SIZE*SIZE ; j++) {
                    HashSet<Integer> hs1 = new HashSet<Integer>();
                    loydetytRivi.add(hs1);
                    HashSet<Integer> hs2 = new HashSet<Integer>();
                    loydetytSarake.add(hs2);
                    HashSet<Integer> hs3 = new HashSet<Integer>();
                    loydetytRuutu.add(hs3);
                }

                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudokuC[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                }
                
                while (!etsiVarmat(paikat, sudokuC).isEmpty()){};
                if (sudokuValmis(sudokuC)) {
                    for (int y = 0 ; y < (SIZE*SIZE) ; y++) {
                        for (int x = 0 ; x < (SIZE*SIZE); x++) {
                            sudoku[y][x] = sudokuC[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                mahdolliset = new ArrayList<HashSet<Integer>>();
                asetaMahdolliset(sudokuC);
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    mahdollisetC.add(mahdolliset.get(j));
                }

                for ( int a = 0; a < SIZE*SIZE*SIZE*SIZE; a++) {
                    ArrayList<Integer> ind = new ArrayList<Integer>();
                    ArrayList<Integer> luku = new ArrayList<Integer>();
                    for ( int m : mahdollisetOrg.get(a)) {
                        //System.out.println(a+" "+m+" "+sudokuA[a/(SIZE*SIZE)][a%(SIZE*SIZE)] +" "+ sudokuB[a/(SIZE*SIZE)][a%(SIZE*SIZE)] +" "+ mahdollisetA.get(a).toString() +" "+ mahdollisetB.get(a).toString());
                        if (sudokuA[a/(SIZE*SIZE)][a%(SIZE*SIZE)] != m && sudokuB[a/(SIZE*SIZE)][a%(SIZE*SIZE)] != m && sudokuC[a/(SIZE*SIZE)][a%(SIZE*SIZE)] != m
                            && !(mahdollisetA.get(a).contains(m) || mahdollisetB.get(a).contains(m) || mahdollisetC.get(a).contains(m))) {
                            // System.out.println(" Exclusion3 "+a+" : " +m +" " + mahdollisetOrg.get(a).toString());
                            paluu = true;
                            estoLista.get(a).add(m);
                            ind.add(a);
                            luku.add(m);
                            info = true;
                            count++;
                        }
                    }
                    for (int n = 0 ; n < ind.size() ; n++) {
                        mahdollisetOrg.get((int)ind.get(n)).remove((int)luku.get(n));
                    }
                    
                    if (sudokuA[a/(SIZE*SIZE)][a%(SIZE*SIZE)] == 0 && mahdollisetA.get(a).isEmpty()) {
                        //  System.out.println(" Exclusion A "+i+" + " +mahdollisetOrg.get(i).toArray()[0]);
                        estoLista.get(i).add((int)mahdollisetOrg.get(i).toArray()[0]);
                    }
                    if (sudokuB[a/(SIZE*SIZE)][a%(SIZE*SIZE)] == 0 && mahdollisetB.get(a).isEmpty()) {
                        //  System.out.println(" Exclusion B "+i+" + " + mahdollisetOrg.get(i).toArray()[1]);
                        estoLista.get(i).add((int)mahdollisetOrg.get(i).toArray()[1]);
                    }
                    if (sudokuC[a/(SIZE*SIZE)][a%(SIZE*SIZE)] == 0 && mahdollisetC.get(a).isEmpty()) {
                        // System.out.println(" Exclusion C "+i+" + " + mahdollisetOrg.get(i).toArray()[2]);
                        estoLista.get(i).add((int)mahdollisetOrg.get(i).toArray()[2]);
                    }
                    
                }
                sudokuA[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = 0; 
                sudokuB[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = 0; 
                sudokuC[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = 0; 
                if (info) System.out.print("3");
                if (count > 1) break;
            }
        }
        loydetytRivi = new ArrayList<HashSet<Integer>>(); 
        loydetytSarake = new ArrayList<HashSet<Integer>>(); 
        loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
        for (int j = 0 ; j < SIZE*SIZE ; j++) {
            HashSet<Integer> hs1 = new HashSet<Integer>();
            loydetytRivi.add(hs1);
            HashSet<Integer> hs2 = new HashSet<Integer>();
            loydetytSarake.add(hs2);
            HashSet<Integer> hs3 = new HashSet<Integer>();
            loydetytRuutu.add(hs3);
        }

        for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
            AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudoku[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
        }
        mahdolliset = new ArrayList<HashSet<Integer>>();
        asetaMahdolliset(sudoku);
        return paluu;
    }

    private static boolean exclusion(HashSet<Integer> paikat, int[][] sudoku) {
        // tulostaSudoku(sudoku);
        //tulostaVaihtoehdot(sudoku);
        ArrayList<HashSet<Integer>> estoListaOrg = new ArrayList<HashSet<Integer>>();
        System.out.print("e");
        boolean paluu = false;
        int [][] sudokuA = new int[SIZE*SIZE][SIZE*SIZE];
        int [][] sudokuB = new int[SIZE*SIZE][SIZE*SIZE];
        ArrayList<HashSet<Integer>> mahdollisetOrg = new ArrayList<HashSet<Integer>>();
        for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
            mahdollisetOrg.add(mahdolliset.get(j));
            HashSet<Integer> hs = new HashSet<Integer>();
            estoListaOrg.add(hs);
            estoListaOrg.get(j).addAll(estoLista.get(j));
        }
        int count = 0;
        int checkpoint = 0;
        // for (int i = 0 ; i < SIZE*SIZE*SIZE*SIZE ; i ++) {
        for (int i: paikat) {
            ++checkpoint;
            boolean info = false;
            ArrayList<HashSet<Integer>> mahdollisetA = new ArrayList<HashSet<Integer>>();
            ArrayList<HashSet<Integer>> mahdollisetB = new ArrayList<HashSet<Integer>>();
            if (mahdollisetOrg.get(i).size() == 2) {
                for (int y = 0 ; y < SIZE*SIZE ; y++) {
                    for (int x = 0 ; x < SIZE*SIZE; x++) {
                        sudokuA[y][x] = sudoku[y][x];
                        sudokuB[y][x] = sudoku[y][x];
                    }
                }
                if (SIZE > 6) System.out.println(" Exclusion "+ checkpoint +"/" +paikat.size());
                sudokuA[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)mahdollisetOrg.get(i).toArray()[0];
                loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                for (int j = 0 ; j < SIZE*SIZE ; j++) {
                    HashSet<Integer> hs1 = new HashSet<Integer>();
                    loydetytRivi.add(hs1);
                    HashSet<Integer> hs2 = new HashSet<Integer>();
                    loydetytSarake.add(hs2);
                    HashSet<Integer> hs3 = new HashSet<Integer>();
                    loydetytRuutu.add(hs3);
                }
                estoLista = new ArrayList<HashSet<Integer>>();
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudokuA[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                    HashSet<Integer> hs = new HashSet<Integer>();
                    estoLista.add(hs);
                    estoLista.get(j).addAll(estoListaOrg.get(j));
                }
                boolean uusiksiA = true;
                while(uusiksiA) { 
                    uusiksiA=false;
                    boolean haeVarmatA = true;
                    while (haeVarmatA)  {
                        HashSet<Integer> vA = etsiVarmat(paikat, sudokuA);
                        if (vA.isEmpty()) {
                            haeVarmatA = false;
                        }
                    }
                    mahdolliset = new ArrayList<HashSet<Integer>>();
                    asetaMahdolliset(sudokuA);
                    uusiksiA = lukitseRivi(sudoku) || uusiksiA;
                    uusiksiA = lukitseRuutu(sudoku) || uusiksiA;
                    uusiksiA = nakuMonikotRivi(2,3,sudoku) || uusiksiA;
                    uusiksiA = nakuMonikotSarake(2,3,sudoku) || uusiksiA;
                    uusiksiA = nakuMonikotRuutu(2,3,sudoku) || uusiksiA;
                    uusiksiA = piiloMonikotRivi(2,3,sudoku) || uusiksiA;
                    uusiksiA = piiloMonikotSarake(2,3,sudoku) || uusiksiA;
                    uusiksiA = piiloMonikotRuutu(2,3,sudoku) || uusiksiA;
                }
                if (sudokuValmis(sudokuA)) {
                    for (int y = 0 ; y < (SIZE*SIZE) ; y++) {
                        for (int x = 0 ; x < (SIZE*SIZE); x++) {
                            sudoku[y][x] = sudokuA[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                mahdolliset = new ArrayList<HashSet<Integer>>();
                asetaMahdolliset(sudokuA);
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    mahdollisetA.add(mahdolliset.get(j));
                }
                // tulostaVaihtoehdot(sudokuA);
                sudokuB[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)mahdollisetOrg.get(i).toArray()[1];
                loydetytRivi = new ArrayList<HashSet<Integer>>(); 
                loydetytSarake = new ArrayList<HashSet<Integer>>(); 
                loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
                for (int j = 0 ; j < SIZE*SIZE ; j++) {
                    HashSet<Integer> hs1 = new HashSet<Integer>();
                    loydetytRivi.add(hs1);
                    HashSet<Integer> hs2 = new HashSet<Integer>();
                    loydetytSarake.add(hs2);
                    HashSet<Integer> hs3 = new HashSet<Integer>();
                    loydetytRuutu.add(hs3);
                }

                estoLista = new ArrayList<HashSet<Integer>>();
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudokuB[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
                    HashSet<Integer> hs = new HashSet<Integer>();
                    estoLista.add(hs);
                    estoLista.get(j).addAll(estoListaOrg.get(j));
                }
                boolean uusiksiB = true;
                while(uusiksiB) { 
                    uusiksiB=false;
                    boolean haeVarmatB = true;
                    while (haeVarmatB)  {
                        HashSet<Integer> vB = etsiVarmat(paikat, sudokuB);
                        if (vB.isEmpty()) {
                            haeVarmatB = false;
                        }
                    }
                    mahdolliset = new ArrayList<HashSet<Integer>>();
                    asetaMahdolliset(sudokuB);
                    uusiksiB = lukitseRivi(sudoku) || uusiksiB;
                    uusiksiB = lukitseRuutu(sudoku) || uusiksiB;
                    uusiksiB = nakuMonikotRivi(2,3,sudoku) || uusiksiB;
                    uusiksiB = nakuMonikotSarake(2,3,sudoku) || uusiksiB;
                    uusiksiB = nakuMonikotRuutu(2,3,sudoku) || uusiksiB;
                    uusiksiB = piiloMonikotRivi(2,3,sudoku) || uusiksiB;
                    uusiksiB = piiloMonikotSarake(2,3,sudoku) || uusiksiB;
                    uusiksiB = piiloMonikotRuutu(2,3,sudoku) || uusiksiB;
                }
                if (sudokuValmis(sudokuB)) {
                    for (int y = 0 ; y < (SIZE*SIZE) ; y++) {
                        for (int x = 0 ; x < (SIZE*SIZE); x++) {
                            sudoku[y][x] = sudokuB[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                mahdolliset = new ArrayList<HashSet<Integer>>();
                asetaMahdolliset(sudokuB);
                for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
                    mahdollisetB.add(mahdolliset.get(j));
                }
                for ( int a = 0; a < SIZE*SIZE*SIZE*SIZE; a++) {
                    ArrayList<Integer> ind = new ArrayList<Integer>();
                    ArrayList<Integer> luku = new ArrayList<Integer>();
                    for ( int m : mahdollisetOrg.get(a)) {
                        //System.out.println(a+" "+m+" "+sudokuA[a/(SIZE*SIZE)][a%(SIZE*SIZE)] +" "+ sudokuB[a/(SIZE*SIZE)][a%(SIZE*SIZE)] +" "+ mahdollisetA.get(a).toString() +" "+ mahdollisetB.get(a).toString());
                        if (sudokuA[a/(SIZE*SIZE)][a%(SIZE*SIZE)] != m && sudokuB[a/(SIZE*SIZE)][a%(SIZE*SIZE)] != m && !(mahdollisetA.get(a).contains(m) || mahdollisetB.get(a).contains(m))) {
                            // System.out.println(" Exclusion "+a+" : " +m +" " + mahdollisetOrg.get(a).toString());
                            paluu = true;
                            estoListaOrg.get(a).add(m);
                            ind.add(a);
                            luku.add(m);
                            info = true;
                            count++;
                        }
                    }
                    for (int n = 0 ; n < ind.size() ; n++) {
                        mahdollisetOrg.get((int)ind.get(n)).remove((int)luku.get(n));
                    }
                    
                    if (sudokuA[a/(SIZE*SIZE)][a%(SIZE*SIZE)] == 0 && mahdollisetA.get(a).isEmpty()) {
                        //  System.out.println(" Exclusion A "+i+" + " +mahdollisetOrg.get(i).toArray()[0]);
                        estoLista.get(i).add((int)mahdollisetOrg.get(i).toArray()[0]);
                    }
                    if (sudokuB[a/(SIZE*SIZE)][a%(SIZE*SIZE)] == 0 && mahdollisetB.get(a).isEmpty()) {
                        //  System.out.println(" Exclusion B "+i+" + " + mahdollisetOrg.get(i).toArray()[1]);
                        estoLista.get(i).add((int)mahdollisetOrg.get(i).toArray()[1]);
                    }
                    
                }
                sudokuA[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = 0; 
                sudokuB[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = 0; 
                if (info) System.out.print("e");
            }
            if (count > 3) break;
        }
        loydetytRivi = new ArrayList<HashSet<Integer>>(); 
        loydetytSarake = new ArrayList<HashSet<Integer>>(); 
        loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
        for (int j = 0 ; j < SIZE*SIZE ; j++) {
            HashSet<Integer> hs1 = new HashSet<Integer>();
            loydetytRivi.add(hs1);
            HashSet<Integer> hs2 = new HashSet<Integer>();
            loydetytSarake.add(hs2);
            HashSet<Integer> hs3 = new HashSet<Integer>();
            loydetytRuutu.add(hs3);
        }
        estoLista = new ArrayList<HashSet<Integer>>();
        for (int j = 0 ; j < SIZE*SIZE*SIZE*SIZE ; j++) {
            AsetaVarma(j/(SIZE*SIZE),j%(SIZE*SIZE),sudoku[j/(SIZE*SIZE)][j%(SIZE*SIZE)]);
            HashSet<Integer> hs = new HashSet<Integer>();
            estoLista.add(hs);
            estoLista.get(j).addAll(estoListaOrg.get(j));
            
        }
        mahdolliset = new ArrayList<HashSet<Integer>>();
        asetaMahdolliset(sudoku);
        
        return paluu;
    }

    private static HashSet<Integer> vaakarivi(int rivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < SIZE*SIZE; i++) {
            if(sudoku[rivi][i] != 0) hs.add(sudoku[rivi][i]);
        }
        
        return hs;
    }

    private static HashSet<Integer> pystyrivi(int rivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < SIZE*SIZE; i++) {
            if(sudoku[i][rivi] != 0) hs.add(sudoku[i][rivi]);
        }
        return hs;
    }

    private static HashSet<Integer> ruutu(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        int vaaka = (vaakarivi/SIZE)*SIZE;
        int pysty = (pystyrivi/SIZE)*SIZE;
        
        for (int y = vaaka ; y < vaaka+SIZE; y++) {
            for (int x = pysty ; x < pysty+SIZE; x++) {
                if(sudoku[y][x] != 0) hs.add(sudoku[y][x]);
            }
        }
        return hs;
    }

    private static HashSet<Integer> orpoVaakaRivi(int[][] sudoku) {
        HashSet<Integer> varmat = new HashSet<Integer>();
        for (int rivi = 0; rivi < SIZE*SIZE; rivi++) {
            
            ArrayList<Integer> kaikkiVaihtoehdot = new ArrayList<Integer>();
            for (int i = 0 ; i < SIZE*SIZE ; i++) {
                kaikkiVaihtoehdot.addAll(vaihtoehdot(rivi, i, sudoku));
            }
            int[] kertaa = new int[SIZE*SIZE+1];
            for (int luku : kaikkiVaihtoehdot) {
                kertaa[luku]++;
            }
            for (int i = 1 ; i <= SIZE*SIZE ; i++) {
                if (kertaa[i] == 1) {
                    for (int a = 0; a < SIZE*SIZE ; a++) {
                        if (vaihtoehdot(rivi, a, sudoku).contains(i)) {
                            varmat.add(rivi*(SIZE*SIZE)+a);
                            sudoku[rivi][a] = i;
                            AsetaVarma(rivi, a, i);
                            a = SIZE*SIZE;
                        }
                    }
                }
            }
        }
        return varmat;
    }

    private static HashSet<Integer> orpoPystyRivi(int[][] sudoku) {
        HashSet<Integer> varmat = new HashSet<Integer>();
        for (int sarake = 0; sarake < SIZE*SIZE; sarake++) {
            ArrayList<Integer> kaikkiVaihtoehdot = new ArrayList<Integer>();
            for (int i = 0 ; i < SIZE*SIZE ; i++) {
                kaikkiVaihtoehdot.addAll(vaihtoehdot(i, sarake, sudoku));
            }
            int[] kertaa = new int[SIZE*SIZE+1];
            for (int luku : kaikkiVaihtoehdot) {
                kertaa[luku]++;
            }
            for (int i = 1 ; i <= SIZE*SIZE ; i++) {
                if (kertaa[i] == 1) {
                    for (int a = 0; a < SIZE*SIZE ; a++) {
                        if (vaihtoehdot(a, sarake, sudoku).contains(i)) {
                            varmat.add(a*(SIZE*SIZE)+sarake);
                            sudoku[a][sarake] = i;
                            AsetaVarma(a, sarake, i);
                            a = SIZE*SIZE;
                        }
                    }
                }
            }
        }
        return varmat;
    }

    private static HashSet<Integer> orpoRuutu(int[][] sudoku) {
        HashSet<Integer> varmat = new HashSet<Integer>();

        for ( int rivi = 0 ;rivi < SIZE*SIZE ; rivi=rivi+SIZE) {
            for ( int sarake = 0 ;sarake < SIZE*SIZE ; sarake=sarake+SIZE) {
                int vaaka = (rivi/SIZE)*SIZE;
                int pysty = (sarake/SIZE)*SIZE;
                ArrayList<Integer> kaikkiVaihtoehdot = new ArrayList<Integer>();
                for (int y = vaaka ; y < vaaka+SIZE; y++) {
                    for (int x = pysty ; x < pysty+SIZE; x++) {
                        kaikkiVaihtoehdot.addAll(vaihtoehdot(y, x, sudoku));
                    }
                }
                int[] kertaa = new int[SIZE*SIZE+1];
                for (int luku : kaikkiVaihtoehdot) {
                    kertaa[luku]++;
                }
            
                for (int i = 1 ; i <= SIZE*SIZE ; i++) {
                    if (kertaa[i] == 1) {
                        for (int a = rivi; a < rivi+SIZE ; a++) {
                            for (int b = sarake; b < sarake+SIZE ; b++) {
                                if (vaihtoehdot(a, b, sudoku).contains(i)) {
                                    varmat.add(a*(SIZE*SIZE)+b);
                                    sudoku[a][b] = i;
                                    AsetaVarma(a, b, i);
                                    b = sarake+SIZE;
                                    a = rivi+SIZE;
                                }
                            }
                        }
                    }
                }
            }
        }
        return varmat;
    }

    private static boolean tuplat(int[][] sudoku) {
        boolean tuplia = false;
        for (int i = 0 ; i < SIZE*SIZE ; i++) {
            ArrayList<Integer> setti = new ArrayList<Integer>();
            for (int j = 0 ; j < SIZE*SIZE ; j++) {
                if ( sudoku[i][j] != 0) setti.add(sudoku[i][j]);
            }
            for (int a : setti) {
                if (setti.indexOf(a) != setti.lastIndexOf(a)) {
                    if (!omaMerkisto) {
                    System.out.println("Tuplanumero " + a + " rivilla " +(i+1));
                    } else {
                        System.out.println("Tuplamerkki " + merkisto.get(a-1) + " rivilla " +(i+1));
                    }
                    tuplia = true;
                }
            }
        }
        for (int i = 0 ; i < SIZE*SIZE ; i++) {
            ArrayList<Integer> setti = new ArrayList<Integer>();
            for (int j = 0 ; j < SIZE*SIZE ; j++) {
                if ( sudoku[j][i] != 0) setti.add(sudoku[j][i]);
            }
            for (int a : setti) {
                if (setti.indexOf(a) != setti.lastIndexOf(a)) {
                    if (!omaMerkisto) {
                    System.out.println("Tuplanumero " + a + " sarakkeella " +(i+1));
                    } else {
                        System.out.println("Tuplamerkki " + merkisto.get(a-1) + " sarakkeella " +(i+1));
                    }
                    tuplia = true;
                }
            }
        }
        
        for (int i = 0 ; i < SIZE*SIZE ; i= i+SIZE) {
            for (int j = 0 ; j < SIZE*SIZE ; j=j+SIZE) {
                ArrayList<Integer> setti = new ArrayList<Integer>();
                for ( int y = i ; y < i+SIZE ; y++) {
                    for ( int x = j ; x < j+SIZE ; x++) {
                        if ( sudoku[y][x] != 0) setti.add(sudoku[y][x]);
                    }
                }
                for (int a : setti) {
                    if (setti.indexOf(a) != setti.lastIndexOf(a)) {
                        if (!omaMerkisto) {
                            System.out.println("Tuplanumero " + a + " ruudussa (" +(i/SIZE*SIZE + j/SIZE +1)+")");
                        } else {
                            System.out.println("Tuplamerkki " + merkisto.get(a-1) + " ruudussa (" +(i/SIZE*SIZE + j/SIZE +1)+")");
                        }
                        tuplia = true;
                    }
                }
            }
        }
        return tuplia;
    }

    private static void AsetaVarma(int y, int x, int luku) {
        if (luku != 0) {
            loydetytRivi.get(y).add(luku);
            loydetytSarake.get(x).add(luku);
            int ruutu = y/SIZE*SIZE + x/SIZE;
            loydetytRuutu.get(ruutu).add(luku);
        }
    }

    private static HashSet<Integer> vaihtoehdot(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> ts = new HashSet<Integer>();
        if (sudoku[vaakarivi][pystyrivi] == 0) {
            laskuri++;
            ts.addAll(arvot);
            ts.removeAll(loydetytRivi.get(vaakarivi));
            ts.removeAll(loydetytSarake.get(pystyrivi));
            ts.removeAll(loydetytRuutu.get(vaakarivi/SIZE*SIZE + pystyrivi/SIZE));
            ts.removeAll(estoLista.get(vaakarivi*SIZE*SIZE+pystyrivi));
        }
        return ts;
    }

    private static void tulostaSudoku(int[][] sudoku) {
        System.out.println();
        System.out.print(" +");
        for(int a = 0 ; a < SIZE; a++) {
            for(int b = 0 ; b < SIZE; b++) {
                System.out.print("---");
            }
            System.out.print("-+");
        }
        System.out.println();
        if (omaMerkisto) {
            for (int i = 0; i < SIZE*SIZE; i++) {
                System.out.print(" |");
                for (int j = 0; j < SIZE*SIZE; j++) {
                    if (sudoku[i][j] == 0) 
                        System.out.print(" . ");
                    else 
                        System.out.print(" "+merkisto.get(sudoku[i][j]-1)+" ");
                    if ((j+1)%SIZE==0) System.out.print(" |");
                }
                System.out.println();
                if ((i+1)%SIZE==0) {
                    System.out.print(" +");
                    for(int a = 0 ; a < SIZE; a++) {
                        for(int b = 0 ; b < SIZE; b++) {
                            System.out.print("---");
                        }
                        System.out.print("-+");
                    }
                    System.out.println();
                }
            }
        } else {
            for (int i = 0; i < SIZE*SIZE; i++) {
                System.out.print(" |");
                for (int j = 0; j < SIZE*SIZE; j++) {
                    if (sudoku[i][j] < 10) System.out.print(" "); 
                    if (sudoku[i][j] < 100 && SIZE > 9) System.out.print(" "); 
                    if (sudoku[i][j] == 0) 
                        System.out.print(" .");
                    else
                        System.out.print(" "+sudoku[i][j]);
                    
                    if ((j+1)%SIZE==0) System.out.print(" |");
                }
                System.out.println();
                if ((i+1)%SIZE==0) {
                    System.out.print(" +");
                    for(int a = 0 ; a < SIZE; a++) {
                        for(int b = 0 ; b < SIZE; b++) {
                            if (SIZE < 10)
                                System.out.print("---");
                            else 
                                System.out.print("----");
                        }
                        System.out.print("-+");
                    }
                    System.out.println();
                }
            }
        }
    }

    private static String haetulostus(int[][] sudoku, ArrayList<HashSet<Integer>> mahdolliset , int rivi, int ruuturivi, int ruutu) {
        StringBuilder paluu = new StringBuilder();
        
        for (int luku = ruuturivi*SIZE+1 ; luku < ruuturivi*SIZE+SIZE+1 ; luku++) {
            if (mahdolliset.get(rivi*SIZE*SIZE + ruutu ).contains(luku)) {
                paluu.append(merkisto.get(luku-1));
                paluu.append("");
            } else paluu.append(" ");
        }
        paluu.append(" ");
        
        if (mahdolliset.get(rivi*SIZE*SIZE + ruutu ).isEmpty()) {
            paluu = new StringBuilder();
            if (SIZE <4) {

                for (int t=0; t<SIZE; t++) {
                    if (sudoku[rivi][ruutu]!=0) {
                        paluu.append(merkisto.get(sudoku[rivi][ruutu]-1));
                    } else {
                        paluu.append(" ");
                    }
                }
            } else {
                paluu.append(" ");
                for (int t=0; t<SIZE-2; t++) {
                    if (ruuturivi==0 || ruuturivi==SIZE-1) {
                        paluu.append(" ");
                    } else {
                        paluu.append(merkisto.get(sudoku[rivi][ruutu]-1));
                    }
                }
            paluu.append(" ");
            }
            paluu.append(" ");
        }
        
        return paluu.toString();
    }
    
    private static void tulostaVaihtoehdotA(int[][] sudoku) {
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        System.out.print("+-");
        for (int a = 0 ; a < SIZE; a++) {
            for (int b = 0 ; b < SIZE*(SIZE+1); b++) {
                System.out.print("-");
            }
            System.out.print("+-");
        }
        System.out.println();
        
        for (int rivi = 0 ; rivi < SIZE*SIZE ; rivi ++) {
            for (int ruuturivi = 0 ; ruuturivi < SIZE ; ruuturivi ++) {
                System.out.print("| ");
                for (int ruutu = 0 ; ruutu < SIZE*SIZE ; ruutu ++) {
                    System.out.print(haetulostus(sudoku, mahdolliset, rivi, ruuturivi, ruutu )); 
                    if ((ruutu+1)%SIZE==0) System.out.print("| ");
                }
                System.out.println(" ");
            }
            if ((rivi+1)%SIZE == 0) {
                System.out.print("+-");
                for (int a = 0 ; a < SIZE; a++) {
                    for (int b = 0 ; b < SIZE*(SIZE+1); b++) {
                        System.out.print("-");
                    }
                    System.out.print("+-");
                }
                System.out.println();
            }
            
        }
    }

    private static void tulostaVaihtoehdot(int[][] sudoku) {
        if (omaMerkisto) {
            tulostaVaihtoehdotA(sudoku); 
            return;
        }
        System.out.println();
        for (int i = 0; i < SIZE*SIZE; i++) {
            for (int j = 0; j < SIZE*SIZE; j++) {
                if (!omaMerkisto) {
                    System.out.print(vaihtoehdot(i, j, sudoku));
                    
                } else {
                    System.out.print("[");
                    HashSet<Integer> v= vaihtoehdot(i, j, sudoku);
                    for (int c : v ) {
                        System.out.print(merkisto.get(c-1));
                    }
                    System.out.print("]");
                }
                if ((j+1)%SIZE==0) System.out.print("   ");
            }
            System.out.println();
            if ((i+1)%SIZE==0) System.out.println();
        }
    }

    public static boolean sudokuValmis(int[][] sudoku) {
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 if (sudoku[y][x] == 0) return false;
            }
        }
        return true;
    }

    public static int nollia(int[][] sudoku) {
        int paluu = 0;
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 if (sudoku[y][x] == 0) paluu++;
            }
        }
        return paluu;
    }
    
    public static int paikkoja(int[][] sudoku) {
        int paluu = 0;
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 if (!vaihtoehdot(y, x, sudoku).isEmpty()) paluu++;
            }
        }
        return paluu;
    }
    
    private static boolean rekurse(int i, int[][] sudoku, ArrayList<HashSet<Integer>> vrivi, ArrayList<HashSet<Integer>> vsarake, ArrayList<HashSet<Integer>> vruutu) {
        int y = i/(SIZE*SIZE);
        int x =  i%(SIZE*SIZE);
        int ruutu = (y/SIZE)*SIZE + x/SIZE;
        if (i > (SIZE*SIZE*SIZE*SIZE)-1 ) return true;
        if (sudoku[y][x] != 0) {
             return rekurse( i+1,  sudoku , vrivi, vsarake, vruutu );
        }
        HashSet<Integer> lista = new HashSet<Integer>(arvot);
        lista.removeAll(vrivi.get(y));
        lista.removeAll(vsarake.get(x));
        lista.removeAll(vruutu.get(ruutu));
        lista.removeAll(estoLista.get(i));
        laskuri++;

        for ( int luku : lista) {
            sudoku[y][x] = luku;
            vrivi.get(y).add(luku);
            vsarake.get(x).add(luku);
            vruutu.get(ruutu).add(luku);
            if (rekurse( i+1,  sudoku , vrivi, vsarake, vruutu)) {
                return true;
            } else {
                sudoku[y][x] = 0;
                vrivi.get(y).remove(luku);
                vsarake.get(x).remove(luku);
                vruutu.get(ruutu).remove(luku);
            }
        }
        return false;
    } 
    
    private static HashSet<Integer> etsiVarmat(HashSet<Integer> paikat, int[][] sudoku) { 
        HashSet<Integer> uudetVarmat = new HashSet<Integer>();
        
        for (int paikka : paikat) {
            int y = paikka / (SIZE*SIZE);
            int x = paikka % (SIZE*SIZE);
            if (sudoku[y][x] == 0) {
                HashSet<Integer> hs = new HashSet<Integer>();
                hs = vaihtoehdot(y, x, sudoku);
                // System.out.println("vaihtoehdot: " + y + " " +x+ " : "+hs);
                if (hs.size() == 1) {
                    sudoku[y][x] = (int)hs.toArray()[0];
                    AsetaVarma(y, x, sudoku[y][x]);
                    uudetVarmat.add(paikka);
                }
            }
        }
        uudetVarmat.addAll(orpoVaakaRivi(sudoku));
        uudetVarmat.addAll(orpoPystyRivi(sudoku));
        uudetVarmat.addAll(orpoRuutu(sudoku));

        return uudetVarmat;
    }

    private static HashSet<Integer> alusta(int[][] sudoku) { 
        estoLista = new ArrayList<HashSet<Integer>>();
        loydetytRivi = new ArrayList<HashSet<Integer>>(); 
        loydetytSarake = new ArrayList<HashSet<Integer>>(); 
        loydetytRuutu = new ArrayList<HashSet<Integer>>(); 
        
        for (int i = 0 ; i < SIZE*SIZE ; i++) {
            HashSet<Integer> hs1 = new HashSet<Integer>();
            loydetytRivi.add(hs1);
            HashSet<Integer> hs2 = new HashSet<Integer>();
            loydetytSarake.add(hs2);
            HashSet<Integer> hs3 = new HashSet<Integer>();
            loydetytRuutu.add(hs3);
        }

        HashSet<Integer> paluu = new HashSet<Integer>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE ; x++) {
                HashSet<Integer> hs = new HashSet<Integer>();
                estoLista.add(hs);
                if (sudoku[y][x] == 0) {
                    HashSet<Integer> v = vaihtoehdot(y, x, sudoku);
                    //System.out.println("vaihtoehdot: " + y + " " +x+ " : "+ts);
                    if (v.size() == 1) {
                        sudoku[y][x] = (int)v.toArray()[0];
                        AsetaVarma(y, x, sudoku[y][x]);
                    } else {
                        paluu.add(y*SIZE*SIZE+x);
                    } 
                } else AsetaVarma(y, x, sudoku[y][x]);
            }
        }
        return paluu;
    }


    public static void ratkaise(int[][] sudoku) { 
        
        HashSet<Integer> vapaatPaikat = alusta(sudoku);
        System.out.println("(" + (SIZE*SIZE*SIZE*SIZE-vapaatPaikat.size())+"/"+SIZE*SIZE*SIZE*SIZE +")");
        boolean uusiksi = true;
        //  tulostaVaihtoehdot(sudoku);
        while (uusiksi) {
            uusiksi = false;
            boolean haeVarmat = true;
            while (haeVarmat)  {
                
                HashSet<Integer> v = etsiVarmat(vapaatPaikat, sudoku);
                if (v.isEmpty()) {
                    haeVarmat = false;
                } else vapaatPaikat.removeAll(v);
                 System.out.print("*");
            }
            if (!sudokuValmis(sudoku)) {
                if (SIZE > 6) System.out.print("-"+vapaatPaikat.size());
                System.out.print("@");
                mahdolliset = new ArrayList<HashSet<Integer>>();
                asetaMahdolliset(sudoku);

                uusiksi = lukitseRivi(sudoku) || uusiksi;
                uusiksi = lukitseRuutu(sudoku) || uusiksi;

                uusiksi = nakuMonikotRivi(2,3,sudoku) || uusiksi;
                uusiksi = nakuMonikotSarake(2,3,sudoku) || uusiksi;
                uusiksi = nakuMonikotRuutu(2,3,sudoku) || uusiksi;

                uusiksi = piiloMonikotRivi(2,3,sudoku) || uusiksi;
                uusiksi = piiloMonikotSarake(2,3,sudoku) || uusiksi;
                uusiksi = piiloMonikotRuutu(2,3,sudoku) || uusiksi;

                if (!uusiksi) System.out.print(">"); else continue;
                uusiksi = xWingVaaka(sudoku)  || uusiksi;
                uusiksi = xWingPysty(sudoku) || uusiksi;

                if (SIZE > 3 && !uusiksi) {
                    uusiksi = uusiksi || nakuMonikotRivi(4,5,sudoku);
                    uusiksi = uusiksi || nakuMonikotSarake(4,5,sudoku);
                    uusiksi = uusiksi || nakuMonikotRuutu(4,5,sudoku);
                    uusiksi = uusiksi || swordfishVaaka(sudoku);
                    uusiksi = uusiksi || swordfishPysty(sudoku);
                    uusiksi = uusiksi || jellyfishVaaka(sudoku);
                    uusiksi = uusiksi || jellyfishPysty(sudoku);
                    if (!uusiksi) System.out.print(">"); else continue;
                    uusiksi = piiloMonikotRivi(4,5,sudoku) || uusiksi;
                    uusiksi = piiloMonikotSarake(4,5,sudoku) || uusiksi;
                    uusiksi = piiloMonikotRuutu(4,5,sudoku) || uusiksi;
                }
                
                uusiksi = uusiksi || exclusion(vapaatPaikat, sudoku);
                if (SIZE > 3) {
                    uusiksi = uusiksi || exclusion3(vapaatPaikat, sudoku);
                    uusiksi = uusiksi || nishio(vapaatPaikat, sudoku);
                }
            }
        }

        if (!sudokuValmis(sudoku)) {
            //tulostaSudoku(sudoku);
            //System.out.println("Ratkaistu : " + valmiina+"/"+SIZE*SIZE*SIZE*SIZE);
            //tulostaVaihtoehdot(sudoku);
            if (paikkoja(sudoku) == nollia(sudoku)) {
                System.out.print(" *");
                rekurse(0, sudoku, loydetytRivi, loydetytSarake, loydetytRuutu);
            } else {
                System.out.print(" Mahdotonta! ");
            }
        }
    }

    public static void tarkasta(int[][] sudoku) {
        for (int i = 0 ; i < (SIZE*SIZE*SIZE*SIZE) ; i++) {
            if (sudoku[i/(SIZE*SIZE)][i%(SIZE*SIZE)] == 0) {
                System.out.println("Virhe sudokussa nollia");
                break;
            }
        }
        for (int i = 0 ; i < SIZE*SIZE ; i++) {
            if (vaakarivi(i, sudoku).size() != SIZE*SIZE) {
                System.out.println("Virhe sudokussa rivilla "+ (i+1) );
            }
            if (pystyrivi(i, sudoku).size() != SIZE*SIZE) {
                System.out.println("Virhe sudokussa sarakkeessa "+(i+1) );
            }
        }
        for (int i = 0 ; i < SIZE*SIZE ; i=i+SIZE) {
            for (int j = 0 ; j < SIZE*SIZE ; j=j+SIZE) {
                if (ruutu(i,j, sudoku).size() != SIZE*SIZE) {
                    System.out.println("Virhe sudokussa ruudussa "+ (i/SIZE*SIZE+j/SIZE+1));
                }
            }
        }
    }
          
     
    public static void main(String[] args) throws FileNotFoundException {
        
        rivit = new HashSet<>();
        arvot = new HashSet<>();
        boolean tulostaMerkisto=true;
        long startTime = System.currentTimeMillis();
        
        laskuri = 0;
        String s ="";
        if (args.length == 2 ) {
            try {
                s = lueLuvut(args[1]);
            } catch(FileNotFoundException e) {
                 System.out.println("Ei loydy tiedostoa " + args[1]);
            }
        } else if (args.length == 1 ) {
            if (args[0].startsWith("-"))  {
                s = args[0].substring(1);
                s = s.replaceAll("\\s+", "");
                s = s.replaceAll("/[^0-9]/", "0");
                omaMerkisto=true;
                tulostaMerkisto = false;
                merkisto = new ArrayList<>();
                if (s.length()>80) {
                    for (Character c : "123456789".toCharArray()) {
                        merkisto.add(""+c);
                    }
                } else if (s.length() > 15) {
                    for (Character c : "1234".toCharArray()) {
                        merkisto.add(""+c);
                    }
                } else {
                    merkisto.add("1");
                }
                if (s.contains("@")) {
                    tulostaMerkisto = true;
                    merkisto = new ArrayList<>();
                    String m = s.substring(s.indexOf("@")+1);
                    for (Character c : m.toCharArray()) {
                        merkisto.add(""+c);
                    }
                }
            } else {
                try {
                    s = lueLuvut(args[0]);
                } catch(FileNotFoundException e) {
                        System.out.println("Ei loydy tiedostoa "+ args[0]);
                }
            }
        } else {
            System.out.println("Anna koko ja/tai sudokutiedoston nimi");
        }

        if (!s.isEmpty()) {
        //System.out.println("Taulu "+s );
            ArrayList<Integer> al  = parsiSisalto(s);
            SIZE = (int) Math.sqrt(al.size());
            SIZE = (int) Math.sqrt(SIZE);
            
            for (int i = 0 ; i < SIZE*SIZE; i++) rivit.add(i);
            for (int i = 1 ; i <= SIZE*SIZE; i++) arvot.add(i);
            System.out.println(" "+SIZE + " " +SIZE*SIZE);
            

            int[][] sudoku = new int[SIZE*SIZE][SIZE*SIZE];
            for (int i = 0; i < SIZE*SIZE*SIZE*SIZE; i++) {
                sudoku[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)al.get(i) ;    
            }
            if (!merkisto.isEmpty() && tulostaMerkisto) System.out.println(" "+ merkisto);
            if (merkisto.isEmpty() || merkisto.size() == SIZE*SIZE) {
                tulostaSudoku(sudoku);
                if (!tuplat(sudoku)) {
                    ratkaise(sudoku);
                    System.out.println(" * "+ laskuri +" *");
                    tulostaSudoku(sudoku);
                    tarkasta(sudoku);
                } else {
                    System.out.println("Virheellinen lahtotilanne");
                }
            } else {
                System.out.println("Virheellinen sudoku tai merkisto");
            }
            
        }
        System.out.println(" # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
    
    public static ArrayList<Integer> parsiSisalto(String sisalto) {
        ArrayList<Integer> s = new ArrayList<>();
        ArrayList<String> lukuArray = new ArrayList<>();
        if (!omaMerkisto) {
            sisalto = sisalto.replaceAll("^\\s+", "");
            for (String luku : sisalto.split("\\s")) {
                lukuArray.add(luku);
            }
        } else {
            sisalto = sisalto.replaceAll("\\s+", "");
            for (Character c : sisalto.toCharArray()) {
                lukuArray.add(""+c);
            }
        }
       
        String[] luvut = lukuArray.stream().toArray(String[]::new);
        int lukuja = luvut.length;
         
        if (luvut.length < lukuja) {
            System.out.println("\n liian pieni tiedosto " +luvut.length);
            lukuja = luvut.length;
        }
        for (int i = 0; i < lukuja; i++) {
            int c = 0;
            
            if (omaMerkisto) {
                c = merkisto.indexOf(""+luvut[i]) + 1;
                if (c < 0) c = 0;
            } else {
                if (Pattern.matches("[0-9]+", luvut[i])) {
                    c = Integer.parseInt(luvut[i]);
                } else {
                    c = 0;
                }
            }
            s.add(c);
        }
        return s;
    }
    public static String lueLuvut(String tiedosto) throws FileNotFoundException {
        Scanner lukija = new Scanner(new File(tiedosto));
        StringBuilder sisalto = new StringBuilder();
        StringBuilder merkit = new StringBuilder();
        merkisto = new ArrayList<>();
        try {
            while (lukija.hasNext()) {
                String s = lukija.next();
                if ((s.startsWith("#")) || s.startsWith("!")) {
                    if (lukija.hasNext()) s = lukija.nextLine();
                } else if (s.startsWith("@"))  {
                    merkit.append(s);
                } else {
                    sisalto.append(" ");
                    sisalto.append(s);
                }
            }
        } catch (Exception e) {
          System.out.println("Virhe: " + e.getMessage());
        } finally {
            if(lukija !=null){lukija.close();}
        }
        //System.out.println(" merkit " + merkit.toString());
        if (merkit.length() != 0) {
            for(Character c : merkit.toString().toCharArray()) {
                if (c != '@') merkisto.add(""+c);
            }
            if (merkisto.size() >0 ) omaMerkisto = true;
        }
        
        return sisalto.toString();
    }
}

