import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class SamuraiSudokuSolver {
    private Utilities utilities;
    private StringBuilder sudokuTL;
    private StringBuilder sudokuTR;
    private StringBuilder sudokuBL;
    private StringBuilder sudokuBR;
    private StringBuilder sudokuC;
    
    public SamuraiSudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
        sudokuTL = new StringBuilder();
        sudokuTR = new StringBuilder();
        sudokuC = new StringBuilder();
        sudokuBL = new StringBuilder();
        sudokuBR = new StringBuilder();
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

    private void printSamuraiSudoku(SudokuBoard btl, SudokuBoard btr, SudokuBoard bc, SudokuBoard bbl, SudokuBoard bbr) {
        System.out.println("\n\n");
        System.out.println("   " + getSudokuPart(btl,0,9) + getSpace(4) + getSudokuPart(btr,0,9));
        System.out.println("   " + getSudokuPart(btl,9,9) + getSpace(4) + getSudokuPart(btr,9,9));
        System.out.println("   " + getSudokuPart(btl,18,9) + getSpace(4) + getSudokuPart(btr,18,9));
        System.out.println();
        System.out.println("   " + getSudokuPart(btl,27,9) + getSpace(4) + getSudokuPart(btr,27,9));
        System.out.println("   " + getSudokuPart(btl,36,9) + getSpace(4) + getSudokuPart(btr,36,9));
        System.out.println("   " + getSudokuPart(btl,45,9) + getSpace(4) + getSudokuPart(btr,45,9));
        System.out.println();
        System.out.println("   " + getSudokuPart(btl,54,9) + getSudokuPart(bc,3,3) + getSudokuPart(btr,54,9));
        System.out.println("   " + getSudokuPart(btl,63,9) + getSudokuPart(bc,12,3) + getSudokuPart(btr,63,9));
        System.out.println("   " + getSudokuPart(btl,72,9) + getSudokuPart(bc,15,3) + getSudokuPart(btr,72,9));
        System.out.println();
        System.out.println("   " + getSpace(8) + getSudokuPart(bc,27,9) );
        System.out.println("   " + getSpace(8) + getSudokuPart(bc,36,9) );
        System.out.println("   " + getSpace(8) + getSudokuPart(bc,45,9) );
        System.out.println();
        System.out.println("   " + getSudokuPart(bbl,0,9) + getSudokuPart(bc,57,3) + getSudokuPart(bbr,0,9));
        System.out.println("   " + getSudokuPart(bbl,9,9) + getSudokuPart(bc,66,3) + getSudokuPart(bbr,9,9));
        System.out.println("   " + getSudokuPart(bbl,18,9) + getSudokuPart(bc,75,3) + getSudokuPart(bbr,18,9));
        System.out.println();
        System.out.println("   " + getSudokuPart(bbl,27,9) + getSpace(4) + getSudokuPart(btr,27,9));
        System.out.println("   " + getSudokuPart(bbl,36,9) + getSpace(4) + getSudokuPart(btr,36,9));
        System.out.println("   " + getSudokuPart(bbl,45,9) + getSpace(4) + getSudokuPart(btr,45,9));
        System.out.println();
        System.out.println("   " + getSudokuPart(bbl,54,9) + getSpace(4) + getSudokuPart(bbr,54,9));
        System.out.println("   " + getSudokuPart(bbl,63,9) + getSpace(4) + getSudokuPart(bbr,63,9));
        System.out.println("   " + getSudokuPart(bbl,72,9) + getSpace(4) + getSudokuPart(bbr,72,9));
        System.out.println();
    }

    private void parseSudokus(String s) {
        sudokuTL.append("-");
        sudokuTR.append("-");
        sudokuC.append("-");
        sudokuBL.append("-");
        sudokuBR.append("-");
        sudokuTL.append(s.substring(0,9));
        sudokuTR.append(s.substring(9,18));
        sudokuTL.append(s.substring(18,27));
        sudokuTR.append(s.substring(27,36));
        sudokuTL.append(s.substring(36,45));
        sudokuTR.append(s.substring(45,54));
        sudokuTL.append(s.substring(54,63));
        sudokuTR.append(s.substring(63,72));
        sudokuTL.append(s.substring(72,81));
        sudokuTR.append(s.substring(81,90));
        sudokuTL.append(s.substring(90,99));
        sudokuTR.append(s.substring(99,108));
        sudokuTL.append(s.substring(108,117));
        sudokuC.append(s.substring(114,123));
        sudokuTR.append(s.substring(120,129));
        sudokuTL.append(s.substring(129,138));
        sudokuC.append(s.substring(135,144));
        sudokuTR.append(s.substring(141,150));
        sudokuTL.append(s.substring(150,159));
        sudokuC.append(s.substring(156,165));
        sudokuTR.append(s.substring(162,171));
        sudokuC.append(s.substring(171,180));
        sudokuC.append(s.substring(180,189));
        sudokuC.append(s.substring(189,198));
        sudokuBL.append(s.substring(198,207));
        sudokuC.append(s.substring(204,213));
        sudokuBR.append(s.substring(210,219));
        sudokuBL.append(s.substring(219,228));
        sudokuC.append(s.substring(225,234));
        sudokuBR.append(s.substring(231,240));
        sudokuBL.append(s.substring(240,249));
        sudokuC.append(s.substring(246,255));
        sudokuBR.append(s.substring(252,261));
        sudokuBL.append(s.substring(261,270));
        sudokuBR.append(s.substring(270,279));
        sudokuBL.append(s.substring(279,288));
        sudokuBR.append(s.substring(288,297));
        sudokuBL.append(s.substring(297,306));
        sudokuBR.append(s.substring(306,315));
        sudokuBL.append(s.substring(315,324));
        sudokuBR.append(s.substring(324,333));
        sudokuBL.append(s.substring(333,342));
        sudokuBR.append(s.substring(342,351));
        sudokuBL.append(s.substring(351,360));
        sudokuBR.append(s.substring(360));
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

    private boolean syncSudokus(SudokuBoard btl, SudokuBoard btr, SudokuBoard bc, SudokuBoard bbl, SudokuBoard bbr){
        boolean returnValue = false;
        HashSet<Integer> boxArea = new HashSet<Integer>(Arrays.asList(0,1,2,9,10,11,18,19,20));
        for (int a : boxArea) {
            returnValue = mergeSudokuCell(bc,  0+a, btl, 60+a) || returnValue;
            returnValue = mergeSudokuCell(bc,  6+a, btr, 54+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 54+a, bbl,  6+a) || returnValue;
            returnValue = mergeSudokuCell(bc, 60+a, bbr,  0+a) || returnValue;
        }
        return returnValue;
    }

    private String resizeString(String s, int n) {
        return utilities.resizeString(s, n);
    }

    public static void main(String[] args)  {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line,true);
        SamuraiSudokuSolver samuraiSolver = new SamuraiSudokuSolver(board.values);
        
        int size = 5*81 - 4*9;
        if (board.getSudokuString().length() != size) {
            System.out.println("Invalid size " + board.getSudokuString().length() +"/" + size);
            board.setSudokuString(samuraiSolver.resizeString(board.getSudokuString() , size));
        }
        samuraiSolver.parseSudokus(board.getSudokuString());

        System.out.println("TL: " +samuraiSolver.sudokuTL.toString());
        System.out.println("TR: " +samuraiSolver.sudokuTR.toString());
        System.out.println("C:  " +samuraiSolver.sudokuC.toString());
        System.out.println("BL: " +samuraiSolver.sudokuBL.toString());
        System.out.println("BR: " +samuraiSolver.sudokuBR.toString());

        SudokuBoard boardTL = new SudokuBoard(samuraiSolver.sudokuTL.toString() + board.getCharSet());
        SudokuBoard boardTR = new SudokuBoard(samuraiSolver.sudokuTR.toString() + board.getCharSet());
        SudokuBoard boardC = new SudokuBoard(samuraiSolver.sudokuC.toString() + board.getCharSet());
        SudokuBoard boardBL = new SudokuBoard(samuraiSolver.sudokuBL.toString() + board.getCharSet());
        SudokuBoard boardBR = new SudokuBoard(samuraiSolver.sudokuBR.toString() + board.getCharSet());

        SudokuSolver solverTL = new SudokuSolver(boardTL.values);
        SudokuSolver solverTR = new SudokuSolver(boardTR.values);
        SudokuSolver solverC = new SudokuSolver(boardC.values);
        SudokuSolver solverBL = new SudokuSolver(boardBL.values);
        SudokuSolver solverBR = new SudokuSolver(boardBR.values);
        
        samuraiSolver.printSamuraiSudoku(boardTL, boardTR, boardC, boardBL, boardBR);
        System.out.println();
        samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR);
        boolean cont = true; 
        while (cont) {
            cont = false;
            cont = samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR) || cont;
            if (boardTL.getFreeSlots().size() > 0) solverTL.solve(boardTL);
            cont = samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR) || cont;
            if (boardTR.getFreeSlots().size() > 0) solverTR.solve(boardTR);
            cont = samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR) || cont;
            if (boardBL.getFreeSlots().size() > 0)solverBL.solve(boardBL);
            cont = samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR) || cont;
            if (boardBR.getFreeSlots().size() > 0)solverBR.solve(boardBR);
            cont = samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR) || cont;
            if (boardC.getFreeSlots().size() > 0)solverC.solve(boardC);
        }
        if  ((boardTL.getFreeSlots().size()+boardTR.getFreeSlots().size()+boardC.getFreeSlots().size()+boardBL.getFreeSlots().size()+boardBR.getFreeSlots().size())>0) {
            System.out.print(" Force");
            if (boardC.getFreeSlots().size() > 0) {
                boardC.setExLimit(0); 
                solverC.solve(boardC);
                samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR);
            }
            if (boardTL.getFreeSlots().size() > 0) {
                boardTL.setExLimit(0); 
                solverTL.solve(boardTL);
                samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR);
            }
            if (boardTR.getFreeSlots().size() > 0) {
                boardTR.setExLimit(0);                 
                solverTR.solve(boardTR);
                samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR);
            }
            if (boardBL.getFreeSlots().size() > 0) {
                boardBL.setExLimit(0); 
                solverBL.solve(boardBL);
                samuraiSolver.syncSudokus(boardTL, boardTR, boardC, boardBL, boardBR);
            }
            if (boardBR.getFreeSlots().size() > 0) {
                boardBR.setExLimit(0);                 
                solverBR.solve(boardBR);
            }
        };
        //System.out.println(boardTL.getFreeSlots()+""+ boardTR.getFreeSlots()+""+boardC.getFreeSlots()+""+boardBL.getFreeSlots()+""+ boardBR.getFreeSlots());
        samuraiSolver.printSamuraiSudoku(boardTL, boardTR, boardC, boardBL, boardBR);
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}