package image_processing.test_scripts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import image_processing.singleton.Container;
import image_processing.algorithms.BFS;
import image_processing.algorithms.Polygon;
import image_processing.controller.Image_Processing;
import image_processing.data.Pixel;

public class Main_Test {

	public static void main(String[] args) throws IOException {

		/*
		File file = new File("src/image/testImage.png");

		BufferedImage image = ImageIO.read(file);
		
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
		*/
//		System.out.println(Container.getInstance().getList().print());
//		Container.getInstance().getList().Search_and_return_list(194).printTable();
//		Container.getInstance().findABCD();
//		Container.getInstance().printData();
//		Polygon p = new Polygon();
//		p.generatePolygon(Container.getInstance().getA(),
//						  Container.getInstance().getB(),
//						  Container.getInstance().getC(),
//						  Container.getInstance().getD(),
//						  Container.getInstance().getLeft(),
//						  Container.getInstance().getRight(),
//						  Container.getInstance().getMM_PX());
//		System.out.println(p.toString());
//		p.centerPolygon(Container.getInstance().getTop(),
//						Container.getInstance().getBot(),
//						Container.getInstance().getLeft(),
//						Container.getInstance().getRight(),
//						image.getHeight(),
//						image.getWidth());
//		System.out.println("\nChecks if (609, 112) is inside margin box - expects FALSE");
//		System.out.println(p.isInside(image.getWidth(), new Pixel(609, 112)));
//		System.out.println("Checks if (400, 300) is inside margin box - expects TRUE");
//		System.out.println(new Image_Processing(file).fly_go_nogo());
//		System.out.println("Checks if (359, 200) is inside margin box - expects FALSE");
//		System.out.println(p.isInside(image.getWidth(), new Pixel(359, 200)));
//		System.out.println("Checks if (425, 300) is inside margin box - expects TRUE");
//		System.out.println(p.isInside(image.getWidth(), new Pixel(425, 300)));
//		System.out.println("Checks if (377, 548) is inside margin box - expects FALSE");
//		System.out.println(p.isInside(image.getWidth(), new Pixel(377, 548)));
		
		System.out.println("Checks \"left\" - expects FALSE");
		System.out.println(new Image_Processing(new File("src/image_processing/test_data/left.png")).fly_go_nogo());
		System.out.println("Checks \"right\" - expects FALSE");
		System.out.println(new Image_Processing(new File("src/image_processing/test_data/right.png")).fly_go_nogo());
		System.out.println("Checks \"top\" - expects FALSE");
		System.out.println(new Image_Processing(new File("src/image_processing/test_data/top.png")).fly_go_nogo());
		System.out.println("Checks \"bot\" - expects FALSE/TRUE");
		System.out.println(new Image_Processing(new File("src/image_processing/test_data/bot.png")).fly_go_nogo());
		System.out.println("Checks \"center\" - expects TRUE");
		System.out.println(new Image_Processing(new File("src/image_processing/test_data/center.png")).fly_go_nogo());
	}
}
