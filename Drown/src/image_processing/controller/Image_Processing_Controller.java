package image_processing.controller;

import java.awt.image.BufferedImage;
import application.MainController;
import application.autonomy.CMDQueue;
import application.autonomy.Command;
import image_processing.singleton.Container;
import image_processing.singleton.File_Lock;
import image_processing.singleton.Rings;

public class Image_Processing_Controller implements Runnable{

	private Image_Processing ip;
	private BufferedImage file;
	private final MainController MC;
	private final CMDQueue CMDq;
	
	public Image_Processing_Controller(MainController mc, CMDQueue cmdQueue) {
		this.MC = mc;
		this.CMDq = cmdQueue;
	}
	
	@Override
	public void run() {
		
		while ((file = File_Lock.getInstance().take()) != null) {
			
			ip = new Image_Processing(file);
			
			if (ip.fly_go_nogo()) {
				
				//if (ip.distance()) {
					
					// Pass command into commandque

					CMDq.add(Command.CommandType.LAND, 0, 1);
					MC.logWrite("Drone is centered PogChamp - GOGOGO");
					Container.getInstance().setPrevPicDist(0);
					Rings.getInstance().ringPassed();
			/*	}
				
			else {
					// Fly backwards
					CMDq.add(Command.CommandType.MOVEBACKWARD, 5, 500); 
					MC.logWrite("Distance NOT long enough. Moving back" );
					
					// Placement of this is still sketchy..
					Container.getInstance().setPrevPicDist(Container.getInstance().getTop().getY() - 
							Container.getInstance().getBot().getY());
				}
				*/
			} 
			
			else {
				// Pass command into commandque				
				
				CMDq.add(ip.direction_to_fly(), 10, 500);
				MC.logWrite(("Not centered. Moving: " + ip.direction_to_fly().toString()));
				//System.out.println("Not centered. Moving: " + ip.direction_to_fly().toString());
				
			}
		}
	}
}
