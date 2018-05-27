import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class Samurai4SudokuSolver {
    private Utilities utilities;
    private StringBuilder sudokuT;
    private StringBuilder sudokuR;
    private StringBuilder sudokuL;
    private StringBuilder sudokuB;
    
    public Samurai4SudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
        sudokuT = new StringBuilder();
        sudokuR = new StringBuilder();
        sudokuL = new StringBuilder();
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

    private void printSamuraiSudoku(SudokuBoard bt, SudokuBoard bl, SudokuBoard br, SudokuBoard bb) {
        System.out.println("\n\n");
        System.out.println(getSpace(12) + getSudokuPart(bt,0,9));
        System.out.println(getSpace(12) + getSudokuPart(bt,9,9));
        System.out.println(getSpace(12) + getSudokuPart(bt,18,9));
        System.out.println();
        System.out.println(getSpace(12) + getSudokuPart(bt,27,9));
        System.out.println(getSpace(12) + getSudokuPart(bt,36,9));
        System.out.println(getSpace(12) + getSudokuPart(bt,45,9));
        System.out.println();
        System.out.println(getSpace(4) + getSudokuPart(bl,0,9) + getSudokuPart(bt,57,3) + getSudokuPart(br,0,9));
        System.out.println(getSpace(4) + getSudokuPart(bl,9,9) + getSudokuPart(bt,66,3) + getSudokuPart(br,9,9));
        System.out.println(getSpace(4) + getSudokuPart(bl,18,9) + getSudokuPart(bt,75,3) + getSudokuPart(br,18,9));
        System.out.println();
        System.out.println(getSpace(4) + getSudokuPart(bl,27,9) + getSpace(4) + getSudokuPart(br,27,9));
        System.out.println(getSpace(4) + getSudokuPart(bl,36,9) + getSpace(4) + getSudokuPart(br,36,9));
        System.out.println(getSpace(4) + getSudokuPart(bl,45,9) + getSpace(4) + getSudokuPart(br,45,9));
        System.out.println();
        System.out.println(getSpace(4) + getSudokuPart(bl,54,9) + getSudokuPart(bb,3,3) + getSudokuPart(br,54,9));
        System.out.println(getSpace(4) + getSudokuPart(bl,63,9) + getSudokuPart(bb,12,3) + getSudokuPart(br,63,9));
        System.out.println(getSpace(4) + getSudokuPart(bl,72,9) + getSudokuPart(bb,21,3) + getSudokuPart(br,72,9));
        System.out.println();
        System.out.println(getSpace(12) + getSudokuPart(bb,27,9));
        System.out.println(getSpace(12) + getSudokuPart(bb,36,9));
        System.out.println(getSpace(12) + getSudokuPart(bb,45,9));
        System.out.println();
        System.out.println(getSpace(12) + getSudokuPart(bb,54,9));
        System.out.println(getSpace(12) + getSudokuPart(bb,63,9));
        System.out.println(getSpace(12) + getSudokuPart(bb,72,9));
        System.out.println();
        
    }

    private void parseSudokus(String s) {
        sudokuT.append("-");
        sudokuL.append("-");
        sudokuR.append("-");
        sudokuB.append("-");
        sudokuT.append(s.substring(0,54));
        sudokuL.append(s.substring(54,63));
        sudokuT.append(s.substring(60,69));
        sudokuR.append(s.substring(66,75));
        sudokuL.append(s.substring(75,84));
        sudokuT.append(s.substring(81,90));
        sudokuR.append(s.substring(87,96));
        sudokuL.append(s.substring(96,105));
        sudokuT.append(s.substring(102,111));
        sudokuR.append(s.substring(108,117));
        sudokuL.append(s.substring(117,126));
        sudokuR.append(s.substring(126,135));
        sudokuL.append(s.substring(135,144));
        sudokuR.append(s.substring(144,153));
        sudokuL.append(s.substring(153,162));
        sudokuR.append(s.substring(162,171));
        sudokuL.append(s.substring(171,180));
        sudokuB.append(s.substring(177,186));
        sudokuR.append(s.substring(183,192));
        sudokuL.append(s.substring(192,201));
        sudokuB.append(s.substring(198,207));
        sudokuR.append(s.substring(204,213));
        sudokuL.append(s.substring(213,222));
        sudokuB.append(s.substring(219,228));
        sudokuR.append(s.substring(225,234));
        sudokuB.append(s.substring(234,288));
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

    private boolean syncSudokus(SudokuBoard bt, SudokuBoard bl, SudokuBoard br, SudokuBoard bb){
        boolean returnValue = false;
        HashSet<Integer> boxArea = new HashSet<Integer>(Arrays.asList(0,1,2,9,10,11,18,19,20));
        for (int a : boxArea) {
            returnValue = mergeSudokuCell(bt, 54+a, bl, 6+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 60+a, br, 0+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 60+a, bb, 0+a) || returnValue;
            returnValue = mergeSudokuCell(br, 54+a, bb, 6+a) || returnValue;
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
        Samurai4SudokuSolver samuraiSolver = new Samurai4SudokuSolver(board.values);
        
        int size = 4*63 + 4*9;
        if (board.getSudokuString().length() != size) {
            System.out.println("Invalid size " + board.getSudokuString().length() +"/" + size);
            board.setSudokuString(samuraiSolver.resizeString(board.getSudokuString() , size));
        }
        samuraiSolver.parseSudokus(board.getSudokuString());

        SudokuBoard boardT = new SudokuBoard(samuraiSolver.sudokuT.toString() + board.getCharSet());
        SudokuBoard boardL = new SudokuBoard(samuraiSolver.sudokuL.toString() + board.getCharSet());
        SudokuBoard boardR = new SudokuBoard(samuraiSolver.sudokuR.toString() + board.getCharSet());
        SudokuBoard boardB = new SudokuBoard(samuraiSolver.sudokuB.toString() + board.getCharSet());

        SudokuSolver solverT = new SudokuSolver(boardT.values);
        SudokuSolver solverL = new SudokuSolver(boardL.values);
        SudokuSolver solverR = new SudokuSolver(boardR.values);
        SudokuSolver solverB = new SudokuSolver(boardB.values);
        
        samuraiSolver.printSamuraiSudoku(boardT, boardL, boardR, boardB);
        System.out.println();
        samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB);
        boolean cont = true; 
        boardT.setIsOverlap(true);
        boardL.setIsOverlap(true);
        boardR.setIsOverlap(true);
        boardB.setIsOverlap(true);
        boardT.setExLimit(81);
        boardL.setExLimit(81);
        boardR.setExLimit(81);
        boardB.setExLimit(81);
        while (cont) {
            cont = false;
            solverT.solve(boardT);
            cont = samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB) || cont;
            solverL.solve(boardL);
            cont = samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB) || cont;
            solverR.solve(boardR);
            cont = samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB) || cont;
            solverB.solve(boardB);
            cont = samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB) || cont;
        }
        if  ((boardT.getFreeSlots().size()+boardL.getFreeSlots().size()+boardR.getFreeSlots().size()+boardB.getFreeSlots().size())>0) {
            System.out.print(" Force");
            if (boardT.getFreeSlots().size() > 0) {
                boardT.setExLimit(0); 
                solverT.solve(boardT);
                samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB);
            }
            if (boardL.getFreeSlots().size() > 0) {
                boardL.setExLimit(0);                 
                solverL.solve(boardL);
                samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB);
            }
            if (boardR.getFreeSlots().size() > 0) {
                boardR.setExLimit(0); 
                solverR.solve(boardR);
                samuraiSolver.syncSudokus(boardT, boardL, boardR, boardB);
            }
            if (boardB.getFreeSlots().size() > 0) {
                boardB.setExLimit(0);                 
                solverB.solve(boardB);
            }
        };
        //System.out.println(boardTL.getFreeSlots()+""+ boardTR.getFreeSlots()+""+boardC.getFreeSlots()+""+boardBL.getFreeSlots()+""+ boardBR.getFreeSlots());
        samuraiSolver.printSamuraiSudoku(boardT, boardL, boardR, boardB);
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}