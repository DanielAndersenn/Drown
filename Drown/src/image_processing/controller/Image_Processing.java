package image_processing.controller;

import java.awt.image.BufferedImage;

import application.autonomy.Command;
import image_processing.singleton.Container;
import image_processing.algorithms.BFS;
import image_processing.algorithms.Polygon;
import image_processing.data.Pixel;

public class Image_Processing {

	// TODO ER DE 1000/900/800 MM INKL. ELLER EKLS. RINGEN?
	
	/**
	 * Returns true or false whether the drone is capable of passing through the ring - 
	 * without touching the edges.
	 * Date: 9/6 '17 
	 * @author Haugaard
	 * 
	 * 
	 */
	
	private BufferedImage image;

	/**
	 * 
	 * @param Image
	 * @param object_height_mm
	 */
	public Image_Processing(BufferedImage Image) {

		
		Container.getInstance().resetList();
		this.image = Image;
		analyzeImage();
		Container.getInstance().findABCD();
	}

	// Method til at afgøre om man kan flyve igennem
	public boolean fly_go_nogo() {
		
		Polygon p = new Polygon();
		p.generatePolygon(Container.getInstance().getA(),
						  Container.getInstance().getB(),
						  Container.getInstance().getC(),
						  Container.getInstance().getD(),
						  Container.getInstance().getLeft(),
						  Container.getInstance().getRight(),
						  Container.getInstance().getMM_PX());

		/**
		 * We should not center the margin box around the center of the picture.
		 * I removed this part (centerPolygon()).
		 */
		
		return p.isInside(image.getWidth(), new Pixel(image.getWidth()/2, image.getHeight()/2));
	}
	
	/**
	 * 
	 * @return
	 */
	public Command.CommandType direction_to_fly() {

		// Calculate vector from center of circle to center of image
		double c_y = Container.getInstance().getTop().getY() - 
					 ((Container.getInstance().getTop().getY() - Container.getInstance().getBot().getY())/2);
		double c_x = Container.getInstance().getRight().getX() - 
					 ((Container.getInstance().getRight().getX() - Container.getInstance().getLeft().getX())/2);

		Pixel o = new Pixel((image.getWidth()/2) - c_x,
							(image.getHeight()/2) - c_y);
		
		if (Math.abs(o.getX()) > Math.abs(o.getY())) {
			if (o.getX() > 0) {
				// Fly Right
				return Command.CommandType.MOVERIGHT;
			}
			else {
				// Fly left
				return Command.CommandType.MOVELEFT;
			}
		}
		else {
			if (o.getY() > 0) {
				// Fly up
				return Command.CommandType.MOVEUP;
			}
			else {
				// Fly down
				return Command.CommandType.MOVEDOWN;
			}
		}
	}

	// Metode til at generere data
	private void analyzeImage() {

		// Run through all pixels
		for (int y = 0; y < image.getHeight(); y++) {

			for (int x = 0; x < image.getWidth(); x++) {

				int color = image.getRGB(x, y);

				// Extract RGB of pixel
				int r = (color & 0x00ff0000) >> 16; // R (red)
				int g = (color & 0x0000ff00) >> 8;	// G (green)
				int b = color & 0x000000ff;			// B (blue)

				if ((r == 255 && g == 255 && b == 255)) { // If the pixel is white and not marked
					new BFS().bfs(new Pixel((double) x, (double) y), 
								  Container.getInstance().getList(), image);
				} // if end
			} // inner for loop end

		} // outer for loop end
	}
}
