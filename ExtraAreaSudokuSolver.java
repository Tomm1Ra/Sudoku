import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class ExtraAreaSudokuSolver {
    private Utilities utilities;
    private ArrayList<HashSet<Integer>> areaSets;
    private HashSet<Integer> xTopLeftToBottomRight;
    private HashSet<Integer> xBottomLeftToTopRight;
    
    public ExtraAreaSudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
        areaSets = new ArrayList<HashSet<Integer>>();
        this.xTopLeftToBottomRight = new HashSet<Integer>();
        this.xBottomLeftToTopRight = new HashSet<Integer>();
    }

    private void setXs(int size) {
        for (int i = 0 ; i < size; i++) {
            this.xTopLeftToBottomRight.add(i*size +i);
            this.xBottomLeftToTopRight.add(size*(size-i-1) + i);
        }
    }
    private void setHyperAreasOdd(int size) {
        int step = (int)Math.sqrt(size) + 1;
        int boxSize  = (int)Math.sqrt(size);
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

    private void setHyperAreasEven(int size) {
        int n = (int)Math.sqrt(size)-2;
        int step = (int)Math.sqrt(size) * 2;
        int boxSize  = (int)Math.sqrt(size);
        int startPoint = (int)Math.sqrt(size) / 2;
        areaSets = new ArrayList<HashSet<Integer>>();
        for (int y = startPoint ; y < size ; y=y+step) {
            for (int x = startPoint ; x < size ; x=x+step) {
                HashSet<Integer> hs = new HashSet<Integer>();
                for (int yy = 0 ; yy < boxSize ; yy ++) {
                    for (int xx = 0 ; xx < boxSize ; xx ++) {
                        hs.add((y+yy)*size + xx+x);
                    }
                }
                System.out.println(hs);
                areaSets.add(hs);
            }
        }
    }

    private void setCenterDotArea(SudokuBoard board) {
        int size =  board.getBoardWidth();
        int xo = (int)board.getBoxWidth()/2;
        int yo = (int)board.getBoxHeigth()/2;
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int y = yo ; y < size ; y=y+board.getBoxHeigth()) {
            for (int x = xo ; x < size ; x=x+board.getBoxWidth()) {
                hs.add(y*size + x);
            }
        }
        areaSets.add(hs);
    }
    
    private void setOffsetAreas(SudokuBoard board) {
        int size =  board.getBoardWidth();
        int xo = (int)board.getBoxWidth();
        int yo = (int)board.getBoxHeigth();
        for (int n = 0 ; n < size ; n++) {
            int yStart = n / xo;
            int xStart = n % xo;
            HashSet<Integer> hs = new HashSet<Integer>();
            for (int y = yStart ; y < size ; y=y+yo) {
                for (int x = xStart ; x < size ; x=x+xo) {
                    hs.add(y*size + x);
                }
            }
            areaSets.add(hs);
        }
    }

    private void setAsteriskArea(int size) {
        HashSet<Integer> hs = new HashSet<Integer>();
        switch (size) {
            case 4:
            hs.addAll(Arrays.asList(0,3,12,15));
            break;
            case 5:
            hs.addAll(Arrays.asList(6,8,12,16,18));
            break;
            case 6:
            hs.addAll(Arrays.asList(7,14,16,19,21,28));
            break;
            case 9:
            hs.addAll(Arrays.asList(13,20,24,37,40,43,56,60,67));
            break;
            case 16:
            hs.addAll(Arrays.asList(17,30,37,41,98,109,119,120,135,136,146,157,225,213,217,238));
            break;
            default:
            break;
        }
        areaSets.add(hs);
    }

    private void setGirandolaArea(int size) {
        HashSet<Integer> hs = new HashSet<Integer>();
        switch (size) {
            case 9:
            hs.addAll(Arrays.asList(0,8,13,37,40,43,67,72,80));
            break;
            default:
            break;
        }
        areaSets.add(hs);
    }

    private void setArgyleArea(int size) {
        areaSets = new ArrayList<HashSet<Integer>>();
        HashSet<Integer> upStartCells = new HashSet<Integer>();
        HashSet<Integer> downStartCells = new HashSet<Integer>();
        if (size == 9) {
            upStartCells.addAll(Arrays.asList(36,63,73,76));
            downStartCells.addAll(Arrays.asList(1,4,9,36));
        } else if (size == 25){
            upStartCells.addAll(Arrays.asList(175,300,425,550,602,607,612,617));
            downStartCells.addAll(Arrays.asList(2,7,12,17,50,175,300,425));
        } else if (size == 4){
            upStartCells.addAll(Arrays.asList(8,13));
            downStartCells.addAll(Arrays.asList(1,4));
        }
        for (int u : upStartCells) {
            HashSet<Integer> hs = new HashSet<Integer>();
            for (int n = 0 ; n < size ; n++) {
                if (u%size+n < size && u/size - n >= 0) {
                    hs.add((u/size-n)*size + u%size+n);
                } 
            }
            areaSets.add(hs);
        }
        for (int u : downStartCells) {
            HashSet<Integer> hs = new HashSet<Integer>();
            for (int n = 0 ; n < size ; n++) {
                if (u%size+n < size && u/size + n < size) {
                    hs.add((u/size+n)*size + u%size+n);
                }  
            }
            areaSets.add(hs);
        }
    }

    public void parseAreas(SudokuBoard board) {
        String s = board.getSudokuPattern();
        int size =  board.getBoardWidth();
        s = s.replaceAll("\\s+", "");
        s = s.replaceAll(":", "");
        setXs(size);
        String specialShapes = "X/\\*CHOGA";
        
        while (s.length() > 0 && specialShapes.indexOf(s.substring(0,1)) > -1) {
            switch (s.substring(0,1)) {
                case "X" : System.out.println("X"); 
                    areaSets.add(this.xTopLeftToBottomRight);
                    areaSets.add(this.xBottomLeftToTopRight);
                break;
                case "/" : 
                    System.out.println("/"); 
                    areaSets.add(this.xBottomLeftToTopRight);
                break;
                case "\\" : 
                    System.out.println("\\"); 
                    areaSets.add(this.xTopLeftToBottomRight);
                break;
                case "*" : 
                    System.out.println("*");
                    this.setAsteriskArea(size); 
                break;
                case "C" : 
                    System.out.println("C"); 
                    this.setCenterDotArea(board); 
                break;
                case "H" :
                    System.out.println("H"); 
                    if (size%2 == 0) this.setHyperAreasEven(size);
                    else this.setHyperAreasOdd(size);
                break;
                case "O" :
                    System.out.println("O"); 
                    this.setOffsetAreas(board);
                break;
                case "G" :
                System.out.println("G"); 
                this.setGirandolaArea(size);
                break;
                case "A" :
                System.out.println("A"); 
                this.setArgyleArea(size);
                break;
            }
            s = s.substring(1);
        }
        if (s.length() > size*size) {
            System.out.println(s+ " Warning too large pattern "+s.length() + " Board size "+size*size);
            s = s.substring(0, size*size);
        }
        if (s.length() > 0 && s.length() < size*size) {
            System.out.println(" warning short pattern "+s.length() + " Board size "+size*size);
        }
        
        String aCells = "abcdefghijklmnopqrstuvwxyz";
        for (Character a : aCells.toCharArray()) {
            int cell = 0;
            HashSet<Integer> hs = new HashSet<Integer>();
            for (Character c : s.toCharArray()) {
                if (c == a ) hs.add(cell++); else cell++;
            }
            if (!hs.isEmpty()) areaSets.add(hs);
        }
        for (int i= 0 ; i < areaSets.size() ; i++) {
            if (areaSets.get(i).size() > size && areaSets.get(i).size() != 0) {
                String s2 = aCells.substring(i,i+1);
                System.out.println("Incorrect extra area " + s2 + " size "+areaSets.get(i).size()); 
                System.exit(0);
            }
        }
        for (int n = 0 ; n < areaSets.size() ; n++) {
            System.out.println("Area " + n +" " +areaSets.get(n));
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
        ExtraAreaSudokuSolver areaSolver = new ExtraAreaSudokuSolver(board.values);
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        areaSolver.parseAreas(board);
        for (int n = 0 ; n < areaSolver.areaSets.size() ; n++) {
            board.setExtraArea(areaSolver.areaSets.get(n));
            for (int a: areaSolver.areaSets.get(n)) {
                if (board.isFilled(a)) board.setCellValue(a,board.getCellValue(a));
            }
        }
        System.out.println("\n" + board.dumpPattern());
        solver.solve(board);
        if (board.getFreeSlots().size() > 0) {
            if (board.validateSudoku2()) {
                System.out.print("x");
                areaSolver.findSolution(board.getBoard(), 0, board.getBoardWidth(), board.getBoxHeigth(), board.getBoxWidth()); 
            }
        }
        board.validateAreas();
        System.out.println("\n\n" + board.dumpBoard());
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}