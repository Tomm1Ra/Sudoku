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
   
//Pointing Pairs, Pointing Triples
    private static boolean lukitseRivi(int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        //vaakaan
        for (int ruutuY = 0 ; ruutuY < SIZE*SIZE ; ruutuY=ruutuY+SIZE) {
            for (int ruutuX = 0 ; ruutuX < SIZE*SIZE ; ruutuX=ruutuX+SIZE) {
                for (int vertailu = 0 ; vertailu < SIZE ; vertailu++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sisus = 0 ; sisus < SIZE*SIZE ; sisus ++) {
                        if (sisus >= vertailu*SIZE && sisus < vertailu*SIZE+SIZE) {
                            omat.addAll(mahdolliset.get((ruutuY+sisus/SIZE)*SIZE*SIZE+ruutuX+sisus%SIZE));
                        } else {
                            muut.addAll(mahdolliset.get((ruutuY+sisus/SIZE)*SIZE*SIZE+ruutuX+sisus%SIZE));
                        }

                    }
                    //System.out.println(ruutuY+""+ruutuX+" - " + omat.toString()+muut.toString());
                
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int y = ruutuY+vertailu;
                        for (int luku : omat) {
                            for (int x = 0 ; x < SIZE*SIZE; x++) {
                                if ( x < ruutuX || x >= ruutuX+SIZE ) {
                                    if (sudoku[y][x] == 0 && !estoLista.get(y*SIZE*SIZE+x).contains(luku)) {
                                        //System.out.println("LukitseRuutu " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE+x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE+x).remove(luku);
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
                for (int vertailu = 0 ; vertailu < SIZE ; vertailu++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sisus = 0 ; sisus < SIZE*SIZE ; sisus ++) {
                        if (sisus%SIZE == vertailu) {
                            omat.addAll(mahdolliset.get((ruutuY+sisus/SIZE)*SIZE*SIZE+ruutuX+sisus%SIZE));
                        } else {
                            muut.addAll(mahdolliset.get((ruutuY+sisus/SIZE)*SIZE*SIZE+ruutuX+sisus%SIZE));
                        }

                    }
                    //System.out.println(ruutuY+""+ruutuX+" - " + omat.toString()+muut.toString());
                
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int x = ruutuX+vertailu;
                        for (int luku : omat) {
                            for (int y = 0 ; y < SIZE*SIZE; y++) {
                                if ( y < ruutuY || y >= ruutuY+SIZE ) {
                                    if (sudoku[y][x] == 0 && !estoLista.get(y*SIZE*SIZE+x).contains(luku)) {
                                        //System.out.println("LukitsePysty " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE+x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE+x).remove(luku);
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
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
    
        //rivi
        for (int rivi = 0 ; rivi < SIZE*SIZE ; rivi++) {
            for (int vertailu = 0 ; vertailu < SIZE*SIZE ; vertailu=vertailu+SIZE) {
                HashSet<Integer> muut = new HashSet<Integer>();
                HashSet<Integer> omat = new HashSet<Integer>();
                for (int sisus = 0 ; sisus < SIZE*SIZE ; sisus ++) {
                    if (sisus >= vertailu && sisus < vertailu+SIZE) {
                        omat.addAll(mahdolliset.get(rivi*SIZE*SIZE+sisus));
                    } else {
                        muut.addAll(mahdolliset.get(rivi*SIZE*SIZE+sisus));
                    }
                    
                }
                //System.out.println("Omat"+rivi+" "+vertailu +omat.toString()+muut.toString());
                omat.removeAll(muut);
                if (!omat.isEmpty()) {
                    int alkuY = rivi/SIZE * SIZE;
                    int alkuX = vertailu;
                    for (int luku : omat) {
                        for (int y = alkuY ; y < alkuY+SIZE; y++) {
                            for (int x = alkuX ; x < alkuX+SIZE; x++) {
                                if ( y != rivi ) {
                                    if (sudoku[y][x] == 0 && !estoLista.get(y*SIZE*SIZE+x).contains(luku)) {
                                        //System.out.println("LukitseRivi " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE+x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE+x).remove(luku);
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
            for (int vertailu = 0 ; vertailu < SIZE*SIZE ; vertailu=vertailu+SIZE) {
                HashSet<Integer> muut = new HashSet<Integer>();
                HashSet<Integer> omat = new HashSet<Integer>();
                for (int sisus = 0 ; sisus < SIZE*SIZE ; sisus ++) {
                    if (sisus >= vertailu && sisus < vertailu+SIZE) {
                        omat.addAll(mahdolliset.get(sisus*SIZE*SIZE +sarake));
                    } else {
                        muut.addAll(mahdolliset.get(sisus*SIZE*SIZE + sarake));
                    }
                    
                }
                omat.removeAll(muut);
                if (!omat.isEmpty()) {
                    int alkuY = vertailu;
                    int alkuX = sarake/SIZE*SIZE;
                    for (int luku : omat) {
                        for (int y = alkuY ; y < alkuY+SIZE; y++) {
                            for (int x = alkuX ; x < alkuX+SIZE; x++) {
                                if ( x != sarake ) {
                                    if (sudoku[y][x] == 0 && !estoLista.get(y*SIZE*SIZE+x).contains(luku)) {
                                        //System.out.println("LukitseSarake " + (y*SIZE*SIZE+x) +" "+luku);   
                                        estoLista.get(y*SIZE*SIZE+x).add(luku);
                                        mahdolliset.get(y*SIZE*SIZE+x).remove(luku);
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
    
    private static boolean piiloMonikotRivi(int montako,int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int rivi = 0; rivi < SIZE*SIZE; rivi++) {
            HashSet<Integer> rivinarvot = new HashSet<Integer>();
            for (int i = 0 ; i < SIZE*SIZE ; i++) {
                rivinarvot.addAll(mahdolliset.get(rivi*SIZE*SIZE+i));
            }
            
            if (rivinarvot.size() >= montako && rivinarvot.size() < 20) {
                Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                int lista[] = new int[rivinarvot.size()];
                for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                    lista[i] = kokolista[i];
                   } 
                int vaihtoehto[] = new int[montako];
                hakuLista = new ArrayList<HashSet<Integer>>();
                combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                //System.out.println(""+hakuLista.toString());
                for (HashSet<Integer> ts : hakuLista) {
                    HashSet<Integer> poisto = new HashSet<Integer>();
                    poisto.addAll(rivinarvot);
                    poisto.removeAll(ts);
                    //System.out.println(rivi+ " "+rivinarvot.toString());
                    int loytynytpiilo = 0;
                    ArrayList<Integer> loytyneetpiilopaikat = new ArrayList<Integer>();
                    for (int sarake = 0 ; sarake < SIZE*SIZE ;sarake++) {
                        HashSet<Integer> apuPiilo = new HashSet<Integer>();
                        HashSet<Integer> temp = mahdolliset.get(rivi*SIZE*SIZE+sarake);
                        if (!temp.isEmpty()) {
                            apuPiilo.addAll(temp);
                            //System.out.println("XX " +apu.toString()+ts.toString());
                            apuPiilo.removeAll(poisto);
                             if (!apuPiilo.isEmpty()) {
                                 loytynytpiilo++;
                                 loytyneetpiilopaikat.add(sarake);
                             }
                        }
                    }
                    if (loytynytpiilo == montako) {
                        HashSet<Integer> estot = new HashSet<Integer>(rivit);
                        estot.removeAll(ts);
                        //System.out.println(montako+" Piilo "+rivi+" "+estot.toString()+ts.toString());
                        for (int r : loytyneetpiilopaikat) {
                            if (!estoLista.get(SIZE*SIZE*rivi+r).containsAll(estot)) {
                                estoLista.get(SIZE*SIZE*rivi+r).addAll(estot);
                                mahdolliset.get(SIZE*SIZE*rivi+r).removeAll(estot);
                                paluu = true; 
                            }    
                        }
                    }
                }
            }
        }
        return paluu;
    }

    private static boolean nakuMonikotRivi(int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < SIZE*2 ; montako++) {
            if (paluu && montako > SIZE) return true;
            for (int rivi = 0; rivi < SIZE*SIZE; rivi++) {
                HashSet<Integer> ruudut = new HashSet<Integer>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    if (mahdolliset.get(rivi*SIZE*SIZE+i).size() != 0 && mahdolliset.get(rivi*SIZE*SIZE+i).size() <= montako ){
                        ruudut.add(i);
                    }
                }
                if (ruudut.size() >= montako && ruudut.size() < 20) {
                    Integer kokolista [] = ruudut.toArray(new Integer[ruudut.size()]);
                    int lista[] = new int[ruudut.size()];
                    for (int i = 0 ; i < ruudut.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                    //C
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
                                        if (!estoLista.get(SIZE*SIZE*rivi+r).contains(luku)) {
                                            //System.out.println(montako+" EstaR " +(SIZE*SIZE*rivi+r)+" "+(luku));
                                            estoLista.get(SIZE*SIZE*rivi+r).add(luku);
                                            mahdolliset.get(SIZE*SIZE*rivi+r).remove(luku);
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

    private static boolean piiloMonikotSarake(int montako,int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int sarake = 0; sarake < SIZE*SIZE; sarake++) {
            HashSet<Integer> rivinarvot = new HashSet<Integer>();
            for (int i = 0 ; i < SIZE*SIZE ; i++) {
                rivinarvot.addAll(mahdolliset.get(i*SIZE*SIZE + sarake));
            }
            if (rivinarvot.size() >= montako && rivinarvot.size() < 20) {
                //System.out.println("Nelikko " +rivinarvot.toString());
                Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                int lista[] = new int[rivinarvot.size()];
                for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                    lista[i] = kokolista[i];
                } 
                int vaihtoehto[] = new int[montako];
                hakuLista = new ArrayList<HashSet<Integer>>();
                combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                //System.out.println(""+hakuLista.toString());
                for (HashSet<Integer> ts : hakuLista) {
                    HashSet<Integer> poisto = new HashSet<Integer>();
                    poisto.addAll(rivinarvot);
                    poisto.removeAll(ts);
                    int loytynyt = 0;

                    ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                    for (int rivi = 0 ; rivi < SIZE*SIZE ;rivi++) {
                        HashSet<Integer> apu = new HashSet<Integer>();
                        HashSet<Integer> temp = mahdolliset.get(rivi*SIZE*SIZE+sarake);
                        if (!temp.isEmpty()) {
                            apu.addAll(temp);
                            apu.removeAll(poisto);
                            if (!apu.isEmpty()) {
                                loytynyt++;
                                loytyneetpaikat.add(rivi);
                            }
                        }
                    }
                    if (loytynyt == montako) {
                        HashSet<Integer> estot = new HashSet<Integer>(arvot);
                        estot.removeAll(ts); 
                        for (int r : loytyneetpaikat) {
                            if (!estoLista.get(SIZE*SIZE*r+sarake).containsAll(estot)) {
                                estoLista.get(SIZE*SIZE*r + sarake).addAll(estot);
                                mahdolliset.get(SIZE*SIZE*r + sarake).removeAll(estot);
                                paluu = true; 
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }
    
    private static boolean nakuMonikotSarake(int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < SIZE*2 ; montako++) {
            if (paluu && montako > SIZE) return true;
            for (int sarake = 0; sarake < SIZE*SIZE; sarake++) {
                HashSet<Integer> ruudut = new HashSet<Integer>();
                for (int i = 0 ; i < SIZE*SIZE ; i++) {
                    if (mahdolliset.get(i*SIZE*SIZE+sarake).size() != 0 && mahdolliset.get(i*SIZE*SIZE+sarake).size() <= montako ){
                        ruudut.add(i);
                    }
                }
                if (ruudut.size() >= montako && ruudut.size() < 20) {
                    //System.out.println("Nelikko " +rivinarvot.toString());
                    Integer kokolista [] = ruudut.toArray(new Integer[ruudut.size()]);
                    int lista[] = new int[ruudut.size()];
                    for (int i = 0 ; i < ruudut.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
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
                                        if (!estoLista.get(SIZE*SIZE*r+sarake).contains(luku)) {
                                            //System.out.println(montako+" EstaS " +(SIZE*SIZE*r+sarake)+" "+(luku));
                                            estoLista.get(SIZE*SIZE*r+sarake).add(luku);
                                            mahdolliset.get(SIZE*SIZE*r+sarake).remove(luku);
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

    private static boolean piiloMonikotRuutu(int montako,int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for ( int rivi = 0 ;rivi < SIZE*SIZE ; rivi=rivi+SIZE) {
            for ( int sarake = 0 ;sarake < SIZE*SIZE ; sarake=sarake+SIZE) {
                int vaaka = (rivi/SIZE)*SIZE;
                int pysty = (sarake/SIZE)*SIZE;

                HashSet<Integer> rivinarvot = new HashSet<Integer>();
                for (int y = vaaka ; y < vaaka+SIZE; y++) {
                    for (int x = pysty ; x < pysty+SIZE; x++) {
                        rivinarvot.addAll(mahdolliset.get(SIZE*SIZE*vaaka+pysty));
                    }
                }

                if (rivinarvot.size() >= montako && rivinarvot.size() < 20) {
                    Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                    int lista[] = new int[rivinarvot.size()];
                    for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<HashSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (HashSet<Integer> ts : hakuLista) {
                        HashSet<Integer> poisto = new HashSet<Integer>();
                        poisto.addAll(rivinarvot);
                        poisto.removeAll(ts);
                        int loytynyt = 0;
                        ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                        for (int y = 0 ; y < SIZE ;y++) {
                            for (int x = 0 ; x < SIZE ;x++) {
                        
                                HashSet<Integer> apu = new HashSet<Integer>();
                                int paikka = (rivi+y)*SIZE*SIZE+sarake+x;
                                HashSet<Integer> temp = mahdolliset.get(paikka);
                                
                                if (!temp.isEmpty()) {
                                    apu.addAll(temp);
                                    apu.removeAll(poisto);
                                    if (!apu.isEmpty()) {
                                        loytynyt++;
                                        loytyneetpaikat.add(paikka);
                                    }
                                }
                            }
                        }
                        if (loytynyt == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(arvot);
                            estot.removeAll(ts);
                            for (int r : loytyneetpaikat) {
                                if (!estoLista.get(r).containsAll(estot)) {
                                    estoLista.get(r).addAll(estot);
                                    mahdolliset.get(r).removeAll(estot);
                                    //System.out.println(montako+" EstaPiiloX " +r+" "+estot);
                                    paluu = true; 
                                }
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }       
   
    private static boolean nakuMonikotRuutu(int[][] sudoku) {
        boolean paluu = false;
        ArrayList<HashSet<Integer>> mahdolliset = new ArrayList<HashSet<Integer>>();
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < SIZE*2 ; montako++) {
            if (paluu && montako > SIZE) return true;
            for ( int rivi = 0 ;rivi < SIZE*SIZE ; rivi=rivi+SIZE) {
                for ( int sarake = 0 ;sarake < SIZE*SIZE ; sarake=sarake+SIZE) {
                    int vaaka = (rivi/SIZE)*SIZE;
                    int pysty = (sarake/SIZE)*SIZE;
                    HashSet<Integer> ruudut = new HashSet<Integer>();
                    HashSet<Integer> alue = new HashSet<Integer>();
                    
                    for (int y = vaaka ; y < vaaka+SIZE; y++) {
                        for (int x = pysty ; x < pysty+SIZE; x++) {
                            if (mahdolliset.get(y*SIZE*SIZE+x).size() != 0 && mahdolliset.get(y*SIZE*SIZE+x).size() <= montako ){
                                ruudut.add(y*SIZE*SIZE+x);
                            }    
                            alue.add(y*SIZE*SIZE+x);
                        }
                    }

                    if (ruudut.size() >= montako && ruudut.size() < 20) {
                        Integer kokolista [] = ruudut.toArray(new Integer[ruudut.size()]);
                        int lista[] = new int[ruudut.size()];
                        for (int i = 0 ; i < ruudut.size(); i++ ) {
                            lista[i] = kokolista[i];
                        } 
                        int vaihtoehto[] = new int[montako];
                        hakuLista = new ArrayList<HashSet<Integer>>();
                        combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                        //System.out.println(montako+" "+hakuLista.toString());
                        
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
                                            if (!estoLista.get(r).contains(luku)) {
                                                //System.out.println(montako+" nakuMon EstaX " +r+" "+(luku));
                                                estoLista.get(r).add(luku);
                                                mahdolliset.get(r).remove(luku);
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

    private static int orpoVaakaRivi(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        for (int i = 0 ; i < SIZE*SIZE ; i++) {
            if (sudoku[vaakarivi][i] == 0) {
                HashSet<Integer> ts = vaihtoehdot(vaakarivi, i, sudoku);
                if (i == pystyrivi) {
                    omat.addAll(ts);
                } else {
                    muut.addAll(ts);
                }
            }
            
        }
        //System.out.println("vaaka:" +omat.toString()+" "+muut.toString());
        omat.removeAll(muut);
        if (omat.size()==1) {
            return (int)omat.toArray()[0];
        }
        
        return 0;
    }

    private static int orpoPystyRivi(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        for (int i = 0 ; i < SIZE*SIZE ; i++) {
            if (sudoku[i][pystyrivi] == 0) {
                HashSet<Integer> ts = vaihtoehdot(i, pystyrivi, sudoku);
                if (i == vaakarivi) {
                    omat.addAll(ts);
                } else {
                    muut.addAll(ts);
                }
            }
            
        }
        //System.out.println("pysty:" +omat.toString()+" "+muut.toString());
        omat.removeAll(muut);
        if (omat.size()==1) {
            return (int)omat.toArray()[0];
        }
        return 0;
    }

    private static int orpoRuutu(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        int vaaka = (vaakarivi/SIZE)*SIZE;
        int pysty = (pystyrivi/SIZE)*SIZE;
        
        for (int y = vaaka ; y < vaaka+SIZE; y++) {
            for (int x = pysty ; x < pysty+SIZE; x++) {
                if (sudoku[y][x] == 0) {
                    HashSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                    if ((y == vaakarivi) && (x == pystyrivi)) {
                        omat.addAll(ts);
                    } else {
                        muut.addAll(ts);
                    }
                }
           }
        }

        //System.out.println("ruutu:" +omat.toString()+" "+muut.toString());
        omat.removeAll(muut);
        if (omat.size() == 1){
            return (int)omat.toArray()[0];
        }
        return 0;
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
        loydetytRivi.get(y).add(luku);
        loydetytSarake.get(x).add(luku);
        int ruutu = y/SIZE*SIZE + x/SIZE;
        loydetytRuutu.get(ruutu).add(luku);
    }

    private static HashSet<Integer> vaihtoehdot(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> ts = new HashSet<Integer>();
        ts.addAll(arvot);
        laskuri++;
        if (sudoku[vaakarivi][pystyrivi] == 0) {
            ts.removeAll(loydetytRivi.get(vaakarivi));
            ts.removeAll(loydetytSarake.get(pystyrivi));
            ts.removeAll(loydetytRuutu.get(vaakarivi/SIZE*SIZE + pystyrivi/SIZE));
            ts.removeAll(estoLista.get(vaakarivi*SIZE*SIZE+pystyrivi));
        } else {
            ts = new HashSet<Integer>();
        }
        return ts;
    }


    private static void tulostaSudoku(int[][] sudoku) {
        System.out.println();
        for(int n=0;n<(SIZE*SIZE*3 + SIZE*2) ;n++) System.out.print("-");
        System.out.println();
        if (omaMerkisto) {
            for (int i = 0; i < SIZE*SIZE; i++) {
                for (int j = 0; j < SIZE*SIZE; j++) {
                    if (sudoku[i][j] == 0) 
                        System.out.print(" . ");
                    else 
                        System.out.print(" "+merkisto.get(sudoku[i][j]-1)+" ");
                    if ((j+1)%SIZE==0) System.out.print(" |");
                }
                System.out.println();
                if ((i+1)%SIZE==0) {
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
    
    private static void tulostaVaihtoehdot(int[][] sudoku) {
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
    
    private static boolean etsiVarmat(int[][] sudoku) { 
        boolean loytyi = false;
        for (int y = 0 ; y < SIZE*SIZE ; y++) {
            for (int x = 0 ; x < SIZE*SIZE ; x++) {

                if (sudoku[y][x] == 0) {
                    HashSet<Integer> ts = new HashSet<Integer>();
                    ts = vaihtoehdot(y, x, sudoku);
                    //System.out.println("vaihtoehdot: " + y + " " +x+ " : "+ts);
                    if (ts.size() == 1) {
                        sudoku[y][x] = (int)ts.toArray()[0];
                        AsetaVarma(y, x, sudoku[y][x]);
                        //System.out.println("valinta: " + y + " " +x+ " : "+ts.first());
                        loytyi = true;
                    }
                    if (sudoku[y][x] == 0) {
                        //System.out.println(y+" "+x+" "+vaihtoehdot(y, x, sudoku));
                        sudoku[y][x] = orpoVaakaRivi(y, x, sudoku);
                        if (sudoku[y][x] != 0) {
                            AsetaVarma(y, x, sudoku[y][x]);
                            loytyi = true;
                            //System.out.println(y+" vaaka "+x+" "+sudoku[y][x]);
                        }

                        if (sudoku[y][x] == 0) {
                            sudoku[y][x] = orpoPystyRivi(y, x, sudoku);
                            if (sudoku[y][x] != 0) {
                                AsetaVarma(y, x, sudoku[y][x]);
                                loytyi = true;
                                //System.out.println(y+" pysty "+x+" "+sudoku[y][x]);
                            }
                        }
                        if (sudoku[y][x] == 0) {
                            sudoku[y][x] = orpoRuutu(y, x, sudoku);
                            if (sudoku[y][x] != 0) {
                                AsetaVarma(y, x, sudoku[y][x]);
                                loytyi = true;
                                //System.out.println(y+" ruutu "+x+" "+sudoku[y][x]);
                            }
                        }
                    }
                }
            }
        }
        return loytyi;
    }
 
    public static void ratkaise(int[][] sudoku) { 
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
        estoLista = new ArrayList<HashSet<Integer>>(); 
        for (int i = 0 ; i < SIZE*SIZE*SIZE*SIZE ; i++) {
            HashSet<Integer> hs = new HashSet<Integer>();
            estoLista.add(hs);
            if (sudoku[i/(SIZE*SIZE)][i%(SIZE*SIZE)] !=0 )
            AsetaVarma(i/(SIZE*SIZE),i%(SIZE*SIZE),sudoku[i/(SIZE*SIZE)][i%(SIZE*SIZE)]);
        }
        System.out.println("Ratkaistu : " + (SIZE*SIZE*SIZE*SIZE-nollia(sudoku))+"/"+SIZE*SIZE*SIZE*SIZE);
        boolean uusiksi = true;
        //tulostaVaihtoehdot(sudoku);
        while (uusiksi) {
            uusiksi = false;
            while (etsiVarmat(sudoku))  {
                System.out.print("*");
            }
            if (nollia(sudoku)!=0) {
                System.out.print("@");
                int a = 0;
                uusiksi = uusiksi || nakuMonikotRivi(sudoku);
                uusiksi = uusiksi || nakuMonikotSarake(sudoku);
                uusiksi = uusiksi || nakuMonikotRuutu(sudoku);
                uusiksi = uusiksi || lukitseRivi(sudoku);
                uusiksi = uusiksi || lukitseRuutu(sudoku);
                for (int montako = 2 ; montako < SIZE+2; montako++) {
                    if (uusiksi && montako > SIZE) continue;
                    uusiksi = uusiksi || piiloMonikotRivi(montako,sudoku);
                    uusiksi = uusiksi || piiloMonikotSarake(montako,sudoku);
                    uusiksi = uusiksi || piiloMonikotRuutu(montako,sudoku);
                    //System.out.print("@");
                }
            }
        }

        if (nollia(sudoku)!=0) {
            //tulostaSudoku(sudoku);
            //System.out.println("Ratkaistu : " + (SIZE*SIZE*SIZE*SIZE-nollia(sudoku))+"/"+SIZE*SIZE*SIZE*SIZE);
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
                for (Character c : "123456789".toCharArray()) {
                    merkisto.add(""+c);
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
            System.out.println("SIZE :"+SIZE + " " +SIZE*SIZE);
            

            int[][] sudoku = new int[SIZE*SIZE][SIZE*SIZE];
            for (int i = 0; i < SIZE*SIZE*SIZE*SIZE; i++) {
                sudoku[i/(SIZE*SIZE)][i%(SIZE*SIZE)] = (int)al.get(i) ;    
            }
            if (!merkisto.isEmpty() && tulostaMerkisto) System.out.println("Merkisto: "+ merkisto);
            if (merkisto.isEmpty() || merkisto.size() == SIZE*SIZE) {
                tulostaSudoku(sudoku);
                if (!tuplat(sudoku)) {
                    ratkaise(sudoku);
                    System.out.println("\n ** Ratkaisu * "+ laskuri +" * \n");
                    tulostaSudoku(sudoku);
                    tarkasta(sudoku);
                } else {
                    System.out.println("Virheellinen lahtotilanne");
                }
            } else {
                System.out.println("Virheellinen sudoku tai merkisto");
            }
            
        }

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
                    s = lukija.nextLine();
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

