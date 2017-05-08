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

class Vertex implements Comparable<Vertex> {
    public final int  id;
    public Vertex(int argName) { 
        this.id = argName; 
    }
    public ArrayList<Edge> adjacencies;
    public int minDistance = Integer.MAX_VALUE;
    public Vertex previous;
    
    public String toString() { return id+""; }
    public int compareTo(Vertex other)
    {
        return Long.compare(minDistance, other.minDistance);
    }
}

class Edge {
    public final Vertex naapuri;
    public final int matka;
    public Edge(Vertex kohde, int etaisyys)
    { 
        this.naapuri = kohde; 
        this.matka = etaisyys; 
    }
}

public class Sudoku {
    private static ArrayList<HashSet<Integer>> estoLista;
    private static ArrayList<TreeSet<Integer>> hakuLista;
    private static int laskuri;
   
    private static void combinationUtil(int arr[], int data[], int start,
                                int end, int index, int r)
    {
        // Current combination is ready to be printed, print it
        if (index == r)
        {
           TreeSet<Integer> ts = new TreeSet<Integer>();
            for (int j=0; j<r; j++) ts.add(data[j]);
                //System.out.print(data[j]+" ");
                
            //System.out.println("");
            hakuLista.add(ts);
            return;
        }
 
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    public static void computePaths(Vertex source) {
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies) {
                Vertex v = e.naapuri;
                int weight = e.matka;
                int distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);
                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }    
   
    private static boolean yhteys(int a, int b) {
        if(a%9 == b%9) return true;
        if(a/9 == b/9) return true;
        if ((a/9/3 == b/9/3) && (a%9/3 == b%9/3)) return true;
        return false;
    }
    
    private static boolean samaRivi(int a, int b) {
        if(a/9 == b/9) return true;
        return false;    
    }

    private static boolean samaSarake(int a, int b) {
        if(a%9 == b%9) return true;
        return false;    
    }

    private static boolean samaRuutu(int a, int b) {
        if (((a/9)/3 == (b/9)/3) && (a%9/3 == b%9/3)) return true;
        return false;    
    }

    private static TreeSet<Integer> haeNumerollatRivi(int rivi, int numero, ArrayList<TreeSet<Integer>> mahdolliset) {
        TreeSet<Integer> paluu = new TreeSet<Integer>();
        for (int i = 0 ; i < 9 ; i++) {
            if (mahdolliset.get(rivi*9+i).contains(numero)) {
                paluu.add(rivi*9+i);
            }
        }
        return paluu;
    }
    private static TreeSet<Integer> haeNumerollaPysty(int sarake, int numero, ArrayList<TreeSet<Integer>> mahdolliset) {
        TreeSet<Integer> paluu = new TreeSet<Integer>();
        for (int i = 0 ; i < 9 ; i++) {
            if (mahdolliset.get(i*9+sarake).contains(numero)) {
                paluu.add(i*9+sarake);
            }
        }
        return paluu;
    }

    private static TreeSet<Integer> haeNumerollatRuutu(int ruutu, int numero, ArrayList<TreeSet<Integer>> mahdolliset) {
        int alkuY = ruutu/3 * 3;
        int alkuX = ruutu%3 * 3;
        TreeSet<Integer> ruudut = new TreeSet<Integer>();
        for (int i = alkuY ; i < alkuY+3 ; i++) {
            for (int j = alkuX ; j < alkuX+3 ; j++) {
                if (mahdolliset.get(i*9 + j).contains(numero)) {
                    ruudut.add(i*9 + j);
                }
            }
        }
        return ruudut;
    }

