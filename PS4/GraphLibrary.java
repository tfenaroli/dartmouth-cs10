import java.util.*;

/**
 * Graph library methods
 * @author Thomas Fenaroli, Spring 2021, CS10
 * @author Adam Budin, Spring 2021, CS10
 */
public class GraphLibrary {

    /**
     * sorts vertices by in degree
     * @param g     graph
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return sorted vertices by degree
     */
    public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
        ArrayList<V> sortedVertices = new ArrayList<>();
        for (V vertex: g.vertices()) {
            sortedVertices.add(vertex);
        }

        Collections.sort(sortedVertices, Comparator.comparingInt(g::inDegree));

        return sortedVertices;
    }

    /**
     * sorts vertices by average separation
     * @param g     graph
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return sorted vertices by average separation
     */
    public static <V,E> List<V> verticesBySeparation(Graph<V,E> g) {
        Map<V, Double> separationMap = new HashMap<>();
        ArrayList<V> sortedVertices = new ArrayList<>();

        for (V vertex: g.vertices()) {
            sortedVertices.add(vertex);
            Graph tree = bfs(g, vertex);
            double avgSeparation = averageSeparation(tree, vertex);
            separationMap.put(vertex, avgSeparation);
        }

        /**
         * comparator for actors
         */
        class ActorComparator implements Comparator<V> {
            @Override
            public int compare(V v1, V v2) {
                return (int)(Math.signum(separationMap.get(v1) - separationMap.get(v2)));
            }
        }
        ActorComparator comparator = new ActorComparator();
        Collections.sort(sortedVertices, comparator);

        return sortedVertices;
    }

    /**
     * performs breadth first search
     * @param g     graph
     * @param source    source vertex
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return  path tree
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
        Graph<V,E> pathTree = new AdjacencyMapGraph<>();
        Queue<V> bfsQueue = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        bfsQueue.add(source);
        pathTree.insertVertex(source);
        visited.add(source);
        while (!bfsQueue.isEmpty()) {
            V u = bfsQueue.remove();
            for (V neighbor : g.outNeighbors(u)) {
                if (!visited.contains(neighbor)) {
                    bfsQueue.add(neighbor);
                    visited.add(neighbor);
                    pathTree.insertVertex(neighbor);
                    pathTree.insertDirected(neighbor, u, g.getLabel(neighbor, u));
                }
            }
        }
        return pathTree;
    }

    /**
     * gets path from vertex to root
     * @param tree  tree
     * @param v     vertex
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return  vertex path
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        ArrayList<V> vertexPath = new ArrayList<>();
        V currentVertex = v;
        vertexPath.add(currentVertex);
        while (tree.outNeighbors(currentVertex).iterator().hasNext()) {
            currentVertex = tree.outNeighbors(currentVertex).iterator().next();
            vertexPath.add(currentVertex);
        }
        return vertexPath;
    }

    /**
     * finds missing vertices
     * @param graph     graph
     * @param subgraph  subgraph
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return  missing vertices
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        Set<V> missingVertices = new HashSet<>();
        for (V vertex : graph.vertices()) {
            if (!subgraph.hasVertex(vertex)) {
                missingVertices.add(vertex);
            }
        }
        return missingVertices;
    }

    /**
     * finds average separation using helper function
     * @param tree  tree
     * @param root  root node
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return  average separation
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        return (totalDistance(0, tree, root)/(double)tree.numVertices());
    }

    /**
     * helper function for averageSeparation
     * @param distSoFar     distance so far
     * @param tree  tree
     * @param root  root
     * @param <V>   vertex
     * @param <E>   set of strings
     * @return  total distance
     */
    public static <V,E> int totalDistance(int distSoFar, Graph<V,E> tree, V root) {
        int total = 0;
        total += distSoFar;
        for (V neighbor : tree.inNeighbors(root)) {
            total += totalDistance(distSoFar + 1, tree, neighbor);
        }
        return total;
    }

    /**
     * tests out methods
     * @param args
     */
    public static void main(String args[]) {
        Graph<String, Set<String>> test = new AdjacencyMapGraph<>();
        test.insertVertex("Alice");
        test.insertVertex("Bob");
        test.insertVertex("Charlie");
        test.insertVertex("Dartmouth");
        test.insertVertex("Kevin Bacon");
        test.insertVertex("Nobody");
        test.insertVertex("Nobody's Friend");
        Set<String> movies = new HashSet<>();
        movies.add("A Movie");
        movies.add("E Movie");
        test.insertUndirected("Alice", "Kevin Bacon", movies);
        movies.remove("E Movie");
        test.insertUndirected("Bob", "Kevin Bacon", movies);
        test.insertUndirected("Alice", "Bob", movies);
        movies.remove("A Movie");
        movies.add("D Movie");
        test.insertUndirected("Alice", "Charlie", movies);
        movies.remove("D Movie");
        movies.add("C Movie");
        test.insertUndirected("Charlie", "Bob", movies);
        movies.remove("C Movie");
        movies.add("B Movie");
        test.insertUndirected("Dartmouth", "Charlie", movies);
        movies.remove("B Movie");
        movies.add("F Movie");
        test.insertUndirected("Nobody", "Nobody's Friend", movies);
        movies.remove("F Movie");
        Graph<String, Set<String>> testPath = bfs(test, "Kevin Bacon");
        System.out.println(getPath(testPath, "Dartmouth"));
        System.out.println(missingVertices(test, testPath));
        System.out.println(averageSeparation(testPath, "Kevin Bacon"));
    }
}