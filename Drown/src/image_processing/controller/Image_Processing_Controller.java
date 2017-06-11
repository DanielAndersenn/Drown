package image_processing.controller;

import java.awt.image.BufferedImage;

import image_processing.singleton.File_Lock;

public class Image_Processing_Controller implements Runnable{

	private Image_Processing ip;
	private BufferedImage file;
	
	@Override
	public void run() {
		
		while ((file = File_Lock.getInstance().take()) != null) {
			
			ip = new Image_Processing(file);
			
			if (ip.fly_go_nogo()) {
				// Pass command into commandque
				System.out.println("We can fly through");
			}
			else {
				// Pass command into commandque
				System.out.println("We can not fly through. Direction to adjust:");
				ip.direction_to_fly();
			}
		}
	}
}
