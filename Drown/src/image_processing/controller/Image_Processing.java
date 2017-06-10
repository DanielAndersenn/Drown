package image_processing.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import image_processing.singleton.Container;
import image_processing.algorithms.BFS;
import image_processing.algorithms.Polygon;
import image_processing.data.Pixel;

public class Image_Processing {

	// Billedets data skal gemmes i et globalt scope så flere metoder kan gøre brug af
	// informationen - løst ved en singleton lige nu
	// Ulempen ved singleton er så at jeg ikke kan have 2 image_processing objekter på samme tid
	// da de resetter container ved initializering
	// En anden grund til at Container er en singleton er fordi at den indeholder data
	// som det er hurtigst at opdatere mens at billedet kigges igennem, istedet for først
	// at gøre det efter
	// Alternativet er at passe Container objektet med hele vejen ned igennem algoritmen?
	
	// Er det her at vi får brug for semafor? Dronen venter på at f.eks. en boolean bliver sat at 
	// fly_go_nogo()?
	
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
	 * @param file
	 * @param object_height_mm
	 */
	public Image_Processing(File file) {

		try {
			Container.getInstance().resetList();
			this.image = ImageIO.read(file);
			analyzeImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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

	// Metode til at afgøre hvilken retning at dronen skal flyve i
	//TODO Skal returnere en form for enum - MEN ER BARE VOID FOR NU
	public void direction_to_fly() {

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
				System.out.println("RIGHT");
			}
			else {
				// Fly left
				System.out.println("LEFT");
			}
		}
		else {
			//TODO Sketchy
			if (o.getY() > 0) {
				// Fly up
				System.out.println("DOWN");
			}
			else {
				// Fly down
				System.out.println("UP");
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
