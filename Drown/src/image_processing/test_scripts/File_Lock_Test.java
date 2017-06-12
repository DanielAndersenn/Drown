package image_processing.test_scripts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import image_processing.singleton.File_Lock;
import image_processing.controller.Image_Processing;

public class File_Lock_Test {
	
	// Start each class in its own thread

	public static void main(String[] args) {
		(new Thread(new producer())).start();
		(new Thread(new consumer())).start();
	}

}

// This is from image processor POV
class consumer implements Runnable {

	public void run() {
		
		System.out.println("Consumer : Begun waiting for file to be defined");
		
		BufferedImage f = null;
		
		while ((f = File_Lock.getInstance().take()) != null) {
			
//			f = File_Lock.getInstance().take();
			
			System.out.format("Consumer : File received:\n");
			
			Image_Processing ip = new Image_Processing(f);
			
			if (ip.fly_go_nogo()) {
				System.out.println("We can fly through");
			}
			else {
				System.out.println("We can not fly through. Direction to adjust:");
				ip.direction_to_fly();
			}
			
//			f = null; // Shouldnt be necesarry though, since take() should block..
			
		}

	} // run end
} // consumer end

// This is from the drones POV
class producer implements Runnable {

	public void run() {
		try {
		BufferedImage importantInfo[] = {
				ImageIO.read(new File("src/image_processing/test_data/left.png")),
				ImageIO.read(new File("src/image_processing/test_data/right.png")),
				ImageIO.read(new File("src/image_processing/test_data/top.png")),
				ImageIO.read(new File("src/image_processing/test_data/bot.png")),
				ImageIO.read(new File("src/image_processing/test_data/center.png"))
		};

		for (int i = 0; i < importantInfo.length; i++) {
			// Define the enum
			File_Lock.getInstance().put(importantInfo[i]);
			
			System.out.println("Producer : File is set");
			
			try {
				Thread.sleep(5000); // Sleep 5 seconds to simulate processing
			} 
			catch (InterruptedException e) {
				// Empty
			} // try/catch end
		} // for end
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // run end

} // producer end