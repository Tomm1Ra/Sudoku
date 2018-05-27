import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors;

public class SudokuSolver {
    private Utilities utilities;
    
    public SudokuSolver(HashSet<Integer> values) {
        this.utilities = new Utilities(values);
    }

    private boolean boxLineReduction(SudokuBoard board) {
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

    private boolean pointingPairs(SudokuBoard board) {
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

    public boolean hiddenRow(SudokuBoard board, int a, int z) {
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
                                    board.removeOptions(board.getBoardWidth()*row+r, blocks);
                                    //reductions++;
                                    returnValue = true; 
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

    public boolean nakedRow(SudokuBoard board, int a, int z) {
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

    public boolean hiddenColumn(SudokuBoard board, int a, int z) {
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
                                    board.removeOptions(board.getBoardWidth()*r + column, blocks);
                                    //reductions++;
                                    returnValue = true; 
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
    
    public boolean nakedColumn(SudokuBoard board, int a, int z) {
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

    private boolean hiddenBox(SudokuBoard board, int a, int z) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize <= z ; setSize++) {
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
                                        board.removeOptions(r, blocks);
                                        //reductions++;
                                        returnValue = true; 
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
   
    private boolean nakedBox(SudokuBoard board, int a, int z) {
        boolean returnValue = false;
        //int reductions = 0;
        for (int setSize = a ; setSize <= z ; setSize++) {
            for ( int row = 0 ; row < board.getBoardWidth() ; row=row+board.getBoxHeigth()) {
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
            while (findSingles(board))  {}
            cont = boxLineReduction(board) || cont;
            cont = pointingPairs(board) || cont;
            cont = nakedRow(board, 2,3) || cont;
            cont = nakedColumn(board, 2,3) || cont;
            cont = nakedBox(board, 2,3) || cont;
            cont = hiddenRow(board, 2,3) || cont;
            cont = hiddenColumn(board, 2,3) || cont;
            cont = hiddenBox(board, 2,3) || cont;
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
        if (ops > 3 && board.getBoardWidth() > 16) return false;
        System.out.print("e");
        boolean returnValue = false;
        int count = 0;
        int counter = 0;
        ArrayList<Integer> cells = new ArrayList<Integer>();
        ArrayList<Integer> values = new ArrayList<Integer>();
        ArrayList<Integer> freeCells = board.getFreeSlots().stream().filter(l -> board.getOptions(l).size() == ops).collect(Collectors.toCollection(ArrayList::new));
        //System.out.print("f:"+freeCells.size());
        for (int i: freeCells) {
            counter++;
            if (ops == 2) if (board.getExStart() >= counter) continue;
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
                    if (isExclusionReady(board, boards.get(a))) return true;
                }

                for ( int a : board.getFreeSlots() ) {
                    HashSet<Integer> blocks = new HashSet<Integer>();
                    for ( int m :board.getOptions(a)) {
                        if (boards.stream().allMatch(b -> b.getCellValue(a) != m) && boards.stream().noneMatch(b -> b.getOptions(a).contains(m))) {
                            blocks.add(m);
                            if (count < 7) System.out.print(".");
                            count++;
                        }
                    }
                    int refValue = boards.get(0).getCellValue(a);
                    if (refValue != 0) {
                        if (boards.stream().allMatch(b -> b.getCellValue(a) == refValue)) {
                            cells.add(a);
                            values.add(refValue);
                            count=count+3;
                        }
                    }
                    for (int x : blocks) {
                        if (board.getOptions(a).contains(x)) {
                            board.removeOptions(a, x);
                        }
                    }
                    for (int b = 0 ; b < ops ; b++) {
                        if (!boards.get(b).isFilled(a) && boards.get(b).getOptions(a).isEmpty()) {
                            board.removeOptions(i, boards.get(b).getCellValue(i));
                            count++;
                        }
                    }
                }
            }
            if (count > 6) break;
        }
        if (count > 0) returnValue = true;
        for (int b = 0;  b < cells.size(); b++) {
            if (b == 0) System.out.print(":");
            board.setCellValue(cells.get(b), values.get(b));
            board.removeFreeSlots(cells.get(b));
        }
        return returnValue;
    }


    private boolean hiddenSinglesRow(SudokuBoard board) {
        boolean returnValue = false;
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
                    returnValue = true;
                    board.setCellValue(row*board.getBoardWidth()+cell[i], i);
                    board.removeFreeSlots(row*board.getBoardWidth()+cell[i]);
                }
            }
        }
        return returnValue;
    }

    private boolean hiddenSinglesCol(SudokuBoard board) {
        boolean returnValue = false;
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
                    returnValue = true;
                    board.setCellValue(cell[i]*board.getBoardWidth()+col, i);
                    board.removeFreeSlots(cell[i]*board.getBoardWidth()+col);
                }
            }
        }
        return returnValue;
    }

