import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class FlowerSudokuSolver {
    private Utilities utilities;
    private StringBuilder sudokuT;
    private StringBuilder sudokuL;
    private StringBuilder sudokuR;
    private StringBuilder sudokuB;
    private StringBuilder sudokuC;
    
    public FlowerSudokuSolver(HashSet<Integer> values) {
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

    private void printFlowerSudoku(SudokuBoard bt, SudokuBoard bl, SudokuBoard bc, SudokuBoard br, SudokuBoard bb) {
        int margin = 4;
        System.out.println("\n\n");
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,0,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,9,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bt,18,9));
        System.out.println();
        System.out.println(getSpace(margin) + getSudokuPart(bl,0,9) + getSudokuPart(br,3,6));
        System.out.println(getSpace(margin) + getSudokuPart(bl,9,9) + getSudokuPart(br,12,6));
        System.out.println(getSpace(margin) + getSudokuPart(bl,18,9) + getSudokuPart(br,21,6));
        System.out.println();
        System.out.println(getSpace(margin) + getSudokuPart(bl,27,9) + getSudokuPart(br,30,6));
        System.out.println(getSpace(margin) + getSudokuPart(bl,36,9) + getSudokuPart(br,39,6));
        System.out.println(getSpace(margin) + getSudokuPart(bl,45,9) + getSudokuPart(br,48,6));
        System.out.println();
        System.out.println(getSpace(margin) + getSudokuPart(bl,54,9) + getSudokuPart(br,57,6));
        System.out.println(getSpace(margin) + getSudokuPart(bl,63,9) + getSudokuPart(br,66,6));
        System.out.println(getSpace(margin) + getSudokuPart(bl,72,9) + getSudokuPart(br,75,6));
        System.out.println();
        System.out.println(getSpace(margin+4) + getSudokuPart(bb,54,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bb,63,9));
        System.out.println(getSpace(margin+4) + getSudokuPart(bb,72,9));
        System.out.println();
    }

    private void parseSudokus(String s) {
        sudokuT.append("-");
        sudokuL.append("-");
        sudokuC.append("-");
        sudokuR.append("-");
        sudokuB.append("-");
        sudokuT.append(s.substring(0,27));
        for (int t = 0 ; t < 6 ; t++) {
            sudokuT.append(s.substring(30 + t*15, 30 + t*15 + 9));
        }
        for (int l = 0 ; l < 9 ; l++) {
            sudokuL.append(s.substring(27 + l*15, 27 + l*15 + 9));
        }
        for (int c = 0 ; c < 9 ; c++) {
            sudokuC.append(s.substring(30 + c*15, 30 + c*15 + 9));
        }
        for (int r = 0 ; r < 9 ; r++) {
            sudokuR.append(s.substring(33 + r*15, 33 + r*15 + 9));
        }
        for (int b = 0 ; b < 6 ; b++) {
            sudokuB.append(s.substring(75 + b*15, 75 + b*15 + 9));
        }
        sudokuB.append(s.substring(162));
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

    private boolean syncSudokus(SudokuBoard bt, SudokuBoard bl, SudokuBoard bc, SudokuBoard br, SudokuBoard bb){
        boolean returnValue = false;
        HashSet<Integer> boxArea = new HashSet<Integer>(Arrays.asList(0,1,2,9,10,11,18,19,20));
        for (int a : boxArea) {
            returnValue = mergeSudokuCell(bc,  0+a, bt, 27+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  0+a, bl,  3+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 27+a, bl,  3+a) || returnValue;

            returnValue = mergeSudokuCell(bc,  3+a, bt, 30+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  3+a, bl,  6+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  3+a, br,  0+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 30+a, bl,  6+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 30+a, br,  0+a) || returnValue;
            returnValue = mergeSudokuCell(bl,  6+a, br,  0+a) || returnValue;
            
            returnValue = mergeSudokuCell(bc,  6+a, bt, 33+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  6+a, br,  3+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 33+a, br,  3+a) || returnValue;
            
            returnValue = mergeSudokuCell(bc, 27+a, bt, 54+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 27+a, bl, 30+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 27+a, bb,  0+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 54+a, bl, 30+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 54+a, bb,  0+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 30+a, bb,  0+a) || returnValue;

            returnValue = mergeSudokuCell(bc, 30+a, bt, 57+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 30+a, bl, 33+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 30+a, br, 27+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 30+a, bb,  3+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 57+a, bl, 33+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 57+a, br, 27+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 57+a, bb,  3+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 33+a, br, 27+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 33+a, bb,  3+a) || returnValue;
            returnValue = mergeSudokuCell(br, 27+a, bb,  3+a) || returnValue;
            
            returnValue = mergeSudokuCell(bc, 33+a, bt, 60+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 33+a, br, 30+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 33+a, bb,  6+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 60+a, br, 30+a) || returnValue;
            returnValue = mergeSudokuCell(bt, 60+a, bb,  6+a) || returnValue;
            returnValue = mergeSudokuCell(br, 30+a, bb,  6+a) || returnValue;

            returnValue = mergeSudokuCell(bc, 54+a, bl, 57+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 54+a, bb, 27+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 57+a, bb, 27+a) || returnValue;

            returnValue = mergeSudokuCell(bc, 57+a, bl, 60+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 57+a, br, 54+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 57+a, bb, 30+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 60+a, br, 54+a) || returnValue;
            returnValue = mergeSudokuCell(bl, 60+a, bb, 30+a) || returnValue;
            returnValue = mergeSudokuCell(br, 54+a, bb, 30+a) || returnValue;

            returnValue = mergeSudokuCell(bc, 60+a, br, 57+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 60+a, bb, 33+a) || returnValue;
            returnValue = mergeSudokuCell(br, 57+a, bb, 33+a) || returnValue;
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
        FlowerSudokuSolver flowerSolver = new FlowerSudokuSolver(board.values);
        int size = 81+4*27;
        if (board.getSudokuString().length() != size) {
            System.out.println("Invalid size " + board.getSudokuString().length() +"/" + size);
            board.setSudokuString(flowerSolver.resizeString(board.getSudokuString() , size));
        }
        flowerSolver.parseSudokus(board.getSudokuString());

        System.out.println("T: " +flowerSolver.sudokuT.toString());
        System.out.println("L: " +flowerSolver.sudokuL.toString());
        System.out.println("C: " +flowerSolver.sudokuC.toString());
        System.out.println("R: " +flowerSolver.sudokuR.toString());
        System.out.println("B: " +flowerSolver.sudokuB.toString());
        SudokuBoard boardT = new SudokuBoard(flowerSolver.sudokuT.toString() + board.getCharSet());
        SudokuBoard boardL = new SudokuBoard(flowerSolver.sudokuL.toString() + board.getCharSet());
        SudokuBoard boardC = new SudokuBoard(flowerSolver.sudokuC.toString() + board.getCharSet());
        SudokuBoard boardR = new SudokuBoard(flowerSolver.sudokuR.toString() + board.getCharSet());
        SudokuBoard boardB = new SudokuBoard(flowerSolver.sudokuB.toString() + board.getCharSet());

        SudokuSolver solverT = new SudokuSolver(boardT.values);
        SudokuSolver solverL = new SudokuSolver(boardL.values);
        SudokuSolver solverC = new SudokuSolver(boardC.values);
        SudokuSolver solverR = new SudokuSolver(boardR.values);
        SudokuSolver solverB = new SudokuSolver(boardB.values);
        
        flowerSolver.printFlowerSudoku(boardT, boardL, boardC, boardR, boardB);
        System.out.println();
        flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
        boolean cont = true; 
        boardT.setIsOverlap(true);
        boardL.setIsOverlap(true);
        boardC.setIsOverlap(true);
        boardR.setIsOverlap(true);
        boardB.setIsOverlap(true);
        boardT.setExLimit(81);
        boardL.setExLimit(81);
        boardC.setExLimit(81);
        boardR.setExLimit(81);
        boardB.setExLimit(81);
        while (cont) {
            cont = false;
            cont = flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardT.getFreeSlots().size() > 0) solverT.solve(boardT);
            cont = flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardL.getFreeSlots().size() > 0) solverL.solve(boardL);
            cont = flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardR.getFreeSlots().size() > 0)solverR.solve(boardR);
            cont = flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardB.getFreeSlots().size() > 0)solverB.solve(boardB);
            cont = flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB) || cont;
            if (boardC.getFreeSlots().size() > 0)solverC.solve(boardC);
        }
        if  ((boardT.getFreeSlots().size()+boardL.getFreeSlots().size()+boardC.getFreeSlots().size()+boardR.getFreeSlots().size()+boardB.getFreeSlots().size())>0) {
            System.out.print(" Force");
            if (boardC.getFreeSlots().size() > 0) {
                boardC.setExLimit(0); 
                solverC.solve(boardC);
                flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardT.getFreeSlots().size() > 0) {
                boardT.setExLimit(0); 
                solverT.solve(boardT);
                flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardL.getFreeSlots().size() > 0) {
                boardL.setExLimit(0);                 
                solverL.solve(boardL);
                flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardR.getFreeSlots().size() > 0) {
                boardR.setExLimit(0); 
                solverR.solve(boardR);
                flowerSolver.syncSudokus(boardT, boardL, boardC, boardR, boardB);
            }
            if (boardB.getFreeSlots().size() > 0) {
                boardB.setExLimit(0);                 
                solverB.solve(boardB);
            }
        };
        //System.out.println(boardT.getFreeSlots()+""+ boardL.getFreeSlots()+""+boardC.getFreeSlots()+""+boardR.getFreeSlots()+""+ boardB.getFreeSlots());
        flowerSolver.printFlowerSudoku(boardT, boardL, boardC, boardR, boardB);
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}