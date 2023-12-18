public class EdgeObj {
    public int ts; // time of creation
    public int td; // time of deletion
    public int s; //sender
    public int d; //receiver
    public int id;


    public EdgeObj(int startT, int endT, int sender, int receiver, int id) {
        this.ts = startT;
        this.td = endT;
        this.s = sender;
        this.d = receiver;
        this.id = id;
    }

    public String insertRowString(){
        return this.id + ", " + this.s + ", " + this.d + ", " + this.ts;
    }

    public String deleteRowString(){
        return this.id + ", " + this.s + ", " + this.d + ", " + this.td;
    }
}
