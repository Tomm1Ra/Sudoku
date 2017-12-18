import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class JigsawSudokuSolver {
    private Utilities utilities;
    private HashSet<Integer> fullSet;
    private ArrayList<HashSet<Integer>> jAreas;
    
    public JigsawSudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
        this.fullSet = values;
        jAreas = new ArrayList<HashSet<Integer>>();
        for (int a = 0 ; a < values.size(); a++) {
             HashSet<Integer> hs = new HashSet<Integer>();
             jAreas.add(hs);
        }
    }

    private void initOptions(SudokuBoard board) {
        for (int i = 0 ; i < board.getBoardSize(); i++) {
            board.setOptions(i,fullSet);
        }
        for (int i = 0 ; i < board.getBoardSize(); i++) {
            if (board.isFilled(i)) {
                int targetArea = 999;
                for (int a = 0 ; a < jAreas.size(); a++) {
                    if (jAreas.get(a).contains(i)) {targetArea = a;break;}
                }
                board.setCellValue(i, board.getCellValue(i), jAreas.get(targetArea));
            } 
        }
    }

    public HashSet<Integer> getValues(Integer []s, int r, int c, int bw) {
        HashSet<Integer> values = new HashSet<Integer>();
        values.addAll(this.fullSet);
        for (int row=0; row < bw; row++) values.remove(s[row*bw + c]);
        for (int column=0; column < bw; column++) values.remove(s[r*bw + column]);
        int cell = r*bw + c;
        int targetArea = 999;
        for (int a = 0 ; a < jAreas.size(); a++) {
            if (jAreas.get(a).contains(cell)) {targetArea = a;break;}
        }
        for (int b : jAreas.get(targetArea)) {
            values.remove(s[b]);
        }
        return values;
    }

    public void parseJigsaw(String s, int size) {
        s = s.replaceAll("\\s+", "");
        s = s.replaceAll(":", "");
        int cell = 0;
        String jCells = "abcdefghijklmnopqrstuvwxyz";
        for (Character c : s.toCharArray()) {
            if (jCells.indexOf(c+"") >= size || jCells.indexOf(c+"") < 0) {
                System.out.println("Incorrect jigsaw area " + c);
                System.exit(0);
            }
            jAreas.get(jCells.indexOf(c+"")).add(cell++);
        }
        if (jAreas.size() != size) {System.out.println("Incorrect number of jigsaw areas " + jAreas.size()); System.exit(0);}
        for (int i= 0 ; i < jAreas.size() ; i++) {
            if (jAreas.get(i).size() != size) {
                String s2 = jCells.substring(i,i+1);
                System.out.println("Incorrect jigsaw area " + s2 + " size "+jAreas.get(i).size()); 
                System.exit(0);
            }
        }
    }

    public void printJigsawPattern(SudokuBoard board) {
        String aCells = "abcdefghijklmnopqrstuvwxyz";
        System.out.println("\n Jigsaw pattern:");
        for (int i = 0 ; i < board.getBoardSize(); i++) {
            if (i % board.getBoardWidth() == 0) System.out.print("\n  ");
            for (int n = 0; n < jAreas.size(); n++) {
                if (jAreas.get(n).contains(i)) {
                    System.out.print (aCells.substring(n, n+1));
                }
            }
        }
        System.out.println("\n");
    }

    private boolean findSolution(Integer[] s, int index, int bw) {
        int y = index / bw;
        int x = index % bw;
        //int box = y/sh*sh + x/sw;
        if (index == s.length ) return true;
        
        if (s[index] !=0 ) return findSolution(s, index+1, bw);
        HashSet<Integer> optionsForCell = getValues(s, y, x, bw);
        for (int option : optionsForCell) {
            s[index] = option;
            if (findSolution(s, index+1, bw)) return true;
            else s[index] = 0;
        }
        return false;
    } 

    private boolean hiddenSinglesRow(SudokuBoard board) {
        boolean returnValue = false;
        for (int row = 0; row < board.getBoardWidth(); row++) {
            int[] counts = new int[board.getBoardWidth()+1];
            int[] cell = new int[board.getBoardWidth()+1];
            for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                for (int j : board.getOptions(row*board.getBoardWidth()+i)) {
                    counts[j]++;
                    cell[j] = i;
                }
            }
            for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                if (counts[i] == 1) {
                    returnValue = true;
                    int targetArea = 999;
                    for (int a = 0 ; a < jAreas.size(); a++) {
                        if (jAreas.get(a).contains(row*board.getBoardWidth()+cell[i])) {targetArea = a;break;}
                    }
                    board.setCellValue(row*board.getBoardWidth()+cell[i], i, jAreas.get(targetArea));
                    board.removeFreeSlots(row*board.getBoardWidth()+cell[i]);
                }
            }
        }
        return returnValue;
    }

    private boolean hiddenSinglesCol(SudokuBoard board) {
        boolean returnValue = false;
        for (int col = 0; col < board.getBoardWidth(); col++) {
            int[] counts = new int[board.getBoardWidth()+1];
            int[] cell = new int[board.getBoardWidth()+1];
            for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                for (int j : board.getOptions(board.getBoardWidth()*i+col)) {
                    counts[j]++;
                    cell[j] = i;
                }
            }
            for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                if (counts[i] == 1) {
                    returnValue = true;
                    int targetArea = 999;
                    for (int a = 0 ; a < jAreas.size(); a++) {
                        if (jAreas.get(a).contains(cell[i]*board.getBoardWidth()+col)) {targetArea = a;break;}
                    }
                    board.setCellValue(cell[i]*board.getBoardWidth()+col, i, jAreas.get(targetArea));
                    board.removeFreeSlots(cell[i]*board.getBoardWidth()+col);
                }
            }
        }
        return returnValue;
    }

    private boolean hiddenSinglesBox(SudokuBoard board) {
        boolean returnValue = false;
        for (int area = 0; area < board.getBoardWidth(); area++) {
            int[] counts = new int[board.getBoardWidth()+1];
            int[] cell = new int[board.getBoardWidth()+1];
            for (int c : jAreas.get(area)) {
                for (int j : board.getOptions(c)) {
                    counts[j]++;
                    cell[j] = c;
                }
            }
            for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                if (counts[i] == 1) {
                    returnValue = true;
                    int targetArea = 999;
                    for (int a = 0 ; a < jAreas.size(); a++) {
                        if (jAreas.get(a).contains(cell[i])) {targetArea = a;break;}
                    }
                    board.setCellValue(cell[i], i, jAreas.get(targetArea));
                    board.removeFreeSlots(cell[i]);
                }
            }
        }
        return returnValue;
    }

    public boolean findSingles(SudokuBoard board) { 
        boolean returnValue = false;
        HashSet<Integer> free = new HashSet<Integer>();
        for (int slot : board.getFreeSlots()) {
            if (!board.isFilled(slot)) {
                //HashSet<Integer> hs = getValues(board.getBoard(), slot/board.getBoardWidth(), slot%board.getBoardWidth(), board.getBoardWidth());
                HashSet<Integer> hs = board.getOptions(slot);
                if (hs.size() == 1) {
                    int targetArea = 999;
                    for (int a = 0 ; a < jAreas.size(); a++) {
                        if (jAreas.get(a).contains(slot)) {targetArea = a;break;}
                    }
                    board.setCellValue(slot, (int)hs.toArray()[0], jAreas.get(targetArea));
                    returnValue = true;
                    free.add(slot);
                }
            }
        }
        board.removeFreeSlots(free);
        returnValue = hiddenSinglesRow(board) || returnValue;
        returnValue = hiddenSinglesCol(board) || returnValue;
        returnValue = hiddenSinglesBox(board) || returnValue;

        return returnValue;
    }

    public static void main(String[] args)  {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line);
        SudokuSolver solver = new SudokuSolver(board.values);
        JigsawSudokuSolver jigsawSudokuSolver = new JigsawSudokuSolver(board.values);
        board.setBoxHeigth(board.getBoardWidth());
        board.setBoxWidth(board.getBoardWidth());
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        jigsawSudokuSolver.parseJigsaw(board.getSudokuPattern(), board.getBoardWidth());
        jigsawSudokuSolver.initOptions(board);
        jigsawSudokuSolver.printJigsawPattern(board);
        boolean cont = true;
        int counter = 0;
        while (cont) {
            cont = false;
            while (jigsawSudokuSolver.findSingles(board)) {System.out.print("*");}
            cont = solver.nakedRow(board, 2,3) || cont;
            cont = solver.nakedColumn(board, 2,3) || cont;
            cont = solver.hiddenRow(board, 2,3) || cont;
            cont = solver.hiddenColumn(board, 2,3) || cont;
            System.out.print("*"+board.getFreeSlots().size());
        }
        if (board.getFreeSlots().size() > 0) {
            System.out.print("x");
            jigsawSudokuSolver.findSolution(board.getBoard(), 0, board.getBoardWidth()); 
        }
        System.out.println("\n\n" + board.dumpBoard());
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}