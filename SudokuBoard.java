import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;

public class SudokuBoard {
    private Utilities utilities;
    private int exStart;
    private int ex3Start;
    private int exLimit;
    private int boardSize;
    private int boardWidth;
    private int boxWidth;
    private int boxHeigth;
    private Integer[] board; 
    private HashSet<Integer> freeSlots;
    private ArrayList<String> sudokuCharSet;
    HashSet<Integer> lines;
    HashSet<Integer> values;
    private ArrayList<HashSet<Integer>> optionsList;
    private ArrayList<HashSet<Integer>> extraAreas;
    private boolean isExtraAreas;
    private int blockAreaType;
    private boolean isBlockAreas;
    private boolean isOverlap;
    private StringBuilder sudokuPattern;
    private String sudokuString;

    SudokuBoard(ArrayList<Integer> al) {
        this.utilities = new Utilities(new HashSet<Integer>());
        this.freeSlots  = new HashSet<Integer>();
        this.solveBoardSize(al.size());
        this.board = new Integer[this.boardSize];
        this.setExLimit(30); 
        this.isExtraAreas = false;
        this.isBlockAreas = false;
        this.isOverlap = false;
        
        initTables();
        int index = 0;
        for (int value : al) {
            if (value <= 0) {
                value = 0;
                this.freeSlots.add(index);
            }
            setCellValue(index++, value);
        }
    }

    SudokuBoard(String s, boolean b) {
        this.utilities = new Utilities(new HashSet<Integer>());
        this.sudokuCharSet = new ArrayList<>();
        this.sudokuPattern = new StringBuilder();
        this.setExLimit(30);
        this.isExtraAreas = false;
        this.isBlockAreas = false;
        this.setIsOverlap(b);
        if (s.startsWith("-")) {
            s = s.substring(1);
            s = s.replaceAll("\\s+", "");
            if (!s.contains("@")) {
                for (Character c : "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ+".toCharArray()) {
                    sudokuCharSet.add(""+c);
                }
            }
            if (s.contains(":")) {
                this.sudokuPattern.append(s.substring(s.indexOf(":")));
                s = s.substring(0, s.indexOf(":"));
            }
        } else {
            try {
             s = readSudoku(s);
            } catch(FileNotFoundException e) {System.out.println("File " +s+" not found");System.exit(0);}
        }

        if (s.contains("/")) {
            String bSet = s.substring(s.indexOf("/")+1);
            ArrayList<String> boxSizes = new ArrayList<>();
            bSet = bSet.replaceAll("\\s+", "");
            for (String value : bSet.split(",")) {
                boxSizes.add(value);
            }
            if (boxSizes.size() == 2) {
                if (Pattern.matches("[0-9]+",boxSizes.get(0))) {
                    this.boxWidth = Integer.parseInt(boxSizes.get(0));
                    if (Pattern.matches("[0-9]+", boxSizes.get(1))) {
                        this.boxHeigth = Integer.parseInt(boxSizes.get(1));
                    }
                }
            } else {System.out.println("Invalid box size");System.exit(0);}
            s = s.substring(0, s.indexOf("/"));
        }

        String cSet ="";
        if (s.contains("@")) {
            cSet = s.substring(s.indexOf("@")+1);
            if (cSet.length()==0) {
                for (Character c : "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ+".toCharArray()) {
                    sudokuCharSet.add(""+c);
                } 
            } else {
                for (Character c : cSet.toCharArray()) {
                    sudokuCharSet.add(""+c);
                }
            }
            s = s.substring(0, s.indexOf("@"));
        }
        if (this.getIsOverlap()) {
            setSudokuString(s.replaceAll("\\s+", "")); 
            System.out.println("Overlap Sudoku");
            return;
        } 
        ArrayList<Integer> al = parseContent(s, sudokuCharSet);
        this.freeSlots  = new HashSet<Integer>();
        
        this.solveBoardSize(al.size());
        this.board = new Integer[this.boardSize];
        if (boardSize != al.size() || (sudokuCharSet.size() > 0 && sudokuCharSet.size() < boardWidth)) {
            System.out.println("\n Invalid sudoku (" +al.size() +") or charset ("+sudokuCharSet.size()+") size " + boardSize);
            System.exit(0);
        }
        else {
            initTables();
            int index = 0;
            for (int value : al) {
                if (value <= 0) {
                    value = 0;
                    this.freeSlots.add(index);
                }
                setCellValue(index++, value);
            }
            if (this.getSudokuPattern().length() < 1 && !validateSudoku(al)) {
                System.out.print(dumpBoard());
                System.out.println("\n\n Invalid sudoku");
                System.exit(0);
            }
        }
    }

