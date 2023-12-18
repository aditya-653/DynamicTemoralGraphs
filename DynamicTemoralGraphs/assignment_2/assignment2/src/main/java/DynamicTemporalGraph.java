import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.GirvanNewmanClustering;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class DynamicTemporalGraph {

    public static final int StartTime = 1289192400;
    // 2010-11-8

    public static final int EndTime = 1453438800;
    // 2016-1-22

    public static final int fourWeeks = 2_419_200;

    public static final int oneWeek = 604_800;
            //for 28-day

    public static List<DataObjM> rawData;
    public static Set<Integer> timestamps;
    public static Map<Integer, List<DataObjM>> timeMap;

    public static List<EdgeObj> edgeList;


    public DynamicTemporalGraph(List<DataObjM> rawData) throws FileNotFoundException {
        this.rawData = rawData;
        getHashmap();
        setEdgeList(oneWeek);
    }

    // for assignment 1, there was no time information, now we create a list of edges with time
    // since we only have start time, the duration should be set manually, for example, we inspect
    // the transactions in 7 days or 28 days.
    public void setEdgeList(int timeInterval){
        List<EdgeObj> tempList = new ArrayList<>();
        int id = 1;
        for(DataObjM d : rawData){
            EdgeObj newEdge = new EdgeObj(d.time, d.time + timeInterval, d.s, d.r, id);
            id++;
            tempList.add(newEdge);
        }
        edgeList = tempList;
    }

    // read csv file into a sorted list of data objects


    public void getHashmap(){
        // sort hashmap based on its key
        List<DataObjM> originalList = rawData;
        Set<Integer> linkedHashSet = new LinkedHashSet<>();


        // Create a HashMap to store the indices of elements in the PriorityQueue
        Map<Integer, List<DataObjM>> indexMap = new HashMap<>();

        for (DataObjM element : originalList) {
            int timepoint = element.time;
            linkedHashSet.add(timepoint);
            if(indexMap.get(timepoint) == null){
                List<DataObjM> tempList = new ArrayList<>();
                tempList.add(element);
                indexMap.put(timepoint, tempList);


            }else{
                List<DataObjM> tempList = indexMap.get(timepoint);
                tempList.add(element);
                indexMap.put(timepoint, tempList);
            }
        }

        timestamps = linkedHashSet;
        timeMap = indexMap;

        return;

    }


    public Graph<Integer, DefaultEdge> getSnapshot(int t){
        Map<Integer, List<DataObjM>> originalMap = timeMap;
        Graph<Integer, DefaultEdge> simpleGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        List<DataObjM> objList =  originalMap.get(t);
        addToGraph(simpleGraph, objList);
        return simpleGraph;
    }


    public static Graph<Integer, DefaultEdge> addToGraph(Graph<Integer, DefaultEdge> graph, List<DataObjM> objList){
        Set<Integer> vSet = new HashSet<>();

        //1. add v
        for (DataObjM m : objList){
            vSet.add(m.s);
            vSet.add(m.r);
        }// if there is no duplications this step shall be skipped
        for(Integer i : vSet){
            graph.addVertex(i);
        }
        //2. add edges
        for(DataObjM m : objList){
            graph.addEdge(m.s, m.r);
        }

        return graph;
    }


    public Graph<Integer, DefaultEdge> getSnapshotAggregated(int t1, int t2) {
        Map<Integer, List<DataObjM>> originalMap = timeMap;
        Graph<Integer, DefaultEdge> simpleGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int time : timestamps){
            if(time < t1){
                continue;
            }else if(time <= t2){
                List<DataObjM> objList =  originalMap.get(time);
                addToGraph(simpleGraph, objList);
            }else{
                break;
            }
        }

        return simpleGraph;
    }


    // Method to perform Girvan-Newman clustering on a snapshot
    private static List<Set<Integer>> clusterGraph(Graph<Integer, DefaultEdge> graph, int k) {
        // Use Girvan-Newman algorithm to find communities
        //System.out.println("k value " + k);
        GirvanNewmanClustering<Integer, DefaultEdge> clustering = new GirvanNewmanClustering<>(graph, k);
        return clustering.getClustering().getClusters();
    }

    // wrapped method to make sure the k is correct.
    private static List<Set<Integer>> clusterGraphWrapper(Graph<Integer, DefaultEdge> graph, int k){
        int max = graph.vertexSet().size();
        List<Set<Integer>> ccs = new ConnectivityInspector<>(graph).connectedSets();
        int min = Math.max(1, ccs.size());
        //System.out.println("amount of vertices: " +  max);
        //System.out.println("connected parts: " +  ccs.size());

        if( k >= min || k <= max){
            return clusterGraph(graph, k);
        }else if(k > max){
            return clusterGraph(graph, max);
        }else{
            return clusterGraph(graph, min);
        }

    }




    public static void main(String[] args) throws IOException {

        //-------------------------------------------
        //           begin assignment 1
        // main method to show the community detection and similarities


        DynamicTemporalGraph d = new DynamicTemporalGraph(Util.readGraph2());

        // Take a snapshot at a specific time 't'
        int snapshotTime = 1387170000; // Snapshot timestamp
        Graph<Integer, DefaultEdge> snapshotGraph = d.getSnapshotAggregated(StartTime, snapshotTime);

        // Take a snapshot at a specific time 't'
        int snapshotTime2 = 1388293200; // Snapshot timestamp
        Graph<Integer, DefaultEdge> snapshotGraph2 = d.getSnapshotAggregated(StartTime, snapshotTime2);


        //-------------------------------------------------------------------
        // end assignment 1

        //task 2
        //output the edges into a file
        //Util.outputEdgeFile(edgeList);

        //task 2
        //read graph from the txt file of edges
        Graph<Integer, DefaultEdge> graph2 = Util.readEdgeFileToGraph();
        //Util.printGraph(graph2);



        // Implement clusterGraph method to create communities



        List<Set<Integer>> communities1 = clusterGraphWrapper(snapshotGraph, 4);

        List<Set<Integer>> communities2 = clusterGraphWrapper(snapshotGraph2, 4);

        Util.printCommunities(communities1);
        Util.printCommunities(communities2);






        double[][] similarityMatrix = Algorithmen.computeSimilarityMatrix(communities1, communities2);



        int[][] matching = Algorithmen.findBestMatching(similarityMatrix);


        for(int[] list : matching){
            System.out.println();
            for (int e : list){
                System.out.print(e + " ");}
        }

        System.out.println("---------------------------");

        // Analyze matched pairs of communities
        System.out.println("\nMatched Communities:");
        for (int[] match : matching) {
            int community1Index = match[0] -1;
            int community2Index = match[1] -1;
            System.out.println(communities1.get(community1Index) + " <-> " + communities2.get(community2Index));
        }



        // Print communities
        System.out.println("\nCommunities in Snapshot Graph 1:");
        Util.printCommunities(communities1);

        System.out.println("\nCommunities in Snapshot Graph 2:");
        Util.printCommunities(communities2);


        // Implement Jaccard Similarity to find similarity among two communities


        for (Set<Integer> community1 : communities1) {
            for (Set<Integer> community2 : communities2) {
                double similarity = Util.jaccardSimilarity(community1, community2);

                if (similarity > 0.5) {
                    // If similarity exceeds threshold, consider communities as similar or related
                    System.out.println("\nSimilar Communities: ");
                    System.out.println(community1);
                    System.out.println(community2);
                    System.out.println("Jaccard Similarity: " + similarity + "\n");
                } else{
                    //System.out.println("Jaccard Similarity: " + similarity + "\n");
                }
            }
        }

         



    }
}
