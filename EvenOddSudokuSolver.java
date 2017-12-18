import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class EvenOddSudokuSolver {
    private HashSet<Integer> fullSet;
    private HashSet<Integer> oddSet;
    private HashSet<Integer> evenSet;
    private ArrayList<HashSet<Integer>> areas;
    
    public EvenOddSudokuSolver(HashSet<Integer> values) {
        areas = new ArrayList<HashSet<Integer>>();
        fullSet = new HashSet<Integer>();
        oddSet = new HashSet<Integer>();
        evenSet = new HashSet<Integer>();
        for (int a = 0 ; a < 2; a++) {
            HashSet<Integer> hs = new HashSet<Integer>();
            areas.add(hs);
        }
        for (int i : values) {
            fullSet.add(i);
            if (i%2 == 0) evenSet.add(i);
            else oddSet.add(i); 
        }
    }

    private void parseMap(String s) {
        s = s.replaceAll("\\s+", "");
        s = s.replaceAll(":", "");
        int cell = 0;
        String oddEven = "oe";
        for (Character c : s.toCharArray()) {
            if (oddEven.indexOf(c+"") > -1) areas.get(oddEven.indexOf(c+"")).add(cell);
            cell++;
        }
    }

    public HashSet<Integer> getValues(Integer []s, int r, int c, int box, int bw, int sh, int sw) {
        HashSet<Integer> values = new HashSet<Integer>();
        values.addAll(fullSet);
        int cell = r*bw + c;
        if (areas.get(0).contains(cell)) values.removeAll(evenSet);
        else if (areas.get(1).contains(cell)) values.removeAll(oddSet);
        int boxRow = box/sh * sh, boxColumn = box %  sh * sw;
        for (int rowOffset=0; rowOffset<sh; rowOffset++) {
            for (int columnOffset=0; columnOffset<sw; columnOffset++) {
                values.remove(s[(boxRow+rowOffset)*bw + boxColumn+columnOffset]);
            }
        }
        for (int row=0; row < bw; row++) values.remove(s[row*bw + c]);
        for (int column=0; column < bw; column++) values.remove(s[r*bw + column]);
        return values;
    }

    private boolean findSolution(Integer[] s, int index, int bw, int sh, int sw) {
        int y = index / bw;
        int x = index % bw;
        int box = y/sh*sh + x/sw;
        if (index == s.length ) return true;
        
        if (s[index] !=0 ) return findSolution(s, index+1, bw , sh ,sw);
        HashSet<Integer> optionsForCell = getValues(s, y, x, box, bw, sh, sw);
        for (int option : optionsForCell) {
            s[index] = option;
            if (findSolution(s, index+1, bw, sh, sw)) return true;
            else s[index] = 0;
        }
        return false;
    }

    public String evenOddPattern(SudokuBoard board) {
        StringBuilder patternAsString = new StringBuilder();
        patternAsString.append("\n  ");
        String area="";
        for (int i = 0 ; i < board.getBoardSize(); i++) {
            if (i > 0 && i % board.getBoxWidth() == 0) patternAsString.append("  ");
            if (i > 0 && i % board.getBoardWidth() == 0) patternAsString.append("\n  ");
            if (i > 0 && i % board.getBoardWidth() == 0 && i % (board.getBoxHeigth()*board.getBoardWidth()) == 0 && board.getBoxHeigth() > 1) patternAsString.append("\n  ");
            area =".";
            if (areas.get(0).contains(i)) area ="o";
            if (areas.get(1).contains(i)) area ="e";
            patternAsString.append(area);
        }
        patternAsString.append("\n");
        return patternAsString.toString();
    }

    public static void main(String[] args)  {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line);
        SudokuSolver solver = new SudokuSolver(board.values);
        EvenOddSudokuSolver evenoddSolver = new EvenOddSudokuSolver(board.values);
        evenoddSolver.parseMap(board.getSudokuPattern());
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        for (int cell = 0 ; cell < board.getBoardSize(); cell++) {
            if (evenoddSolver.areas.get(0).contains(cell)) board.removeOptions(cell, evenoddSolver.evenSet);
            else if (evenoddSolver.areas.get(1).contains(cell)) board.removeOptions(cell, evenoddSolver.oddSet);
        }
        System.out.println("\n" + evenoddSolver.evenOddPattern(board));
        board.setExLimit(board.getBoardSize());
        solver.solve(board);
        if (board.getFreeSlots().size() > 0) {
            System.out.print("x");
            if (board.validateSudoku2()) {
                evenoddSolver.findSolution(board.getBoard(), 0, board.getBoardWidth(), board.getBoxHeigth(), board.getBoxWidth()); 
            }
        }
        System.out.println("\n\n" + board.dumpBoard());
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}