    SudokuBoard(String s) {
        this(s,false);
    }

    private void solveBoardSize(int s) {
        this.boardWidth = (int)Math.sqrt(s);
        if (this.boxWidth == 0) {
            this.boxWidth = (int)Math.sqrt(this.boardWidth);
            if (this.boxWidth * this.boxWidth == this.boardWidth) this.boxHeigth = this.boxWidth;
            else {
                if (this.boardWidth % 5 == 0) {
                int a = this.boardWidth/5;
                this.boxWidth = Math.max( a, this.boardWidth/a);
                this.boxHeigth = this.boardWidth / this.boxWidth;
                } else if (this.boardWidth % 4 == 0) {
                    int a = this.boardWidth/4;
                    this.boxWidth = Math.max( a, this.boardWidth/a);
                    this.boxHeigth = this.boardWidth / this.boxWidth;
                } else if (this.boardWidth % 3 == 0) {
                    int a = this.boardWidth/3;
                    this.boxWidth = Math.max( a, this.boardWidth/a);
                    this.boxHeigth = this.boardWidth / this.boxWidth;
                } else if (this.boardWidth % 2 == 0) {
                    int a = this.boardWidth/2;
                    this.boxWidth = Math.max( a, this.boardWidth/a);
                    this.boxHeigth = this.boardWidth / this.boxWidth;
                } else {
                    this.boxWidth = this.boardWidth;
                    this.boxHeigth = 1;
                }
            }
        }
        if (this.boxWidth * this.boxHeigth != this.boardWidth) {System.out.println("Invalid box size");System.exit(0);}
        this.boardSize = this.boardWidth * this.boardWidth;
        //System.out.println("\n\n " + this.boardSize +" "+ this.boardWidth + " " + this.boxWidth +" " + this.boxHeigth);

    }
    private void initTables() {
        this.optionsList = new ArrayList<HashSet<Integer>>();
        this.extraAreas = new ArrayList<HashSet<Integer>>();
        lines = new HashSet<>();
        values = new HashSet<>();
        for (int i = 0 ; i < this.boardWidth; i++) {
            lines.add(i);
            values.add(i+1);
        }
        for (int i = 0 ; i < this.getBoardSize(); i++) {
            HashSet<Integer> hs2 = new HashSet<Integer>();
            this.optionsList.add(hs2);
        }
        for (int i = 0 ; i < this.getBoardSize(); i++) {
            setOptions(i,this.values);
        }

    }

    public boolean isFilled(int cell) {
        return board[cell] != 0;
    }

    public void setCellValue(int cell, int value) {
        board[cell] = value;
        if (value !=0) {
            int box = (cell / this.boardWidth)/boxHeigth*boxHeigth + (cell % this.boardWidth)/boxWidth;
            optionsList.get(cell).clear();
            removeOptions(value, cell/this.boardWidth, cell%this.boardWidth, box);
        }
        if (isExtraAreas) removeExtraAreaOptions(cell, value);
        if (isBlockAreas) removeBlockAreaOptions(cell, value);
    }

    public void setCellValue(int cell, int value, HashSet<Integer> area) {
        board[cell] = value;
        if (value !=0) {
            optionsList.get(cell).clear();
            removeOptions(value, cell/this.boardWidth, cell%this.boardWidth, area);
        }
    }

    public int getCellValue(int row, int column) {
        return board[row*boardWidth + column];
    }

    public int getCellValue(int cell) {
        return board[cell];
    }

    public String getCellValueChar(int i) {
        if (sudokuCharSet.isEmpty()) {
            return ""+i;
        } else
        return sudokuCharSet.get(i-1);
    }

