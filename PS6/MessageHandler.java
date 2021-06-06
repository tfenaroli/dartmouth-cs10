import java.awt.*;
import java.util.TreeMap;

/**
 * handles messages for clients and server
 * @author Thomas Fenaroli, CS10, Spring 2021
 * @author Adam Budin, CS10, Spring 2021
 */
public class MessageHandler {
    public static int index = 0;

    /**
     * handles draw
     * @param shapeMap  map of shapes
     * @param splitLine     message
     */
    public static void handleDraw(TreeMap<Integer, Shape> shapeMap, String[] splitLine) {
        if (splitLine[1].equals("ellipse")) {
            int x1 = Integer.valueOf(splitLine[2]);
            int y1 = Integer.valueOf(splitLine[3]);
            int x2 = Integer.valueOf(splitLine[4]);
            int y2 = Integer.valueOf(splitLine[5]);
            Color color = new Color(Integer.valueOf(splitLine[6]));
            shapeMap.put(index, new Ellipse(x1, y1, x2, y2, color));
            index++;
        }
        else if (splitLine[1].equals("rectangle")) {
            int x1 = Integer.valueOf(splitLine[2]);
            int y1 = Integer.valueOf(splitLine[3]);
            int x2 = Integer.valueOf(splitLine[4]);
            int y2 = Integer.valueOf(splitLine[5]);
            Color color = new Color(Integer.valueOf(splitLine[6]));
            shapeMap.put(index, new Rectangle(x1, y1, x2, y2, color));
            index++;
        }
        else if (splitLine[1].equals("segment")) {
            int x1 = Integer.valueOf(splitLine[2]);
            int y1 = Integer.valueOf(splitLine[3]);
            int x2 = Integer.valueOf(splitLine[4]);
            int y2 = Integer.valueOf(splitLine[5]);
            Color color = new Color(Integer.valueOf(splitLine[6]));
            shapeMap.put(index, new Segment(x1, y1, x2, y2, color));
            index++;
        }
        else if (splitLine[1].equals("polyline")) {
            Color color = new Color(Integer.valueOf(splitLine[2]));
            int x1 = Integer.valueOf(splitLine[3]);
            int y1 = Integer.valueOf(splitLine[4]);
            Polyline polyline = new Polyline(x1, y1, color);
            for (int i = 0; i < splitLine.length; i++) {
                if (i > 2) {
                    int point = Integer.valueOf(splitLine[i]);
                    polyline.getPointPairs().add(point);
                }
            }
            shapeMap.put(index, polyline);
            index++;
        }
    }

    /**
     * handles move
     * @param shapeMap  map of shapes
     * @param splitLine     message
     * @param movingId  ID of shape being moved
     */
    public static void handleMove(TreeMap<Integer, Shape> shapeMap, String[] splitLine, Integer movingId) {
        int x1 = Integer.valueOf(splitLine[2]);
        int y1 = Integer.valueOf(splitLine[3]);
        int x2 = Integer.valueOf(splitLine[4]);
        int y2 = Integer.valueOf(splitLine[5]);
        shapeMap.get(movingId).moveBy(x2 - x1, y2 - y1);
    }

    /**
     * handles recolor
     * @param shapeMap  map of shapes
     * @param splitLine     message
     * @param shapeId   ID of shape being recolored
     */
    public static void handleRecolor(TreeMap<Integer, Shape> shapeMap, String[] splitLine, Integer shapeId) {
        Color color = new Color(Integer.valueOf(splitLine[2]));
        shapeMap.get(shapeId).setColor(color);
    }

    /**
     * handles delete
     * @param shapeMap  map of shapes
     * @param shapeId   ID of shape being deleted
     */
    public static void handleDelete(TreeMap<Integer, Shape> shapeMap, Integer shapeId) {
        shapeMap.remove(shapeId);
    }
}
