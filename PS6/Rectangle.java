import java.awt.Color;
import java.awt.Graphics;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, updated Fall 2016
 * @author Thomas Fenaroli, CS10, Spring 2021
 * @author Adam Budin, CS10, Spring 2021
 */
public class Rectangle implements Shape {
	// TODO: YOUR CODE HERE
	private int x1, y1, x2, y2;
	private Color color;

	/**
	 * An "empty" rectangle, with only one point set so far
	 */
	public Rectangle(int x1, int y1, Color color) {
		this.x1 = x1; this.x2 = x1;
		this.y1 = y1; this.y2 = y1;
		this.color = color;
	}

	/**
	 * A rectangle defined by two corners
	 */
	public Rectangle(int x1, int y1, int x2, int y2, Color color) {
		this.x1 = x1; this.x2 = x2;
		this.y1 = y1; this.y2 = y2;
		this.color = color;
	}

	/**
	 * Redefines the rectangle based on new corners
	 */
	public void setCorners(int x1, int y1, int x2, int y2) {
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
	}

	/**
	 * moves the shape
	 * @param dx 	dx
	 * @param dy 	dy
	 */
	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx;
		y1 += dy;
		x2 += dx;
		y2 += dy;
	}

	/**
	 * gets color of shape
	 * @return 	color
	 */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * sets color of shape
	 * @param color The shape's color
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * determines if shape contains mouse click
	 * @param x 	dx
	 * @param y 	dy
	 * @return 	boolean whether shape contains mouse click
	 */
	@Override
	public boolean contains(int x, int y) {
		boolean containsX = (x1 <= x && x <= x2);
		boolean containsY = (y1 <= y && y <= y2);
		return (containsX && containsY);
	}

	/**
	 * draws shape
	 * @param g 	graphics
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x1, y1, x2-x1, y2-y1);
	}

	/**
	 * string representation
	 * @return 	string
	 */
	public String toString() {
		return "rectangle " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + color.getRGB();
	}
}
