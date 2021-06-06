import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Thomas Fenaroli, CS10, Spring 2021
 * @author Adam Budin, CS10, Spring 2021
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE
	private ArrayList<Integer> pointPairs = new ArrayList<>();
	private Color color;
	private double minDistThreshold = 3.0;

	/**
	 * constructor
	 * @param x1 	x-coordinate
	 * @param y1 	y-coordinate
	 * @param color 	color
	 */
	public Polyline(int x1, int y1, Color color) {
		pointPairs.add(x1);
		pointPairs.add(y1);
		this.color = color;
	}

	/**
	 * getter for point list
	 * @return 	pointPairs
	 */
	public ArrayList<Integer> getPointPairs() {
		return pointPairs;
	}

	/**
	 * moves the polyline
	 * @param dx 	dx
	 * @param dy 	dy
	 */
	@Override
	public void moveBy(int dx, int dy) {
		for (int i = 0; i < pointPairs.size(); i++) {
			if (i % 2 == 0) {
				pointPairs.set(i, pointPairs.get(i) + dx);
			}
			else {
				pointPairs.set(i, pointPairs.get(i) + dy);
			}
		}
	}

	/**
	 * getter for color
	 * @return 	color
	 */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * setter for color
	 * @param color The shape's color
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * determines if shape contains the mouse click
	 * @param x 	x-coordinate
	 * @param y 	y-coordinate
	 * @return 	boolean whether or not it contains the click
	 */
	@Override
	public boolean contains(int x, int y) {
		double minDist = minDistThreshold + 1;
		for (int i = 0; i < pointPairs.size() - 3; i++) {
			double dist;
			int x1 = pointPairs.get(i);
			int y1 = pointPairs.get(i + 1);
			int x2 = pointPairs.get(i + 2);
			int y2 = pointPairs.get(i + 3);
			if ((dist = Segment.pointToSegmentDistance(x, y, x1, y1, x2, y2)) < minDist) {
				minDist = dist;
			}
		}
		if (minDist < minDistThreshold) {
			return true;
		}
		return false;
	}

	/**
	 * draws the polyline
	 * @param g 	graphics
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		for (int i = 0; i < pointPairs.size() - 3; i++) {
			if (i % 2 == 0) {
				int x1 = pointPairs.get(i);
				int y1 = pointPairs.get(i + 1);
				int x2 = pointPairs.get(i + 2);
				int y2 = pointPairs.get(i + 3);
				g.drawLine(x1, y1, x2, y2);
			}
		}
	}

	/**
	 * string representation
	 * @return 	string
	 */
	@Override
	public String toString() {
		String res = "polyline " + color.getRGB();
		for (Integer point : pointPairs) {
			res += " " + point;
		}
		return res;
	}
}
