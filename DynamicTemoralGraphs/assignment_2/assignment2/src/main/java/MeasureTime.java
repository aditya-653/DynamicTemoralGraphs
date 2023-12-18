import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

// this class was for assignment 1
// it is unchanged and we dont really need it here

public class MeasureTime {


    public static void measureReadGraph() throws FileNotFoundException {
        long startTime = System.nanoTime();
        List<DataObjM> rawData = Util.readGraph();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;


        System.out.println("time for reading the whole dataset" +
                ", Duration: " + duration + " nanoseconds");

    }

    public static void measureHash() throws FileNotFoundException {

        long startTime = System.nanoTime();
        DynamicTemporalGraph d = new DynamicTemporalGraph(Util.readGraph());
        long endTime = System.nanoTime();
        long duration = endTime - startTime;


        System.out.println("time for creating object" +
                ", Duration: " + duration + " nanoseconds");

    }
    public static void main(String[] args) throws FileNotFoundException {
        measureReadGraph();
        measureHash();

        DynamicTemporalGraph d = new DynamicTemporalGraph(Util.readGraph());

        long startTime;
        long endTime;
        long duration;

        for (int i = 0; i < 10; i++){

            System.out.println("----------------------------------------");

            for (Integer time : Arrays.asList(1307505600, 1307592000, 1307160000, 1307073600, 1306987200,
                    1304136000, 1309752000, 1338868800, 1365393600, 1353214800)) {

                startTime = System.nanoTime();
                d.getSnapshot(time);
                endTime = System.nanoTime();

                duration = endTime - startTime;
                System.out.println("time for get the snapshot" +
                        ", Duration: " + duration + " nanoseconds");
            }

            System.out.println("----------------------------------------");

        }


        for (int i = 0; i < 10; i++){

            System.out.println("----------------------------------------");

            for (Integer time : Arrays.asList(1307505600, 1307592000, 1307160000, 1307073600, 1306987200,
                    1304136000, 1309752000, 1338868800, 1365393600, 1353214800)) {

                startTime = System.nanoTime();
                d.getSnapshotAggregated(DynamicTemporalGraph.StartTime, time);
                endTime = System.nanoTime();

                duration = endTime - startTime;
                System.out.println("time for get the aggregated snapshot" +
                        ", Duration: " + duration + " nanoseconds");
            }

            System.out.println("----------------------------------------");

        }





    }

}
