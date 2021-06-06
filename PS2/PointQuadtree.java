import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * @author Thomas Fenaroli, Spring 2021
 * @author Adam Budin, Spring 2021
 *
 */
public class PointQuadtree<E extends Point2D> {
	private E point;                     // the point anchoring this node
	private int x1, y1;                      // upper-left corner of the region
	private int x2, y2;                      // bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;   // children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 * @param point 	point
	 * @param x1    first x-coordinate
	 * @param y1	first y-coordinate
	 * @param x2    second x-coordinate
	 * @param y2	second y-coordinate
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		// assigns to instance variables point, x1, y1, x2, y2
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant 1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant 1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 * @param p2 	element being inserted
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		// if p2 in Q1
		if (p2.getX() > point.getX() && p2.getY() < point.getY()) {
			// recursively insert into that child if it exists
			if (hasChild(1)) {
				c1.insert(p2);
			}
			// else create a new child
			else {
				c1 = new PointQuadtree<E>(p2, (int) point.getX(), y1, x2, (int) point.getY());
			}
		}

		// if p2 in Q2
		else if (p2.getX() < point.getX() && p2.getY() < point.getY()) {
			// recursively insert into that child if it exists
			if (hasChild(2)) {
				c2.insert(p2);
			}
			// else create a new child
			else {
				c2 = new PointQuadtree<E>(p2, x1, y1, (int) point.getX(), (int) point.getY());
			}
		}

		// if p2 in Q3
		else if (p2.getX() < point.getX() && p2.getY() > point.getY()) {
			// recursively insert into that child if it exists
			if (hasChild(3)) {
				c3.insert(p2);
			}
			// else create a new child
			else {
				c3 = new PointQuadtree<E>(p2, x1, (int) point.getY(), (int) point.getX(), y2);
			}
		}

		// if p2 in Q4
		else if (p2.getX() > point.getX() && p2.getY() > point.getY()) {
			// recursively insert into that child if it exists
			if (hasChild(4)) {
				c4.insert(p2);
			}
			// else create a new child
			else {
				c4 = new PointQuadtree<E>(p2, (int) point.getX(), (int) point.getY(), x2, y2);
			}
		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		// recursively count every child's size
		int sum = 1;
		// add child's size for every child
		for (int i=1; i<5; i++) {
			if (hasChild(i)) {sum += getChild(i).size();}
		}
		// return sum
		return sum;
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		// create ArrayList to store points
		ArrayList<E> pointList = new ArrayList<>();
		// calls addToPointList on that list
		addToPointList(pointList);
		// returns pointList
		return pointList;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx   circle center x
	 * @param cy   circle center y
	 * @param cr   circle radius
	 * @return     the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// creates ArrayList to store points in circle
		ArrayList<E> circlePoints = new ArrayList<>();
		// calls findInCirlce on that list
		findInCircle(cx, cy, cr, circlePoints);
		// returns circlePoints
		return circlePoints;
	}

	/**
	 * @param cx           circle center x
	 * @param cy 		   circle center y
	 * @param cr           circle radius
	 * @param circlePoints list of points within specified circle
	 */
	public void findInCircle(double cx, double cy, double cr, List<E> circlePoints) {
		// TODO: YOUR CODE HERE
		// if circle overlaps rectangle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			// if point is in circle
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
				// add point to circle points
				circlePoints.add(point);
			}
			// for every child
			for (int i=1; i<5; i++) {
				// recursively call findInCircle on that child if it exists
				if (hasChild(i)) {
					getChild(i).findInCircle(cx, cy, cr, circlePoints);
				}
			}
		}
	}

	/**
	 * adds to point list
	 * @param pointList list of points
	 */
	// TODO: YOUR CODE HERE for any helper methods
	public void addToPointList(ArrayList<E> pointList) {
		// for every child, recursively call addToPointList if that child exists
		for (int i = 1; i < 5; i++) {
			if (hasChild(i)) {
				getChild(i).addToPointList(pointList);
			}
		}
		// add point to point list
		pointList.add(point);
	}
}