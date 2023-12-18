// this class defines the object of edges, it helps with
// reading data from our csv file
public class DataObjM {
    public int s;
    public int r;
    public int rank;
    public int time;

    public DataObjM(int s, int r, int rank, int time) {
        this.s = s;
        this.r = r;
        this.rank = rank;
        this.time = time;
    }

    public DataObjM(int s, int r, int time) {
        this.s = s;
        this.r = r;
        this.time = time;
    }

    public String toString(){
        return "s: " + s + ", r: " + r + ", t: " + time;

    }
}
