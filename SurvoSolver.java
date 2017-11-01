import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors.*;

public class SurvoSolver {
    private int [] resultY;
    private int [] resultX;
    private int [] board;
    private int width;
    private int heigth;
    private ArrayList<Integer> fullSet;

    public SurvoSolver() {
        this.width = 5;
        this.heigth = 3;
    }

    private String readSurvo(String fileName) throws FileNotFoundException {
        
        if (fileName.charAt(0) == '-') return fileName.substring(1);
        Scanner fileReader = new Scanner(new File(fileName));
        StringBuilder content = new StringBuilder();
        try {
            while (fileReader.hasNext()) {
                String s = fileReader.next();
                if ((s.startsWith("#")) || s.startsWith("!")) {
                    if (fileReader.hasNext()) s = fileReader.nextLine();
                } else {
                    content.append(" ");
                    content.append(s);
                }
            }
        } catch (Exception e) {
          System.out.println("Error: " + e.getMessage());
        } finally {
            if(fileReader !=null){fileReader.close();}
        }
        return content.toString();
    }

    private  ArrayList<Integer> parseContent(String fileContent) {
        ArrayList<Integer> s = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        
        fileContent = fileContent.replaceAll("^\\s+", "");
        fileContent = fileContent.replaceAll(",", " ");
        fileContent = fileContent.replaceAll("\\s+", " ");
        for (String luku : fileContent.split("\\s")) {
            values.add(luku);
        }
        for (int i = 0; i < values.size(); i++) {
            int c = 0;
            if (Pattern.matches("@[0-9]+", values.get(i))) {
                this.width = Integer.parseInt(values.get(i).substring(1));
                if (Pattern.matches("[0-9]+", values.get(i+1))) {
                    this.heigth = Integer.parseInt(values.get(++i));
                }
            } else  {
                if (Pattern.matches("[0-9]+", values.get(i))) {
                    c = Integer.parseInt(values.get(i));
                } else {
                    c = 0;
                }
                s.add(c);
            }   
        }
        return s;
    }

    private void setContent(ArrayList<Integer> al) {
        this.board = new int[this.width * this.heigth];
        this.resultY = new int[this.heigth];
        this.resultX = new int[this.width];

        if (al.size() != board.length + this.heigth + this.width) {
            System.out.println("\n Invalid puzzle");
            System.out.println(al.size() + " / " +(board.length + this.heigth + this.width)+ " elements in input" );
            System.exit(0);
        }
        int i = 0, ib = 0, ix = 0, iy = 0;
        for (int y = 0; y <= this.heigth; y++) {
            for (int x = 0; x <= this.width; x++) {
                if (x < this.width && y < this.heigth) this.board[ib++] = al.get(i++);
                else if (y == this.heigth && x < this.width) resultX[ix++] = al.get(i++);
                else if (x == this.width && y < this.heigth) resultY[iy++] = al.get(i++);
            }
        }
        int sumB=0, sumY=0, sumX=0;
        for (int b = 1 ; b <= board.length ; b++) sumB += b;
        for (int h = 0 ; h < this.heigth ; h++) sumY += resultY[h];
        for (int w = 0 ; w < this.width ; w++) sumX += resultX[w];
        if (sumB != sumX || sumB != sumY) {
            System.out.println("\n Invalid puzzle");
            System.out.println(" Sums");
            System.out.println(" Board      :" +sumB);
            System.out.println(" Horizontal :" +sumX);
            System.out.println(" Vertictal  :" +sumY);
            this.printBoard();
            System.exit(0);
        }
        fullSet = new ArrayList<Integer>();
        for (int c = 1 ; c <= board.length ; c++) {
            fullSet.add(c);
        }
    }

    private HashSet<Integer> getRange(int min, int max) {
        HashSet<Integer> returnSet = new HashSet<Integer>();
        returnSet.addAll(fullSet.subList(min-1, max));
        return returnSet;
    }