    public ArrayList<Integer> getAllValues() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        for (int i = 0 ; i < this.boardSize ; i ++) al.add(board[i]);
        return al;
    }
    
    public HashSet<Integer> getFreeSlots() {
        return this.freeSlots;
    }

    public void removeFreeSlots(int r) {
        this.freeSlots.remove(r);
    }
    public void removeFreeSlots(HashSet<Integer> r) {
        this.freeSlots.removeAll(r);
    }

    public int getBoardSize() {
        return this.boardSize;
    }

    public int getBoardWidth() {
        return this.boardWidth;
    }

    public int getBoxWidth() {
        return this.boxWidth;
    }

    public int getBoxHeigth() {
        return this.boxHeigth;
    }

    public void setBoxWidth(int i) {
        this.boxWidth = i;
    }

    public void setBoxHeigth(int i) {
        this.boxHeigth = i;
    }

    public Integer[] getBoard() {
        return this.board;
    }

    public String getSudokuString() {
        return this.sudokuString;
    }

    public void setSudokuString(String s) {
        this.sudokuString = s;
    }

    public HashSet<Integer> getOptions(int i) {
        return this.optionsList.get(i);
    }

    public void setOptions(int i, HashSet<Integer> hs) {
        this.optionsList.get(i).addAll(hs);
    }

    public void removeOptions(int i, HashSet<Integer> hs) {
        this.optionsList.get(i).removeAll(hs);
    }

    public void removeOptions(int i, int o) {
        this.optionsList.get(i).remove(o);
    }

    public void removeOptions(int value, int r, int c, int b) {
        for (int i = 0 ; i < this.boardWidth ; i++) {
            this.removeOptions(r * this.boardWidth + i,  value);
            this.removeOptions(i * this.boardWidth + c,  value);
        }
        int y0 = (b/this.boxHeigth)*this.boxHeigth;
        int x0 = (b%this.boxHeigth)*this.boxWidth;
        for (int y = y0 ; y < (y0+this.boxHeigth); y++) {
            for (int x = x0 ; x < (x0+this.boxWidth); x++) {
                this.removeOptions(y*this.boardWidth +x, value);
            }
        }
    }

    public void removeOptions(int value, int r, int c, HashSet<Integer> area) {
        for (int i = 0 ; i < this.boardWidth ; i++) {
            this.removeOptions(r * this.boardWidth + i,  value);
            this.removeOptions(i * this.boardWidth + c,  value);
        }
        for (int a: area) {
            this.removeOptions(a, value);
        }
    }

    public void removeExtraAreaOptions(int cell, int value) {
        for (HashSet<Integer> a : extraAreas) {
            if (a.contains(cell)) {
                for (int areaCell : a) {
                    this.removeOptions(areaCell, value);
                }
            }
        }
    }

    public void removeBlockAreaOptions(int cell, int value) {
        switch (this.blockAreaType) {
            case 1:
                for (int a : utilities.getKingArea(cell, this.boardWidth)) {
                    this.removeOptions(a, value);
                }
            break;
            case 2:
                for (int a : utilities.getKnightArea(cell, this.boardWidth)) {
                    this.removeOptions(a, value);
                }
            break;
            case 3:
                for (int a : utilities.getCrossArea(cell, this.boardWidth)) {
                    this.removeOptions(a, value-1);
                    this.removeOptions(a, value+1);
                }
            break;
            default:
            break;
        }
    }

    public int getExStart(){
        return exStart;
    }

    public void setExStart(int i){
        this.exStart = i;
    }

    public int getEx3Start(){
        return ex3Start;
    }

    public void setEx3Start(int i){
        this.ex3Start = i;
    }
    
    public int getExLimit(){
        return exLimit;
    }

    public void setExLimit(int i){
        this.exLimit = i;
    }

    public String getSudokuPattern(){
        return this.sudokuPattern.toString();
    }

    public void setExtraArea(HashSet<Integer> area) {
        this.isExtraAreas = true;
        extraAreas.add(area);
    }

    public boolean getIsExtraAreas() {
        return this.isExtraAreas;
    }

    public void setIsOverlap(boolean b) {
        this.isOverlap = b;
    }

    public boolean getIsOverlap() {
        return this.isOverlap;
    }

    public void setBlockArea(int blockAreaType) {
        this.isBlockAreas = true;
        this.blockAreaType = blockAreaType;
    }

    public String getCharSet() {
        StringBuilder content = new StringBuilder();
        if (!sudokuCharSet.isEmpty()) {
            content.append("@");
            String cs = sudokuCharSet.toString().replaceAll(",", "").replaceAll("\\[","").replaceAll("\\]","").replaceAll("\\s+", "");
            content.append(cs);
        }
        return content.toString();
    }

    public ArrayList<HashSet<Integer>> getExtraAreas() {
        return this.extraAreas;
    }

    public boolean validateSudoku(ArrayList<Integer> al) {
        boolean returnValue = true;
        for (int a : al) {
            if (a > boardWidth) {
                System.out.println("Invalid value vs CharSet");
                return false;
            }
        }
        for (int i = 0 ; i < this.boardWidth ; i++) {
            int[]  row = new int[boardWidth+1];
            int[]  col = new int[boardWidth+1];
            int[]  box = new int[boardWidth+1];
            for (int j = 0 ; j < this.boardWidth ; j++) {
                row[al.get(i*boardWidth + j)]++;
                col[al.get(j*boardWidth + i)]++;
                int y0 = (i/this.boxHeigth)*this.boxHeigth;
                int x0 = (i%this.boxHeigth)*this.boxWidth;
                int y = y0 + j/boxWidth;
                int x = x0 + j%boxWidth;
                box[al.get(y*boardWidth+x)]++;
            }
            for (int a = 1; a <= this.boardWidth ; a ++) {
                if (row[a] > 1) {System.out.println("Row "+(i+1)+" double number(s) "); returnValue = false;}
                if (col[a] > 1) {System.out.println("Col "+(i+1)+" double number(s) "); returnValue = false;}
                if (box[a] > 1) {System.out.println("Box "+(i+1)+" double number(s) "); returnValue = false;}
            }
        }
        return returnValue;
    }

    public boolean validateSudoku2() {
        boolean returnValue = true;
        for (int i = 0 ; i < this.boardWidth ; i++) {
            HashSet<Integer> row = new HashSet<Integer>();
            HashSet<Integer> col = new HashSet<Integer>();
            HashSet<Integer> box = new HashSet<Integer>();
            int emptyRow = 0;
            int emptyCol = 0;
            int emptyBox = 0;
            for (int j = 0 ; j < this.boardWidth ; j++) {
                if (!this.isFilled(i*boardWidth + j) && getOptions(i*boardWidth + j).size() ==0) {System.out.println("cell "+(i*boardWidth + j)+" no options "); returnValue = false;}
                if (!this.isFilled(i*boardWidth + j)) {emptyRow++;row.addAll(getOptions(i*boardWidth + j));}
                if (!this.isFilled(j*boardWidth + i)) {emptyCol++;col.addAll(getOptions(j*boardWidth + i));}

                int y0 = (i/this.boxHeigth)*this.boxHeigth;
                int x0 = (i%this.boxHeigth)*this.boxWidth;
                int y = y0 + j/boxWidth;
                int x = x0 + j%boxWidth;
                if (!this.isFilled((y*boardWidth+x))) {emptyBox++; box.addAll(getOptions((y*boardWidth+x)));}
            }
            if (row.size() != emptyRow) {System.out.println("Row "+(i+1)+" invalid "); returnValue = false;}
            if (col.size() != emptyCol) {System.out.println("Col "+(i+1)+" invalid "); returnValue = false;}
            if (box.size() != emptyBox) {System.out.println("Box "+(i+1)+" invalid "); returnValue = false;}
        }
        return returnValue;
    }

    public boolean validateAreas() {
        boolean returnValue = true;
        for (HashSet<Integer> area : this.extraAreas) {
            int[] values = new int[area.size()+1];
            for (int j : area) {
                if (!this.isFilled(j)) {values[this.board[j]]++;}
            }
            for (int k = 1; k <= area.size(); k++) {
                if (values[k] > 1) {System.out.println("Area invalid "); returnValue = false;}
            }
        }
        return returnValue;
    }

    public String dumpBoard() {
        StringBuilder boardAsString = new StringBuilder();
        boardAsString.append("\n  ");
        for (int i = 0 ; i < this.board.length; i++) {
            if (i > 0 && i % boxWidth == 0) boardAsString.append("  ");
            if (i > 0 && i % boardWidth == 0) boardAsString.append("\n  ");
            if (i > 0 && i % boardWidth == 0 && i % (boxHeigth*boardWidth) == 0 && boxHeigth> 1) boardAsString.append("\n  ");
            if (sudokuCharSet.isEmpty()) {
                boardAsString.append(" ");
                if (boardWidth>9 && board[i] <10) boardAsString.append(" ");
                if (boardWidth>99 && board[i] <100) boardAsString.append(" ");
                boardAsString.append(board[i] != 0 ? ""+board[i] : ".");
            } else {
                boardAsString.append(board[i] != 0 ? ""+sudokuCharSet.get(board[i]-1) : ".");
            }
        }
        boardAsString.append("\n");
        return boardAsString.toString();
    }

    public String dumpPattern() {
        StringBuilder patternAsString = new StringBuilder();
        patternAsString.append("\n  ");
        String aCells = "abcdefghijklmnopqrstuvwxyz";
        String area="";
        for (int i = 0 ; i < this.board.length; i++) {
            if (i > 0 && i % boxWidth == 0) patternAsString.append("  ");
            if (i > 0 && i % boardWidth == 0) patternAsString.append("\n  ");
            if (i > 0 && i % boardWidth == 0 && i % (boxHeigth*boardWidth) == 0 && boxHeigth> 1) patternAsString.append("\n  ");
            area =".";
            for (int n = 0; n < extraAreas.size(); n++) {
                if (extraAreas.get(n).contains(i)) {
                    area = aCells.substring(n, n+1);
                    break;
                }
            }
            patternAsString.append(area);
        }
        patternAsString.append("\n");
        return patternAsString.toString();
    }

    private String readSudoku(String fileName) throws FileNotFoundException {
        Scanner fileReader = new Scanner(new File(fileName));
        StringBuilder content = new StringBuilder();
        StringBuilder charSet = new StringBuilder();

        try {
            while (fileReader.hasNext()) {
                String s = fileReader.next();
                if ((s.startsWith("#")) || s.startsWith("!")) {
                    if (fileReader.hasNext()) s = fileReader.nextLine();
                } else if (s.startsWith("@") || s.startsWith("/"))  {
                    charSet.append(s);
                } else if (s.startsWith(":"))  {
                    sudokuPattern.append(s);
                } else {
                    content.append(" ");
                    content.append(s);
                }
            }
        } catch (Exception e) {
          System.out.println("Error: " + e.getMessage());
          System.exit(0);
        } finally {
            if(fileReader !=null){fileReader.close();}
        }
        return content.toString() +  charSet.toString();
    }

    private  ArrayList<Integer> parseContent(String fileContent, ArrayList<String> charSet) {
        ArrayList<Integer> s = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        if (charSet.isEmpty()) {
            fileContent = fileContent.replaceAll("^\\s+", "");
            fileContent = fileContent.replaceAll(",", " ");
            for (String luku : fileContent.split("\\s")) {
                values.add(luku);
            }
        } else {
            if (fileContent.indexOf('!') > 0) fileContent = fileContent.substring(0, fileContent.indexOf('!'));
            if (fileContent.indexOf('#') > 0) fileContent = fileContent.substring(0, fileContent.indexOf('#'));
            fileContent = fileContent.replaceAll(",", " ");
            fileContent = fileContent.replaceAll("\\s+", "");
            for (Character c : fileContent.toCharArray()) {
                values.add(""+c);
            }
            StringBuilder content = new StringBuilder();
            content.append("-");
            content.append(values.toString());
            content.append("@");
            String cs = charSet.toString().replaceAll(",", "").replaceAll("\\[","").replaceAll("\\]","").replaceAll("\\s+", "");
            content.append(cs.substring(0, Math.min(cs.length(),(int)Math.sqrt(values.size()))));
            //System.out.println("  "+content.toString().replaceAll(",", "").replaceAll("\\[","").replaceAll("\\]","").replaceAll("\\s+", "")+"\n");
        }
        for (int i = 0; i < values.size(); i++) {
            int c = 0;
            if (!charSet.isEmpty()) {
                c = charSet.indexOf(""+values.get(i)) + 1;
                if (c < 0) c = 0;
            } else {
                if (Pattern.matches("[0-9]+", values.get(i))) {
                    c = Integer.parseInt(values.get(i));
                } else {
                    c = 0;
                }
            }
            s.add(c);
        }
        return s;
    }
    
    private String getOptionsOutput(int row, int boxRow, int col) {
        StringBuilder returnString = new StringBuilder();
        
        for (int value = boxRow*this.getBoxWidth()+1 ; value < boxRow*this.getBoxWidth()+this.getBoxWidth()+1 ; value++) {
            if (this.optionsList.get(row*this.getBoardWidth() + col ).contains(value)) {
                returnString.append(this.sudokuCharSet.get(value-1));
                returnString.append("");
            } else returnString.append(" ");
        }
        if ((col+1)%this.boxWidth==0) returnString.append("| "); else returnString.append(". "); 
        if (this.optionsList.get(row*this.getBoardWidth() + col ).isEmpty()) {
            returnString = new StringBuilder();
            if (this.getBoxWidth() < 4) {
                for (int t=0; t < this.getBoxWidth(); t++) {
                        if ((this.board[row*this.boardWidth + col]-1) != -1)
                            returnString.append(this.sudokuCharSet.get(this.board[row*this.boardWidth + col]-1));
                        else 
                            returnString.append("-");
                }
            } else {
                returnString.append(" ");
                for (int t=0; t < this.getBoxWidth()-2; t++) {
                    if (boxRow==0 || boxRow==this.boxHeigth-1) {
                        returnString.append(" ");
                    } else {
                        returnString.append(this.sudokuCharSet.get(this.board[row*this.boardWidth + col]-1));
                    }
                }
            returnString.append(" ");
            }
            if ((col+1)%this.boxWidth==0) returnString.append("| "); else returnString.append(". ");
        }
       
        return returnString.toString();
    }

    public void showOptions() {
        System.out.println();
        if (!sudokuCharSet.isEmpty() || this.getBoardWidth() < 10) {
        if (sudokuCharSet.isEmpty())  sudokuCharSet = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9"));
        System.out.print("+-");
        for (int a = 0 ; a < this.getBoardWidth() ; a++) {
            for (int b = 0 ; b < this.getBoxWidth(); b++) {
                System.out.print("-");
            }
            System.out.print("+-");
        }
        System.out.println();
        
        for (int row = 0 ; row < this.getBoardWidth() ; row ++) {
            
            for (int boxRow = 0 ; boxRow < this.getBoxHeigth() ; boxRow ++) {
                System.out.print("| ");    
                for (int col = 0 ; col < this.getBoardWidth() ; col ++) {
                    //System.out.print(haetulostus(sudoku, mahdolliset, rivi, ruuturivi, ruutu )); 
                    System.out.print(getOptionsOutput(row, boxRow, col));
                    //if ((col+1)%this.getBoxWidth()==0) System.out.print("| ");
                }
                System.out.println(); 
            }

            if ((row+1)%this.boxHeigth == 0) {
                System.out.print("+-");
                for (int a = 0 ; a < this.getBoardWidth() ; a++) {
                    for (int b = 0 ; b < this.getBoxWidth(); b++) {
                        System.out.print("-");
                    }
                    System.out.print("+-");
                }
            } else {
                System.out.print("+.");
                for (int a = 0 ; a < this.getBoardWidth() ; a++) {
                    for (int b = 0 ; b < this.getBoxWidth(); b++) {
                        System.out.print(".");
                    }
                    System.out.print("+.");
                }
            }
            System.out.println();
            
        }
    } else {
        System.out.println();
        System.out.println(dumpBoard());
        for (int i = 0; i < this.getBoardWidth(); i++) {
            for (int j = 0; j < this.getBoardWidth(); j++) {
                System.out.print(this.optionsList.get(i*this.getBoardWidth()+j));
                if ((j+1)%this.getBoxWidth()==0) System.out.print("   ");
            }
            System.out.println();
            if ((i+1)%this.getBoxHeigth()==0) System.out.println();
        }
    }
    }
    
    public HashSet<String> getValueChars(HashSet<Integer> ts) {
        HashSet<String> rv = new HashSet<String>();
            for (int c : ts) {
                rv.add(getCellValueChar(c));
            }
        return rv;

    }

    public int getBox(int cell) {
        
        return (cell/boardWidth)/boxHeigth*(boardWidth/boxWidth) + (cell%boardWidth)/boxWidth +1;
        
    }

}