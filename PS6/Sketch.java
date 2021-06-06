import java.util.TreeMap;

/**
 * holds sketch for server and clients with various methods for the sketch
 * @author Thomas Fenaroli, CS10, Spring 2021
 * @author Adam Budin, CS10, Spring 2021
 */
public class Sketch {
    private TreeMap<Integer, Shape> idToShape;

    /**
     * constructor
     */
    public Sketch() {
        idToShape = new TreeMap<>();
    }

    /**
     * gets the shape map
     * @return shape map
     */
    public synchronized TreeMap<Integer, Shape> getIDToShape() {
        return idToShape;
    }

    /**
     * determines if a click is on a shape
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @return  returns shape's ID if there is a shape, else -1
     */
    public synchronized Integer onShape(int x, int y) {
        for (Integer shapeID : idToShape.descendingKeySet()) {
            if (idToShape.get(shapeID).contains(x, y)) {
                return shapeID;
            }
        }
        return -1;
    }
}
