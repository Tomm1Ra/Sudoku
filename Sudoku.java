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
    private static ArrayList<ArrayList<Integer>> ketjut;
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
        if (a%9 == b%9) return true;
        if (a/9 == b/9) return true;
        if ((a/9/3 == b/9/3) && (a%9/3 == b%9/3)) return true;
        return false;
    }
    
    private static boolean samaRivi(int a, int b) {
        if (a/9 == b/9) return true;
        return false;    
    }

    private static boolean samaSarake(int a, int b) {
        if (a%9 == b%9) return true;
        return false;    
    }

    private static boolean samaRuutu(int a, int b) {
        if (((a/9)/3 == (b/9)/3) && (a%9/3 == b%9/3)) return true;
        return false;    
    }

    private static TreeSet<Integer> haeNumerollaRivi(int rivi, int numero, ArrayList<TreeSet<Integer>> mahdolliset) {
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

    private static TreeSet<Integer> haeNumerollaRuutu(int ruutu, int numero, ArrayList<TreeSet<Integer>> mahdolliset) {
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

    private static boolean jellyfishVaaka(int[][] sudoku) {
        boolean paluu = false;
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
                        if (mahdolliset.get(9*y+x).contains(numero)) {
                            //System.out.println("Vaaka Jellyfish vaaka esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                            paluu = true;
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
                                            if (mahdolliset.get(9*y+x).contains(numero)) {
                                                //System.out.println("Vaaka Jellyfish Piilo esta " + numero +" "+(9*y+x));
                                                estoLista.get(9*y+x).add(numero);
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
    
    private static boolean jellyfishPysty(int[][] sudoku) {
        boolean paluu = false;
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
                        if (mahdolliset.get(9*y+x).contains(numero)) {
                            //System.out.println("Pysty Jellyfish  pysty esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                            paluu = true;
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
                                            if (mahdolliset.get(9*y+x).contains(numero)) {
                                                //System.out.println("Pysty Jellyfish Piilo esta " + numero +" "+(9*y+x));
                                                estoLista.get(9*y+x).add(numero);
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
    
    private static boolean swordfishVaaka(int[][] sudoku) {
        boolean paluu = false;
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
                        if (mahdolliset.get(9*y+x).contains(numero)) {
                            //System.out.println("Vaaka Swordfish esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                            paluu = true;
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
                                        if (mahdolliset.get(9*y+x).contains(numero)) {
                                            //System.out.println("Vaaka Swordfish Piilo esta " + numero +" "+(9*y+x));
                                            estoLista.get(9*y+x).add(numero);
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

    private static boolean swordfishPysty(int[][] sudoku) {
        boolean paluu = false;
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
                        if (mahdolliset.get(9*y+x).contains(numero)) {
                            //System.out.println("Pysty Swordfish esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                            paluu = true;
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
                                        if (mahdolliset.get(9*y+x).contains(numero)) {
                                            //System.out.println("Pysty swordfish Piilo esta " + numero +" "+(9*y+x));
                                            estoLista.get(9*y+x).add(numero);
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

    private static boolean xWingVaaka(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        boolean paluu = false;
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
                        if (mahdolliset.get(9*y+x).contains(numero)) {
                            //System.out.println("Vaaka xwing esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                            paluu = true;
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
                                    if (mahdolliset.get(9*y+x).contains(numero)) {
                                        //System.out.println("Vaaka xwing Piilo esta " + numero +" "+(9*y+x));
                                        estoLista.get(9*y+x).add(numero);
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

    private static boolean xWingPysty(int[][] sudoku) {
        boolean paluu = false;
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
                        if (mahdolliset.get(9*y+x).contains(numero)) {
                            //System.out.println("Pysty xwing esta " + numero +" "+(9*y+x));
                            estoLista.get(9*y+x).add(numero);
                            paluu = true;
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
                                    if (mahdolliset.get(9*y+x).contains(numero)) {
                                        //System.out.println("Pysty xwing Piilo esta " + numero +" "+(9*y+x));
                                        estoLista.get(9*y+x).add(numero);
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
    
    private static boolean bug(int[][] sudoku) {
        boolean paluu = false;
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
                        return true;
                    }
                }    
            }
        }
        return paluu;
    }

    private static boolean simpleColouring(int[][] sudoku) {
        boolean paluu = false;
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
                
                ts = haeNumerollaRivi(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);
                
                ts =haeNumerollaPysty(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);
                
                ts =haeNumerollaRuutu(x, n, mahdolliset);
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
                        if (samaRivi(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaRivi(samatSolut.get(b)/9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaSarake(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaPysty(samatSolut.get(b)%9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaRuutu(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaRuutu(samatSolut.get(b)/9/3*3 + samatSolut.get(b)%9/3 , n, mahdolliset).size() == 2) {
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
                                    if (solmut[x].minDistance < Integer.MAX_VALUE && solmut[x].minDistance >= 2 && solmut[x].minDistance % 2 == 0 && mahdolliset.get(solmut[x].id).contains(n)) {
                                        //System.out.println(solmut[b].minDistance+" Simple colouring esto " +solmut[x].id +" "+n);
                                        estoLista.get(solmut[x].id).add(n);
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

    private static boolean simpleColouring2(int[][] sudoku) {
        boolean paluu = false;
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

                ts = haeNumerollaRivi(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);

                ts =haeNumerollaPysty(x, n, mahdolliset);
                if (ts.size() == 2) ketjuSolut.addAll(ts);

                ts =haeNumerollaRuutu(x, n, mahdolliset);
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
                        if (samaRivi(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaRivi(samatSolut.get(b)/9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaSarake(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaPysty(samatSolut.get(b)%9, n, mahdolliset).size() == 2) {
                            //System.out.println("Yhteys " + samatSolut.get(b)+" "+samatSolut.get(c));
                            solmut[b+1].adjacencies.add(new Edge(solmut[c+1],1)); // kaikkien etaisyys 1 toisistaan
                            solmut[c+1].adjacencies.add(new Edge(solmut[b+1],1));
                        }
                        if (samaRuutu(samatSolut.get(b), samatSolut.get(c)) && haeNumerollaRuutu(samatSolut.get(b)/9/3*3 + samatSolut.get(b)%9/3 , n, mahdolliset).size() == 2) {
                            
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
                            //System.out.println(solmut[b].minDistance+" SimpleColouring "+ samatSolut.get(a-1) +" "+samatSolut.get(b-1) +" "+ mahdolliset.get(i));
                            //System.out.println("SimpleC 2 estä " +((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9) +" " +(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9)+" "+n);
                            if (!yhteys((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9 , samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9)) {  
                                if (mahdolliset.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).contains(n)) {
                                    estoLista.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).add(n);
                                    paluu = true;
                                }
                                if (mahdolliset.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).contains(n)) {
                                    estoLista.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).add(n);
                                    paluu = true;
                                }
                                // estetaan samassa ruudussa olevat jos a ja b samalla ruudukon rivilla tai sarakkeella
                                int a1rivi = samatSolut.get(a-1)/9/3;
                                int a1sarake = samatSolut.get(a-1)%9/3;
                                int b1rivi = samatSolut.get(b-1)/9/3;
                                int b1sarake = samatSolut.get(b-1)%9/3;
                                if (a1rivi == b1rivi) {
                                    int aY = samatSolut.get(a-1) / 9;
                                    int bX0 = samatSolut.get(b-1) % 9 / 3 * 3;
                                    int bY = samatSolut.get(b-1) / 9;
                                    int aX0 = samatSolut.get(a-1) % 9 / 3 * 3;
                                    for (int kohde = bX0 ; kohde < bX0+3 ; kohde++) {
                                        if (mahdolliset.get(kohde + aY*9).contains(n)) {
                                            estoLista.get(kohde + aY*9).add(n);
                                            // System.out.println("SC esta rivi B " + (kohde + aY*9) + " " +n);
                                            paluu = true;
                                        }
                                    }
                                    for (int kohde = aX0 ; kohde < aX0+3 ; kohde++) {
                                        if (mahdolliset.get(kohde + bY*9).contains(n)) {
                                            estoLista.get(kohde + bY*9).add(n);
                                            // System.out.println("SC esta rivi A " + (kohde + bY*9) + " " +n);
                                            paluu = true;
                                        }
                                    }
                                }
                                if (a1sarake == b1sarake) {
                                    int aX = samatSolut.get(a-1) % 9;
                                    int bY0 = samatSolut.get(b-1) / 9 / 3 * 3;
                                    int bX = samatSolut.get(b-1) % 9;
                                    int aY0 = samatSolut.get(a-1) / 9 / 3 * 3;
                                    for (int kohde = bY0 ; kohde < bY0+3 ; kohde++) {
                                        if (mahdolliset.get(kohde*9 + aX).contains(n)) {
                                            estoLista.get(kohde*9 + aX).add(n);
                                            // System.out.println("SC esta sarake B " + (kohde*9 + aX) + " " +n);
                                            paluu = true;
                                        }
                                    }
                                    for (int kohde = aY0 ; kohde < aY0+3 ; kohde++) {
                                        if (mahdolliset.get(kohde*9 + bX).contains(n)) {
                                            estoLista.get(kohde*9 + bX).add(n);
                                            // System.out.println("SC esta sarake A " + (kohde*9 +bX) + " " +n);
                                            paluu = true;
                                        }
                                    }
                                }
                            } //else System.out.println("Sama alue ei estoa");
                        }
                    }
                }
            }
        }
        return paluu;
    }    

    private static boolean uniqueRectangle(int[][] sudoku) {
        boolean paluu = false;
        //tulostaVaihtoehdot(sudoku);
        ArrayList<Integer> mahdolliset = new ArrayList<Integer>();
        ArrayList<TreeSet<Integer>> kaikkiMahdolliset = new ArrayList<TreeSet<Integer>>();
        ArrayList<Integer> kaksiot = new ArrayList<Integer>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                kaikkiMahdolliset.add(ts);
                if (ts.size() == 2) {
                    mahdolliset.add(ts.first()*10+ts.last());
                    kaksiot.add(y*9+x);
                } else {
                    mahdolliset.add(0);
                }
            }
        }
        if (kaksiot.size() < 3) return false;
        for (int ai = 0 ; ai < kaksiot.size()-1 ; ai++) {
            int i = kaksiot.get(ai);
            for (int bi = ai+1 ; bi < kaksiot.size() ; bi++) {
                int j = kaksiot.get(bi);
                if (mahdolliset.get(j) == mahdolliset.get(i) && samaRuutu(i,j)) {
                    if (samaRivi(i,j)) {
                        int s1 = i%9;
                        int s2 = j%9;
                        int kulma1 = 99;
                        int kulma2 = 99;
                        for (int r = 0 ; r < 9 ; r++) {
                            if (mahdolliset.get(i) == mahdolliset.get(r*9 +s1)) {
                                if (r*9 + s1 != i) kulma1 = r*9 +s1;
                            }
                            if (mahdolliset.get(i) == mahdolliset.get(r*9 +s2)) {
                                if (r*9 + s2 != j) kulma2 = r*9 +s2;
                            }
                        }
                        int kulma = 99;
                        if (kulma1 !=99) {
                            kulma = kulma1/9*9 + j%9;
                        }
                        if (kulma2 !=99) {
                            kulma = kulma2/9*9 + i%9;
                        }
                        //System.out.println(" uniq rec base" + " "+i+" " + j+" " + kulma1+" " + kulma2 + " " + kulma);
                        if (kulma < 99) {
                            if (kaikkiMahdolliset.get(kulma).contains(mahdolliset.get(i)/10) && kaikkiMahdolliset.get(kulma).contains(mahdolliset.get(i)%10)) {
                                //System.out.println("unique rectangle rivi!!! " + kulma +" " + (mahdolliset.get(i)/10) +" " +(mahdolliset.get(i)%10) );
                                estoLista.get(kulma).add(mahdolliset.get(i)/10);
                                estoLista.get(kulma).add(mahdolliset.get(i)%10);
                                paluu = true;
                            }
                        }
                    } else if (samaSarake(i,j)) {
                        int r1 = i/9;
                        int r2 = j/9;
                        int kulma1 = 99;
                        int kulma2 = 99;
                        for (int s = 0 ; s < 9 ; s++) {
                            if (mahdolliset.get(i) == mahdolliset.get(r1*9 + s)) {
                                if (r1*9 + s != i) kulma1 = r1*9 +s;
                            }
                            if (mahdolliset.get(i) == mahdolliset.get(r2*9 +s)) {
                                if (r2*9 + s != j) kulma2 = r2*9 +s;
                            }
                        }
                        int kulma = 99;
                        if (kulma1 !=99) {
                            kulma = kulma1%9 + j/9*9;
                        }
                        if (kulma2 !=99) {
                            kulma = kulma2%9 + i/9*9;
                        }
                        //System.out.println(" uniq rec base" + " "+i+" " + j+" " + kulma1+" " + kulma2 + " " + kulma);
                        if (kulma < 99) {
                            if (kaikkiMahdolliset.get(kulma).contains(mahdolliset.get(i)/10) && kaikkiMahdolliset.get(kulma).contains(mahdolliset.get(i)%10)) {
                                //System.out.println("unique rectangle Sarake!!! " + kulma +" " + (mahdolliset.get(i)/10) +" " +(mahdolliset.get(i)%10) );
                                estoLista.get(kulma).add(mahdolliset.get(i)/10);
                                estoLista.get(kulma).add(mahdolliset.get(i)%10);
                                paluu = true;
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }
    
    private static boolean etaPari(int[][] sudoku) {
        boolean paluu = false;
        ArrayList<Integer> mahdolliset = new ArrayList<Integer>();
        ArrayList<TreeSet<Integer>> kaikkiMahdolliset = new ArrayList<TreeSet<Integer>>();
        ArrayList<Integer> kaksiot = new ArrayList<Integer>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                kaikkiMahdolliset.add(ts);
                if (ts.size() == 2) {
                    //System.out.println((y*9+x)+" arvot "+ts.first()+" "+ts.last());
                    mahdolliset.add(ts.first()*10+ts.last());
                    kaksiot.add(y*9+x);
                } else {
                    mahdolliset.add(0);
                }
            }
        }
        for (int ai = 0 ; ai < kaksiot.size() ; ai++) {
            int i = kaksiot.get(ai);
            ArrayList<Integer> samatSolut = new ArrayList<Integer>();
            samatSolut.add(i);
            for (int bi = 0 ; bi < kaksiot.size() ; bi++) {
                int j = kaksiot.get(bi);
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
                            if (kaikkiMahdolliset.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).contains(mahdolliset.get(i)%10)) {
                                estoLista.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).add(mahdolliset.get(i)%10);
                                paluu = true;
                            }
                            if(kaikkiMahdolliset.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).contains(mahdolliset.get(i)/10)) {
                                estoLista.get((samatSolut.get(a-1)/9)*9 + samatSolut.get(b-1)%9).add(mahdolliset.get(i)/10);
                                paluu = true;
                            }
                            if (kaikkiMahdolliset.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).contains(mahdolliset.get(i)%10)) {
                                estoLista.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).add(mahdolliset.get(i)%10);
                                paluu = true;
                            }
                            if (kaikkiMahdolliset.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).contains(mahdolliset.get(i)/10)) {
                                estoLista.get(samatSolut.get(a-1)%9 + (samatSolut.get(b-1)/9)*9).add(mahdolliset.get(i)/10);
                                paluu = true;
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }   
    
    private static boolean haeCircle( int alku, int seuraava, HashMap<Integer,HashSet<Integer>> wl, HashMap<Integer,HashSet<Integer>> sl, ArrayList<Integer> kaydyt, boolean linkki) {

        if (seuraava > -1) {
            if (seuraava == alku) {
                ArrayList<Integer> tulos = new ArrayList<Integer>();
                tulos.add(alku);
                tulos.addAll(kaydyt);
                tulos.add(seuraava);
                ketjut.add(new ArrayList<Integer>(tulos));
              return true;
            } 
        } 
        if (seuraava == -1) {
            seuraava = alku;
        } else {
            kaydyt.add(seuraava);
            
        }
        HashSet<Integer> seuraavat = new HashSet<Integer>();
        if (linkki) {
            seuraavat.addAll(sl.get(seuraava));
            linkki = false;
        } else {
            seuraavat.addAll(wl.get(seuraava));
            linkki = true;
        }
        seuraavat.removeAll(kaydyt);

        for (int s : seuraavat) {
            if (!kaydyt.contains(s)) {
                if (haeCircle(alku, s, wl, sl, kaydyt, linkki)) {
                } else {
                    kaydyt.remove(kaydyt.size()-1);
                }
            }
        }
        return false;
        
    }
    
    private static boolean xCycles(int[][] sudoku) {
        boolean paluu = false;
        //tulostaVaihtoehdot(sudoku);
 
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
            }
        }
        for (int numero = 1 ; numero <=9 ; numero++ ) {
            TreeSet<Integer> ts = new TreeSet<Integer>();
            HashSet<Integer> ketjuSolut = new HashSet<Integer>();
            for (int x = 0 ; x < 9 ; x++){
                ts = haeNumerollaRivi(x, numero, mahdolliset);
                ketjuSolut.addAll(ts);
            } 
            
            HashMap<Integer,HashSet<Integer>> weakLinks = new HashMap<Integer,HashSet<Integer>>();
            HashMap<Integer,HashSet<Integer>> strongLinks = new HashMap<Integer,HashSet<Integer>>();
            ArrayList<Integer> ketju = new ArrayList<Integer>();
            for ( int a : ketjuSolut) {
                HashSet<Integer> hsw = new HashSet<>();
                HashSet<Integer> hss = new HashSet<>();
                weakLinks.put(a,hsw);
                strongLinks.put(a,hss);
                ketju.add(a);
            }

            for (int a : ketju) {
                for (int b : ketju) {
                    if (yhteys(a, b) && a!=b) {
                        HashSet<Integer> wla = weakLinks.get(a);
                        HashSet<Integer> wlb = weakLinks.get(b);
                        HashSet<Integer> sla = strongLinks.get(a);
                        HashSet<Integer> slb = strongLinks.get(b);

                        if (((samaRivi(a, b) && samaRuutu(a, b)) || (samaRuutu(a, b) && samaSarake(a, b))) == true ) {
                        } else {
                            if (samaRivi(a, b)) {
                                if (haeNumerollaRivi(a/9, numero, mahdolliset).size() == 2) {
                                    sla.add(b);
                                    slb.add(a);
                                } else {
                                    wla.add(b);
                                    wlb.add(a);
                                }
                            }
                            if (samaSarake(a, b)) { 
                                if (haeNumerollaPysty(a%9, numero, mahdolliset).size() == 2) {
                                    sla.add(b);
                                    slb.add(a);
                                } else {
                                    wla.add(b);
                                    wlb.add(a);
                                }
                            }
                            if (samaRuutu(a, b)) {
                                if (haeNumerollaRuutu(a/9/3*3 + a%9/3, numero, mahdolliset).size() == 2) {
                                    sla.add(b);
                                    slb.add(a);
                                } else {
                                    wla.add(b);
                                    wlb.add(a);
                                }
                            }
                            weakLinks.put(a,wla);
                            weakLinks.put(b,wlb);
                            strongLinks.put(a,sla);
                            strongLinks.put(b,slb);
                        }
                    }
                }
            }
            HashSet<Integer> solut = new HashSet<Integer>();                
            solut.addAll(weakLinks.keySet());
            solut.addAll(strongLinks.keySet());
            for ( int j : solut) {
                ketjut = new ArrayList<ArrayList<Integer>>(); 
                ArrayList<Integer> kaydyt = new ArrayList<Integer>();
                haeCircle(j, -1, weakLinks, strongLinks, kaydyt, true); // aloitetaan strong linkilla
                haeCircle(j, -1, weakLinks, strongLinks, kaydyt, false);

                for (ArrayList<Integer> k : ketjut) {
                    if (k.size()%2==1) {
                        if (k.size() % 2 == 1 && k.size() > 3) {
                            for (int a1=0 ; a1 < k.size()-1 ; a1++) {
                                for (int a2 = a1+1; a2 < k.size() ; a2++) {
                                    if (a1%2 != a2%2) {
                                        if (!yhteys(k.get(a1),k.get(a2))) {
                                            //System.out.println(" x-cycle "+numero + " " + k2.get(a1) +" " + k2.get(a2));
                                            if (mahdolliset.get(k.get(a1)/9*9 + k.get(a2)%9).contains(numero)) {
                                                estoLista.get(k.get(a1)/9*9 + k.get(a2)%9).add(numero);
                                                paluu = true;
                                            }
                                            if (mahdolliset.get(k.get(a2)/9*9 + k.get(a1)%9).contains(numero)) {
                                                estoLista.get(k.get(a2)/9*9 + k.get(a1)%9).add(numero);
                                                paluu = true;
                                            }
                                        } else if (samaRivi(k.get(a1), k.get(a2))) {
                                            int alku = k.get(a1)/9*9;
                                            for (int e = alku ; e < alku+9 ; e++) {
                                                if ( e != k.get(a1) && e != k.get(a2)) {
                                                    //System.out.println("x-c rivi" +e);
                                                    if (mahdolliset.get(e).contains(numero)) {
                                                        estoLista.get(e).add(numero);
                                                        paluu = true;
                                                    }
                                                }
                                            }
                                        } else if (samaSarake(k.get(a1), k.get(a2))) {
                                            int alku = k.get(a1)%9;
                                            for (int e = alku ; e < 81 ; e=e+9) {
                                                if ( e != k.get(a1) && e != k.get(a2)) {
                                                    //System.out.println("x-c sarake" +e);
                                                    if (mahdolliset.get(e).contains(numero)) {
                                                        estoLista.get(e).add(numero);
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
                    if (k.size() % 2 == 0) {
                        if (strongLinks.get(k.get(0)).contains(k.get(1))) {
                            //System.out.println("XCycle strong odd" + k + numero);
                            estoLista.get(k.get(0)).addAll(Arrays.asList(1,2,3,4,5,6,7,8,9));
                            estoLista.get(k.get(0)).remove(numero);
                            paluu = true;
                        } else {
                            //System.out.println("XCycle weak odd" + k + numero);
                            if (mahdolliset.get(k.get(0)).contains(numero)) {
                                estoLista.get(k.get(0)).add(numero);
                                paluu = true;
                            }
                        }
                    }
                }
            }
        }
        return paluu;
    }

    private static boolean haeChain( int luku, int seuraava, int numero, HashMap<Integer,TreeSet<Integer>> ts, ArrayList<Integer> kaydyt, HashMap<Integer,TreeSet<Integer>> lukumap) {

        if (!kaydyt.isEmpty() && lukumap.get(seuraava).contains(luku)) {
            ketjut.add(new ArrayList<Integer>(kaydyt));
        }
        if (lukumap.get(seuraava).contains(numero)) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(lukumap.get(seuraava));
            hs.remove(numero);
            //System.out.println(" "+ seuraava +" x " +hs);
            numero = (int)hs.toArray()[0];
            for (int s : ts.get(seuraava)) {
                if (!kaydyt.contains(s) && lukumap.get(seuraava).contains(numero)) {
                    kaydyt.add(s);   
                    if (haeChain(luku, s, numero, ts, kaydyt, lukumap)) {
                        return true;
                    } else {
                        kaydyt.remove(kaydyt.size()-1);
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean xyChain(int[][] sudoku) {
        boolean paluu = false;
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        ArrayList<TreeSet<Integer>> kaikkiMahdolliset = new ArrayList<TreeSet<Integer>>();
        HashMap<Integer,TreeSet<Integer>> lukumap = new HashMap<Integer,TreeSet<Integer>>();
        ArrayList<Integer> parit = new ArrayList<Integer>();
        ArrayList<Integer> luvut = new ArrayList<Integer>();
        ArrayList<Integer> kaikkiluvut = new ArrayList<Integer>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9 ; x++) {
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                kaikkiMahdolliset.add(ts);
                if (ts.size() == 2) {
                   mahdolliset.add(ts);
                   TreeSet<Integer> lmap = new TreeSet<Integer>();
                   lmap.addAll(ts);
                   lukumap.put(y*9+x, ts);
                   parit.add(y*9+x);
                   luvut.add(ts.first()*10 + ts.last());
                   kaikkiluvut.add(ts.first()*10 + ts.last());
                } else {
                    mahdolliset.add(new TreeSet<Integer>());
                    kaikkiluvut.add(0);
                }
            }
        } 
        
        int psize = parit.size();    
        HashMap<Integer,TreeSet<Integer>> solut = new HashMap<Integer,TreeSet<Integer>>();
        for (int i : parit) {
            TreeSet<Integer> ts = new TreeSet<Integer>();
            solut.put(i,ts);
        }
        for (int a = 0 ; a < psize-1 ;a++ ) {
            for (int b = a+1 ; b < psize ; b++) {
                if (yhteys(parit.get(a), parit.get(b)) && (luvut.get(a)/10 == luvut.get(b)%10 
                                                           || luvut.get(a)%10 == luvut.get(b)/10 
                                                           || luvut.get(a)%10 == luvut.get(b)%10 
                                                           || luvut.get(a)/10 == luvut.get(b)/10)) {
                    TreeSet<Integer> tsa = solut.get(parit.get(a));
                    TreeSet<Integer> tsb = solut.get(parit.get(b));
                    tsa.add(parit.get(b));
                    tsb.add(parit.get(a));
                    solut.put(parit.get(a),tsa);
                    solut.put(parit.get(b),tsb);
                    //System.out.println("xyChain " + parit.get(a)  +" "+ parit.get(b) +" "+luvut.get(a) +" "+ luvut.get(b));
                }
            }
        }
        for ( int aloitus : solut.keySet()) {
            for (int numero = 1; numero <=9 ; numero++) {
                ketjut = new ArrayList<>();
                ArrayList<Integer> kaydyt = new ArrayList<Integer>();

                kaydyt.add(aloitus);
                haeChain(numero, aloitus, numero, solut, kaydyt, lukumap);

                int ketjunumero = numero;

                for (ArrayList<Integer> ketju : ketjut) {
                    if (ketju.size() > 8) continue;
                    int n1 = kaikkiluvut.get(ketju.get(0))/10;
                    int n2 = kaikkiluvut.get(ketju.get(0))%10;
                    if (numero == n1) ketjunumero = n2; else ketjunumero = n1;
                    boolean kay = true;
                    //System.out.println(numero+" : Ketju " + ketju+" "+ketju.size());
                    for (int k = 1 ; k < ketju.size() ; k++) {
                        int a1 = kaikkiluvut.get(ketju.get(k))/10;
                        int a2 = kaikkiluvut.get(ketju.get(k))%10;
                        if (a1 == ketjunumero ) {
                            ketjunumero = a2;
                        } else if (a2 == ketjunumero) {
                            ketjunumero = a1;
                        } else {
                            kay = false;
                        }

                        if (ketjunumero == numero && k <= 2) kay = false;
                        if (ketjunumero == numero && k >= 3  && kay) {
                            int lyhin=99;
                            int paikka = 0;
                            for (ArrayList<Integer> tarkistusketju : ketjut) {
                                paikka = tarkistusketju.indexOf(ketju.get(k));
                                if (paikka > 0) lyhin = Math.min(lyhin, paikka);
                            }
                            if (lyhin != k) continue;
                            kay = false;
                            //System.out.println("XYCHAIN " + ketju.get(0) + " " + ketju.get(k) +" " +numero + ketju);
                            int kohde1 = ketju.get(0)/9*9 + ketju.get(k)%9;
                            int kohde2 = ketju.get(0)%9 + ketju.get(k)/9*9;
                            if (kaikkiMahdolliset.get(kohde1).contains(numero)) {
                                estoLista.get(kohde1).add(numero);
                                paluu = true;
                            }
                            if (kaikkiMahdolliset.get(kohde2).contains(numero)) {
                                estoLista.get(kohde2).add(numero);
                                paluu = true;
                            }

                            if (samaRuutu(kohde1, ketju.get(0))) {
                                int rY = ketju.get(0)/9/3*3;
                                int rX = ketju.get(0)%9/3*3;
                                for (int ryy = rY; ryy< rY+3 ; ryy++) {
                                    for (int rxx = rX; rxx< rX+3 ; rxx++) {
                                        if (yhteys(ryy*9+rxx, ketju.get(k))) {
                                            //System.out.println("XYCHAIN ruutu " +(ryy*9+rxx) +" " +ketju.get(k) +" "+numero);
                                            if (kaikkiMahdolliset.get(ryy*9+rxx).contains(numero)) {
                                                estoLista.get(ryy*9+rxx).add(numero);
                                                paluu = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (samaRuutu(kohde2, ketju.get(0))) {
                                int rY = ketju.get(0)/9/3*3;
                                int rX = ketju.get(0)%9/3*3;
                                for (int ryy = rY; ryy< rY+3 ; ryy++) {
                                    for (int rxx = rX; rxx< rX+3 ; rxx++) {
                                        if (yhteys(ryy*9+rxx, ketju.get(k))) {
                                            //System.out.println("XYCHAIN r2uutu " +(ryy*9+rxx) +" " +ketju.get(k) +" "+numero);
                                            if (kaikkiMahdolliset.get(ryy*9+rxx).contains(numero)) {
                                                estoLista.get(ryy*9+rxx).add(numero);
                                                paluu = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (samaRuutu(kohde1, ketju.get(k))) {
                                int rY = ketju.get(k)/9/3*3;
                                int rX = ketju.get(k)%9/3*3;
                                for (int ryy = rY; ryy< rY+3 ; ryy++) {
                                    for (int rxx = rX; rxx< rX+3 ; rxx++) {
                                        if (yhteys(ryy*9+rxx, ketju.get(0))) {
                                            //System.out.println("XYCHAIN r3uutu " +(ryy*9+rxx) +" " +ketju.get(0) +" "+numero);
                                            if (kaikkiMahdolliset.get(ryy*9+rxx).contains(numero)) {
                                                estoLista.get(ryy*9+rxx).add(numero);
                                                paluu = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (samaRuutu(kohde2, ketju.get(k))) {
                                int rY = ketju.get(k)/9/3*3;
                                int rX = ketju.get(k)%9/3*3;
                                for (int ryy = rY; ryy< rY+3 ; ryy++) {
                                    for (int rxx = rX; rxx< rX+3 ; rxx++) {
                                        if (yhteys(ryy*9+rxx, ketju.get(0))) {
                                            //System.out.println("XYCHAIN r4uutu " +(ryy*9+rxx) +" " +ketju.get(0) +" "+numero);
                                            if (kaikkiMahdolliset.get(ryy*9+rxx).contains(numero)) {
                                                estoLista.get(ryy*9+rxx).add(numero);
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

    private static boolean xyzWing(int[][] sudoku) {
        boolean paluu = false;
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        ArrayList<Integer> parit = new ArrayList<Integer>();
        ArrayList<Integer> triplat = new ArrayList<Integer>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                mahdolliset.add(ts);
                if (ts.size() == 2) {
                   parit.add(y*9+x);
                }
                if (ts.size() == 3) {
                    triplat.add(y*9+x);
                }
            }
        } 
        int psize = parit.size();
        for (int tripla : triplat) {
            TreeSet<Integer> trio = new TreeSet<Integer>();
            trio.addAll(mahdolliset.get(tripla));
            for ( int pari1 = 0 ; pari1 < psize-1 ; pari1++) {
                TreeSet<Integer> ps1 = new TreeSet<Integer>();
                ps1.addAll(mahdolliset.get(parit.get(pari1)));
                for ( int pari2 = pari1+1 ; pari2 < psize ; pari2++) {
                    TreeSet<Integer> ps2 = new TreeSet<Integer>();
                    ps2.addAll(mahdolliset.get(parit.get(pari2)));
                    TreeSet<Integer> parityhdessa = new TreeSet<Integer>();
                    parityhdessa.addAll(ps1);
                    parityhdessa.addAll(ps2);
                    if (parityhdessa.size() == 3) {
                        TreeSet<Integer> onkoSamat = new TreeSet<Integer>();
                        onkoSamat.addAll(trio);
                        onkoSamat.removeAll(parityhdessa);
                        if (onkoSamat.isEmpty()) {
                            int numero = 0;
                            for (int n : ps1) {
                                if (ps2.contains(n)) {
                                    numero = n;
                                }
                            }
                            //System.out.println("xyz-wing " +numero+ " "+tripla +" "+ parit.get(pari1)+ " " + parit.get(pari2));
                            if (yhteys(tripla,parit.get(pari1)) && yhteys(tripla,parit.get(pari2)) && !yhteys(parit.get(pari1),parit.get(pari2))) {
                                //System.out.println("xyz-wing Toteutuu " +numero+ " "+tripla +" "+ parit.get(pari1)+ " " + parit.get(pari2));
                                int ruutuY = tripla/9/3*3;
                                int ruutuX = tripla%9/3*3;
                                for (int ry = ruutuY ; ry < ruutuY+3 ; ry++) {
                                    for (int rx = ruutuX ; rx < ruutuX+3 ; rx++) {
                                        if (sudoku[(ry*9+rx)/9][(ry*9+rx)%9] == 0)
                                        if (yhteys(parit.get(pari1),(ry*9+rx)) && yhteys(parit.get(pari2),(ry*9+rx))) {
                                            if ((ry*9+rx) != tripla && (ry*9+rx) != parit.get(pari1) && (ry*9+rx) != parit.get(pari2)) {
                                                //System.out.println("Esta xyzWing " + (ry*9+rx) + " " +numero);
                                                if (mahdolliset.get((ry*9+rx)).contains(numero)) {
                                                    estoLista.get((ry*9+rx)).add(numero);
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
        }
        return paluu;
    }

    private static boolean xyWing(int[][] sudoku) {
        boolean paluu = false;
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        ArrayList<TreeSet<Integer>> kaikkiMahdolliset = new ArrayList<TreeSet<Integer>>();
        ArrayList<Integer> kaksiot = new ArrayList<Integer>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9 ; x++) {
                TreeSet<Integer> ts = vaihtoehdot(y, x, sudoku);
                kaikkiMahdolliset.add(ts);
                if (ts.size() == 2) {
                   mahdolliset.add(ts);
                   kaksiot.add(y*9+x);
                } else {
                    mahdolliset.add(new TreeSet<Integer>());
                }
            }
        } 
        //tulostaVaihtoehdot(sudoku);
        if (kaksiot.size() < 3) return false;
        for (int ai = 0 ; ai < kaksiot.size()-2 ; ai++) {
            HashSet<Integer> summa = new HashSet<Integer>();
            int a = kaksiot.get(ai);
            for (int bi = ai+1 ; bi < kaksiot.size()-1 ; bi++) {
                int b = kaksiot.get(bi);
                int abyhteys = 0;
                if (yhteys(a,b)) abyhteys = 1;
                summa.clear();
                summa.addAll(mahdolliset.get(a));
                summa.addAll(mahdolliset.get(b));
                if (summa.size() != 3) continue;
                for (int ci = bi+1 ; ci < kaksiot.size() ; ci++) {
                    int c = kaksiot.get(ci);
                    
                    int yhteyksia = abyhteys;
                    if (yhteys(a,c)) yhteyksia++;
                    if (yhteys(c,b)) yhteyksia++;
                    if (yhteyksia != 2) continue;

                    summa.clear();
                    summa.addAll(mahdolliset.get(a));
                    summa.addAll(mahdolliset.get(b));
                    summa.addAll(mahdolliset.get(c));
                    if (summa.size() != 3) continue;

                    summa.clear();
                    summa.addAll(mahdolliset.get(a));
                    summa.addAll(mahdolliset.get(c));
                    if (summa.size() != 3) continue;

                    summa.clear();
                    summa.addAll(mahdolliset.get(b));
                    summa.addAll(mahdolliset.get(c));
                    if (summa.size() != 3) continue;

                    // System.out.println("Mahollinen xywing "+a+" "+b+" "+c);
                    if ((yhteys(a,b)) && (yhteys(a,c))) {

                        HashSet<Integer> numerot = new HashSet<>();
                        numerot.addAll(mahdolliset.get(b));
                        numerot.removeAll(mahdolliset.get(a));
                        int yhteinenNumero = (int)numerot.toArray()[0];
                        //System.out.println(" Esta " + (b/9*9+c%9) +" "+(c/9*9+b%9) +" "+ yhteinenNumero);
                        if (kaikkiMahdolliset.get(b/9*9+c%9).contains(yhteinenNumero)) {
                            estoLista.get(b/9*9+c%9).add(yhteinenNumero);
                            paluu = true;
                        }
                        if (kaikkiMahdolliset.get(c/9*9+b%9).contains(yhteinenNumero)) {
                            estoLista.get(c/9*9+b%9).add(yhteinenNumero);
                            paluu = true;
                        }
                        if (samaRuutu(a, b)) {
                            //System.out.println(" xywing ruutu");
                            if (samaRivi(a, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (b/9*9 +c%9/3*3+i) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get(b/9*9 + c%9/3*3+i).contains(yhteinenNumero)) {
                                        estoLista.get(b/9*9 + c%9/3*3+i).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            } else if (samaSarake(a, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta a" + ((c/9/3*3+i)*9 +b%9) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get((c/9/3*3+i)*9 +b%9).contains(yhteinenNumero)) {
                                        estoLista.get((c/9/3*3+i)*9 +b%9).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                        if (samaRuutu(a, c)) {
                            //System.out.println(" xywing ruutu");
                            if (samaRivi(a, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (c/9*9 +b%9/3*3+i) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get(c/9*9 +b%9/3*3+i).contains(yhteinenNumero)) {
                                        estoLista.get(c/9*9 +b%9/3*3+i).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            } else if (samaSarake(a, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta b" + ((b/9/3*3+i)*9 +c%9) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get((b/9/3*3+i)*9 +c%9).contains(yhteinenNumero)) {
                                        estoLista.get((b/9/3*3+i)*9 +c%9).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            }
                        }

                    }

                    if ((yhteys(b,a)) && (yhteys(b,c))) {
                        HashSet<Integer> numerot = new HashSet<>();
                        numerot.addAll(mahdolliset.get(a));
                        numerot.removeAll(mahdolliset.get(b));
                        int yhteinenNumero = (int)numerot.toArray()[0];
                        //System.out.println(" Esta " + (c/9*9+a%9) +" "+(a/9*9+c%9) +" "+ yhteinenNumero);
                        if (kaikkiMahdolliset.get(c/9*9+a%9).contains(yhteinenNumero)) {
                            estoLista.get(c/9*9+a%9).add(yhteinenNumero);
                            paluu = true;
                        }
                        if (kaikkiMahdolliset.get(a/9*9+c%9).contains(yhteinenNumero)) {
                            estoLista.get(a/9*9+c%9).add(yhteinenNumero);
                            paluu = true;
                        }

                        if (samaRuutu(b, a)) {
                            if (samaRivi(b, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (a/9*9 +c%9/3*3+i) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get(a/9*9 +c%9/3*3+i).contains(yhteinenNumero)) {
                                        estoLista.get(a/9*9 +c%9/3*3+i).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            } else if (samaSarake(b, c)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta c" + ((c/9/3*3+i)*9 +a%9) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get((c/9/3*3+i)*9 +a%9).contains(yhteinenNumero)) {
                                        estoLista.get((c/9/3*3+i)*9 +a%9).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                        if (samaRuutu(b, c)) {
                            if (samaRivi(b, a)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (c/9*9 + a%9/3*3+i) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get(c/9*9 + a%9/3*3+i).contains(yhteinenNumero)) {
                                        estoLista.get(c/9*9 + a%9/3*3+i).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            } else if (samaSarake(b, a)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta d" + ((a/9/3*3+i)*9 + c%9) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get((a/9/3*3+i)*9 + c%9).contains(yhteinenNumero)) {
                                        estoLista.get((a/9/3*3+i)*9 + c%9).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                    }
                    if ((yhteys(c,a)) && (yhteys(c,b))) {
                        HashSet<Integer> numerot = new HashSet<>();
                        numerot.addAll(mahdolliset.get(a));
                        numerot.removeAll(mahdolliset.get(c));
                        int yhteinenNumero = (int)numerot.toArray()[0];
                        //System.out.println(" Esta " + (b/9*9+a%9) +" "+(a/9*9+b%9) +" "+ yhteinenNumero);
                        if (kaikkiMahdolliset.get(b/9*9+a%9).contains(yhteinenNumero)) {
                            estoLista.get(b/9*9+a%9).add(yhteinenNumero);
                            paluu = true;
                        }
                        if (kaikkiMahdolliset.get(a/9*9+b%9).contains(yhteinenNumero)) {
                            estoLista.get(a/9*9+b%9).add(yhteinenNumero);
                            paluu = true;
                        }
                        if (samaRuutu(c, a)) {
                            if (samaRivi(c, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (a/9*9 + b%9/3*3+i) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get(a/9*9 + b%9/3*3+i).contains(yhteinenNumero)) {
                                        estoLista.get(a/9*9 + b%9/3*3+i).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            } else if (samaSarake(c, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta e" + ((b/9/3*3+i)*9 + a%9) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get((b/9/3*3+i)*9 + a%9).contains(yhteinenNumero)) {
                                        estoLista.get((b/9/3*3+i)*9 + a%9).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            }
                        }
                        if (samaRuutu(c, b)) {
                            if (samaRivi(c, a)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta " + (c/9*9 + c%9/3*3+i) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get(b/9*9 + a%9/3*3+i).contains(yhteinenNumero)) {
                                        estoLista.get(b/9*9 + a%9/3*3+i).add(yhteinenNumero);
                                        paluu = true;
                                    }
                                }
                            } else if (samaSarake(c, b)) {
                                for (int i = 0 ; i < 3 ; i++) {
                                    //System.out.println(" Esta f" + ((a/9/3*3+i)*9 + c%9) +" "+ yhteinenNumero);
                                    if (kaikkiMahdolliset.get((a/9/3*3+i)*9 + b%9).contains(yhteinenNumero)) {
                                        estoLista.get((a/9/3*3+i)*9 + b%9).add(yhteinenNumero);
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
                        HashSet<Integer> apu = new HashSet<Integer>();
                        //TreeSet<Integer> apuNaku = new TreeSet<Integer>();
                        TreeSet<Integer> temp = mahdolliset.get(rivi*9+sarake);
                        //apu sisaltaa jonkun ts:sta

                        if (!temp.isEmpty()) {
                            apu.addAll(temp);
                            //System.out.println("XX " +apu.toString()+ts.toString());
                            apu.removeAll(poisto);
                             if (!apu.isEmpty()) {
                                 loytynytpiilo++;
                                 loytyneetpiilopaikat.add(sarake);
                             }
                        }
                    }
                    if (loytynytpiilo == montako) {
                        HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
                        estot.removeAll(ts);
                        for (int r : loytyneetpiilopaikat) {
                            if (!Collections.disjoint(mahdolliset.get(9*rivi+r),estot)) {
                                //System.out.println(montako+" PiiloR "+(9*rivi+r)+" "+estot.toString()+mahdolliset.get(9*rivi+r));
                                estoLista.get(9*rivi+r).addAll(estot);
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
                HashSet<Integer> ruudut = new HashSet<Integer>();
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(rivi*9+i).size() != 0 && mahdolliset.get(rivi*9+i).size() <= montako ){
                        ruudut.add(i);
                    }
                }
                if (ruudut.size() >= montako) {
                    Integer kokolista [] = ruudut.toArray(new Integer[ruudut.size()]);
                    int lista[] = new int[ruudut.size()];
                    for (int i = 0 ; i < ruudut.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<TreeSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                    //System.out.println(""+hakuLista.toString());
                    for (TreeSet<Integer> ts : hakuLista) {
                        HashSet<Integer> nakumonikko = new HashSet<Integer>();
                        for (int c : ts) {
                            nakumonikko.addAll(mahdolliset.get(rivi*9+c));
                        }
                        if (nakumonikko.size() == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                            estot.removeAll(ts);
                            //System.out.println(loytynyt+" "+rivi+" "+estot.toString()+ts.toString());
                            for (int s : estot) {
                                if (sudoku[rivi][s] == 0) {
                                    for (int luku : nakumonikko) {
                                        if (mahdolliset.get(9*rivi+s).contains(luku)) {
                                            //System.out.println(montako+" EstaR " +(9*rivi+s)+" "+(luku));
                                            estoLista.get(9*rivi+s).add(luku);
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
                            apu.addAll(temp);
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
                            if (!Collections.disjoint(mahdolliset.get(9*r+sarake),estot)) {
                                //System.out.println(montako+" PiiloS "+(9*r + sarake)+" "+estot.toString()+mahdolliset.get(9*r+sarake));
                                estoLista.get(9*r + sarake).addAll(estot);
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
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int montako = 2 ; montako < 5 ; montako++) {
            for (int sarake = 0; sarake < 9; sarake++) {
                HashSet<Integer> ruudut = new HashSet<Integer>();
                for (int i = 0 ; i < 9 ; i++) {
                    if (mahdolliset.get(i*9+sarake).size() != 0 && mahdolliset.get(i*9+sarake).size() <= montako ){
                        ruudut.add(i);
                    }
                }
                if (ruudut.size() >= montako) {
                    Integer kokolista [] = ruudut.toArray(new Integer[ruudut.size()]);
                    int lista[] = new int[ruudut.size()];
                    for (int i = 0 ; i < ruudut.size(); i++ ) {
                        lista[i] = kokolista[i];
                    } 
                    int vaihtoehto[] = new int[montako];
                    hakuLista = new ArrayList<TreeSet<Integer>>();
                    combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                    for (TreeSet<Integer> ts : hakuLista) {
                        HashSet<Integer> nakumonikko = new HashSet<Integer>();
                        for (int c : ts) {
                            nakumonikko.addAll(mahdolliset.get(c*9+sarake));
                        }
                        if (nakumonikko.size() == montako) {
                            HashSet<Integer> estot = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                            estot.removeAll(ts);
                            for (int r : estot) {
                                if (sudoku[r][sarake] == 0) {
                                    for (int luku : nakumonikko) {
                                        if (mahdolliset.get(9*r+sarake).contains(luku)) {
                                            //System.out.println(montako+" EstaS " +(9*r+sarake)+" "+(luku));
                                            estoLista.get(9*r+sarake).add(luku);
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
                        rivinarvot.addAll(mahdolliset.get(9*y+x));
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
                                    apu.addAll(temp);
                                    apu.removeAll(poisto);
                                    if (!apu.isEmpty()) {
                                        loytynyt++;
                                        loytyneetpaikat.add(y*3+x);
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
                                if (!Collections.disjoint(mahdolliset.get(9*(rivi+r/3)+sarake+r%3),estot)) {
                                    //System.out.println(montako+" PiiloX "+(9*(rivi+r/3)+sarake+r%3)+" "+estot.toString()+mahdolliset.get(9*(rivi+r/3)+sarake+r%3));
                                    estoLista.get(9*(rivi+r/3)+sarake+r%3).addAll(estot);
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
                    HashSet<Integer> ruudut = new HashSet<Integer>();
                    HashSet<Integer> alue = new HashSet<Integer>();
                    
                    for (int y = vaaka ; y < vaaka+3; y++) {
                        for (int x = pysty ; x < pysty+3; x++) {
                            if (mahdolliset.get(y*9+x).size() != 0 && mahdolliset.get(y*9+x).size() <= montako ){
                                ruudut.add(y*9+x);
                            }    
                            alue.add(y*9+x);
                        }
                    }

                    if (ruudut.size() >= montako) {
                        Integer kokolista [] = ruudut.toArray(new Integer[ruudut.size()]);
                        int lista[] = new int[ruudut.size()];
                        for (int i = 0 ; i < ruudut.size(); i++ ) {
                            lista[i] = kokolista[i];
                        } 
                        int vaihtoehto[] = new int[montako];
                        hakuLista = new ArrayList<TreeSet<Integer>>();
                        combinationUtil(lista, vaihtoehto, 0, ruudut.size()-1, 0, montako);
                        //System.out.println(montako+" "+hakuLista.toString());
                        for (TreeSet<Integer> ts : hakuLista) {
                            //System.out.println("ts " +ts);
                            HashSet<Integer> nakumonikko = new HashSet<Integer>();
                            for (int c : ts) {
                                nakumonikko.addAll(mahdolliset.get(c));
                            }
                            if (nakumonikko.size() == montako) {
                                //System.out.println(rivi+":"+sarake+"  "+montako+" "+nakumonikko+" "+ alue);
                                HashSet<Integer> estot = new HashSet<Integer>(alue);
                                estot.removeAll(ts);
                                for (int r : estot) {
                                    if (sudoku[r/9][r%9] == 0) {
                                        for (int luku : nakumonikko) {
                                            if (mahdolliset.get(r).contains(luku)) {
                                                //System.out.println(montako+" nakuMon EstaX " +r+" "+(luku));
                                                estoLista.get(r).add(luku);
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
   
    //Pointing Pairs, Pointing Triples
    private static boolean lukitseRivi(int[][] sudoku) {
        boolean paluu = false;
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
                for (int ruudunRivi = 0 ; ruudunRivi < 3 ; ruudunRivi++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sy = 0 ; sy < 3 ; sy++) {
                        for (int sx = 0 ; sx < 3 ; sx++) {
                            if (sy == ruudunRivi) {
                                omat.addAll(mahdolliset.get((ruutuY+sy)*9 + ruutuX + sx));
                            } else {
                                muut.addAll(mahdolliset.get((ruutuY+sy)*9 + ruutuX + sx));
                            }
                        }
                    }
                    //System.out.println("Vaaka: "+ruutuY+":"+ruutuX+" " +ruudunRivi +" - " + omat.toString()+muut.toString());
                   
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int y = ruutuY+ruudunRivi;
                        for (int luku : omat) {
                            for (int x = 0 ; x < 9; x++) {
                                if ( x < ruutuX || x >= ruutuX+3 ) {
                                    if (mahdolliset.get(y*9+x).contains(luku)) {
                                        //System.out.println("LukitseRivi " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
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
        for (int ruutuY = 0 ; ruutuY < 9 ; ruutuY=ruutuY+3) {
            for (int ruutuX = 0 ; ruutuX < 9 ; ruutuX=ruutuX+3) {
                for (int ruudunSarake = 0 ; ruudunSarake < 3 ; ruudunSarake++) {
                    HashSet<Integer> muut = new HashSet<Integer>();
                    HashSet<Integer> omat = new HashSet<Integer>();
                    for (int sy = 0 ; sy < 3 ; sy++) {
                        for (int sx = 0 ; sx < 3 ; sx++) {
                            if (sx == ruudunSarake) {
                                omat.addAll(mahdolliset.get((ruutuY+sy)*9 + ruutuX + sx));
                            } else {
                                muut.addAll(mahdolliset.get((ruutuY+sy)*9 + ruutuX + sx));
                            }
                        }
                    }
                    //System.out.println("Pysty: "+ruutuY+":"+ruutuX+" " +ruudunSarake +" - " + omat.toString()+muut.toString());
                    omat.removeAll(muut);
                    if (!omat.isEmpty()) {
                        int x = ruutuX+ruudunSarake;
                        for (int luku : omat) {
                            for (int y = 0 ; y < 9; y++) {
                                if ( y < ruutuY || y >= ruutuY+3 ) {
                                    if (mahdolliset.get(y*9+x).contains(luku)) {
                                        //System.out.println("LukitsePysty " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
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
                                    if (mahdolliset.get(y*9+x).contains(luku)) {
                                        //System.out.println("LukitseRuutu " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
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
                                    if (mahdolliset.get(y*9+x).contains(luku)) {
                                        //System.out.println("LukitseSarake " + (y*9+x) +" "+luku);   
                                        estoLista.get(y*9+x).add(luku);
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
    private static boolean nishio(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        HashMap<Integer,Integer> poistot = new HashMap<>();
        int [][] kopio = new int[9][9];
               
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int mahdollisuuksia = 3 ; mahdollisuuksia < 5 ; mahdollisuuksia++) {
            for (int i = 0 ; i < 81 ; i ++) {
                if (sudoku[i/9][i%9] == 0 && mahdolliset.get(i).size() == mahdollisuuksia) {
                    for (int n : mahdolliset.get(i)) {
                        for (int y = 0 ; y < 9 ; y++) {
                            for (int x = 0 ; x < 9; x++) {
                                kopio[y][x] = sudoku[y][x];
                            }
                        }
                        kopio[i/9][i%9] = n;
                        while (etsiVarmat(kopio)){};
                        if (sudokuValmis(kopio)) {
                            for (int y = 0 ; y < 9 ; y++) {
                                for (int x = 0 ; x < 9; x++) {
                                    sudoku[y][x] = kopio[y][x];
                                }
                            }
                            System.out.print("!");
                            //System.out.println("Nishio: touchdown " +i +" " +n);
                            return true;
                        }
                        for ( int a = 0; a < 81; a++) {
                            if (kopio[a/9][a%9] == 0) {
                                if (vaihtoehdot(a/9, a%9, kopio).isEmpty()) {
                                    System.out.print("N");
                                    estoLista.get(i).add(n);
                                    return true;
                                }
                            }
                        }
                    }
                    kopio[i/9][i%9] = 0; 
                }
            }
        }
        return false;
    }

    private static boolean exclusion(int[][] sudoku) {
        //tulostaVaihtoehdot(sudoku);
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        System.out.print("e");
        boolean paluu = false;
        int [][] sudokuA = new int[9][9];
        int [][] sudokuB = new int[9][9];
        
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        for (int i = 0 ; i < 81 ; i ++) {
            ArrayList<TreeSet<Integer>> mahdollisetA = new ArrayList<TreeSet<Integer>>();
            ArrayList<TreeSet<Integer>> mahdollisetB = new ArrayList<TreeSet<Integer>>();
            if (mahdolliset.get(i).size() == 2) {
                for (int y = 0 ; y < 9 ; y++) {
                    for (int x = 0 ; x < 9; x++) {
                        sudokuA[y][x] = sudoku[y][x];
                        sudokuB[y][x] = sudoku[y][x];
                    }
                }
                sudokuA[i/9][i%9] = (int)mahdolliset.get(i).toArray()[0];
                sudokuB[i/9][i%9] = (int)mahdolliset.get(i).toArray()[1];
                while (etsiVarmat(sudokuA)){};
                if (sudokuValmis(sudokuA)) {
                    for (int y = 0 ; y < 9 ; y++) {
                        for (int x = 0 ; x < 9; x++) {
                            sudoku[y][x] = sudokuA[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                while (etsiVarmat(sudokuB)){};
                if (sudokuValmis(sudokuB)) {
                    for (int y = 0 ; y < 9 ; y++) {
                        for (int x = 0 ; x < 9; x++) {
                            sudoku[y][x] = sudokuB[y][x];
                        }
                    }
                    System.out.print("!");
                    return true;
                }
                for (int y = 0 ; y < 9 ; y++) {
                    for (int x = 0 ; x < 9; x++) {
                        mahdollisetA.add(vaihtoehdot(y, x, sudokuA));
                        mahdollisetB.add(vaihtoehdot(y, x, sudokuB));
                    }
                }
                for ( int a = 0; a < 81; a++) {
                    for ( int m : mahdolliset.get(a)) {
                        if (sudokuA[a/9][a%9] != m && sudokuB[a/9][a%9] != m && !(mahdollisetA.get(a).contains(m) || mahdollisetB.get(a).contains(m))) {
                            //System.out.println(" Exclusion "+a+" + " +m);
                            paluu = true;
                            estoLista.get(a).add(m);
                        }
                    }
                    if (sudokuA[a/9][a%9] == 0 && mahdollisetA.get(a).isEmpty()) { // vaihtoehdot(a/9, a%9, sudokuA).isEmpty()) {
                        // System.out.println(" Exclusion A "+i+" + " +mahdolliset.get(i).toArray()[0]);
                        estoLista.get(i).add((int)mahdolliset.get(i).toArray()[0]);
                    }
                    if (sudokuB[a/9][a%9] == 0 && mahdollisetB.get(a).isEmpty()) { //vaihtoehdot(a/9, a%9, sudokuB).isEmpty()) {
                        // System.out.println(" Exclusion B "+i+" + " + mahdolliset.get(i).toArray()[1]);
                        estoLista.get(i).add((int)mahdolliset.get(i).toArray()[1]);
                    }
                }
                sudokuA[i/9][i%9] = 0; 
                sudokuB[i/9][i%9] = 0; 
                if (paluu) {
                    return paluu;
                }
            }
        }
        
        return false;
    }
    
    private static HashSet<Integer> vaakarivi(int rivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < 9; i++) {
            if (sudoku[rivi][i] != 0) hs.add(sudoku[rivi][i]);
        }
        return hs;
    }

    private static HashSet<Integer> pystyrivi(int rivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int i = 0; i < 9; i++) {
            if (sudoku[i][rivi] != 0) hs.add(sudoku[i][rivi]);
        }
        return hs;
    }

    private static HashSet<Integer> ruutu(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> hs = new HashSet<Integer>();
        int vaaka = (vaakarivi/3)*3;
        int pysty = (pystyrivi/3)*3;
        
        for (int y = vaaka ; y < vaaka+3; y++) {
            for (int x = pysty ; x < pysty+3; x++) {
                if (sudoku[y][x] != 0) hs.add(sudoku[y][x]);
            }
        }
        return hs;
    }
    
    private static int orpoVaakaRivi(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        for (int i = 0 ; i < 9 ; i++) {
            if (sudoku[vaakarivi][i] == 0) {
                if (i == pystyrivi) {
                    omat.addAll(vaihtoehdot(vaakarivi, i, sudoku));
                } else {
                    muut.addAll(vaihtoehdot(vaakarivi, i, sudoku));
                }
            }
        }
        //System.out.println("vaaka:" +omat.toString()+" "+muut.toString());
        omat.removeAll(muut);
        if (!omat.isEmpty()) {
            return (int) omat.toArray()[0];
        }
        return 0;
    }

    private static int orpoPystyRivi(int vaakarivi, int pystyrivi, int[][] sudoku) {
        HashSet<Integer> muut = new HashSet<Integer>();
        HashSet<Integer> omat = new HashSet<Integer>();
        for (int i = 0 ; i < 9 ; i++) {
            if (sudoku[i][pystyrivi] == 0) {
                if (i == vaakarivi) {
                    omat.addAll(vaihtoehdot(i, pystyrivi, sudoku));
                } else {
                    muut.addAll(vaihtoehdot(i, pystyrivi, sudoku));
                }
            }
            
        }
        //System.out.println("pysty:" +omat.toString()+" "+muut.toString());
        omat.removeAll(muut);
        if (!omat.isEmpty()) {
            return (int) omat.toArray()[0];
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
                    if ((y == vaakarivi) && (x == pystyrivi)) {
                        omat.addAll(vaihtoehdot(y, x, sudoku));
                    } else {
                        muut.addAll(vaihtoehdot(y, x, sudoku));
                    }
                }
            }
        }

        //System.out.println("ruutu:" +omat.toString()+" "+muut.toString());
        omat.removeAll(muut);
        if (!omat.isEmpty()) {
            return (int) omat.toArray()[0];
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
        if (sudoku[vaakarivi][pystyrivi] == 0) {
            laskuri++;
            ts.removeAll(vaakarivi(vaakarivi, sudoku));
            ts.removeAll(pystyrivi(pystyrivi, sudoku));
            ts.removeAll(ruutu(vaakarivi, pystyrivi, sudoku));
            ts.removeAll(estoLista.get(vaakarivi*9+pystyrivi));
        } else {
            return new TreeSet<Integer>();
        }
        if (ts.size() == 1) sudoku[vaakarivi][pystyrivi] = ts.first();
        return ts;
    }

    private static void tulostaSudoku(int[][] sudoku) {
        System.out.println();
        for (int i = 0; i < 9; i++) {
            System.out.print(" ");
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] != 0) {
                    System.out.print(" "+sudoku[i][j]);
                } else {
                    System.out.print(" .");
                }
                if ((j+1)%3==0) System.out.print("  ");
            }
            System.out.println();
            if ((i+1)%3==0) System.out.println();
        }
    }
    
    private static String haetulostus(int[][] sudoku, ArrayList<TreeSet<Integer>> mahdolliset , int rivi, int ruuturivi, int ruutu) {
        StringBuilder paluu = new StringBuilder();
        
            for (int luku = ruuturivi*3+1 ; luku < ruuturivi*3+3+1 ; luku++) {
                if (mahdolliset.get(rivi*9 + ruutu ).contains(luku)) {
                    paluu.append(luku);
                } else paluu.append(" ");
            }
        paluu.append(" ");
        
        if (mahdolliset.get(rivi*9 + ruutu ).isEmpty()) {
            paluu = new StringBuilder();
            if (ruuturivi==1) {
            paluu.append("(");
            paluu.append(sudoku[rivi][ruutu]);
            paluu.append(") ");
            } else paluu.append("    ");
        }
        
        return paluu.toString();
    }
    
    private static void tulostaVaihtoehdot(int[][] sudoku) {
        ArrayList<TreeSet<Integer>> mahdolliset = new ArrayList<TreeSet<Integer>>();
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                mahdolliset.add(vaihtoehdot(y, x, sudoku));
            }
        }
        System.out.println("-------------+-------------+-------------+");
        for (int rivi = 0 ; rivi < 9 ; rivi ++) {
            for (int ruuturivi = 0 ; ruuturivi < 3 ; ruuturivi ++) {
                System.out.print(" ");
                for (int ruutu = 0 ; ruutu < 9 ; ruutu ++) {
                    System.out.print(haetulostus(sudoku, mahdolliset, rivi, ruuturivi, ruutu )); 
                    if ((ruutu+1)%3==0) System.out.print("| ");
                }
                System.out.println(" ");
            }
            if ((rivi+1)%3 == 0) System.out.println("-------------+-------------+-------------+");
            
        }
    }

    public static boolean sudokuValmis(int[][] sudoku) {
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 if (sudoku[y][x] == 0) return false;
            }
        }
        return true;
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

    public static int vaihtoehtoja(int[][] sudoku) {
        int paluu = 0;
        for (int y = 0 ; y < 9 ; y++) {
            for (int x = 0 ; x < 9; x++) {
                 paluu += vaihtoehdot(y, x, sudoku).size();
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
                        //sudoku[y][x] = ts.first();
                        //System.out.println("valinta: " + y + " " +x+ " : "+ts.first());
                        loytyi = true;
                        continue;
                    } 
                    //System.out.println(y+" "+x+" "+vaihtoehdot(y, x, sudoku));
                    sudoku[y][x] = orpoVaakaRivi(y, x, sudoku);
                    if (sudoku[y][x] != 0) {
                        loytyi = true;
                        //System.out.println(y+" vaaka "+x+" "+sudoku[y][x]);
                        continue;
                    } 
                    sudoku[y][x] = orpoPystyRivi(y, x, sudoku);
                    if (sudoku[y][x] != 0) {
                        loytyi = true;
                        //System.out.println(y+" pysty "+x+" "+sudoku[y][x]);
                        continue;
                    } 
                    sudoku[y][x] = orpoRuutu(y, x, sudoku);
                    if (sudoku[y][x] != 0) {
                        loytyi = true;
                        //System.out.println(y+" ruutu "+x+" "+sudoku[y][x]);
                        continue;
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
        boolean jatko = true;
        lukitseRivi(sudoku);
        lukitseRuutu(sudoku);
        
        while (jatko) {
            jatko = false;
            //tulostaVaihtoehdot(sudoku);
            //System.out.print("<"+vaihtoehtoja(sudoku)+">");
            while (etsiVarmat(sudoku))  {
                System.out.print("*");
            }
            if (sudokuValmis(sudoku)) {
                //System.out.println("Adios Amigos ");
                return;
            }
            System.out.print("@");
                
            jatko = lukitseRivi(sudoku) || jatko;
            jatko = lukitseRuutu(sudoku) || jatko;
            jatko = nakuMonikotRivi(sudoku) || jatko;
            jatko = nakuMonikotSarake(sudoku) || jatko;
            jatko = nakuMonikotRuutu(sudoku) || jatko;

            jatko = jatko || xWingPysty(sudoku);
            jatko = jatko || xWingVaaka(sudoku);
            jatko = jatko || etaPari(sudoku);
            jatko = jatko || simpleColouring2(sudoku);
            jatko = jatko || simpleColouring(sudoku);

            jatko = jatko || swordfishVaaka(sudoku);
            jatko = jatko || swordfishPysty(sudoku);
            
            jatko = jatko || xCycles(sudoku);
            jatko = jatko || piiloMonikotRivi(2,sudoku);
            jatko = jatko || piiloMonikotSarake(2,sudoku);
            jatko = jatko || piiloMonikotRuutu(2, sudoku);
            
            jatko = jatko || xyWing(sudoku);
            jatko = jatko || xyzWing(sudoku);
            jatko = jatko || jellyfishVaaka(sudoku);
            jatko = jatko || jellyfishPysty(sudoku);
            
            jatko = jatko || piiloMonikotRivi(3, sudoku);
            jatko = jatko || piiloMonikotSarake(3,sudoku);
            jatko = jatko || piiloMonikotRuutu(3, sudoku);
            jatko = jatko || piiloMonikotSarake(4,sudoku);
            jatko = jatko || piiloMonikotRuutu(4, sudoku);
            jatko = jatko || piiloMonikotRivi(4, sudoku);

            jatko = jatko || xyChain(sudoku);
            jatko = jatko || bug(sudoku);
            jatko = jatko || uniqueRectangle(sudoku);
            jatko = jatko || exclusion(sudoku);
            jatko = jatko || nishio(sudoku);

            //tulostaVaihtoehdot(sudoku);
            //System.out.println(nollia(sudoku)+"/"+paikkoja(sudoku));
        }
        if (nollia(sudoku)!=0) {
            //tulostaVaihtoehdot(sudoku);
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
        System.out.println("\n"+s );
        if (s.length() < 81 ) {
            System.out.println("Virhe: liian pieni taulukko");
        } else {
            sudoku = parsiSisalto(s);
            tulostaSudoku(sudoku);
            if (!tuplat(sudoku)) {
                ratkaise(sudoku);
                System.out.println(" * "+ laskuri +" *");
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
                if ((s.startsWith("#")) || s.startsWith("!") || s.startsWith("@")) {
                    s = lukija.nextLine();
                } else {
                    sisalto.append(s);
                }
            }
        } catch (Exception e) {
          System.out.println("Virhe: " + e.getMessage());
        } finally {
            if (lukija !=null){lukija.close();}
        }
        
        return sisalto.toString();
    }
}

