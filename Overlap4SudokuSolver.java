import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class Overlap4SudokuSolver {

    private StringBuilder sudokuA;
    private StringBuilder sudokuB;
    private int boardw;
    private int boxw;
    private int boxh;
    
    public Overlap4SudokuSolver() {
        sudokuA = new StringBuilder();
        sudokuB = new StringBuilder();
    }

    private String getSpace(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n ; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private String getSudokuPart(SudokuBoard b, int start, int n) {
        char [] charSet = b.getCharSet().toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start+n ; i++) {
            if (b.getCellValue(i) !=0)
                if (charSet.length < 2)
                    sb.append(""+b.getCellValue(i));
                else
                    sb.append(charSet[b.getCellValue(i)]);
            else sb.append(".");
            if ((i+1)%b.getBoxWidth() == 0) sb.append(" ");
        }
        return sb.toString();
    }

    private void printOverlapSudoku(SudokuBoard ba, SudokuBoard bb) {
        System.out.println("\n\n");
        int margin = 4;
        for (int a = 0 ; a < ba.getBoardWidth()-2*ba.getBoxHeigth(); a++ ) {
            System.out.println(getSpace(margin) + getSudokuPart(ba,a*ba.getBoardWidth() ,ba.getBoardWidth()));
            if (a > 0 && (a+1)%boxh == 0) System.out.println();
        }
        for (int c = 0 ; c < 2*ba.getBoxHeigth(); c++) {
             System.out.print(getSpace(margin) + getSudokuPart(ba, (ba.getBoardWidth()-2*ba.getBoxHeigth()+c)*ba.getBoardWidth() ,ba.getBoardWidth()-2*ba.getBoxWidth()));
             System.out.println(getSudokuPart(bb, c*bb.getBoardWidth() ,bb.getBoardWidth()));
             if (c > 0 && (c+1)%boxh == 0 && c+1 < 2*ba.getBoxHeigth()) System.out.println();
        }
        margin = margin + bb.getBoardWidth()-2*bb.getBoxWidth() + (bb.getBoardWidth()-2*bb.getBoxWidth())/boxw;
        for (int b = 2*bb.getBoxHeigth() ; b < bb.getBoardWidth(); b++ ) {
            if (b > 0 && b%boxh == 0) System.out.println();
            System.out.println(getSpace(margin) + getSudokuPart(bb,b*bb.getBoardWidth() ,bb.getBoardWidth()));
        }
    }

    private void solveSizes(SudokuBoard board, String s) {
        if (board.getBoxWidth() == 0) {
            for (int i = 1 ; i < 36 ; i++) {
                int cArea = 4*i;
                int t = (int)Math.sqrt((s.length()+cArea)/2);
                if ( t*t ==  (s.length()+cArea)/2) {boardw = i;break;}
            }
            boxw = (int)Math.sqrt(boardw);
            if (boxw * boxw == boardw) boxh = boxw;
            else {
                if (boardw % 5 == 0) {
                int a = boardw/5;
                boxw = Math.max( a, boardw/a);
                boxh = boardw / boxw;
                } else if (boardw % 4 == 0) {
                    int a = boardw/4;
                    boxw = Math.max( a, boardw/a);
                    boxh = boardw / boxw;
                } else if (boardw % 3 == 0) {
                    int a = boardw/3;
                    boxw = Math.max( a, boardw/a);
                    boxh = boardw / boxw;
                } else if (boardw % 2 == 0) {
                    int a = boardw/2;
                    boxw = Math.max( a, boardw/a);
                    boxh = boardw / boxw;
                } else {
                    boxw = boardw;
                    boxh = 1;
                }
            }
        } else {
            boxw = board.getBoxWidth();
            boxh = board.getBoxHeigth();
            boardw = boxw * boxh;
        }
    }
    private void parseSudokus(SudokuBoard board, String s) {
        int posA=0, posB=0;
        sudokuA.append("-");
        sudokuB.append("-");
        posA = (boardw-2*boxh) * boardw;
        if (posA > s.length()) return;
        sudokuA.append(s.substring(0,posA));
        for (int h = 0 ; h < 2*boxh ; h++) {
            posB = posA + boardw;
            if (posB > s.length()) return;
            sudokuA.append(s.substring(posA, posB));
            sudokuB.append(s.substring(posB-2*boxw, posB-2*boxw+boardw));
            posA = posB-2*boxw+boardw;
        }
        sudokuB.append(s.substring(posA));
    }

    private boolean mergeSudokuCell(SudokuBoard a, int an, SudokuBoard b, int bn){
        boolean returnValue = false;
        if (a.getCellValue(an) == 0 && b.getCellValue(bn) != 0) {a.setCellValue(an, b.getCellValue(bn));a.removeFreeSlots(an);returnValue = true;}
        if (a.getCellValue(an) != 0 && b.getCellValue(bn) == 0) {b.setCellValue(bn, a.getCellValue(an));b.removeFreeSlots(bn);returnValue = true;}
        HashSet<Integer> ao = a.getOptions(an);
        HashSet<Integer> bo = b.getOptions(bn);
        HashSet<Integer> ra = new HashSet<Integer>();
        for (int o : ao) {
            if (!bo.contains(o)) ra.add(o);
        }
        a.removeOptions(an, ra);
        HashSet<Integer> rb = new HashSet<Integer>();
        for (int o : bo) {
            if (!ao.contains(o)) rb.add(o);
        }
        b.removeOptions(bn, rb);
        return returnValue;
    }

    private boolean syncSudokus(SudokuBoard ba, SudokuBoard bb) {
        boolean returnValue = false;
        HashSet<Integer> boxArea = new HashSet<Integer>();
        for (int y = 0 ; y < boxh; y++) {
            for (int x = 0 ; x < boxw; x++) {
                boxArea.add((boxh*boxw)*y + x);
            }
        }
        for (int a : boxArea) {
            returnValue = mergeSudokuCell(ba, (boardw-2*boxh)*boardw + (boardw-2*boxw) +a, bb, a) || returnValue;
            returnValue = mergeSudokuCell(ba, (boardw-2*boxh)*boardw + (boardw-boxw) +a, bb, a+boxw) || returnValue;
            returnValue = mergeSudokuCell(ba, (boardw-boxh)*boardw + (boardw-2*boxw) +a, bb, boxh*boardw+a) || returnValue;
            returnValue = mergeSudokuCell(ba, (boardw-boxh)*boardw + (boardw-boxw) +a, bb, boxh*boardw+boxw+a) || returnValue;
        }
        return returnValue;
    }

    private String boxSizes() {
        return "/"+boxw+","+boxh;
    }

    public static void main(String[] args)  {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line , true);
        Overlap4SudokuSolver overlapSolver = new Overlap4SudokuSolver();
        
        overlapSolver.solveSizes(board, board.getSudokuString());
        overlapSolver.parseSudokus(board, board.getSudokuString());

        SudokuBoard boardA = new SudokuBoard(overlapSolver.sudokuA.toString() + board.getCharSet() + overlapSolver.boxSizes());
        SudokuBoard boardB = new SudokuBoard(overlapSolver.sudokuB.toString() + board.getCharSet() + overlapSolver.boxSizes());

        SudokuSolver solverA = new SudokuSolver(boardA.values);
        SudokuSolver solverB = new SudokuSolver(boardB.values);
        
        overlapSolver.printOverlapSudoku(boardA, boardB);
        System.out.println();
        overlapSolver.syncSudokus(boardA, boardB);
        boolean cont = true; 
        int fuse = 0;
        while ((boardA.getFreeSlots().size() + boardB.getFreeSlots().size()) > 0) {
            solverA.solve(boardA);
            cont = overlapSolver.syncSudokus(boardA, boardB);
            solverB.solve(boardB);
            cont = overlapSolver.syncSudokus(boardA, boardB);
            fuse++;
            if (fuse > 4) break;
        }
        if ((boardA.getFreeSlots().size() > 0)) {
           boardA.setExLimit(0);
           solverA.solve(boardA);
           cont = overlapSolver.syncSudokus(boardA, boardB);
        }
        if ((boardB.getFreeSlots().size() > 0)) {
           boardB.setExLimit(0);
           solverB.solve(boardB);
        }

        overlapSolver.printOverlapSudoku(boardA, boardB);
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}