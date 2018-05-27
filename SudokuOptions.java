import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class SudokuOptions {
    private Utilities utilities;
    private boolean waitKey;
    private boolean verbose;
    
    public SudokuOptions(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
        this.waitKey = true;
        this.verbose = false;
    }

    private boolean boxLineReduction(SudokuBoard board, boolean show) {
    boolean returnValue = false;
    //int reductions = 0;
    for (int row = 0 ; row < board.getBoardWidth() ; row++) {
        for (int xBox = 0 ; xBox < board.getBoardWidth() ; xBox=xBox + board.getBoxWidth()) {
            HashSet<Integer> outContent = new HashSet<Integer>();
            HashSet<Integer> inContent = new HashSet<Integer>();
            for (int x = 0 ; x < board.getBoardWidth() ; x ++) {
                if (x >= xBox && x < xBox + board.getBoxWidth()) {
                    inContent.addAll(board.getOptions(row*board.getBoardWidth()+x));
                } else {
                    outContent.addAll(board.getOptions(row*board.getBoardWidth()+x));
                }
                
            }
            inContent.removeAll(outContent);
            if (!inContent.isEmpty()) {
                int startY = (row/board.getBoxHeigth()) * board.getBoxHeigth();
                int startX = xBox;
                for (int value : inContent) {
                    for (int y = startY ; y < startY+board.getBoxHeigth(); y++) {
                        for (int x = startX ; x < startX+board.getBoxWidth(); x++) {
                            if ( y != row ) {
                                if (!board.isFilled(y*board.getBoardWidth()+x) && board.getOptions(y*board.getBoardWidth() + x).contains(value)) {
                                    board.removeOptions(y*board.getBoardWidth() + x, value);
                                    returnValue = true;
                                    //reductions++;
                                    if (show)
                                    System.out.println("Boxline reduction row " + this.utilities.printCell(y*board.getBoardWidth() + x, board.getBoardWidth()) +" " + board.getCellValueChar(value));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // column
    for (int column = 0 ; column < board.getBoardWidth() ; column++) {
        for (int yBox = 0 ; yBox < board.getBoardWidth() ; yBox=yBox + board.getBoxHeigth()) {
            HashSet<Integer> outContent = new HashSet<Integer>();
            HashSet<Integer> inContent = new HashSet<Integer>();
            for (int y = 0 ; y < board.getBoardWidth() ; y ++) {
                if (y >= yBox && y < yBox + board.getBoxHeigth()) {
                    inContent.addAll(board.getOptions(y*board.getBoardWidth() + column));
                } else {
                    outContent.addAll(board.getOptions(y*board.getBoardWidth() + column));
                }
                
            }
            inContent.removeAll(outContent);
            if (!inContent.isEmpty()) {
                int startY = yBox;
                int startX = (column/board.getBoxWidth())*board.getBoxWidth();
                for (int value : inContent) {
                    for (int y = startY ; y < startY+board.getBoxHeigth(); y++) {
                        for (int x = startX ; x < startX+board.getBoxWidth(); x++) {
                            if ( x != column ) {
                                if (!board.isFilled(y*board.getBoardWidth()+x)  && board.getOptions(y*board.getBoardWidth() + x).contains(value)) {
                                    board.removeOptions(y*board.getBoardWidth() + x, value);
                                    returnValue = true;
                                    //reductions++;
                                    if (show)
                                    System.out.println("Boxline reduction col " + this.utilities.printCell(y*board.getBoardWidth() + x, board.getBoardWidth()) +" " + board.getCellValueChar(value));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // if (returnValue) System.out.println(" boxline " +reductions);
    return returnValue;
    }

    private boolean pointingPairs(SudokuBoard board, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int yBox = 0 ; yBox < board.getBoardWidth() ; yBox=yBox+board.getBoxHeigth()) {
            for (int xBox = 0 ; xBox < board.getBoardWidth() ; xBox=xBox+board.getBoxWidth()) {
                for (int boxRow = 0 ; boxRow < board.getBoxHeigth() ; boxRow++) {
                    HashSet<Integer> outContent = new HashSet<Integer>();
                    HashSet<Integer> inContent = new HashSet<Integer>();
                    for (int sy = 0 ; sy < board.getBoxHeigth() ; sy++) {
                        for (int sx = 0 ; sx < board.getBoxWidth() ; sx++) {
                            if (sy == boxRow) {
                                inContent.addAll(board.getOptions((yBox+sy)*board.getBoardWidth() + xBox + sx));
                            } else {
                                outContent.addAll(board.getOptions((yBox+sy)*board.getBoardWidth() + xBox + sx));
                            }
                        }
                    }
                    inContent.removeAll(outContent);
                    if (!inContent.isEmpty()) {
                        int y = yBox+boxRow;
                        for (int value : inContent) {
                            for (int x = 0 ; x < board.getBoardWidth(); x++) {
                                if ( x < xBox || x >= xBox + board.getBoxWidth() ) {
                                    if (!board.isFilled(y*board.getBoardWidth()+x)  && board.getOptions(y*board.getBoardWidth() + x).contains(value)) {
                                        board.removeOptions(y*board.getBoardWidth() + x, value);
                                        returnValue = true;
                                        //reductions++;
                                        if (show)
                                        System.out.println("Pointing pairs row" + this.utilities.printCell(y*board.getBoardWidth() + x, board.getBoardWidth()) +" " + board.getCellValueChar(value));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int yBox = 0 ; yBox < board.getBoardWidth() ; yBox=yBox+board.getBoxHeigth()) {
            for (int xBox = 0 ; xBox < board.getBoardWidth() ; xBox=xBox+board.getBoxWidth()) {
                for (int boxColumn = 0 ; boxColumn < board.getBoxWidth() ; boxColumn++) {
                    HashSet<Integer> outContent = new HashSet<Integer>();
                    HashSet<Integer> inContent = new HashSet<Integer>();
                    for (int sy = 0 ; sy < board.getBoxHeigth() ; sy++) {
                        for (int sx = 0 ; sx < board.getBoxWidth() ; sx++) {
                            if (sx == boxColumn) {
                                inContent.addAll(board.getOptions((yBox+sy)*board.getBoardWidth() + xBox + sx));
                            } else {
                                outContent.addAll(board.getOptions((yBox+sy)*board.getBoardWidth() + xBox + sx));
                            }
                        }
                    }
                    inContent.removeAll(outContent);
                    if (!inContent.isEmpty()) {
                        int x = xBox+boxColumn;
                        for (int value : inContent) {
                            for (int y = 0 ; y < board.getBoardWidth(); y++) {
                                if ( y < yBox || y >= yBox + board.getBoxHeigth() ) {
                                    if (!board.isFilled(y*board.getBoardWidth()+x)  && board.getOptions(y*board.getBoardWidth() + x).contains(value)) {
                                        board.removeOptions(y*board.getBoardWidth() + x, value);
                                        returnValue = true;
                                        //reductions++;
                                        if (show)
                                        System.out.println("Pointing pairs col " + this.utilities.printCell(y*board.getBoardWidth() + x, board.getBoardWidth()) +" " + board.getCellValueChar(value));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" pointing pairs " +reductions);
        return returnValue;
    }

    public boolean hiddenRow(SudokuBoard board, int a, int z, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize <= z ; setSize++) {
            for (int row = 0; row < board.getBoardWidth(); row++) {
                HashSet<Integer> areaValues = new HashSet<Integer>();
                int[] numberCount = new int[board.getBoardWidth()+1];
                for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                    for (int cellCandidate : board.getOptions(row*board.getBoardWidth()+i)) {
                        numberCount[cellCandidate]++;
                    }
                }
                for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                    if (numberCount[i] > 0 && numberCount[i] <= setSize) {
                        areaValues.add(i);
                    }
                }

                int limit = 200;
                if (setSize > 3 ) limit = 28;
                if (areaValues.size() > setSize && areaValues.size() < limit) {
                    int[] lista = areaValues.stream().mapToInt(Number::intValue).toArray(); 
                    int combination[] = new int[setSize];
                    this.utilities.clearList();
                    this.utilities.combinationUtil(lista, combination, 0, areaValues.size()-1, 0, setSize);
                    ArrayList<HashSet<Integer>> searchList = this.utilities.getList();
                    for (HashSet<Integer> ts : searchList) {
                        int hiddenCellCount = 0;
                        ArrayList<Integer> hiddenCells = new ArrayList<Integer>();
                        for (int column = 0 ; column < board.getBoardWidth() ;column++) {
                            if (!Collections.disjoint(board.getOptions(row*board.getBoardWidth()+column), ts)) {
                                hiddenCellCount++;
                                hiddenCells.add(column);
                            }
                            if (hiddenCellCount > setSize) break;
                        }
                        if (hiddenCellCount == setSize) {
                            HashSet<Integer> blocks = new HashSet<Integer>(board.lines);
                            blocks.removeAll(ts);
                            for (int r : hiddenCells) {
                                if (!Collections.disjoint(board.getOptions(board.getBoardWidth()*row+r),blocks)) {
                                    //reductions++;
                                    returnValue = true; 
                                    if (show) {
                                        for (int b : blocks) {
                                            ArrayList<Integer> hC = new ArrayList<Integer>();
                                            for (int h : hiddenCells) hC.add(h+1);
                                            if (board.getOptions(board.getBoardWidth()*row+r).contains(b)) {
                                            System.out.println("Hidden row " + (row+1) + " Values:" +board.getValueChars(ts) + " cells:" + hC +
                                                            this.utilities.printCell(board.getBoardWidth()*row+r, board.getBoardWidth()) +" " + 
                                                            board.getCellValueChar(b));
                                            }
                                        }
                                    }
                                    board.removeOptions(board.getBoardWidth()*row+r, blocks);
                                }    
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" piilomonikko row " +reductions);
        return returnValue;
    }

    public boolean nakedRow(SudokuBoard board, int a, int z, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize <= z ; setSize++) {
            for (int row = 0; row < board.getBoardWidth(); row++) {
                HashSet<Integer> cells = new HashSet<Integer>();
                for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                    int s = board.getOptions(row*board.getBoardWidth()+i).size();
                    if (s != 0 && s <= setSize ){
                        cells.add(i);
                    }
                }
                int limit = 200;
                if (setSize > 3 ) limit = 28;
                if (cells.size() > setSize && cells.size() < limit) {
                    int[] lista = cells.stream().mapToInt(Number::intValue).toArray();
                    // System.out.println(cells +" "+Arrays.toString(lista));
                    int combination[] = new int[setSize];
                    this.utilities.clearList();
                    this.utilities.combinationUtil(lista, combination, 0, cells.size()-1, 0, setSize);
                    ArrayList<HashSet<Integer>> searchList = this.utilities.getList();

                    for (HashSet<Integer> ts : searchList) {
                        HashSet<Integer> nakedSet = new HashSet<Integer>();
                        for (int c : ts) {
                            nakedSet.addAll(board.getOptions(row*board.getBoardWidth()+c));
                        }
                        if (nakedSet.size() == setSize) {
                            HashSet<Integer> blocks = new HashSet<Integer>(board.lines);
                            blocks.removeAll(ts);
                            //System.out.println(loytynyt+" "+row+" "+blocks.toString()+ts.toString());
                            for (int r : blocks) {
                                if (!board.isFilled(board.getBoardWidth()*row+r)) {
                                    for (int cellCandidate : nakedSet) {
                                        if (board.getOptions(board.getBoardWidth()*row+r).contains(cellCandidate)) {
                                            board.removeOptions(board.getBoardWidth()*row+r, cellCandidate);
                                            //reductions++;
                                            returnValue = true; 
                                            if (show) {
                                                ArrayList<Integer> nS = new ArrayList<Integer>();
                                                for (int n : ts) nS.add(n+1);
                                                System.out.println("Naked row " +  (row+1) + " Values" + board.getValueChars(nakedSet) + " Cells" +nS +
                                                                    this.utilities.printCell(board.getBoardWidth()*row+r, board.getBoardWidth()) +" " + 
                                                                    board.getCellValueChar(cellCandidate));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" nakedSet row " +reductions);
        return returnValue;
    }

    public boolean hiddenColumn(SudokuBoard board, int a, int z, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize <= z ; setSize++) {
            for (int column = 0; column < board.getBoardWidth(); column++) {
                HashSet<Integer> areaValues = new HashSet<Integer>();
                int[] numberCount = new int[board.getBoardWidth()+1];
                for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                    for (int cellCandidate : board.getOptions(i*board.getBoardWidth() + column)) {
                        numberCount[cellCandidate]++;
                    }
                }
                for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                    if (numberCount[i] > 0 && numberCount[i] <= setSize) {
                        areaValues.add(i);
                    }
                }
                
                int limit = 200;
                if (setSize > 3 ) limit = 28;
                if (areaValues.size() > setSize && areaValues.size() < limit) {
                    int[] lista = areaValues.stream().mapToInt(Number::intValue).toArray(); 
                    int combination[] = new int[setSize];
                    this.utilities.clearList();
                    this.utilities.combinationUtil(lista, combination, 0, areaValues.size()-1, 0, setSize);
                    ArrayList<HashSet<Integer>> searchList = this.utilities.getList();
                    for (HashSet<Integer> ts : searchList) {
                        
                        int hiddenCellCount = 0;
                        ArrayList<Integer> hiddenCells = new ArrayList<Integer>();
                        for (int row = 0 ; row < board.getBoardWidth() ; row++) {
                            if (!Collections.disjoint(board.getOptions(row*board.getBoardWidth()+column), ts)) {
                                hiddenCellCount++;
                                hiddenCells.add(row);
                            }
                            if (hiddenCellCount > setSize) break;
                        }
                        if (hiddenCellCount == setSize) {
                            HashSet<Integer> blocks = new HashSet<Integer>(board.values);
                            blocks.removeAll(ts); 
                            for (int r : hiddenCells) {
                                if (!Collections.disjoint(board.getOptions(board.getBoardWidth()*r+column),blocks)) {
                                    //reductions++;
                                    returnValue = true; 
                                    if (show) {
                                        for (int b : blocks) {
                                            ArrayList<Integer> hC = new ArrayList<Integer>();
                                            for (int h : hiddenCells) hC.add(h+1);
                                            if (board.getOptions(board.getBoardWidth()*r+column).contains(b)) {
                                                System.out.println("Hidden col " + (column+1) + " Values:" + board.getValueChars(ts) + " cells:" + hC +
                                                            this.utilities.printCell(board.getBoardWidth()*r+column, board.getBoardWidth()) +" " + 
                                                            board.getCellValueChar(b));
                                            }
                                        }
                                    }
                                    board.removeOptions(board.getBoardWidth()*r + column, blocks);
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" piilomonikko column " +reductions);
        return returnValue;
    }
    
    public boolean nakedColumn(SudokuBoard board, int a, int z, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize <= z ; setSize++) {
            for (int column = 0; column < board.getBoardWidth(); column++) {
                HashSet<Integer> cells = new HashSet<Integer>();
                for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                    int s = board.getOptions(i*board.getBoardWidth()+column).size(); 
                    if ( s != 0 && s <= setSize ){
                        cells.add(i);
                    }
                }
                int limit = 200;
                if (setSize > 3 ) limit = 28;
                if (cells.size() > setSize && cells.size() < limit) {
                    int[] lista = cells.stream().mapToInt(Number::intValue).toArray();
                    int combination[] = new int[setSize];
                    this.utilities.clearList();
                    this.utilities.combinationUtil(lista, combination, 0, cells.size()-1, 0, setSize);
                    ArrayList<HashSet<Integer>> searchList = this.utilities.getList();
                    for (HashSet<Integer> ts : searchList) {
                        HashSet<Integer> nakedSet = new HashSet<Integer>();
                        for (int c : ts) {
                            nakedSet.addAll(board.getOptions(c*board.getBoardWidth()+column));
                        }
                        if (nakedSet.size() == setSize) {
                            HashSet<Integer> blocks = new HashSet<Integer>(board.lines);
                            blocks.removeAll(ts);
                            for (int r : blocks) {
                                if (!board.isFilled(board.getBoardWidth()*r+column)) {
                                    for (int cellCandidate : nakedSet) {
                                        if (board.getOptions(board.getBoardWidth()*r+column).contains(cellCandidate)) {
                                            board.removeOptions(board.getBoardWidth()*r+column, cellCandidate);
                                            //reductions++;
                                            returnValue = true; 
                                            if (show) {
                                                ArrayList<Integer> nS = new ArrayList<Integer>();
                                                for (int n : ts) nS.add(n+1);
                                                System.out.println("Naked col " +  (column+1) + " Values" + board.getValueChars(nakedSet) + " Cells" +nS +
                                                                    this.utilities.printCell(board.getBoardWidth()*r+column, board.getBoardWidth()) +" " + 
                                                                    board.getCellValueChar(cellCandidate));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" nakedSet column " +reductions);
        return returnValue;
    }    

    private boolean hiddenBox(SudokuBoard board, int a, int z, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize < z+1 ; setSize++) {
            for ( int row = 0 ;row < board.getBoardWidth() ; row=row+board.getBoxHeigth()) {
                for ( int column = 0 ;column < board.getBoardWidth() ; column=column+board.getBoxWidth()) {
                    int vaaka = (row/board.getBoxHeigth())*board.getBoxHeigth();
                    int pysty = (column/board.getBoxWidth())*board.getBoxWidth();
                    HashSet<Integer> areaValues = new HashSet<Integer>();
                    int[] numberCount = new int[board.getBoardWidth()+1];
                    for (int y = vaaka ; y < vaaka+board.getBoxHeigth(); y++) {
                        for (int x = pysty ; x < pysty+board.getBoxWidth(); x++) {
                            for (int cellCandidate : board.getOptions(board.getBoardWidth()*y+x)) {
                                numberCount[cellCandidate]++;
                            }
                        }
                    }
                    for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                        if (numberCount[i] > 0 && numberCount[i] <= setSize) {
                            areaValues.add(i);
                        }
                    }
                    int limit = 200;
                    if (setSize > 3 ) limit = 28;
                    if (areaValues.size() > setSize && areaValues.size() < limit) {
                        int[] lista = areaValues.stream().mapToInt(Number::intValue).toArray();
                        int combination[] = new int[setSize];
                        this.utilities.clearList();
                        this.utilities.combinationUtil(lista, combination, 0, areaValues.size()-1, 0, setSize);
                        ArrayList<HashSet<Integer>> searchList = this.utilities.getList();
                        for (HashSet<Integer> ts : searchList) {

                            int hiddenCellCount = 0;
                            ArrayList<Integer> hiddenCells = new ArrayList<Integer>();
                            for (int y = 0 ; y < board.getBoxHeigth() ;y++) {
                                for (int x = 0 ; x < board.getBoxWidth() ;x++) {
                                    int cell = (row+y)*board.getBoardWidth()+column+x;
                                    if (!Collections.disjoint(board.getOptions(cell), ts)) {
                                        hiddenCellCount++;
                                        hiddenCells.add(cell);
                                    }
                                }
                                if (hiddenCellCount > setSize) break;
                            }
                            if (hiddenCellCount == setSize) {
                                HashSet<Integer> blocks = new HashSet<Integer>(board.values);
                                blocks.removeAll(ts);
                                for (int r : hiddenCells) {
                                    if (!Collections.disjoint(board.getOptions(r), blocks)) {
                                        //reductions++;
                                        returnValue = true; 
                                        if (show) {
                                            for (int b : blocks) {
                                                ArrayList<Integer> hC = new ArrayList<Integer>();
                                                for (int h : hiddenCells) 
                                                hC.add((h/board.getBoardWidth())%board.getBoxHeigth()*board.getBoxWidth()+h%board.getBoxWidth()+1);
                                                if (board.getOptions(r).contains(b)) {
                                                    System.out.println("Hidden box " + board.getBox(r) + " Values:" + board.getValueChars(ts) + " cells:" + hC +
                                                                this.utilities.printCell(r, board.getBoardWidth()) +" " + 
                                                                board.getCellValueChar(b));
                                                }
                                            }
                                        }
                                        board.removeOptions(r, blocks);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" piilomonikko ruutu " +reductions);
        return returnValue;
    }       
   
    private boolean nakedBox(SudokuBoard board, int a, int z, boolean show) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize < z+1 ; setSize++) {
            for ( int row = 0 ;row < board.getBoardWidth() ; row=row+board.getBoxHeigth()) {
                for ( int column = 0 ;column < board.getBoardWidth() ; column=column+board.getBoxWidth()) {
                    int vaaka = (row/board.getBoxHeigth())*board.getBoxHeigth();
                    int pysty = (column/board.getBoxWidth())*board.getBoxWidth();
                    HashSet<Integer> cells = new HashSet<Integer>();
                    HashSet<Integer> alue = new HashSet<Integer>();
                    
                    for (int y = vaaka ; y < vaaka+board.getBoxHeigth(); y++) {
                        for (int x = pysty ; x < pysty+board.getBoxWidth(); x++) {
                            int s = board.getOptions(y*board.getBoardWidth()+x).size(); 
                            if ( s != 0 && s <= setSize ){
                                cells.add(y*board.getBoardWidth()+x);
                            }    
                            alue.add(y*board.getBoardWidth()+x);
                        }
                    }
                    int limit = 200;
                    if (setSize > 3 ) limit = 28;
                    if (cells.size() > setSize && cells.size() < limit) {
                        int[] lista = cells.stream().mapToInt(Number::intValue).toArray();
                        int combination[] = new int[setSize];
                        this.utilities.clearList();
                        this.utilities.combinationUtil(lista, combination, 0, cells.size()-1, 0, setSize);
                        ArrayList<HashSet<Integer>> searchList = this.utilities.getList();
                        for (HashSet<Integer> ts : searchList) {
                            HashSet<Integer> nakedSet = new HashSet<Integer>();
                            for (int c : ts) {
                                nakedSet.addAll(board.getOptions(c));
                            }
                            if (nakedSet.size() == setSize) {
                                HashSet<Integer> blocks = new HashSet<Integer>(alue);
                                blocks.removeAll(ts);
                                for (int r : blocks) {
                                    if (!board.isFilled(r)) {
                                        for (int cellCandidate : nakedSet) {
                                            if (board.getOptions(r).contains(cellCandidate)) {
                                                board.removeOptions(r, cellCandidate);
                                                //reductions++;
                                                returnValue = true; 
                                                if (show) {
                                                    ArrayList<Integer> nS = new ArrayList<Integer>();
                                                    for (int n : ts) nS.add((n/board.getBoardWidth())%board.getBoxHeigth()*board.getBoxWidth()+n%board.getBoxWidth()+1);
                                                    System.out.println("Naked box " +  board.getBox(r) + " Values" + board.getValueChars(nakedSet) + " Cells" +nS +
                                                                        this.utilities.printCell(r, board.getBoardWidth()) +" " + 
                                                                        board.getCellValueChar(cellCandidate));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // if (returnValue) System.out.println(" nakedSet ruutu " +reductions);
        return returnValue;
    }
    private void exclusionSolve(SudokuBoard board) {
        boolean cont = true;
        while(cont) { 
            cont=false;
            while (findSingles(board, false)!=0)  {}
            cont = boxLineReduction(board, false) || cont;
            cont = pointingPairs(board, false) || cont;
            cont = nakedRow(board, 2,3, false) || cont;
            cont = nakedColumn(board, 2,3, false) || cont;
            cont = nakedBox(board, 2,3, false) || cont;
            cont = hiddenRow(board, 2,3, false) || cont;
            cont = hiddenColumn(board, 2,3, false) || cont;
            cont = hiddenBox(board, 2,3, false) || cont;
        }
    }
    private boolean isExclusionReady(SudokuBoard board, SudokuBoard boardE) {
        boolean returnValue = false;
        if (boardE.getFreeSlots().isEmpty()) {
            for (int c = 0; c < board.getBoardSize() ; c++) {
                if (!board.isFilled(c)) board.setCellValue(c, boardE.getCellValue(c));
                board.removeFreeSlots(c);
            }
            return true;
        }
        return returnValue;
    }

    private boolean exclusion(SudokuBoard board, int ops) {
        System.out.print("_"+ops);
        boolean returnValue = false;
        int count = 0;
        int counter = 0;
        ArrayList<Integer> cells = new ArrayList<Integer>();
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (int i: board.getFreeSlots()) {
            counter++;
            if (ops == 2) if (board.getExStart() > counter) continue;
            if (board.getOptions(i).size() ==  ops) {
                if (ops == 2) board.setExStart(counter); else board.setExStart(0);
                ArrayList<SudokuBoard> boards = new ArrayList<SudokuBoard>();
                for (int a = 0 ; a < ops ; a++) {
                    boards.add(new SudokuBoard(board.getAllValues()));
                }
                
                for (int o = 0 ; o < board.getBoardSize() ; o++) {
                    for (int a = 0 ; a < ops ; a++) {
                        boards.get(a).setOptions(o, board.getOptions(o));
                    }
                }
                for (int a = 0 ; a < ops ; a++) {
                    boards.get(a).removeFreeSlots(i);
                    boards.get(a).setCellValue(i, (int)board.getOptions(i).toArray()[a]);
                }
                for (int a = 0 ; a < ops ; a++) {
                    exclusionSolve(boards.get(a));
                    if (isExclusionReady(board, boards.get(a))) { 
                        System.out.println("\nExclusion "+ops+" Sudoku Ready " + this.utilities.printCell(i,  board.getBoardWidth()) +" " 
                            + board.getCellValueChar(boards.get(a).getCellValue(i)));
                        return true;
                    }
                }

                for ( int a : board.getFreeSlots() ) {
                    HashSet<Integer> blocks = new HashSet<Integer>();
                    for ( int m :board.getOptions(a)) {
                        boolean r1 = boards.stream().allMatch(b -> b.getCellValue(a) != m);
                        boolean r2 = boards.stream().noneMatch(b -> b.getOptions(a).contains(m));
                        if (r1 && r2) {
                            returnValue = true;
                            blocks.add(m);
                            if (count < 5) System.out.print(".");
                            count++;
                        }
                    }
                    int refValue = boards.get(0).getCellValue(a);
                    if (refValue != 0) {
                        if (boards.stream().allMatch(b -> b.getCellValue(a) == refValue)) {
                            cells.add(a);
                            values.add(refValue);
                            returnValue = true;
                            count=count+3;
                        }
                    }
                    
                    for (int x : blocks) {
                        if (board.getOptions(a).contains(x)) {
                            board.removeOptions(a, x);
                            System.out.println("Exclusion"+ops+" remove option " + this.utilities.printCell(a,  board.getBoardWidth()) +" " 
                            + board.getCellValueChar(x));
                        }
                    }
                    for (int b = 0 ; b < ops ; b++) {
                        if (!boards.get(b).isFilled(a) && boards.get(b).getOptions(a).isEmpty()) {
                            board.removeOptions(i, boards.get(b).getCellValue(i));
                            returnValue = true;
                            count++;
                            System.out.println("Exclusion"+ops+" remove option " + this.utilities.printCell(i,  board.getBoardWidth()) +" " 
                            + board.getCellValueChar(boards.get(b).getCellValue(i)));
                        }
                    }
                }
            }
            if (count > 20) break;
        }
        for (int b = 0;  b < cells.size(); b++) {
            if (b == 0) System.out.print(":");
            board.setCellValue(cells.get(b), values.get(b));
            board.removeFreeSlots(cells.get(b));
            System.out.println("Exclusion"+ops+" set cell value " + this.utilities.printCell(cells.get(b),  board.getBoardWidth()) +" " 
            + board.getCellValueChar(values.get(b)));
        }
        return returnValue;
    }

    private int hiddenSinglesRow(SudokuBoard board, boolean show) {
        int returnValue = 0;
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
                    returnValue++;
                    board.setCellValue(row*board.getBoardWidth()+cell[i], i);
                    board.removeFreeSlots(row*board.getBoardWidth()+cell[i]);
                    if (show) {
                        System.out.println("Hidden singles row " + this.utilities.printCell(row*board.getBoardWidth()+cell[i],  board.getBoardWidth()) +" " 
                        + board.getCellValueChar(i));
                        if (verbose) {
                            board.showOptions();
                            if (waitKey) utilities.pressAnykey("Press Enter to continue");
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    private int hiddenSinglesCol(SudokuBoard board, boolean show) {
        int returnValue = 0;
        for (int col = 0; col < board.getBoardWidth(); col++) {
            int[] counts = new int[board.getBoardWidth()+1];
            int[] cell = new int[board.getBoardWidth()+1];
            for (int i = 0 ; i < board.getBoardWidth() ; i++) {
                for (int j : board.getOptions(i*board.getBoardWidth()+col)){
                    counts[j]++;
                    cell[j] = i;
                }
            }
            for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                if (counts[i] == 1) {
                    returnValue++;
                    board.setCellValue(cell[i]*board.getBoardWidth()+col, i);
                    board.removeFreeSlots(cell[i]*board.getBoardWidth()+col);
                    if (show) {
                        System.out.println("Hidden singles col " + this.utilities.printCell(cell[i]*board.getBoardWidth()+col, board.getBoardWidth()) +" " + board.getCellValueChar(i));
                        if (verbose) {
                            board.showOptions();
                            if (waitKey) utilities.pressAnykey("Press Enter to continue");
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    private int hiddenSinglesBox(SudokuBoard board, boolean show) {
        int returnValue = 0;
        for ( int row = 0 ; row < board.getBoardWidth() ; row=row+board.getBoxHeigth()) {
            for ( int col = 0 ;col < board.getBoardWidth() ; col=col+board.getBoxWidth()) {
                int r1 = (row/board.getBoxHeigth())*board.getBoxHeigth();
                int c1 = (col/board.getBoxWidth())*board.getBoxWidth();
                int[] counts = new int[board.getBoardWidth()+1];
                int[] cell = new int[board.getBoardWidth()+1];
                for (int y = r1 ; y < r1+board.getBoxHeigth(); y++) {
                    for (int x = c1 ; x < c1+board.getBoxWidth(); x++) {
                        for (int j : board.getOptions(y*board.getBoardWidth()+x)){
                            counts[j]++;
                            cell[j] = y * board.getBoardWidth() + x;
                        }
                    }
                }
                for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                    if (counts[i] == 1) {
                        returnValue++;
                        board.setCellValue(cell[i], i);
                        board.removeFreeSlots(cell[i]);
                        if (show) {
                            System.out.println("Hidden singles box " + this.utilities.printCell(cell[i], board.getBoardWidth()) +" " + board.getCellValueChar(i));
                            if (verbose) {
                                board.showOptions();
                                if (waitKey) utilities.pressAnykey("Press Enter to continue");
                            }
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    private int hiddenSinglesArea(SudokuBoard board, boolean show) {
        int returnValue = 0;
        ArrayList<HashSet<Integer>> areas = board.getExtraAreas();
        for (int area = 0; area < areas.size(); area++) {
            int[] counts = new int[board.getBoardWidth()+1];
            int[] cell = new int[board.getBoardWidth()+1];
            if (areas.get(area).size() == board.getBoardWidth()) {
                for (int c : areas.get(area)) {
                    for (int j : board.getOptions(c)) {
                        counts[j]++;
                        cell[j] = c;
                    }
                }
                for (int i = 1 ; i <= board.getBoardWidth() ; i++) {
                    if (counts[i] == 1) {
                        returnValue++;
                        int targetArea = 999;
                        for (int a = 0 ; a < areas.size(); a++) {
                            if (areas.get(a).contains(cell[i])) {targetArea = a;break;}
                        }
                        board.setCellValue(cell[i], i);
                        board.removeFreeSlots(cell[i]);
                        if (show) {
                            System.out.println("Extra area single " + this.utilities.printCell(cell[i], board.getBoardWidth()) +" " + board.getCellValueChar(i));
                            if (verbose) {
                                board.showOptions();
                                if (waitKey) utilities.pressAnykey("Press Enter to continue");
                            }
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    private int findSingles(SudokuBoard board, boolean show) { 
        int returnValue = 0;
        HashSet<Integer> free = new HashSet<Integer>();
        for (int slot : board.getFreeSlots()) {
            if (!board.isFilled(slot)) {
                HashSet<Integer> hs = board.getOptions(slot);
                if (hs.size() == 1) {
                    int value = (int)hs.toArray()[0];
                    board.setCellValue(slot, value);
                    if (show) {
                        System.out.println("Naked single " + this.utilities.printCell(slot, board.getBoardWidth()) +" " + board.getCellValueChar(value));
                        if (verbose) {
                            board.showOptions();
                            if (waitKey) utilities.pressAnykey("Press Enter to continue");
                        }
                    }
                    returnValue++;
                    free.add(slot);
                }
            }
        }
        board.removeFreeSlots(free);
        returnValue = hiddenSinglesRow(board, show) + returnValue;
        returnValue = hiddenSinglesCol(board, show) + returnValue;
        returnValue = hiddenSinglesBox(board, show) + returnValue;
        if (board.getIsExtraAreas()) returnValue = hiddenSinglesArea(board, show) + returnValue;

        return returnValue;
    }
     private boolean findSolution(Integer[] s, int index, int bw, int sh, int sw,  ArrayList<HashSet<Integer>> as) {
        int y = index / bw;
        int x = index % bw;
        int box = y/sh*sh + x/sw;
        if (index == s.length ) return true;
        
        if (s[index] !=0 ) return findSolution(s, index+1, bw , sh ,sw, as);
        
        HashSet<Integer> optionsForCell = utilities.getValues(s, y, x, box, bw, sh, sw);
        for (int n = 0 ; n < as.size() ; n++) {
            if (as.get(n).contains(index)) {
                for (int a : as.get(n)) {
                    optionsForCell.remove(s[a]);
                }
            }
        }
        for (int option : optionsForCell) {
            s[index] = option;
            if (findSolution(s, index+1, bw, sh, sw, as)) return true;
            else s[index] = 0;
        }
        return false;
    } 

    public void solveWithOptions(SudokuBoard board, String line2) {
        boolean cont = true, reduceOptions=false;
        boolean useExtra = true;
        int a = 2, z = 3;
        line2 = line2.toUpperCase();
        if (line2.contains("F")) waitKey = false;
        if (line2.contains("V")) verbose = true;

        while (cont) {
            board.showOptions();
            if (waitKey) utilities.pressAnykey("Press Enter to continue");
            cont = false;
            while (findSingles(board, true)!=0) {
                System.out.println("*Singles found");
                board.showOptions();
                if (waitKey) utilities.pressAnykey("Press Enter to continue");
            }
            if (board.getBoxHeigth() > 1 && board.getBoxWidth() > 1) {
                System.out.print("*"+board.getFreeSlots().size());
                if (!cont && board.getFreeSlots().size() > 0) {
                    reduceOptions = pointingPairs(board, true);
                    if (reduceOptions) System.out.println("Pointing pair(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = boxLineReduction(board, true);
                    if (reduceOptions) System.out.println("Box line reduction(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = nakedRow(board, a, z, true) ;
                    if (reduceOptions) System.out.println("Naked row(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = nakedColumn(board, a, z, true);
                    if (reduceOptions) System.out.println("Naked column(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = nakedBox(board, a, z, true); 
                    if (reduceOptions) System.out.println("Naked box(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = hiddenRow(board, a, z, true);
                    if (reduceOptions) System.out.println("Hidden row(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = hiddenColumn(board, a, z, true);
                    if (reduceOptions) System.out.println("Hidden column(s) found");
                    cont = reduceOptions || cont;
                    reduceOptions = hiddenBox(board, a, z, true); 
                    if (reduceOptions) System.out.println("Hidden box(s) found");
                    cont = reduceOptions || cont;
                    if (board.getFreeSlots().size() > board.getExLimit()) {
                        if (!cont && useExtra) {
                            a = 4; z = 5;
                            useExtra = false; 
                            System.out.print("+"); 
                            cont = nakedRow(board, a, z, true) || cont;
                            cont = nakedColumn(board, a, z, true) || cont;
                            cont = nakedBox(board, a, z, true) || cont; 
                            cont = hiddenRow(board, a, z, true) || cont;
                            cont = hiddenColumn(board, a, z, true) || cont;
                            cont = hiddenBox(board, a, z, true) || cont; 
                            if (cont) System.out.print("+"); 
                            a = 2; z = 3;
                        }
                        if (!cont) {
                            int i = 2; 
                            while (i < board.getBoardWidth() && !cont) {
                                cont = exclusion(board, i) || cont;
                                if (cont) System.out.print("Exclusion "+i+" reduction(s)"); 
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args)  {
        String line = args[0];
        String line2 = "";
        if (args.length > 1) line2 = args[1];
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line);
        SudokuOptions solver = new SudokuOptions(board.values);
        ExtraAreaSudokuSolver areaSolver = new ExtraAreaSudokuSolver(board.values);
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        board.setExLimit(0); 
        areaSolver.parseAreas(board);
        for (int n = 0 ; n < areaSolver.getAreaSets().size() ; n++) {
            board.setExtraArea(areaSolver.getAreaSets().get(n));
            for (int a: areaSolver.getAreaSets().get(n)) {
                if (board.isFilled(a)) board.setCellValue(a,board.getCellValue(a));
            }
        }

        solver.solveWithOptions(board, line2);
        if (board.getFreeSlots().size() > 0) {
            System.out.print("x");
            if (board.validateSudoku2()) {
                solver.findSolution(board.getBoard(), 0, board.getBoardWidth(), board.getBoxHeigth(), board.getBoxWidth(), areaSolver.getAreaSets()); 
            }
        }
        System.out.println("\n\n" + board.dumpBoard());
        System.out.println("\n # "+  (System.currentTimeMillis() - startTime )+"ms ");
    }
}