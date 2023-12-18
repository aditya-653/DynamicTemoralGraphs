import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

// we use this class the hide unimportant information
public class Util {

    // there are two readGraph methods, the first for whole data set ,
    // the second for a smaller data set
    public static List<DataObjM> readGraph() throws FileNotFoundException {

        Path currentRelativePath = Paths.get("");
        String s1 = currentRelativePath.toAbsolutePath().toString()
                + "/data.csv";
        String DATABASE_FILE_PATH = s1 ;
        //System.out.println(s1);


        CSVReader csvReader = new CSVReader(new FileReader(DATABASE_FILE_PATH));
        Spliterator<String[]> it = csvReader.spliterator();
        it.tryAdvance(e -> {});
        List<DataObjM> records = StreamSupport
                .stream(it, false)
                .map(fields ->
                        new DataObjM(
                                !fields[0].isEmpty() ? Integer.parseInt(fields[0]) : 0, // lat
                                !fields[1].isEmpty() ? Integer.parseInt(fields[1]) : 0,
                                !fields[2].isEmpty() ? Integer.parseInt(fields[2]) : 0, // lat
                                !fields[3].isEmpty() ? Integer.parseInt(fields[3]) : 0
                        )).toList();

        List<DataObjM> recordSorted = records
                .stream()
                .sorted(Comparator.comparingDouble((DataObjM c) -> c.time))
                .toList();


        return recordSorted;
    }


    public static List<DataObjM> readGraph2() throws FileNotFoundException {

        Path currentRelativePath = Paths.get("");
        String s1 = currentRelativePath.toAbsolutePath().toString()
                + "/bitcoin_copy.csv";
        String DATABASE_FILE_PATH = s1 ;
        //System.out.println(s1);

        CSVReader csvReader = new CSVReaderBuilder(new FileReader(DATABASE_FILE_PATH))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build();


        Spliterator<String[]> it = csvReader.spliterator();
        it.tryAdvance(e -> {});
        List<DataObjM> records = StreamSupport
                .stream(it, false)
                .map(fields ->
                        new DataObjM(
                                !fields[0].isEmpty() ? Integer.parseInt(fields[0]) : 0, // lat
                                !fields[1].isEmpty() ? Integer.parseInt(fields[1]) : 0,
                                !fields[3].isEmpty() ? Integer.parseInt(fields[3]) : 0
                        )).toList();

        List<DataObjM> recordSorted = records
                .stream()
                .sorted(Comparator.comparingDouble((DataObjM c) -> c.time))
                .toList();


        return recordSorted;
    }


    // create graph directly from the edge file()
    public static Graph<Integer, DefaultEdge> readEdgeFileToGraph() throws IOException {

        Path currentRelativePath = Paths.get("");
        String s1 = currentRelativePath.toAbsolutePath().toString()
                + "/test2.txt";
        String DATABASE_FILE_PATH = s1 ;
        //System.out.println(s1);



        BufferedReader csvReader;
        csvReader = new BufferedReader(new FileReader(s1)); // csv file path
        String row = csvReader.readLine(); //skip the first line
        List<EdgeObj> edges = new ArrayList<>();

        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");  // in data you have each column value for each row
            row = csvReader.readLine();
            String[] secondRow = row.split(",");
            int startTime = Integer.parseInt(data[3].trim());
            int endTime = Integer.parseInt(secondRow[3].trim());
            int sender = Integer.parseInt(data[1].trim());
            int receiver = Integer.parseInt(data[2].trim());
            int id = Integer.parseInt(data[0].trim());
            EdgeObj e = new EdgeObj(startTime, endTime, sender, receiver, id);
            edges.add(e);
        }

        Graph<Integer, DefaultEdge> graph = createGraphFromEdgeList(edges);


        return graph;



    }
    // help method for the method above

    public static Graph<Integer, DefaultEdge> createGraphFromEdgeList(List<EdgeObj> edgeList){
        Graph<Integer, DefaultEdge> simpleGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (EdgeObj e : edgeList){
            simpleGraph.addVertex(e.s);
            simpleGraph.addVertex(e.d);
            simpleGraph.addEdge(e.s, e.d);
        }

        return simpleGraph;

    }






    public static void printGraph(Graph<Integer, DefaultEdge> graph){
        DOTExporter<Integer, DefaultEdge> exporter =
                new DOTExporter<>(v -> v.toString());

        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        System.out.println(writer);
    }


    public static void outputEdgeFileCompleted(){
        //
    }

    // create a graph from a list of edges
    public static void outputEdgeFile(List<EdgeObj> edgeList){

        try{
            FileWriter fileWriter = new FileWriter("edgeListOut.txt");
            fileWriter.write("id, s, d, ts/td");
            for(EdgeObj e : edgeList){
                fileWriter.write("\n");
                fileWriter.write(e.insertRowString());
                fileWriter.write("\n");
                fileWriter.write(e.deleteRowString());
            }


            fileWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // create a graph from printed file of edges

    public static void readEdgeFile(){

    }

    public static void printCommunities(List<Set<Integer>> communities) {
        // Print the communities
        for (int i = 0; i < communities.size(); i++) {
            System.out.println("Community " + (i + 1) + ": " + communities.get(i));
        }
    }

    // Method to compute Jaccard similarity
    public static double jaccardSimilarity(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) {
            return 0.0; // If the union is empty, return 0 to avoid division by zero
        } else {
            return (double) intersection.size() / union.size();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        readGraph2();
        //TODO: the path was changed manually and should be corrected
    }

}
