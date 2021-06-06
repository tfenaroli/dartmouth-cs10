import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author Thomas Fenaroli, CS10, PS1
 * @author Adam Budin, CS10, PS1
 */
public class RegionFinder {
	private static final int maxColorDiff = 800;             // how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50;            // how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private final Color regionColor = new Color(100000);

	private ArrayList<ArrayList<Point>> regions = new ArrayList<>();         // a region is a list of points
	// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 * @param targetColor the target color
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Point p = new Point(x, y);
				Color c = new Color(image.getRGB(x,y));
				if (colorMatch(c, targetColor) && visited.getRGB(x, y) == 0) {
					ArrayList<Point> region = new ArrayList<>();
					ArrayList<Point> toVisit = new ArrayList<>();
					toVisit.add(p);
					while (toVisit.size() != 0) {
						Point pixelVisit = toVisit.remove(toVisit.size() - 1);
						region.add(pixelVisit);
						visited.setRGB((int)pixelVisit.getX(), (int)pixelVisit.getY(),1);
						for (int ny = Math.max(0, (int)pixelVisit.getY() - 1);
							 ny < Math.min(image.getHeight(), (int)pixelVisit.getY() + 1 + 1);
							 ny++) {
							for (int nx = Math.max(0, (int)pixelVisit.getX() - 1);
								 nx < Math.min(image.getWidth(), (int)pixelVisit.getX() + 1 + 1);
								 nx++) {
								// Add neighbors to toVisit if close enough to targetColor
								Point neighbor = new Point(nx,ny);
								Color pixelColor = new Color(image.getRGB(nx,ny));
								if (colorMatch(pixelColor, targetColor) && neighbor != pixelVisit && visited.getRGB(nx,ny) == 0) {
									toVisit.add(neighbor);
								}
							}
						}
					}
					if (region.size() >= minRegion) {
						regions.add(region);
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold,
	 * which you can vary).
	 * @param c1 color 1
	 * @param c2 color 2
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		int dif = (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed())
				+ (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
				+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());
		return dif <= maxColorDiff;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		if (regions.size() != 0) {
			ArrayList<Point> maxRegion = regions.get(0);
			for (ArrayList<Point> region: regions) {
				if (region.size() > maxRegion.size()) {
					maxRegion = region;
				}
			}
			return maxRegion;
		}
		ArrayList<Point> emptyList = new ArrayList<>();
		return emptyList;
	}

	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
		int regionColorNumber = (int)(Math.random()*100000000);
		Color regionColor = new Color(regionColorNumber);
		for (Point point: largestRegion()) {
			recoloredImage.setRGB((int)point.getX(),(int)point.getY(), regionColor.getRGB());
		}
	}
}