    public HashSet<Integer> getValues(Integer []s, int y, int x) {
        HashSet<Integer> hs = new HashSet<Integer>();
        for (int c = 0 ; c < board.length; c++) {
            if (s[c] != 0) hs.add(s[c]);
        }
        HashSet<Integer> v = getRange(1 , board.length);
        v.removeAll(hs);
        int numbers[] = new int[v.size()];
        int l = 0;
        for (int n : v) {numbers[l++] = n;}
        Arrays.sort(numbers);

        int sumY = 0, sumX = 0, emptyX = 0, emptyY = 0;
        int min = 1, max = board.length;

        for (int i = 0 ; i < width ; i++) {
            sumY += s[y*width + i];
            if (s[y*width + i] == 0) emptyY++;
        }
        for (int j = 0 ; j < heigth ; j++) {
            sumX += s[j*width + x];
            if (s[j*width + x] == 0) emptyX++;
        }
        int loX = 0, loY=0, hiX=0, hiY=0;
        for (int iy = 1 ; iy < emptyY ; iy++) {
            loY += numbers[iy-1];
            hiY += numbers[numbers.length-iy];
        }
        for (int ix = 1 ; ix < emptyX ; ix++) {
            loX += numbers[ix-1];
            hiX += numbers[numbers.length-ix];
        }
        min = Math.max(min,resultX[x] - sumX - hiX);
        min = Math.max(min,resultY[y] - sumY - hiY);
        max = Math.min(max,resultX[x] - sumX - loX);
        max = Math.min(max,resultY[y] - sumY - loY);
        if (min > max) return new HashSet<Integer>();
        HashSet<Integer> values = getRange(min , max);
        values.removeAll(hs);
        return values;
    }

    private boolean findSolution(Integer[] s, int index) {
        int [] sumY = new int[heigth];
        int [] sumX = new int[width];
        for (int y0 = 0 ; y0 < heigth ; y0++) {
            for (int x0 = 0 ; x0 < width ; x0++) {
                sumY[y0] += s[y0*width + x0];
                sumX[x0] += s[y0*width + x0];
            }
        }

        boolean ready = true;
        for (int y3 = 0 ; y3 < heigth ; y3++) {
            if (sumY[y3] != resultY[y3]) ready = false;
            if (!ready) break;
        }
        for (int x3 = 0 ; x3 < width ; x3++) {
            if (sumX[x3] != resultX[x3]) ready = false;
            if (!ready) break;
        }

        if (ready)  {
            for (int i = 0 ; i < board.length ; i ++) {
                board[i] = s[i];
            }
            return true;
        }
        if (s[index] !=0 ) return findSolution(s, index+1);
        HashSet<Integer> optionsForCell = getValues(s, index/width, index%width);
        for (int option : optionsForCell) {
            s[index] = option;
            if (findSolution(s, index+1)) return true;
            else {
                s[index] = 0;
            }
        }
        return false;
    } 

    private void printBoard() {
        int ib = 0, ix = 0, iy = 0;
        System.out.println("\n\n");
        for (int y = 0; y <= this.heigth; y++) {
            System.out.print("  ");
            for (int x = 0; x <= this.width; x++) {
                if (x < this.width && y < this.heigth) {
                    if (board[ib] < 10) System.out.print(" ");
                    if (board[ib] < 100) System.out.print(" ");
                    if (board[ib] == 0) System.out.print(". "); 
                    else System.out.print(board[ib]+" ");
                    ib++;
                }
                else if (x == this.width && y < this.heigth) {
                    if (resultY[iy] < 10) System.out.print(" ");
                    if (resultY[iy] < 100) System.out.print(" ");
                    System.out.print("  "+resultY[iy++]);
                }
                else if (y == this.heigth && x < this.width) {
                    if (resultX[ix] < 10) System.out.print(" ");
                    if (resultX[ix] < 100) System.out.print(" ");
                    System.out.print(resultX[ix++]+" ");
                }
            }
            System.out.println();
            if (y+1 == this.heigth) System.out.println();
        }
    }

    public void solve(){
        Integer [] a = new Integer[board.length];
        for (int i = 0 ; i < board.length ; i++) {
            a[i] = board[i];
        }
        findSolution(a, 0); 
    }

    public static void main(String[] args) {
        String line = args[0];
        long startTime = System.currentTimeMillis();
        SurvoSolver survo = new SurvoSolver();
        try {
            String s = survo.readSurvo(line);
            ArrayList<Integer> al = survo.parseContent(s);
            survo.setContent(al);
        } catch(FileNotFoundException e) {System.out.println("File " +line+" not found");System.exit(0);}
        survo.printBoard();
        survo.solve();
        survo.printBoard();
        System.out.println("\n   # " + (System.currentTimeMillis() - startTime) + "ms");
    }
}