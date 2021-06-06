import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 * @author Thomas Fenaroli, Spring 2021
 * @author Adam Budin, Spring 2021
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control
	private Color blobColor = Color.RED;				// extra credit blob color

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if (k == '1' || k == '2' || k == '3' || k == '4' || k == '5' || k == '6' || k == '7') { // changes collision color
			if (k == '1') {blobColor = Color.RED;}
			else if (k == '2') {blobColor = Color.ORANGE;}
			else if (k == '3') {blobColor = Color.YELLOW;}
			else if (k == '4') {blobColor = Color.GREEN;}
			else if (k == '5') {blobColor = Color.BLUE;}
			else if (k == '6') {blobColor = new Color(78, 98, 196);} // indigo
			else if (k == '7') {blobColor = new Color(127, 0, 255);} // violet
		}
		else if (k == 'b' || k == 'w'){ // set the type for new blobs
			blobType = k;
		}
		else { // print message if invalid key input
			System.out.println("Invalid key input");
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		for (Blob blob : blobs) {
			blob.draw(g);
		}
		// Ask the colliders to draw themselves in red.
		if (colliders != null) {
			for (Blob collider : colliders) {
				g.setColor(blobColor);
				collider.draw(g);
			}
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// TODO: YOUR CODE HERE
		// Create the tree
		PointQuadtree<Blob> blobPointQuadtree = new PointQuadtree<>(blobs.get(0), 0, 0, width, height);
		for (Blob blob : blobs) {
			blobPointQuadtree.insert(blob);
		}
		// For each blob, see if anybody else collided with it
		colliders = new ArrayList<Blob>();
		for (Blob blob : blobs) {
			// create list of touching blobs
			List<Blob> touching = blobPointQuadtree.findInCircle(blob.x, blob.y, 2*blob.r);
			// add to colliders if size of touching list is greater than 1 (if there is actually a collision)
			if (touching.size() > 1) {
				for (Blob collider : touching) {
					colliders.add(collider);
				}
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
