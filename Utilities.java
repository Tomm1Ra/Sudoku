import java.util.*;


public class Utilities {
    private ArrayList<HashSet<Integer>>  returnList;
    private HashSet<Integer> fullSet;

    public Utilities(HashSet<Integer> fullSet) {
        returnList = new ArrayList<HashSet<Integer>>();
        this.fullSet = fullSet;
    }

    public void combinationUtil(int arr[], int data[], int start, int end, int index, int r) {
        if (index == r) {
           HashSet<Integer> ts = new HashSet<Integer>();
            for (int j=0; j<r; j++) ts.add(data[j]);
            returnList.add(ts);
            return;
        }
 
        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    public ArrayList<HashSet<Integer>> getList() {
        return this.returnList;
    }

    public void clearList() {
        this.returnList = new ArrayList<>();
    }

    public HashSet<Integer> getValues(Integer []s, int r, int c, int box, int bw, int sh, int sw) {
        HashSet<Integer> values = new HashSet<Integer>();
        values.addAll(this.fullSet);
        int boxRow = box/sh * sh, boxColumn = box %  sh * sw;
        for (int rowOffset=0; rowOffset<sh; rowOffset++) {
            for (int columnOffset=0; columnOffset<sw; columnOffset++) {
                values.remove(s[(boxRow+rowOffset)*bw + boxColumn+columnOffset]);
            }
        }
        for (int row=0; row < bw; row++) values.remove(s[row*bw + c]);
        for (int column=0; column < bw; column++) values.remove(s[r*bw + column]);
        return values;
    }

    public String resizeString(String s, int n) {
        StringBuilder sb = new StringBuilder();
        if (s.length() > n) sb.append(s.substring(0,n));
        if (s.length() < n) {
            sb.append(s);
            while (sb.length() < n){
                sb.append("0");
             } 
        }
        return sb.toString();
    }

    public HashSet<Integer> getKingArea(int n, int size) {
        HashSet<Integer> hs = new HashSet<Integer>();
        int y = n/size;
        int x = n%size;
        for (int ys = Math.max(y-1,0); ys < Math.min(size,y+2); ys++) {
            for (int xs = Math.max(x-1,0); xs < Math.min(size,x+2); xs++) {
                hs.add(ys*size + xs);
            }
        }
        return hs;
    }

    public HashSet<Integer> getKnightArea(int n, int size) {
        HashSet<Integer> hs = new HashSet<Integer>();
        int y = n/size;
        int x = n%size;
        if (y-2 >=0 && x-1 >=0) hs.add((y-2)*size + x-1);
        if (y-2 >=0 && x+1 < size) hs.add((y-2)*size + x+1);
        if (y+2 < size && x-1 >= 0) hs.add((y+2)*size + x-1);
        if (y+2 < size && x+1 < size) hs.add((y+2)*size + x+1);
        if (y+1 < size && x+2 < size) hs.add((y+1)*size + x+2);
        if (y-1 >= 0 && x+2 < size) hs.add((y-1)*size + x+2);
        if (y+1 < size && x-2 >= 0) hs.add((y+1)*size + x-2);
        if (y-1 >= 0 && x-2 >= 0) hs.add((y-1)*size + x-2);
        return hs;
    }

    public HashSet<Integer> getCrossArea(int n, int size) {
        HashSet<Integer> hs = new HashSet<Integer>();
        int y = n/size;
        int x = n%size;
        if (y-1 >=0 ) hs.add((y-1)*size + x);
        if (y+1 < size) hs.add((y+1)*size + x);
        if (x+1 < size) hs.add(y*size + x+1);
        if (x-1 >= 0) hs.add(y*size + x-1);
        return hs;
    }
}