import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class HyperSudokuSolver {
    private Utilities utilities;
    private ArrayList<HashSet<Integer>> areaSets;
    
    public HyperSudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
    }

    private void setAreas(int size) {
        int step = (int)Math.sqrt(size) + 1;
        int boxSize  = (int)Math.sqrt(size);
        areaSets = new ArrayList<HashSet<Integer>>();
        for (int y = 1 ; y < size ; y=y+step) {
            for (int x = 1 ; x < size ; x=x+step) {
                HashSet<Integer> hs = new HashSet<Integer>();
                for (int yy = 0 ; yy < boxSize ; yy ++) {
                    for (int xx = 0 ; xx < boxSize ; xx ++) {
                        hs.add((y+yy)*size + xx+x);
                    }
                }
                areaSets.add(hs);
            }
        }
    }

    public HashSet<Integer> getValues(Integer []s, int r, int c, int box, int bw, int sh, int sw) {
        HashSet<Integer> values = utilities.getValues(s, r, c, box, bw, sh, sw);
        int cell = r*bw + c;
        for (int n = 0 ; n < this.areaSets.size() ; n++) {
            if (this.areaSets.get(n).contains(cell)) {
                for (int a : this.areaSets.get(n)) {
                    values.remove(s[a]);
                }
                break;
            }
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
        HyperSudokuSolver hyperSolver = new HyperSudokuSolver(board.values);
        hyperSolver.setAreas(board.getBoardWidth());
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        for (int n = 0 ; n < hyperSolver.areaSets.size() ; n++) {
            board.setExtraArea(hyperSolver.areaSets.get(n));
            for (int a: hyperSolver.areaSets.get(n)) {
                if (board.isFilled(a)) board.setCellValue(a,board.getCellValue(a));
            }
        }
        System.out.println(board.dumpPattern());
        solver.solve(board);

        if (board.getFreeSlots().size() > 0) {
            System.out.print("x");
            if (board.validateSudoku2()) {
                hyperSolver.findSolution(board.getBoard(), 0, board.getBoardWidth(), board.getBoxHeigth(), board.getBoxWidth()); 
            }
        }
        System.out.println("\n\n" + board.dumpBoard());
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}