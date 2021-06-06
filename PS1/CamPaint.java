import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Webcam-based drawing
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';          // what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;         // handles the finding
	private Color targetColor;             // color of regions of interest (set by mouse press)
	private final Color paintColor = Color.blue; // the color to put into the painting from the "brush"
	private BufferedImage painting;          // the resulting masterpiece
	private boolean brush = false;       // toggles paintbrush

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		if(displayMode == 'p' && targetColor != null){
			//System.out.println("painting");
			g.drawImage(painting,0,0,null);
		}
		else if (displayMode == 'r' && targetColor != null) {
			//System.out.println("recolored");
			g.drawImage(finder.getRecoloredImage(),0,0,null);
		}
		else {
			g.drawImage(image, 0, 0, null);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// TODO: YOUR CODE HERE
		if (targetColor != null && image != null) {
			finder = new RegionFinder(image);
			finder.findRegions(targetColor);
			finder.recolorImage();
			if(brush) {
				for (Point point: finder.largestRegion()) {
					painting.setRGB((int)point.getX(),(int)point.getY(),paintColor.getRGB());
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		if (image != null) {
			targetColor = new Color(image.getRGB(x, y));
			System.out.println("Selected color.");
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			System.out.println(k);
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else if (k == 'b') { // toggle brush
			brush = !brush;
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