    private boolean hiddenSinglesBox(SudokuBoard board) {
        boolean returnValue = false;
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
                        returnValue = true;
                        board.setCellValue(cell[i], i);
                        board.removeFreeSlots(cell[i]);
                    }
                }
            }
        }
        return returnValue;
    }

    private boolean hiddenSinglesArea(SudokuBoard board) {
        boolean returnValue = false;
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
                        returnValue = true;
                        int targetArea = 999;
                        for (int a = 0 ; a < areas.size(); a++) {
                            if (areas.get(a).contains(cell[i])) {targetArea = a;break;}
                        }
                        board.setCellValue(cell[i], i);
                        board.removeFreeSlots(cell[i]);
                    }
                }
            }
        }
        return returnValue;
    }

    private boolean findSingles(SudokuBoard board) { 
        boolean returnValue = false;
        HashSet<Integer> free = new HashSet<Integer>();
        for (int slot : board.getFreeSlots()) {
            if (!board.isFilled(slot)) {
                HashSet<Integer> hs = board.getOptions(slot);
                if (hs.size() == 1) {
                    board.setCellValue(slot, (int)hs.toArray()[0]);
                    returnValue = true;
                    free.add(slot);
                }
            }
        }
        board.removeFreeSlots(free);
        returnValue = hiddenSinglesRow(board) || returnValue;
        returnValue = hiddenSinglesCol(board) || returnValue;
        returnValue = hiddenSinglesBox(board) || returnValue;
        if (board.getIsExtraAreas()) returnValue = hiddenSinglesArea(board) || returnValue;

        return returnValue;
    }
     private boolean findSolution(Integer[] s, int index, int bw, int sh, int sw) {
        int y = index / bw;
        int x = index % bw;
        int box = y/sh*sh + x/sw;
        if (index == s.length ) return true;
        
        if (s[index] !=0 ) return findSolution(s, index+1, bw , sh ,sw);
        HashSet<Integer> optionsForCell = utilities.getValues(s, y, x, box, bw, sh, sw);
        for (int option : optionsForCell) {
            s[index] = option;
            if (findSolution(s, index+1, bw, sh, sw)) return true;
            else s[index] = 0;
        }
        return false;
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

    public void solve(SudokuBoard board) {
        boolean cont = true;
        boolean useExtra = true;
        int a = 2, z = 3;
        if (!board.getIsOverlap()) {
           if (board.getBoardWidth() > 9) board.setExLimit(70); else board.setExLimit(20);
        }
        while (cont) {
            cont = false;
            while (findSingles(board)) {System.out.print("*");}
            System.out.print("*"+board.getFreeSlots().size());
            if (board.getBoxHeigth() > 1 && board.getBoxWidth() > 1) {
                if (!cont && board.getFreeSlots().size() > 0) {
                    cont = pointingPairs(board);
                    cont = boxLineReduction(board) || cont;
                    cont = nakedRow(board, a, z) || cont;
                    cont = nakedColumn(board, a, z) || cont;
                    cont = nakedBox(board, a, z) || cont; 
                    cont = hiddenRow(board, a, z) || cont;
                    cont = hiddenColumn(board, a, z) || cont;
                    cont = hiddenBox(board, a, z) || cont;
                    if (board.getFreeSlots().size() > board.getExLimit()) {
                        if (!cont && useExtra) {
                            a = 4; z = 5;
                            useExtra = false; 
                            System.out.print("+"); 
                            cont = nakedRow(board, a, z) || cont;
                            cont = nakedColumn(board, a, z) || cont;
                            cont = nakedBox(board, a, z) || cont; 
                            cont = hiddenRow(board, a, z) || cont;
                            cont = hiddenColumn(board, a, z) || cont;
                            cont = hiddenBox(board, a, z) || cont; 
                            if (cont) System.out.print("+"); 
                            a = 2; z = 3;
                        }
                        if (!cont) {
                            int i = 2; 
                            while (i < board.getBoardWidth() && !cont) {
                                cont = exclusion(board, i) || cont;
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
        long startTime = System.currentTimeMillis();
        SudokuBoard board = new SudokuBoard(line);
        SudokuSolver solver = new SudokuSolver(board.values);
        ExtraAreaSudokuSolver areaSolver = new ExtraAreaSudokuSolver(board.values);
        System.out.println("\n" + board.dumpBoard());
        System.out.println("  " + board.getFreeSlots().size() + " / " + board.getBoardSize()+"\n");
        areaSolver.parseAreas(board);
        if (areaSolver.getAreaSets().size() > 0) {
            for (int n = 0 ; n <  areaSolver.getAreaSets().size() ; n++) {
                board.setExtraArea(areaSolver.getAreaSets().get(n));
                for (int a: areaSolver.getAreaSets().get(n)) {
                    if (board.isFilled(a)) board.setCellValue(a,board.getCellValue(a));
                }
            }
            System.out.println("\n" + board.dumpPattern());
        }
        solver.solve(board);
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