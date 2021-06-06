import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Creates and plays Bacon game
 * @author Thomas Fenaroli, Spring 2021, CS10
 * @author Adam Budin, Spring 2021, CS10
 */
public class BaconGame {
    String root;

    boolean playing = true;

    Graph<String, Set<String>> gameGraph = new AdjacencyMapGraph<>();
    Graph<String, Set<String>> gameTree;

    Map<String, String> movieID = new HashMap<>();
    Map<String, String> actorID = new HashMap<>();
    Map<String, Set<String>> movieToActors = new HashMap<>();

    /**
     * constructor
     * @throws IOException
     */
    public BaconGame() throws IOException {
        root = "Kevin Bacon";
        reader("PS4/movies.txt", "PS4/actors.txt", "PS4/movie-actors.txt");
        fillGraph();
        gameTree = GraphLibrary.bfs(gameGraph, root);
    }

    /**
     * fills graph
     */
    public void fillGraph() {
        for (String vertex : actorID.keySet()) {
            gameGraph.insertVertex(actorID.get(vertex));
        }
        for (String movie : movieToActors.keySet()) {
            for (String actor : movieToActors.get(movie)) {
                for (String otherActor : movieToActors.get(movie)) {
                    if (otherActor != actor) {
                        if (!gameGraph.hasEdge(actorID.get(actor), actorID.get(otherActor))) {
                            Set<String> sharedMovies = new HashSet<>();
                            sharedMovies.add(movie);
                            gameGraph.insertUndirected(actorID.get(actor), actorID.get(otherActor), sharedMovies);
                        }
                        else {
                            if (!gameGraph.getLabel(actorID.get(actor), actorID.get(otherActor)).contains(movie)) {
                                gameGraph.getLabel(actorID.get(actor), actorID.get(otherActor)).add(movie);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * readers from files and fills maps
     * @param moviesFileName    movies file name
     * @param actorsFileName    actors file name
     * @param movieActorsFileName   movies to actors file name
     * @throws IOException
     */
    public void reader(String moviesFileName, String actorsFileName, String movieActorsFileName) throws IOException {

        BufferedReader moviesFileReader = new BufferedReader(new FileReader(moviesFileName));
        BufferedReader actorsFileReader = new BufferedReader(new FileReader(actorsFileName));
        BufferedReader movieActorsFileReader = new BufferedReader(new FileReader(movieActorsFileName));

        String line;

        while ((line = moviesFileReader.readLine()) != null) {
            String[] splitLine = line.split("\\|");
            movieID.put(splitLine[0], splitLine[1]);
        }

        while ((line = actorsFileReader.readLine()) != null) {
            String[] splitLine = line.split("\\|");
            actorID.put(splitLine[0], splitLine[1]);
        }

        while ((line = movieActorsFileReader.readLine()) != null) {
            String[] splitLine = line.split("\\|");
            if (!movieToActors.containsKey(splitLine[0])) {
                Set<String> actors = new HashSet<>();
                actors.add(splitLine[1]);
                movieToActors.put(splitLine[0], actors);
            }
            else {
                movieToActors.get(splitLine[0]).add(splitLine[1]);
            }
        }
        movieActorsFileReader.close();
        actorsFileReader.close();
        movieActorsFileReader.close();
    }

    /**
     * handles keyboard input from scanner
     * @param input     keyboard input
     */
    public void handleInput(String input) {
        String[] inputList = input.split(" ", 2);
        if (inputList[0].equals("u")) {
            if (actorID.containsValue(inputList[1])) {
                root = inputList[1];
                gameTree = GraphLibrary.bfs(gameGraph, root);
                System.out.println(root + " is now the center of the acting universe, connected to " +
                        gameTree.numVertices() + " actors, with average separation of " +
                        GraphLibrary.averageSeparation(gameTree, root));
            }
            else {
                System.out.println("Not a valid actor!");
            }
        }

        else if (inputList[0].equals("s")) {
            if (actorID.containsValue(inputList[1])) {
                if (GraphLibrary.missingVertices(gameGraph, gameTree).contains(inputList[1])) {
                    System.out.println("There is no path from " + inputList[1] + " to " + root);
                }
                else {
                    List path = GraphLibrary.getPath(gameTree, inputList[1]);
                    System.out.println(inputList[1] + "'s " + root + " number is " + (path.size() - 1));
                    for (int i = 0; i < (path.size() - 1); i++) {
                        String movies = "[";
                        for (String movie : gameTree.getLabel((String)path.get(i), (String)path.get(i + 1))) {
                            movies += movieID.get(movie) + ", ";
                        }
                        movies = movies.substring(0, movies.length() - 2);
                        movies += "]";
                        System.out.println(path.get(i) + " was in " + movies + " with " + path.get(i + 1));
                    }
                }
            }
            else {
                System.out.println("Not a valid actor!");
            }
        }

        else if (inputList[0].equals("#")) {
            System.out.println(gameTree.numVertices() + " actors are in the " + root + " universe");
        }

        else if (inputList[0].equals("a")) {
            System.out.println(root + "'s average separation is " + GraphLibrary.averageSeparation(gameTree, root));
        }

        else if (inputList[0].equals("list")) {
            if (inputList[1].equals("d")) {
                System.out.println(GraphLibrary.verticesByInDegree(gameGraph));
            }

            else if (inputList[1].equals("a")) {
                System.out.println(GraphLibrary.verticesBySeparation(gameGraph));
            }

            else {
                System.out.println("Not a valid input!");
            }
        }

        else if (inputList[0].equals("q")) {
            playing = false;
        }

        else if (inputList[0].equals("i")) {
            System.out.println(GraphLibrary.missingVertices(gameGraph, gameTree));
        }

        else {
            System.out.println("Not a valid input!");
        }
    }

    /**
     * runs Bacon game
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        BaconGame game = new BaconGame();
        game.fillGraph();

        System.out.println("Game Instructions: \t" +
                "\n Enter u <actor> to set a new center of the universe \t" +
                "\n Enter # to see how many actors can reach the center of the universe \t" +
                "\n Enter s <actor> to see how an actor can reach the center of the universe \t" +
                "\n Enter list <a/d> to see a sorted list of actors by average separation or degree (low to high) \t" +
                "\n Enter q to quit the game \n");
        Scanner scanner = new Scanner(System.in);

        System.out.println(game.root + " is now the center of the acting universe, connected to " +
                game.gameTree.numVertices() + " actors, with average separation of " +
                GraphLibrary.averageSeparation(game.gameTree, game.root));
        while (game.playing) {
            System.out.println(game.root + " game:");
            String input = scanner.nextLine();
            game.handleInput(input);
            System.out.println("\t");
        }
        scanner.close();
    }
}
