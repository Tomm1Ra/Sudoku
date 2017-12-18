import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class WindmillSudokuSolver {
    private Utilities utilities;
    private StringBuilder sudokuT;
    private StringBuilder sudokuL;
    private StringBuilder sudokuR;
    private StringBuilder sudokuB;
    private StringBuilder sudokuC;
    
    public WindmillSudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
        sudokuT = new StringBuilder();
        sudokuL = new StringBuilder();
        sudokuC = new StringBuilder();
        sudokuR = new StringBuilder();
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
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start+n ; i++) {
            if (b.getCellValue(i) !=0)
            sb.append(""+b.getCellValue(i));
            else sb.append(".");
            if ((i+1)%3 == 0) sb.append(" ");
        }
        return sb.toString();
    }

    private void printWindmillSudoku(SudokuBoard bt, SudokuBoard bl, SudokuBoard bc, SudokuBoard br, SudokuBoard bb) {
        int margin = 4;
        System.out.println("\n\n");
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,0,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,9,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,18,9));
        System.out.println();
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,27,9) + getSudokuPart(br,0,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,36,9) + getSudokuPart(br,9,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,45,9) + getSudokuPart(br,18,9));
        System.out.println();
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,54,9) + getSudokuPart(br,27,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,63,9)  + getSudokuPart(br,36,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,72,9) + getSudokuPart(br,45,9));
        System.out.println();
        System.out.println(getSpace(margin) + getSudokuPart(bl,0,9) + getSudokuPart(bc,30,3) + getSudokuPart(br,54,9));
        System.out.println(getSpace(margin) + getSudokuPart(bl,9,9) + getSudokuPart(bc,39,3) + getSudokuPart(br,63,9));
        System.out.println(getSpace(margin) + getSudokuPart(bl,18,9) + getSudokuPart(bc,48,3) + getSudokuPart(br,72,9));
        System.out.println();
        System.out.println(getSpace(margin) + getSudokuPart(bl,27,9) + getSudokuPart(bb,0,9));
        System.out.println(getSpace(margin) + getSudokuPart(bl,36,9) + getSudokuPart(bb,9,9));
        System.out.println(getSpace(margin) + getSudokuPart(bl,45,9) + getSudokuPart(bb,18,9));
        System.out.println();
        System.out.println(getSpace(margin) + getSudokuPart(bl,54,9) + getSudokuPart(bb,27,9));
        System.out.println(getSpace(margin) + getSudokuPart(bl,63,9) + getSudokuPart(bb,36,9));
        System.out.println(getSpace(margin) + getSudokuPart(bl,72,9) + getSudokuPart(bb,45,9));
        System.out.println();
        System.out.println(getSpace(margin+12) + getSudokuPart(bb,54,9));
        System.out.println(getSpace(margin+12) + getSudokuPart(bb,63,9));
        System.out.println(getSpace(margin+12) + getSudokuPart(bb,72,9));
        System.out.println();
    }

    private void parseSudokus(String s) {
        sudokuT.append("-");
        sudokuL.append("-");
        sudokuC.append("-");
        sudokuR.append("-");
        sudokuB.append("-");
        sudokuT.append(s.substring(0,27));
        int r = 0;
        for (r = 0 ; r < 3 ; r++) {
            sudokuT.append(s.substring(27 + r*18, 27 + r*18 + 9));
            sudokuR.append(s.substring(36 + r*18, 36 + r*18 + 9));
        }
        for (r = 0 ; r < 3 ; r++) {
            sudokuT.append(s.substring(81 + r*18, 81 + r*18 + 9));
            sudokuC.append(s.substring(84 + r*18, 84 + r*18 + 9));
            sudokuR.append(s.substring(90 + r*18, 90 + r*18 + 9));
        }
        for (r = 0 ; r < 3 ; r++) {
            sudokuL.append(s.substring(135 + r*21, 135 + r*21 + 9));
            sudokuC.append(s.substring(141 + r*21, 141 + r*21 + 9));
            sudokuR.append(s.substring(147 + r*21, 147 + r*21 + 9));
        }
        for (r = 0 ; r < 3 ; r++) {
            sudokuL.append(s.substring(198 + r*18, 198 + r*18 + 9));
            sudokuC.append(s.substring(204 + r*18, 204 + r*18 + 9));
            sudokuB.append(s.substring(207 + r*18, 207 + r*18 + 9));
        }
        for (r = 0 ; r < 3 ; r++) {
            sudokuL.append(s.substring(252 + r*18, 252 + r*18 + 9));
            sudokuB.append(s.substring(261 + r*18, 261 + r*18 + 9));
        }
        sudokuB.append(s.substring(306));
    }

    private boolean mergeSudokuCell(SudokuBoard a, int an, SudokuBoard b, int bn){
        boolean returnValue = false;
        if (a.getCellValue(an) == 0 && b.getCellValue(bn) != 0) {a.setCellValue(an, b.getCellValue(bn));a.removeFreeSlots(an);returnValue = true;}
        if (a.getCellValue(an) != 0 && b.getCellValue(bn) == 0) {b.setCellValue(bn, a.getCellValue(an));b.removeFreeSlots(bn);returnValue = true;}
        if (a.getCellValue(an) != b.getCellValue(bn)) {System.out.println("Mismatch \n");}
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

    private boolean syncSudokus(SudokuBoard bt, SudokuBoard bl, SudokuBoard bc, SudokuBoard br, SudokuBoard bb){
        boolean returnValue = false;
        HashSet<Integer> boxArea = new HashSet<Integer>(Arrays.asList(0,1,2,9,10,11,18,19,20));
        for (int a : boxArea) {
            returnValue = mergeSudokuCell(bc,  0+a, bt, 57+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  3+a, bt, 60+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  6+a, br, 27+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 27+a, bl,  6+a) || returnValue;
            
            returnValue = mergeSudokuCell(bc, 33+a, br, 54+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 54+a, bl, 33+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 57+a, bb,  0+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 60+a, bb,  3+a) || returnValue;
        }
        return returnValue;
    }

    private String resizeString(String s, int n) {
        return utilities.resizeString(s, n);
    }

    public static void main(String[] args)  {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line, true);
        WindmillSudokuSolver windmillSolver = new WindmillSudokuSolver(board.values);
        int size = 4*81+9;
        if (board.getSudokuString().length() != size) {
            System.out.println("Invalid size " + board.getSudokuString().length() +"/" + size);
            board.setSudokuString(windmillSolver.resizeString(board.getSudokuString() , size));
        }
        
        windmillSolver.parseSudokus(board.getSudokuString());

        System.out.println("T: " +windmillSolver.sudokuT.toString());
        System.out.println("L: " +windmillSolver.sudokuL.toString());
        System.out.println("C: " +windmillSolver.sudokuC.toString());
        System.out.println("R: " +windmillSolver.sudokuR.toString());
        System.out.println("B: " +windmillSolver.sudokuB.toString());
        SudokuBoard boardT = new SudokuBoard(windmillSolver.sudokuT.toString() + board.getCharSet());
        SudokuBoard boardL = new SudokuBoard(windmillSolver.sudokuL.toString() + board.getCharSet());
        SudokuBoard boardC = new SudokuBoard(windmillSolver.sudokuC.toString() + board.getCharSet());
        SudokuBoard boardR = new SudokuBoard(windmillSolver.sudokuR.toString() + board.getCharSet());
        SudokuBoard boardB = new SudokuBoard(windmillSolver.sudokuB.toString() + board.getCharSet());

        SudokuSolver solverT = new SudokuSolver(boardT.values);
        SudokuSolver solverL = new SudokuSolver(boardL.values);
        SudokuSolver solverC = new SudokuSolver(boardC.values);
        SudokuSolver solverR = new SudokuSolver(boardR.values);
        SudokuSolver solverB = new SudokuSolver(boardB.values);
        
        windmillSolver.printWindmillSudoku(boardT, boardL, boardC, boardR, boardB);
        System.out.println();
        windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
        boolean cont = true; 
        while (cont) {
            cont = false;
            cont = windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardT.getFreeSlots().size() > 0) solverT.solve(boardT);
            cont = windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardL.getFreeSlots().size() > 0) solverL.solve(boardL);
            cont = windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardR.getFreeSlots().size() > 0)solverR.solve(boardR);
            cont = windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardB.getFreeSlots().size() > 0)solverB.solve(boardB);
            cont = windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardC.getFreeSlots().size() > 0)solverC.solve(boardC);
        }
        if  ((boardT.getFreeSlots().size()+boardL.getFreeSlots().size()+boardC.getFreeSlots().size()+boardR.getFreeSlots().size()+boardB.getFreeSlots().size())>0) {
            System.out.print(" Force");
            if (boardC.getFreeSlots().size() > 0) {
                boardC.setExLimit(0); 
                solverC.solve(boardC);
                windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardT.getFreeSlots().size() > 0) {
                boardT.setExLimit(0); 
                solverT.solve(boardT);
                windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardL.getFreeSlots().size() > 0) {
                boardL.setExLimit(0);                 
                solverL.solve(boardL);
                windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardR.getFreeSlots().size() > 0) {
                boardR.setExLimit(0); 
                solverR.solve(boardR);
                windmillSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardB.getFreeSlots().size() > 0) {
                boardB.setExLimit(0);                 
                solverB.solve(boardB);
            }
        };
        //System.out.println(boardT.getFreeSlots()+""+ boardL.getFreeSlots()+""+boardC.getFreeSlots()+""+boardR.getFreeSlots()+""+ boardB.getFreeSlots());
        windmillSolver.printWindmillSudoku(boardT, boardL, boardC, boardR, boardB);
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}