package image_processing.test_scripts;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import image_processing.controller.Image_Processing;

public class Main_Test {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Checks \"left\" - expects FALSE");
		System.out.println(new Image_Processing(ImageIO.read(new File("src/image_processing/test_data/left.png"))).fly_go_nogo());
		System.out.println("Checks \"right\" - expects FALSE");
		System.out.println(new Image_Processing(ImageIO.read(new File("src/image_processing/test_data/right.png"))).fly_go_nogo());
		System.out.println("Checks \"top\" - expects FALSE");
		System.out.println(new Image_Processing(ImageIO.read(new File("src/image_processing/test_data/top.png"))).fly_go_nogo());
		System.out.println("Checks \"bot\" - expects FALSE/TRUE");
		System.out.println(new Image_Processing(ImageIO.read(new File("src/image_processing/test_data/bot.png"))).fly_go_nogo());
		System.out.println("Checks \"center\" - expects TRUE");
		System.out.println(new Image_Processing(ImageIO.read(new File("src/image_processing/test_data/center.png"))).fly_go_nogo());
	}
}
