import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Algorithmen {
    public static int[][] findBestMatching(double[][] similarityMatrix) {
        int numRows = similarityMatrix.length;
        int numCols = similarityMatrix[0].length;

        // Create a bipartite graph with a source, a sink, and edges between rows and columns
        Graph<Integer, DefaultEdge> bipartiteGraph = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);
        int source = 0;
        int sink = numRows + numCols + 1;

        // Add vertices
        for (int i = 1; i <= numRows; i++) {
            bipartiteGraph.addVertex(i);
        }
        for (int j = numRows + 1; j <= numRows + numCols; j++) {
            bipartiteGraph.addVertex(j);
        }

        // Add edges with capacities based on the similarity matrix
        for (int i = 1; i <= numRows; i++) {
            for (int j = numRows + 1; j <= numRows + numCols; j++) {
                double capacity = 1 - similarityMatrix[i - 1][j - numRows - 1];
                //System.out.println(capacity);


                bipartiteGraph.addEdge(i, j);
                bipartiteGraph.setEdgeWeight(bipartiteGraph.getEdge(i, j), capacity);
            }
        }

        // Add edges from source to rows and from columns to sink
        for (int i = 1; i <= numRows; i++) {


            bipartiteGraph.addVertex(source);
            bipartiteGraph.addVertex(i);


            bipartiteGraph.addEdge(source, i);
            bipartiteGraph.setEdgeWeight(bipartiteGraph.getEdge(source, i), 1.0);
        }
        for (int j = numRows + 1; j <= numRows + numCols; j++) {
            bipartiteGraph.addVertex(j);
            bipartiteGraph.addVertex(sink);
            bipartiteGraph.addEdge(j, sink);
            bipartiteGraph.setEdgeWeight(bipartiteGraph.getEdge(j, sink), 1.0);
        }

        // Use Edmonds-Karp algorithm to find the maximum flow
        EdmondsKarpMFImpl<Integer, DefaultEdge> edmondsKarp = new EdmondsKarpMFImpl<>(bipartiteGraph);
        MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> maximumFlow = edmondsKarp.getMaximumFlow(source, sink);


        // Extract matching indices from the residual graph
        int[][] matching = new int[numRows][2];
        int index = 0;
        for (int i = 1; i <= numRows; i++) {
            for (DefaultEdge edge : bipartiteGraph.edgesOf(i)) {
                if (maximumFlow.getFlow(edge) > 0) {
                    int targetVertex = bipartiteGraph.getEdgeTarget(edge);
                    matching[index++] = new int[]{i, targetVertex};
                    break; // Assuming each vertex has at most one matched edge
                }
            }
        }

        return matching;
    }


    public static double[][] computeSimilarityMatrix(List<Set<Integer>> communities1, List<Set<Integer>> communities2) {
        double[][] similarityMatrix = new double[communities1.size()][communities2.size()];
        for (int i = 0; i < communities1.size(); i++) {
            for (int j = 0; j < communities2.size(); j++) {
                similarityMatrix[i][j] = Util.jaccardSimilarity(communities1.get(i), communities2.get(j));
            }
        }
        return similarityMatrix;
    }


}