    private static void jellyfishVaaka(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++) {
            ArrayList<Integer> rivit  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiSarakkeet = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetRivit = new ArrayList<HashSet<Integer>>();
            for (int rivi = 0 ; rivi < 9 ; rivi ++) {
                HashSet<Integer> sarakkeet = new HashSet<>();
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(rivi*9+i).contains(numero)) {
                        sarakkeet.add(i);
                    }
                }
                if (sarakkeet.size() == 2 || sarakkeet.size() == 3 || sarakkeet.size() == 4) {
                    rivit.add(rivi);
                    kaikkiSarakkeet.addAll(sarakkeet);
                    loytyneetRivit.add(sarakkeet);
                }
            }
            if (rivit.size() == 4 && kaikkiSarakkeet.size() == 4) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatRivit = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                blokattavatRivit.removeAll(rivit);
                for ( int y : blokattavatRivit) {
                    for (int x : kaikkiSarakkeet) {
                        if (sudoku[y][x] == 0) {
                            //System.out.println("Vaaka Jellyfish vaaka esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                        }
                    }
                    
                }
            } else if (rivit.size() > 4 ) {
                //System.out.println(numero+ " " +rivit+loytyneetRivit.toString());
                for (int i = 0 ; i < loytyneetRivit.size()-3 ; i++) {
                    for (int j = i+1 ; j < loytyneetRivit.size()-2 ; j++) {
                        for (int k = j+1 ; k < loytyneetRivit.size()-1 ; k++) {
                            for (int l = k+1 ; l < loytyneetRivit.size() ; l++) {
                                HashSet<Integer> apu = new HashSet<Integer>();                        
                                for (int a : loytyneetRivit.get(i)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetRivit.get(j)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetRivit.get(k)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetRivit.get(l)) {
                                    apu.add(a);
                                }

                                if (apu.size() == 4) {
                                   HashSet<Integer> blokattavatRivit = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                                    blokattavatRivit.remove(rivit.get(i));
                                    blokattavatRivit.remove(rivit.get(j));
                                    blokattavatRivit.remove(rivit.get(k));
                                    blokattavatRivit.remove(rivit.get(l));
                                    for ( int y : blokattavatRivit) {
                                        HashSet<Integer> apu2 = new HashSet<Integer>();
                                        apu2.addAll(loytyneetRivit.get(i));
                                        apu2.addAll(loytyneetRivit.get(j));
                                        apu2.addAll(loytyneetRivit.get(k));
                                        apu2.addAll(loytyneetRivit.get(l));
                                        for (int x : apu2) {
                                            if (sudoku[y][x] == 0) {
                                                //System.out.println("Vaaka Jellyfish Piilo esta " + numero +" "+(9*y+x));
                                                estoLista.get(9*y+x).add(numero);
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
    
    private static void jellyfishPysty(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++) {
            ArrayList<Integer> sarakkeet  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiRivit = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetSarakkeet = new ArrayList<HashSet<Integer>>();
            for (int sarake = 0 ; sarake < 9 ; sarake ++) {
                HashSet<Integer> rivit = new HashSet<>();
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(i*9+sarake).contains(numero)) {
                        rivit.add(i);
                    }
                }
                if (rivit.size() == 2 || rivit.size() == 3 || rivit.size() == 4) {
                    sarakkeet.add(sarake);
                    kaikkiRivit.addAll(rivit);
                    loytyneetSarakkeet.add(rivit);
                }
            }
            if (sarakkeet.size() == 4 && kaikkiRivit.size() == 4) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                blokattavatSarakkeet.removeAll(sarakkeet);
                for ( int x : blokattavatSarakkeet) {
                    for (int y : kaikkiRivit) {
                        if (sudoku[y][x] == 0) {
                            //System.out.println("Pysty Jellyfish  pysty esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                        }
                    }
                    
                }
            } else if (sarakkeet.size() > 4 ) {
                //System.out.println(numero+ "P " +sarakkeet+loytyneetSarakkeet.toString());
                for (int i = 0 ; i < loytyneetSarakkeet.size()-3 ; i++) {
                    for (int j = i+1 ; j < loytyneetSarakkeet.size()-2 ; j++) {
                        for (int k = j+1 ; k < loytyneetSarakkeet.size()-1 ; k++) {
                            for (int l = k+1 ; l < loytyneetSarakkeet.size() ; l++) {
                                HashSet<Integer> apu = new HashSet<Integer>();                        
                                for (int a : loytyneetSarakkeet.get(i)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetSarakkeet.get(j)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetSarakkeet.get(k)) {
                                    apu.add(a);
                                }
                                for (int a : loytyneetSarakkeet.get(l)) {
                                    apu.add(a);
                                }
                                if (apu.size() == 4) {
                                   HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                                    blokattavatSarakkeet.remove(sarakkeet.get(i));
                                    blokattavatSarakkeet.remove(sarakkeet.get(j));
                                    blokattavatSarakkeet.remove(sarakkeet.get(k));
                                    blokattavatSarakkeet.remove(sarakkeet.get(l));
                                    for ( int x : blokattavatSarakkeet) {
                                        HashSet<Integer> apu2 = new HashSet<Integer>();
                                        apu2.addAll(loytyneetSarakkeet.get(i));
                                        apu2.addAll(loytyneetSarakkeet.get(j));
                                        apu2.addAll(loytyneetSarakkeet.get(k));
                                        apu2.addAll(loytyneetSarakkeet.get(l));
                                        for (int y : apu2) {
                                            if (sudoku[y][x] == 0) {
                                                //System.out.println("Pysty Jellyfish Piilo esta " + numero +" "+(9*y+x));
                                                estoLista.get(9*y+x).add(numero);
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
    
    private static void swordfishVaaka(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++) {
            ArrayList<Integer> rivit  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiSarakkeet = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetRivit = new ArrayList<HashSet<Integer>>();
            for (int rivi = 0 ; rivi < 9 ; rivi ++) {
                HashSet<Integer> sarakkeet = new HashSet<>();
                boolean seuraava = false;
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(rivi*9+i).contains(numero)) {
                        sarakkeet.add(i);
                        if (mahdolliset.get(rivi*9+i).size() > 3) {
                            seuraava = true;
                        }
                    }
                }
                if (seuraava) continue;
                if (sarakkeet.size() == 2 || sarakkeet.size() == 3) {
                    rivit.add(rivi);
                    kaikkiSarakkeet.addAll(sarakkeet);
                    loytyneetRivit.add(sarakkeet);
                }
            }
            if (rivit.size() == 3 && kaikkiSarakkeet.size() == 3) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatRivit = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                blokattavatRivit.removeAll(rivit);
                for ( int y : blokattavatRivit) {
                    for (int x : kaikkiSarakkeet) {
                        if (sudoku[y][x] == 0) {
                            //System.out.println("Vaaka Swordfish esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                        }
                    }
                    
                }
            } else if (rivit.size() > 3 ) {
                //System.out.println(numero+ " " +rivit+loytyneetRivit.toString());
                for (int i = 0 ; i < loytyneetRivit.size()-2 ; i++) {
                    for (int j = i+1 ; j < loytyneetRivit.size()-1 ; j++) {
                        for (int k = j+1 ; k < loytyneetRivit.size() ; k++) {
                            HashSet<Integer> apu = new HashSet<Integer>();                        
                            for (int a : loytyneetRivit.get(i)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetRivit.get(j)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetRivit.get(k)) {
                                apu.add(a);
                            }
                            
                            if (apu.size() == 3) {
                               HashSet<Integer> blokattavatRivit = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                                blokattavatRivit.remove(rivit.get(i));
                                blokattavatRivit.remove(rivit.get(j));
                                blokattavatRivit.remove(rivit.get(k));
                                for ( int y : blokattavatRivit) {
                                    HashSet<Integer> apu2 = new HashSet<Integer>();
                                    apu2.addAll(loytyneetRivit.get(i));
                                    apu2.addAll(loytyneetRivit.get(j));
                                    apu2.addAll(loytyneetRivit.get(k));
                                    for (int x : apu2) {
                                        if (sudoku[y][x] == 0) {
                                            //System.out.println("Vaaka Swordfish Piilo esta " + numero +" "+(9*y+x));
                                            estoLista.get(9*y+x).add(numero);
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

    private static void swordfishPysty(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++) {
            ArrayList<Integer> sarakkeet  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiRivit = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetSarakkeet = new ArrayList<HashSet<Integer>>();
            for (int sarake = 0 ; sarake < 9 ; sarake ++) {
                HashSet<Integer> rivit = new HashSet<>();
                boolean seuraava = false;
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(i*9+sarake).contains(numero)) {
                        rivit.add(i);
                        if (mahdolliset.get(i*9+sarake).size() > 3) {
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
                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                blokattavatSarakkeet.removeAll(sarakkeet);
                for ( int x : blokattavatSarakkeet) {
                    for (int y : kaikkiRivit) {
                        if (sudoku[y][x] == 0) {
                            //System.out.println("Pysty Swordfish esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                        }
                    }
                    
                }
            } else if (sarakkeet.size() > 3 ) {
                //System.out.println(numero+ "P " +sarakkeet+loytyneetSarakkeet.toString());
                for (int i = 0 ; i < loytyneetSarakkeet.size()-2 ; i++) {
                    for (int j = i+1 ; j < loytyneetSarakkeet.size()-1 ; j++) {
                        for (int k = j+1 ; k < loytyneetSarakkeet.size() ; k++) {
                            HashSet<Integer> apu = new HashSet<Integer>();                        
                            for (int a : loytyneetSarakkeet.get(i)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetSarakkeet.get(j)) {
                                apu.add(a);
                            }
                            for (int a : loytyneetSarakkeet.get(k)) {
                                apu.add(a);
                            }
                            if (apu.size() == 3) {
                               HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                                blokattavatSarakkeet.remove(sarakkeet.get(i));
                                blokattavatSarakkeet.remove(sarakkeet.get(j));
                                blokattavatSarakkeet.remove(sarakkeet.get(k));
                                for ( int x : blokattavatSarakkeet) {
                                    HashSet<Integer> apu2 = new HashSet<Integer>();
                                    apu2.addAll(loytyneetSarakkeet.get(i));
                                    apu2.addAll(loytyneetSarakkeet.get(j));
                                    apu2.addAll(loytyneetSarakkeet.get(k));
                                    for (int y : apu2) {
                                        if (sudoku[y][x] == 0) {
                                            //System.out.println("Pysty swordfish Piilo esta " + numero +" "+(9*y+x));
                                            estoLista.get(9*y+x).add(numero);
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

    private static void xWingVaaka(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++) {
            ArrayList<Integer> rivit  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiSarakkeet = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetRivit = new ArrayList<HashSet<Integer>>();
            for (int rivi = 0 ; rivi < 9 ; rivi ++) {
                HashSet<Integer> sarakkeet = new HashSet<>();
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(rivi*9+i).contains(numero)) {
                        sarakkeet.add(i);
                    }
                }
                //if (seuraava) continue;
                if (sarakkeet.size() == 2 ) {
                    rivit.add(rivi);
                    kaikkiSarakkeet.addAll(sarakkeet);
                    loytyneetRivit.add(sarakkeet);
                } 
            }
            if (rivit.size() == 2 && kaikkiSarakkeet.size() == 2 ) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatRivit = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                blokattavatRivit.removeAll(rivit);
                for ( int y : blokattavatRivit) {
                    for (int x : kaikkiSarakkeet) {
                        if (sudoku[y][x] == 0) {
                            //System.out.println("Vaaka xwing esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                        }
                    }
                    
                }
            } else if (rivit.size() > 2 ) {
                //System.out.println(numero+ " " +rivit+loytyneetRivit.toString());
                for (int i = 0 ; i < loytyneetRivit.size()-1 ; i++) {
                    for (int j = i+1 ; j < loytyneetRivit.size() ; j++) {
                        HashSet<Integer> apu = new HashSet<Integer>();                        
                        for (int a : loytyneetRivit.get(i)) {
                            apu.add(a);
                        }
                        apu.removeAll(loytyneetRivit.get(j));
                        if (apu.isEmpty()) {
                           HashSet<Integer> blokattavatRivit = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                            blokattavatRivit.remove(rivit.get(i));
                            blokattavatRivit.remove(rivit.get(j));
                            for ( int y : blokattavatRivit) {
                                for (int x : loytyneetRivit.get(i)) {
                                    if (sudoku[y][x] == 0) {
                                        //System.out.println("Vaaka xwing Piilo esta " + numero +" "+(9*y+x));
                                        estoLista.get(9*y+x).add(numero);
                                    }
                                }
                            }
                        }
                    }
                }
            } 
        }
    }

    private static void xWingPysty(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++) {
            ArrayList<Integer> sarakkeet  = new ArrayList<Integer>();
            HashSet<Integer> kaikkiRivit = new HashSet<>();
            ArrayList<HashSet<Integer>> loytyneetSarakkeet = new ArrayList<HashSet<Integer>>();
            for (int sarake = 0 ; sarake < 9 ; sarake ++) {
                HashSet<Integer> rivit = new HashSet<>();
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(i*9+sarake).contains(numero)) {
                        rivit.add(i);
                    }
                }
                if (rivit.size() == 2 ) {
                    sarakkeet.add(sarake);
                    kaikkiRivit.addAll(rivit);
                    loytyneetSarakkeet.add(rivit);
                }
            }
            if (sarakkeet.size() == 2 && kaikkiRivit.size() == 2 ) {
                //tulostaVaihtoehdot(sudoku);
                HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                blokattavatSarakkeet.removeAll(sarakkeet);
                for ( int x : blokattavatSarakkeet) {
                    for (int y : kaikkiRivit) {
                        if (sudoku[y][x] == 0) {
                            //System.out.println("Pysty xwing esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                        }
                    }
                    
                }
            } else if (sarakkeet.size() > 2 ) {
                //System.out.println(numero+ "P " +sarakkeet+loytyneetSarakkeet.toString());
                for (int i = 0 ; i < loytyneetSarakkeet.size()-1 ; i++) {
                    for (int j = i+1 ; j < loytyneetSarakkeet.size() ; j++) {
                        HashSet<Integer> apu = new HashSet<Integer>();                        
                        for (int a : loytyneetSarakkeet.get(i)) {
                            apu.add(a);
                        }
                        apu.removeAll(loytyneetSarakkeet.get(j));
                        if (apu.isEmpty()) {
                           HashSet<Integer> blokattavatSarakkeet = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                            blokattavatSarakkeet.remove(sarakkeet.get(i));
                            blokattavatSarakkeet.remove(sarakkeet.get(j));
                            for ( int x : blokattavatSarakkeet) {
                                for (int y : loytyneetSarakkeet.get(i)) {
                                    if (sudoku[y][x] == 0) {
                                        //System.out.println("Pysty xwing Piilo esta " + numero +" "+(9*y+x));
                                        estoLista.get(9*y+x).add(numero);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void bug(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        //tulostaVaihtoehdot(sudoku);
        // solussa 3 vaihtoehtoa ja ymparilla olevissa 2
        for (int i = 0 ; i < 81 ; i++) {
            boolean onBug = true;
            HashSet<Integer> riviArvot = new HashSet<>();
            HashSet<Integer> sarakeArvot = new HashSet<>();
            HashSet<Integer> ruutuArvot = new HashSet<>();
            if (mahdolliset.get(i).size() == 3) {
                for (int x = 0 ; x < 9 ; x++ ) {
                    riviArvot.addAll(mahdolliset.get(i/9*9 + x));
                    if (x != i%9) {
                        if (mahdolliset.get(i/9*9 + x).size() >2 ) { 
                            onBug = false;
                        }
                    }
                }
                if (onBug) {
                    for (int y = 0 ; y < 9 ; y++ ) {
                        sarakeArvot.addAll(mahdolliset.get(9*y + i%9 ));
                        if (y != i/9) {
                            if (mahdolliset.get(9*y + i%9 ).size()>2 ) { 
                                onBug = false;
                            }
                        }
                    }
                }
                if (onBug) {
                    int ruutuY = i/9/3 *3;
                    int ruutuX = i%9/3 *3;
                    for (int y = ruutuY ; y < ruutuY+3 ; y++ ) {
                        for (int x = ruutuX ; x < ruutuX+3 ; x++ ) {
                            ruutuArvot.addAll(mahdolliset.get(9*y + x ));
                            if (y*9+x != i) {
                                if (mahdolliset.get(9*y + x ).size()>2 ) { 
                                    onBug = false;
                                }
                            }
                        }
                    }
                }
                if (onBug) {
                    //System.out.println("**********BUG*****" +i +mahdolliset.get(i));
                    //tulostaVaihtoehdot(sudoku);
                    //TreeSet<Integer> setti = mahdolliset.get(i);
                    int varma = 0;
                    boolean BUGtoteutuu = true;
                    //rivi
                    for (int luku : riviArvot) {
                        int count = 0;
                        for (int x = 0 ; x < 9 ; x++ ) {
                            if (mahdolliset.get(i/9*9 + x).contains(luku)) {
                                count++;
                            }
                        }
                        if (count < 2 || count > 3) BUGtoteutuu = false;
                        if (count == 3) varma = luku;
                        //System.out.println("Bug rivi " +luku +" "+count);
                        
                    }
                    //sarake
                    for (int luku : sarakeArvot) {
                        int count = 0;
                        for (int y = 0 ; y < 9 ; y++ ) {
                            if (mahdolliset.get(y*9 + i%9).contains(luku)) {
                                count++;
                            }
                        }
                        if (count < 2 || count > 3) BUGtoteutuu = false;
                        //System.out.println("Bug sarake " +luku +" "+count);
                    }

                    //ruutu
                    for (int luku : ruutuArvot) {
                        int count = 0;
                        int yStart = i/9/3*3;
                        int xStart = i%9/3*3;
                        for (int y = yStart ; y < yStart+3 ; y++) {
                            for (int x = xStart ; x < xStart+3 ; x++ ) {
                                if (mahdolliset.get(y*9 + x).contains(luku)) {
                                    count++;
                                }
                            }
                        }
                        if (count < 2 || count > 3) BUGtoteutuu = false;
                        //System.out.println("Bug ruutu " +luku +" "+count);
                    }

                    for (int n = 0 ; n < 81 ; n++) {
                        if ( n!=i && mahdolliset.get(n).size() > 2) BUGtoteutuu = false;
                    }
                    
                    if (BUGtoteutuu) {
                        //System.out.println("BUG " +i +" "+varma);
                        sudoku[i/9][i%9] = varma;
                        return;
                    }
                
                }    
                
            }
        }
    }

    private static void simpleColouring(int[][] sudoku) {
        // "twice in the unit""
        //tulostaVaihtoehdot(sudoku);
        for (int n = 1 ; n <=9 ; n++ ) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
            for (int y = 0 ; y < 9 ; y++) {
                for (int x = 0 ; x < 9; x++) {
                    TreeSet<Integer> ts = new TreeSet<Integer>();
                    ts = vaihtoehdot(y, x, sudoku);
                    mahdolliset.add(ts);
                }
            }
            TreeSet<Integer> ts = new TreeSet<Integer>();
            HashSet<Integer> ketjuSolut = new HashSet<Integer>();
            for (int x = 0 ; x < 9 ; x++){
                
                ts = haeNumerollatRivi(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);
                
                ts =haeNumerollaPysty(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);
                
                ts =haeNumerollatRuutu(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);
            } 

            ArrayList<Integer> samatSolut = new ArrayList<Integer>();
            for (int solu : ketjuSolut) {
                samatSolut.add(solu);
            }
            int s = samatSolut.size();
            if (s > 3) {
                Vertex [] solmut = new Vertex[s+1];
                for (int a = 1 ; a <= s ; a ++) {
                    solmut[a] = new Vertex(samatSolut.get(a-1));
                    solmut[a].adjacencies = new ArrayList<Edge>();
                }

                for ( int b = 0; b < s-1; b++ ) {
                    for ( int c = b+1; c < s; c++ ) {
                        if (samaRivi(samatSolut.get(b), samatSolut.get(c)) && haeNumerollatRivi(samatSolut.get(b)/9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaSarake(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaPysty(samatSolut.get(b)%9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaRuutu(samatSolut.get(b), samatSolut.get(c)) && haeNumerollatRuutu(samatSolut.get(b)/9/3*3 + samatSolut.get(b)%9/3 , n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                    }
                }

                for (int a = 1; a <= s ; a++) {
                    for (int d = 1 ; d <= s ; d++) {
                        solmut[d].minDistance = Integer.MAX_VALUE;
                     }
                    computePaths(solmut[a]); // run Dijkstra
                    //tulostaVaihtoehdot(sudoku);
                    for (int b = a+1; b < s ; b++) {
                        if (solmut[b].minDistance < Integer.MAX_VALUE && solmut[b].minDistance > 2 && solmut[b].minDistance % 2 == 0) { 
                            if (yhteys(solmut[a].id,solmut[b].id)) { // twice in the unit rule
                                //for (int x = 1 ; x <= s; x++) {System.out.print(" "+solmut[x].id + "-"+ solmut[x].minDistance); }
                                //System.out.println("");
                                for (int x = 1 ; x <= s ; x ++) {
                                    if (solmut[x].minDistance < Integer.MAX_VALUE && solmut[x].minDistance >= 2 && solmut[x].minDistance % 2 == 0) {
                                        //System.out.println(solmut[b].minDistance+" Simple colouring esto " +solmut[x].id +" "+n);
                                        estoLista.get(solmut[x].id).add(n);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void simpleColouring2(int[][] sudoku) {
        // Two colours 'elsewhere'
        //tulostaVaihtoehdot(sudoku);
            
        for (int n = 1 ; n <=9 ; n++ ) {
            ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
            for (int y = 0 ; y < 9 ; y++) {
                for (int x = 0 ; x < 9; x++) {
                    TreeSet<Integer> ts = new TreeSet<Integer>();
                    ts = vaihtoehdot(y, x, sudoku);
                    mahdolliset.add(ts);
                }
            }
            TreeSet<Integer> ts = new TreeSet<Integer>();
            HashSet<Integer> ketjuSolut = new HashSet<Integer>();
            for (int x = 0 ; x < 9 ; x++){

                ts = haeNumerollatRivi(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);

                ts =haeNumerollaPysty(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);

                ts =haeNumerollatRuutu(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);
            } 

            ArrayList<Integer> samatSolut = new ArrayList<Integer>();
            for (int solu : ketjuSolut) {
                samatSolut.add(solu);
            }
            int s = samatSolut.size();
            if (s > 3) {
                Vertex [] solmut = new Vertex[s+1];
                for (int a = 1 ; a <= s ; a ++) {
                    solmut[a] = new Vertex(samatSolut.get(a-1));
                    solmut[a].adjacencies = new ArrayList<Edge>();
                }

                for ( int b = 0; b < s-1; b++ ) {
                    for ( int c = b+1; c < s; c++ ) {
                        if (samaRivi(samatSolut.get(b), samatSolut.get(c)) && haeNumerollatRivi(samatSolut.get(b)/9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaSarake(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaPysty(samatSolut.get(b)%9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaRuutu(samatSolut.get(b), samatSolut.get(c)) && haeNumerollatRuutu(samatSolut.get(b)/9/3*3 + samatSolut.get(b)%9/3 , n, mahdolliset).size() == 2) {
                            
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                    }
                }

                for (int a = 1; a <= s ; a++) {
                    for (int d = 1 ; d <= s ; d++) {
                        solmut[d].minDistance = Integer.MAX_VALUE;
                    }
                    computePaths(solmut[a]); // run Dijkstra

                    for (int b = a+1; b < s ; b++) {
                        if (solmut[b].minDistance < Integer.MAX_VALUE && solmut[b].minDistance > 2 && solmut[b].minDistance % 2 != 0) {
                            //System.out.println(solmut[b].minDistance+" ETApari "+ samatSolut.get(a-1) +" "+samatSolut.get(b-1) +" "+ mahdolliset.get(i));
                            //System.out.println("SimpleC 2 estä " +((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9) +" " +(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9)+" "+n);
                            if (!yhteys((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9 , samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9)) {  
                                estoLista.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).add(n);
                                estoLista.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).add(n);
                            } //else System.out.println("Sama alue ei estoa");
                        }
                    }
                }
            }
        }
    }    
    
    private static void etaPari(int[][] sudoku) {
        ArrayList<Integer> mahdolliset = new ArrayList<Integer>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                if (ts.size() == 2) {
                    //System.out.println((y*9+x)+" arvot "+ts.first()+" "+ts.last());
                    mahdolliset.add(ts.first()*10+ts.last());
                } else {
                    mahdolliset.add(0);
                }
            }
        }
        
        for (int i = 0 ; i < 81 ; i++) {
            ArrayList<Integer> samatSolut = new ArrayList<Integer>();
            if (mahdolliset.get(i) != 0) {
                samatSolut.add(i);
                for (int j = 0 ; j < 81 ; j++) {
                    if (mahdolliset.get(i) == mahdolliset.get(j) && i!=j) {
                        samatSolut.add(j);        
                    }
                }
                int n = samatSolut.size();
                if (n > 3) {
                    Vertex [] solmut = new Vertex[n+1];
                    for (int a = 1 ; a <= n ; a ++) {
                        solmut[a] = new Vertex(a);
                        solmut[a].adjacencies = new ArrayList<Edge>();
                    }

                    for ( int b = 0; b < n-1; b++ ) {
                        for ( int c = b+1; c < n; c++ ) {
                            if (yhteys(samatSolut.get(b), samatSolut.get(c))) {
                                //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                                solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                                solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                            }
                        }
                    }

                    for (int a = 1; a <= n ; a++) {
                        for (int d = 1 ; d <= n ; d++) {
                            solmut[d].minDistance = Integer.MAX_VALUE;
                         }
                        computePaths(solmut[a]); // run Dijkstra
                        for (int b = a+1; b < n ; b++) {
                            if (solmut[b].minDistance < Integer.MAX_VALUE && solmut[b].minDistance > 2 && solmut[b].minDistance % 2 != 0) {
                                //System.out.println(solmut[b].minDistance+" ETApari "+ samatSolut.get(a-1) +" "+samatSolut.get(b-1) +" "+ mahdolliset.get(i));
                                //System.out.println("Etäpari estä " +((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9) +" " +(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9));
                                
                                estoLista.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).add(mahdolliset.get(i)%10);
                                estoLista.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).add(mahdolliset.get(i)/10);
                                estoLista.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).add(mahdolliset.get(i)%10);
                                estoLista.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).add(mahdolliset.get(i)/10);
                            }
                        }
                    }
                }
            }
        }
    }    
    
    
    private static void xywing(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                if (ts.size() == 2) {
                   mahdolliset.add(ts);
                } else {
                    mahdolliset.add(new TreeSet<Integer>());
                }
            }
        } 

        for (int a = 0 ; a < 79 ; a++) {

            if (mahdolliset.get(a).isEmpty()) continue;
            for (int b = a+1 ; b < 80 ; b++) {
                if (mahdolliset.get(b).isEmpty()) continue;
                for (int c = b+1 ; c < 81 ; c++) {
                    if (mahdolliset.get(c).isEmpty()) continue;
                    TreeSet<Integer> summa = new TreeSet<Integer>();
                    TreeSet<Integer> haku = new TreeSet<Integer>();
                    TreeSet<Integer> aa = new TreeSet<Integer>();
                    haku = mahdolliset.get(a);
                    aa.addAll(haku);
                    TreeSet<Integer> bb = new TreeSet<Integer>();
                    haku = mahdolliset.get(b);
                    bb.addAll(haku);
                    TreeSet<Integer> cc = new TreeSet<Integer>();
                    haku = mahdolliset.get(c);
                    cc.addAll(haku);
                    summa.addAll(aa);
                    summa.addAll(bb);
                    summa.addAll(cc);
                    if (summa.size()!=3) continue;
                    if (aa.first()==bb.first() && aa.last()==bb.last()) continue;
                    if (aa.first()==cc.first() && aa.last()==cc.last()) continue;
                    if (cc.first()==bb.first() && cc.last()==bb.last()) continue;
                    if(samaRivi(a, b) && samaRivi(a, c)) continue;
                    if(samaSarake(a, b) && samaSarake(a, c)) continue;
                    if(samaRuutu(a, b) && samaRuutu(a, c)) continue;
                    //tulostaVaihtoehdot(sudoku);
                    //System.out.println("Mahollinen xywing1 "+aa.toString()+" "+bb.toString()+" "+cc.toString());
                    //System.out.println(a+" " +b+" " +c);
                    if ((yhteys(a,b)) && (yhteys(a,c))) {

                        HashSet<Integer> numerot = new HashSet<>();
                        numerot.addAll(bb);
                        numerot.removeAll(aa);
                        int yhteinenNumero = (int)numerot.toArray()[0];
                                //System.out.println(" xwingA "+yhteinenNumero);
                                //System.out.println(a+" "+b+" "+c );
                                //System.out.println(aa.toString()+bb.toString()+cc.toString());
                                //System.out.println(" Esta " + (b/9*9+c%9) +" "+(c/9*9+b%9) +" "+ yhteinenNumero);
                                estoLista.get(b/9*9+c%9).add(yhteinenNumero);
                                estoLista.get(c/9*9+b%9).add(yhteinenNumero);
                        if (samaRuutu(a, b)) {
                            //System.out.println(" xywing ruutu");
                            if (samaRivi(a, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (b/9*9 +c%9/3*3+i) +" "+ yhteinenNumero);
                                    estoLista.get(b/9*9 + c%9/3*3+i).add(yhteinenNumero);
                                }
                            } else if (samaSarake(a, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta a" + ((c/9/3*3+i)*9 +b%9) +" "+ yhteinenNumero);
                                    estoLista.get((c/9/3*3+i)*9 +b%9).add(yhteinenNumero);
                                }
                            }
                        }
                        if (samaRuutu(a, c)) {
                            //System.out.println(" xywing ruutu");
                            if (samaRivi(a, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (c/9*9 +b%9/3*3+i) +" "+ yhteinenNumero);
                                    estoLista.get(c/9*9 +b%9/3*3+i).add(yhteinenNumero);
                                }
                            } else if (samaSarake(a, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta b" + ((b/9/3*3+i)*9 +c%9) +" "+ yhteinenNumero);
                                    estoLista.get((b/9/3*3+i)*9 +c%9).add(yhteinenNumero);
                                }
                            }
                        }

                    }
                    
                    if ((yhteys(b,a)) && (yhteys(b,c))) {
                        HashSet<Integer> numerot = new HashSet<>();
                        numerot.addAll(aa);
                        numerot.removeAll(bb);
                        int yhteinenNumero = (int)numerot.toArray()[0];
                                //System.out.println(" xwingB "+yhteinenNumero);
                                //System.out.println(a+" "+b+" "+c );
                                //System.out.println(aa.toString()+bb.toString()+cc.toString());
                                //System.out.println(" Esta " + (c/9*9+a%9) +" "+(a/9*9+c%9) +" "+ yhteinenNumero);
                                estoLista.get(c/9*9+a%9).add(yhteinenNumero);
                                estoLista.get(a/9*9+c%9).add(yhteinenNumero);
                                                    
                        if (samaRuutu(b, a)) {
                            if (samaRivi(b, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (a/9*9 +c%9/3*3+i) +" "+ yhteinenNumero);
                                    estoLista.get(a/9*9 +c%9/3*3+i).add(yhteinenNumero);
                                }
                            } else if (samaSarake(b, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta c" + ((c/9/3*3+i)*9 +a%9) +" "+ yhteinenNumero);
                                    estoLista.get((c/9/3*3+i)*9 +a%9).add(yhteinenNumero);
                                }
                            }
                        }
                        if (samaRuutu(b, c)) {
                            if (samaRivi(b, a)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (c/9*9 + a%9/3*3+i) +" "+ yhteinenNumero);
                                    estoLista.get(c/9*9 + a%9/3*3+i).add(yhteinenNumero);
                                }
                            } else if (samaSarake(b, a)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta d" + ((a/9/3*3+i)*9 + c%9) +" "+ yhteinenNumero);
                                    estoLista.get((a/9/3*3+i)*9 + c%9).add(yhteinenNumero);
                                }
                            }
                        }
                    }
                                                    

                    if ((yhteys(c,a)) && (yhteys(c,b))) {
                        HashSet<Integer> numerot = new HashSet<>();
                        numerot.addAll(aa);
                        numerot.removeAll(cc);
                        int yhteinenNumero = (int)numerot.toArray()[0];
                                //System.out.println(" xwingC "+yhteinenNumero);
                                //System.out.println(a+" "+b+" "+c );
                                //System.out.println(aa.toString()+bb.toString()+cc.toString());
                                //System.out.println(" Esta " + (b/9*9+a%9) +" "+(a/9*9+b%9) +" "+ yhteinenNumero);
                                estoLista.get(b/9*9+a%9).add(yhteinenNumero);
                                estoLista.get(a/9*9+b%9).add(yhteinenNumero);
                        if (samaRuutu(c, a)) {
                            if (samaRivi(c, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (a/9*9 + b%9/3*3+i) +" "+ yhteinenNumero);
                                    estoLista.get(a/9*9 + b%9/3*3+i).add(yhteinenNumero);
                                }
                            } else if (samaSarake(c, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta e" + ((b/9/3*3+i)*9 + a%9) +" "+ yhteinenNumero);
                                    estoLista.get((b/9/3*3+i)*9 + a%9).add(yhteinenNumero);
                                }
                            }
                        }
                        if (samaRuutu(c, b)) {
                            if (samaRivi(c, a)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (c/9*9 + c%9/3*3+i) +" "+ yhteinenNumero);
                                    estoLista.get(b/9*9 + a%9/3*3+i).add(yhteinenNumero);
                                }
                            } else if (samaSarake(c, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta f" + ((a/9/3*3+i)*9 + c%9) +" "+ yhteinenNumero);
                                    estoLista.get((a/9/3*3+i)*9 + b%9).add(yhteinenNumero);
                                }
                            }
                        }
                    }
                }
            }
        }
    
    } 
    private static void piiloMonikotRivi(int montako,int[][] sudoku) {
        //System.out.println("Piilorivi " +montako);
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int rivi = 0; rivi < 9; rivi++) {
            HashSet<Integer> rivinarvot = new HashSet<Integer>();
            for (int i = 0 ; i < 9 ; i++) {
                rivinarvot.addAll(mahdolliset.get(rivi*9+i));
            }
            
            if (rivinarvot.size() >= montako) {
                Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                int lista[] = new int[rivinarvot.size()];
                for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                    lista[i] = kokolista[i];
                   } 
                int vaihtoehto[] = new int[montako];
                hakuLista = new ArrayList<TreeSet<Integer>>();
                combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                //System.out.println(""+hakuLista.toString());
                for (TreeSet<Integer> ts : hakuLista) {
                    HashSet<Integer> poisto = new HashSet<Integer>();
                    poisto.addAll(rivinarvot);
                    poisto.removeAll(ts);
                    //System.out.println(rivi+ " "+rivinarvot.toString());
                    int loytynytpiilo = 0;
                    ArrayList<Integer> loytyneetpiilopaikat = new ArrayList<Integer>();
                    for (int sarake = 0 ; sarake < 9 ;sarake++) {
                        HashSet<Integer> apuPiilo = new HashSet<Integer>();
                        //TreeSet<Integer> apuNaku = new TreeSet<Integer>();
                        TreeSet<Integer> temp = mahdolliset.get(rivi*9+sarake);
                        //apu sisaltaa jonkun ts:sta

                        if (!temp.isEmpty()) {
                            for (int z : temp) apuPiilo.add(z);
                            //System.out.println("XX " +apu.toString()+ts.toString());
                            apuPiilo.removeAll(poisto);
                             if (!apuPiilo.isEmpty()) {
                                 loytynytpiilo++;
                                 loytyneetpiilopaikat.add(sarake);
                             }
                        }
                    }
                    if (loytynytpiilo == montako) {
                        HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
                        estot.removeAll(ts);
                        //System.out.println(montako+" Piilo "+rivi+" "+estot.toString()+ts.toString());
                        for (int r : loytyneetpiilopaikat) {
                            estoLista.get(9*rivi+r). addAll(estot);    
                        }
                    }
                }
            }
        }
    }
        
    private static void nakuMonikotRivi(int[][] sudoku) {
        //System.out.println("Nakurivi " +montako);
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < 5 ; montako++) {
            for (int rivi = 0; rivi < 9; rivi++) {
                HashSet<Integer> rivinarvot = new HashSet<Integer>();
                for (int i = 0 ; i < 9 ; i++) {
                    rivinarvot.addAll(mahdolliset.get(rivi*9+i));
                }
                if (rivinarvot.size() >= montako) {
                    Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                    int lista[] = new int[rivinarvot.size()];
                    for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<TreeSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (TreeSet<Integer> ts : hakuLista) {
                        HashSet<Integer> poisto = new HashSet<Integer>();
                        poisto.addAll(rivinarvot);
                        poisto.removeAll(ts);
                        int loytynyt = 0;
                        ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                        for (int sarake = 0 ; sarake < 9 ;sarake++) {
                            HashSet<Integer> apu = new HashSet<Integer>();
                            apu.addAll(mahdolliset.get(rivi*9+sarake));
                            //apu sisaltaa jonkun ts:sta

                            if (!apu.isEmpty()) {
                                //System.out.println("XX " +apu.toString()+ts.toString());
                                apu.removeAll(ts);
                                 if (apu.isEmpty()) {
                                     loytynyt++;
                                     loytyneetpaikat.add(sarake);
                                 }
                            }
                        }
                        if (loytynyt == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                            estot.removeAll(loytyneetpaikat);
                            //System.out.println(loytynyt+" "+rivi+" "+estot.toString()+ts.toString());
                            for (int r : estot) {
                                if (sudoku[rivi][r] == 0) {
                                    for (int luku : ts) {
                                        //System.out.println(montako+" EstaR " +(9*rivi+r)+" "+(luku));
                                        estoLista.get(9*rivi+r).add(luku);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void piiloMonikotSarake(int montako,int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int sarake = 0; sarake < 9; sarake++) {
            HashSet<Integer> rivinarvot = new HashSet<Integer>();
            for (int i = 0 ; i < 9 ; i++) {
                rivinarvot.addAll(mahdolliset.get(i*9 + sarake));
            }
            if (rivinarvot.size() >= montako) {
                //System.out.println("Nelikko " +rivinarvot.toString());
                Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                int lista[] = new int[rivinarvot.size()];
                for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                    lista[i] = kokolista[i];
                } 
                int vaihtoehto[] = new int[montako];
                hakuLista = new ArrayList<TreeSet<Integer>>();
                combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                //System.out.println(""+hakuLista.toString());
                for (TreeSet<Integer> ts : hakuLista) {
                    HashSet<Integer> poisto = new HashSet<Integer>();
                    poisto.addAll(rivinarvot);
                    poisto.removeAll(ts);
                    int loytynyt = 0;

                    ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                    for (int rivi = 0 ; rivi < 9 ;rivi++) {
                        HashSet<Integer> apu = new HashSet<Integer>();
                        TreeSet<Integer> temp = mahdolliset.get(rivi*9+sarake);
                        //apu.addAll(mahdolliset.get(rivi*9+sarake));
                        if (!temp.isEmpty()) {
                            for (int z : temp) apu.add(z);
                            apu.removeAll(poisto);
                             if (!apu.isEmpty()) {
                                 loytynyt++;
                                 loytyneetpaikat.add(rivi);
                             }
                        }
                    }
                    if (loytynyt == montako) {
                        HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
                        estot.removeAll(ts); 
                        for (int r : loytyneetpaikat) {
                            estoLista.get(9*r + sarake). addAll(estot);
                        }
                    }
                }
            }
        }
    }
    
    private static void nakuMonikotSarake(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < 5 ; montako++) {
            for (int sarake = 0; sarake < 9; sarake++) {
                HashSet<Integer> rivinarvot = new HashSet<Integer>();
                for (int i = 0 ; i < 9 ; i++) {
                    rivinarvot.addAll(mahdolliset.get(i*9 + sarake));
                }
                if (rivinarvot.size() >= montako) {
                    //System.out.println("Nelikko " +rivinarvot.toString());
                    Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                    int lista[] = new int[rivinarvot.size()];
                    for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<TreeSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (TreeSet<Integer> ts : hakuLista) {
                        HashSet<Integer> poisto = new HashSet<Integer>();
                        poisto.addAll(rivinarvot);
                        poisto.removeAll(ts);
                        int loytynyt = 0;
                        ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                        for (int rivi = 0 ; rivi < 9 ;rivi++) {
                            HashSet<Integer> apu = new HashSet<Integer>();
                            apu.addAll(mahdolliset.get(rivi*9+sarake));
                            if (!apu.isEmpty()) {
                                apu.removeAll(ts);
                                 if (apu.isEmpty()) {
                                     loytynyt++;
                                     loytyneetpaikat.add(rivi);
                                 }
                            }
                        }
                        if (loytynyt == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                            estot.removeAll(loytyneetpaikat);
                            for (int r : estot) {
                                if (sudoku[r][sarake] == 0) {
                                    for (int luku : ts) {
                                        //System.out.println(montako+" EstaS " +(9*r+sarake)+" "+(luku));
                                        estoLista.get(9*r+sarake).add(luku);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }    

    private static void piiloMonikotRuutu(int montako,int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for ( int rivi = 0 ;rivi < 9 ; rivi=rivi+3) {
            for ( int sarake = 0 ;sarake < 9 ; sarake=sarake+3) {
                int vaaka = (rivi/3)*3;
                int pysty = (sarake/3)*3;

                HashSet<Integer> rivinarvot = new HashSet<Integer>();
                for (int y = vaaka ; y < vaaka+3; y++) {
                    for (int x = pysty ; x < pysty+3; x++) {
                        //rivinarvot.addAll(vaihtoehdot(y, x, sudoku));
                        rivinarvot.addAll(mahdolliset.get(9*vaaka+pysty));
                    }
                }

                if (rivinarvot.size() >= montako) {
                    //System.out.println("Mahdolliset " +mahdolliset.toString());
                    Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                    int lista[] = new int[rivinarvot.size()];
                    for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<TreeSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (TreeSet<Integer> ts : hakuLista) {
                        HashSet<Integer> poisto = new HashSet<Integer>();
                        poisto.addAll(rivinarvot);
                        poisto.removeAll(ts);
                        int loytynyt = 0;
                        ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                        for (int y = 0 ; y < 3 ;y++) {
                            for (int x = 0 ; x < 3 ;x++) {
                        
                            HashSet<Integer> apu = new HashSet<Integer>();
                            TreeSet<Integer> temp = mahdolliset.get((rivi+y)*9+sarake+x);
                            
                            if (!temp.isEmpty()) {
                                for (int z : temp) apu.add(z);
                                    apu.removeAll(poisto);
                                    if (!apu.isEmpty()) {
                                        loytynyt++;
                                        loytyneetpaikat.add(y*3+x);
                                        //System.out.println("Apuva"+temp+apu+ts+poisto+rivinarvot);
                                    }
                                }
                            }
                        }
                        //System.out.println(loytynyt+"/"+montako);
                        if (loytynyt == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
                            estot.removeAll(ts);
                            //tulostaVaihtoehdot(sudoku);
                            for (int r : loytyneetpaikat) {
                                estoLista.get(9*(rivi+r/3)+sarake+r%3).addAll(estot);
                            }
                        }
                    }
                }
            }
        }
    }       
   private static void nakuMonikotRuutu(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < 5 ; montako++) {
            for ( int rivi = 0 ;rivi < 9 ; rivi=rivi+3) {
                for ( int sarake = 0 ;sarake < 9 ; sarake=sarake+3) {
                    int vaaka = (rivi/3)*3;
                    int pysty = (sarake/3)*3;

                    HashSet<Integer> rivinarvot = new HashSet<Integer>();
                    for (int y = vaaka ; y < vaaka+3; y++) {
                        for (int x = pysty ; x < pysty+3; x++) {
                            rivinarvot.addAll(vaihtoehdot(y, x, sudoku));
                        }
                    }

                    if (rivinarvot.size() >= montako) {
                        //System.out.println("Nelikko " +rivinarvot.toString());
                        Integer kokolista [] = rivinarvot.toArray(new Integer[rivinarvot.size()]);
                        int lista[] = new int[rivinarvot.size()];
                        for (int i = 0 ; i < rivinarvot.size(); i++ ) {
                            lista[i] = kokolista[i];
                        } 
                        int vaihtoehto[] = new int[montako];
                        hakuLista = new ArrayList<TreeSet<Integer>>();
                        combinationUtil(lista, vaihtoehto, 0, rivinarvot.size()-1, 0, montako);
                        //System.out.println(""+hakuLista.toString());
                        for (TreeSet<Integer> ts : hakuLista) {
                            //TreeSet<Integer> poisto = new TreeSet<Integer>();
                            //poisto.addAll(rivinarvot);
                            //poisto.removeAll(ts);
                            int loytynyt = 0;
                            ArrayList<Integer> loytyneetpaikat = new ArrayList<Integer>();
                            for (int y = 0 ; y < 3 ;y++) {
                                for (int x = 0 ; x < 3 ;x++) {

                                HashSet<Integer> apu = new HashSet<Integer>();
                                TreeSet<Integer> temp = mahdolliset.get((rivi+y)*9+sarake+x);


                                //apu.addAll(mahdolliset.get((rivi+y)*9+sarake+x));
                                if (!temp.isEmpty()) {
                                    for (int z : temp) apu.add(z);
                                        apu.removeAll(ts);
                                        if (apu.isEmpty()) {
                                        loytynyt++;
                                        loytyneetpaikat.add(y*3+x);
                                        }
                                    }
                                }
                            }
                            if (loytynyt == montako) {
                                HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                                estot.removeAll(loytyneetpaikat);
                                for (int r : estot) {
                                    if (sudoku[(rivi+r/3)][sarake+r%3] == 0) {
                                        for (int luku : ts) {
                                            //System.out.println(montako+" nakuMon EstaX " +(9*(rivi +r/3)+sarake+r%3)+" "+(luku));
                                            estoLista.get(9*(rivi+r/3)+sarake+r%3).add(luku);
                                            
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
   
    //Pointing Pairs, Pointing Triples
    private static void lukitseRivi(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        //vaakaan
        for (int ruutuY = 0 ; ruutuY < 9 ; ruutuY=ruutuY+3) {
            for (int ruutuX = 0 ; ruutuX < 9 ; ruutuX=ruutuX+3) {
                for (int vertailu = 0 ; vertailu < 3 ; vertailu++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sisus = 0 ; sisus < 9 ; sisus ++) {
                        if (sisus >= vertailu*3 && sisus < vertailu*3+3) {
                            //System.out.println(" oma " +((ruutuY+ sisus/3)*9+ruutuX+sisus%3));
                            omat.addAll(mahdolliset.get((ruutuY+sisus/3)*9+ruutuX+sisus%3));
                        } else {
                            //System.out.println(" muu " +((ruutuY+sisus/3)*9+ruutuX+sisus%3));
                            muut.addAll(mahdolliset.get((ruutuY+sisus/3)*9+ruutuX+sisus%3));
                        }

                    }
                    //System.out.println(ruutuY+""+ruutuX+" - " + omat.toString()+muut.toString());
                
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int y = ruutuY+vertailu;
                        for (int luku : omat) {
                            for (int x = 0 ; x < 9; x++) {
                                if ( x < ruutuX || x >= ruutuX+3 ) {
                                    if (sudoku[y][x] == 0) {
                                        //System.out.println("LukitseRuutu " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //pystyyn
        for (int ruutuY = 0 ; ruutuY < 9 ; ruutuY=ruutuY+3) {
            for (int ruutuX = 0 ; ruutuX < 9 ; ruutuX=ruutuX+3) {
                for (int vertailu = 0 ; vertailu < 3 ; vertailu++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sisus = 0 ; sisus < 9 ; sisus ++) {
                        if (sisus%3 == vertailu) {
                            //System.out.println(" oma " +((ruutuY+ sisus/3)*9+ruutuX+sisus%3));
                            omat.addAll(mahdolliset.get((ruutuY+sisus/3)*9+ruutuX+sisus%3));
                        } else {
                            //System.out.println(" muu " +((ruutuY+sisus/3)*9+ruutuX+sisus%3));
                            muut.addAll(mahdolliset.get((ruutuY+sisus/3)*9+ruutuX+sisus%3));
                        }

                    }
                    //System.out.println(ruutuY+""+ruutuX+" - " + omat.toString()+muut.toString());
                
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int x = ruutuX+vertailu;
                        for (int luku : omat) {
                            for (int y = 0 ; y < 9; y++) {
                                if ( y < ruutuY || y >= ruutuY+3 ) {
                                    if (sudoku[y][x] == 0) {
                                        //System.out.println("LukitsePysty " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    //Box Line Reduction
    private static void lukitseRuutu(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
    
        //rivi
        for (int rivi = 0 ; rivi < 9 ; rivi++) {
            for (int vertailu = 0 ; vertailu < 9 ; vertailu=vertailu+3) {
                HashSet<Integer> muut = new HashSet<Integer>();
                HashSet<Integer> omat = new HashSet<Integer>();
                for (int sisus = 0 ; sisus < 9 ; sisus ++) {
                    if (sisus >= vertailu && sisus < vertailu+3) {
                        omat.addAll(mahdolliset.get(rivi*9+sisus));
                    } else {
                        muut.addAll(mahdolliset.get(rivi*9+sisus));
                    }
                    
                }
                //System.out.println("Omat"+rivi+" "+vertailu +omat.toString()+muut.toString());
                omat.removeAll(muut);
                if (!omat.isEmpty()) {
                    int alkuY = rivi/3 * 3;
                    int alkuX = vertailu;
                    for (int luku : omat) {
                        for (int y = alkuY ; y < alkuY+3; y++) {
                            for (int x = alkuX ; x < alkuX+3; x++) {
                                if ( y != rivi ) {
                                    if (sudoku[y][x] == 0) {
                                        //System.out.println("LukitseRivi " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // sarake
        for (int sarake = 0 ; sarake < 9 ; sarake++) {
            for (int vertailu = 0 ; vertailu < 9 ; vertailu=vertailu+3) {
                HashSet<Integer> muut = new HashSet<Integer>();
                HashSet<Integer> omat = new HashSet<Integer>();
                for (int sisus = 0 ; sisus < 9 ; sisus ++) {
                    if (sisus >= vertailu && sisus < vertailu+3) {
                        omat.addAll(mahdolliset.get(sisus*9 +sarake));
                    } else {
                        muut.addAll(mahdolliset.get(sisus*9 + sarake));
                    }
                    
                }
                omat.removeAll(muut);
                if (!omat.isEmpty()) {
                    int alkuY = vertailu;
                    int alkuX = sarake/3*3;
                    for (int luku : omat) {
                        for (int y = alkuY ; y < alkuY+3; y++) {
                            for (int x = alkuX ; x < alkuX+3; x++) {
                                if ( x != sarake ) {
                                    if (sudoku[y][x] == 0) {
                                        //System.out.println("LukitseSarake " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private static boolean nishio(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        HashMap<Integer,Integer> poistot = new HashMap<>();
        int [][] kopio = new int[9][9];
        boolean ulos = false;
        
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int i = 0 ; i < 81 ; i ++) {

            ulos = false;
            int v = mahdolliset.get(i).size();
            if (v > 1 && v < 4) {
                for (int n : mahdolliset.get(i)) {
                    for (int y = 0 ; y < 9 ; y++) {
                        for (int x = 0 ; x < 9; x++) {
                            kopio[y][x] = sudoku[y][x];
                        }
                     }
                    kopio[i/9][i%9] = n;
                    while (etsiVarmat(kopio)){};
                    if (nollia(kopio) == 0) {
                        //estoLista.get(i).addAll(mahdolliset.get(i));
                        //estoLista.get(i).remove(n);

                        for (int y = 0 ; y < 9 ; y++) {
                           for (int x = 0 ; x < 9; x++) {
                               sudoku[y][x] = kopio[y][x];
                           }
                        }
                        //System.out.println("Nishio: touchdown " +i +" " +n);
                        return true;
                    }
                    
                    //tulostaVaihtoehdot(kopio);
                    for ( int a = 0; a < 81; a++) {
                        if (kopio[a/9][a%9] == 0) {
                            if (vaihtoehdot(a/9, a%9, kopio).isEmpty()) {
                                poistot.put(i,n);
                                //System.out.println("Nishio: "+i +" " + n +" a:"+a);
                                ulos = true;
                                break;
                            }
                        }
                    }
                    if (ulos) break;

                }
                kopio[i/9][i%9] = 0; 
            }
        }
        
        for (int e : poistot.keySet()) {
            estoLista.get(e).add(poistot.get(e));
        }

        return false;
    }
   
    
    private static HashSet<Integer> vaakarivi(int rivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < 9; i++) {
            if(sudoku[rivi][i] != 0) hs.add(sudoku[rivi][i]);
        }
        
        return hs;
    }

    private static HashSet<Integer> pystyrivi(int rivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < 9; i++) {
            if(sudoku[i][rivi] != 0) hs.add(sudoku[i][rivi]);
        }
        return hs;
    }

    private static HashSet<Integer> ruutu(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        int vaaka = (vaakarivi/3)*3;
        int pysty = (pystyrivi/3)*3;
        
        for (int y = vaaka ; y < vaaka+3; y++) {
            for (int x = pysty ; x < pysty+3; x++) {
                if(sudoku[y][x] != 0) hs.add(sudoku[y][x]);
            }
        }
        return hs;
    }
    
    private static int orpoVaakaRivi(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        for (int i = 0 ; i < 9 ; i++) {
            if (sudoku[vaakarivi][i] == 0) {
                TreeSet<Integer> ts = vaihtoehdot(vaakarivi, i, sudoku);
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
            for ( int numero : omat) 
               return numero;
        }
        
        return 0;
    }

    private static int orpoPystyRivi(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        for (int i = 0 ; i < 9 ; i++) {
            if (sudoku[i][pystyrivi] == 0) {
                TreeSet<Integer> ts = vaihtoehdot(i, pystyrivi, sudoku);
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
            for ( int numero : omat) 
               return numero;
        }
        return 0;
    }
    private static int orpoRuutu(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        int vaaka = (vaakarivi/3)*3;
        int pysty = (pystyrivi/3)*3;
        
        for (int y = vaaka ; y < vaaka+3; y++) {
            for (int x = pysty ; x < pysty+3; x++) {
                if (sudoku[y][x] == 0) {
                    TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
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
            for ( int numero : omat) 
               return numero;
        }
        return 0;
    }

    private static boolean tuplat(int[][] sudoku) {
        boolean tuplia = false;
        for (int i = 0 ; i < 9 ; i++) {
            ArrayList<Integer> setti = new ArrayList<Integer>();
            for (int j = 0 ; j < 9 ; j++) {
                if ( sudoku[i][j] != 0) setti.add(sudoku[i][j]);
            }
            for (int a : setti) {
                if (setti.indexOf(a) != setti.lastIndexOf(a)) {
                    System.out.println("Tuplanumero " + a + " rivilla " +(i+1));
                    tuplia = true;
                }
            }
        }
        for (int i = 0 ; i < 9 ; i++) {
            ArrayList<Integer> setti = new ArrayList<Integer>();
            for (int j = 0 ; j < 9 ; j++) {
                if ( sudoku[j][i] != 0) setti.add(sudoku[j][i]);
            }
            for (int a : setti) {
                if (setti.indexOf(a) != setti.lastIndexOf(a)) {
                    System.out.println("Tuplanumero " + a + " sarakkeella " +(i+1));
                    tuplia = true;
                }
            }
        }
        
        for (int i = 0 ; i < 9 ; i= i+3) {
            for (int j = 0 ; j < 9 ; j=j+3) {
                ArrayList<Integer> setti = new ArrayList<Integer>();
                for ( int y = i ; y < i+3 ; y++) {
                    for ( int x = j ; x < j+3 ; x++) {
                        if ( sudoku[y][x] != 0) setti.add(sudoku[y][x]);
                    }
                }
            
                for (int a : setti) {
                    if (setti.indexOf(a) != setti.lastIndexOf(a)) {
                        System.out.println("Tuplanumero " + a + " ruudussa (" +(i/3*3 + j/3 +1)+")");
                        tuplia = true;
                    }
                }
            }
        }
        
        return tuplia;
    }
    
    private static TreeSet<Integer> vaihtoehdot(int vaakarivi, int pystyrivi, int[][] sudoku) {
        TreeSet<Integer> ts = new TreeSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
        laskuri++;
        if (sudoku[vaakarivi][pystyrivi] == 0) {
            ts.removeAll(vaakarivi(vaakarivi, sudoku));
            ts.removeAll(pystyrivi(pystyrivi, sudoku));
            ts.removeAll(ruutu(vaakarivi, pystyrivi, sudoku));
            ts.removeAll(estoLista.get(vaakarivi*9+pystyrivi));
        } else {
            ts = new TreeSet<Integer>();
        }
        return ts;
    }

    private static void tulostaSudoku(int[][] sudoku) {
        System.out.println();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(" "+sudoku[i][j]);
                if ((j+1)%3==0) System.out.print("  ");
            }
            System.out.println();
            if ((i+1)%3==0) System.out.println();
        }
        
    }
    
    private static void tulostaVaihtoehdot(int[][] sudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(vaihtoehdot(i, j, sudoku));
                if ((j+1)%3==0) System.out.print("   ");
            }
            System.out.println();
            if ((i+1)%3==0) System.out.println();
        }
    }
    public static int nollia(int[][] sudoku) {
        int paluu = 0;
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 if (sudoku[y][x] == 0) paluu++;
            }
        }
        return paluu;
    }
    
    public static int paikkoja(int[][] sudoku) {
        int paluu = 0;
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 if (!vaihtoehdot(y, x, sudoku).isEmpty()) paluu++;
            }
        }
        return paluu;
    }
    
    private static boolean rekurse(int i, int[][] sudoku) {

        if (i > 80 ) return true;
        if (sudoku[i/9][i%9] != 0) {
             return rekurse( i+1,  sudoku );
        }
        for ( int luku : vaihtoehdot(i/9, i%9, sudoku)) {
            sudoku[i/9][i%9] = luku;
            if (rekurse( i+1,  sudoku )) {
                return true;
            } else {
                sudoku[i/9][i%9] = 0;
            }
        }
        return false;
    } 
    
    private static boolean etsiVarmat(int[][] sudoku) { 
        boolean loytyi = false;
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9 ; x++) {

                if (sudoku[y][x] == 0) {
                    TreeSet<Integer> ts = new TreeSet<Integer>();
                    ts = vaihtoehdot(y, x, sudoku);
                    //System.out.println("vaihtoehdot: " + y + " " +x+ " : "+ts);
                    if (ts.size() == 1) {
                        sudoku[y][x] = ts.first();
                        //System.out.println("valinta: " + y + " " +x+ " : "+ts.first());
                        loytyi = true;
                    }
                    if (sudoku[y][x] == 0) {
                        //System.out.println(y+" "+x+" "+vaihtoehdot(y, x, sudoku));
                        sudoku[y][x] = orpoVaakaRivi(y, x, sudoku);
                        if (sudoku[y][x] != 0) {
                            loytyi = true;
                            //System.out.println(y+" vaaka "+x+" "+sudoku[y][x]);
                        }

                        if (sudoku[y][x] == 0) {
                            sudoku[y][x] = orpoPystyRivi(y, x, sudoku);
                            if (sudoku[y][x] != 0) {
                                loytyi = true;
                                //System.out.println(y+" pysty "+x+" "+sudoku[y][x]);
                            }
                        }
                        if (sudoku[y][x] == 0) {
                            sudoku[y][x] = orpoRuutu(y, x, sudoku);
                            if (sudoku[y][x] != 0) {
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
        estoLista = new ArrayList<HashSet<Integer>>(); 
        for (int i = 0 ; i < 81 ; i++) {
            HashSet<Integer> hs = new HashSet<Integer>();
            estoLista.add(hs);
        }
        int rundi = 0;
        int yritys = 0;
       
         while (yritys < 3) {
            rundi++;
            yritys++;
            //tulostaVaihtoehdot(sudoku);
            while (etsiVarmat(sudoku))  {
                System.out.print("*");
                yritys = 0;
            }
            if (nollia(sudoku) == 0) {
                //System.out.println("Adios Amigos " + rundi);
                return;
            }
            System.out.print("@");
            if (rundi%2 == 0){
                bug(sudoku);
                swordfishVaaka(sudoku);
                swordfishPysty(sudoku);
                jellyfishVaaka(sudoku);
                jellyfishPysty(sudoku);
                piiloMonikotRivi(4, sudoku);
                piiloMonikotSarake(4,sudoku);
                piiloMonikotRuutu(4, sudoku);
                xWingPysty(sudoku);
                xWingVaaka(sudoku);
                xywing(sudoku);
                simpleColouring2(sudoku);
                simpleColouring(sudoku);

            } else {
                etaPari(sudoku);
                lukitseRivi(sudoku);
                lukitseRuutu(sudoku);
                nakuMonikotRivi(sudoku);
                nakuMonikotSarake(sudoku);
                nakuMonikotRuutu(sudoku);
                piiloMonikotRivi(2,sudoku);
                piiloMonikotSarake(2,sudoku);
                piiloMonikotRuutu(2, sudoku);
                piiloMonikotRivi(3, sudoku);
                piiloMonikotSarake(3,sudoku);
                piiloMonikotRuutu(3, sudoku);
                simpleColouring2(sudoku);
                simpleColouring(sudoku);
            }
 
            //System.out.println("Loppu");
            //tulostaVaihtoehdot(sudoku);
            //tulostaSudoku(sudoku);
            //System.out.println(nollia(sudoku)+"/"+paikkoja(sudoku));
        }

        if (nollia(sudoku)!=0) {
            System.out.print("!");
            if (nishio(sudoku)) return;
            
            //System.out.println("Ei ratkennut suoraan " + rundi);
            //tulostaVaihtoehdot(sudoku);
            //tulostaSudoku(sudoku);
            System.out.print("$");
            rekurse(0, sudoku);
        }

    }

    public static void tarkasta(int[][] sudoku) {
        for (int i = 0 ; i < 81 ; i++) {
            if (sudoku[i/9][i%9] == 0) {
                System.out.println("Virhe sudokussa nollia");
                break;
            }
        }
        for (int i = 0 ; i < 9 ; i++) {
            if (vaakarivi(i, sudoku).size() != 9) {
                System.out.println("Virhe sudokussa rivilla "+ (i+1) );
            }
            if (pystyrivi(i, sudoku).size() != 9) {
                System.out.println("Virhe sudokussa sarakkeessa "+(i+1) );
            }
        }
        for (int i = 0 ; i < 9 ; i=i+3) {
            for (int j = 0 ; j < 9 ; j=j+3) {
                if (ruutu(i,j, sudoku).size() != 9) {
                    System.out.println("Virhe sudokussa ruudussa "+ (i/3*3+j/3+1));
                }
            }
        }
    }
          
     
    public static void main(String[] args) throws FileNotFoundException {

        int[][] sudoku = new int[9][9];
        //System.out.println(" " +Arrays.toString(args));
        laskuri = 0;
        String s ="";
        if (args.length == 1 && !args[0].startsWith("-")) {
            try {
                s = lueLuvut(args[0]);
            } catch(FileNotFoundException e) {
                 System.out.println("Ei loydy tiedostoa");
            }
        }
        if ((args.length == 1 && args[0].startsWith("-")))  {
            s = args[0].substring(1);
            s = s.replaceAll("\\s+", "");
            s = s.replaceAll("/[^0-9]/", "0");
        }
        
        if ((args.length == 2) && args[0].equals("-")) {
            s = args[1];
            s = s.replaceAll("\\s+", "");
            s = s.replaceAll("/[^0-9]/", "0");
        }

        if (!s.isEmpty()) {
        System.out.println("Taulu "+s );
        if (s.length() < 81 ) {
            System.out.println("Virhe: liian pieni taulukko");
        } else {
            sudoku = parsiSisalto(s);
            tulostaSudoku(sudoku);
            if (!tuplat(sudoku)) {
                ratkaise(sudoku);
                System.out.println("\n ** Ratkaisu * "+ laskuri +" * \n");
                tulostaSudoku(sudoku);
                tarkasta(sudoku);
            } else {
                System.out.println("Virheellinen lahtotilanne");
            }
            
        }
        } else {
            System.out.println("Anna sudokutiedoston nimi tai merkkijono");
        }
    }
    
    public static int[][] parsiSisalto(String sisalto) {
        int[][] s = new int[9][9];
        for (int i = 0; i < 81; i++) {
            int c = Character.digit(sisalto.charAt(i), 10);
            c = (c < 0) ? 0 : c;
            s[i/9][i%9] = c ;
        }
        return s;
    }
    public static String lueLuvut(String tiedosto) throws FileNotFoundException {
        Scanner lukija = new Scanner(new File(tiedosto));
        StringBuilder sisalto = new StringBuilder();
        try {
            while (lukija.hasNext()) {
                String s = lukija.next();
                if ((s.startsWith("#")) || s.startsWith("!")) {
                    s = lukija.nextLine();
                } else {
                    sisalto.append(s);
                }
            }
        } catch (Exception e) {
          System.out.println("Virhe: " + e.getMessage());
        } finally {
            if(lukija !=null){lukija.close();}
        }
        
        return sisalto.toString();
    }
}

