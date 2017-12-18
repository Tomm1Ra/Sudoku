import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class AntiKingSudokuSolver {
    private Utilities utilities;
    
    public AntiKingSudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
    }

    public HashSet<Integer> getValues(Integer []s, int r, int c, int box, int bw, int sh, int sw) {
        HashSet<Integer> values = utilities.getValues(s, r, c, box, bw, sh, sw);
        int cell = r*bw + c;
        for (int n : utilities.getKingArea(cell, bw)) {
            values.remove(s[n]);
        }
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

    public static void main(String[] args)  {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line);
        SudokuSolver solver = new SudokuSolver(board.values);
        AntiKingSudokuSolver antiKingSolver = new AntiKingSudokuSolver(board.values);
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        
        board.setBlockArea(1);
        for (int n = 0 ; n < board.getBoardSize() ; n++) {
            if (board.isFilled(n)) board.setCellValue(n,board.getCellValue(n));
        }
        solver.solve(board);
        if (board.getFreeSlots().size() > 0) {
            System.out.print("x");
            if (board.validateSudoku2()) {
                antiKingSolver.findSolution(board.getBoard(), 0, board.getBoardWidth(), board.getBoxHeigth(), board.getBoxWidth()); 
            } 
        }
        System.out.println("\n\n" + board.dumpBoard());
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}