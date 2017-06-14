package image_processing.controller;

import java.awt.image.BufferedImage;
import application.MainController;
import application.autonomy.CMDQueue;
import application.autonomy.Command;
import image_processing.singleton.Container;
import image_processing.singleton.File_Lock;

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
				
				if (ip.distance()) {
					
					// Pass command into commandque
//					CMDq.add(Command.CommandType.LAND, 0, 1);
					CMDq.add(Command.CommandType.MOVEFORWARD, 100, 1500);
					MC.logWrite("Drone is centered PogChamp - Flythrough now");
					CMDq.stop();
				}
				else {
					// Fly backwards
					//CMDq.add(Command.CommandType.MOVEBACKWARD, 15, 500);
					MC.logWrite("Drone too close. Distance measured: " + Container.getInstance().getDistance(Container.getInstance().getTop().getY()-Container.getInstance().getBot().getY()) + "mm");
					CMDq.add(Command.CommandType.LAND, 0, 1);
					CMDq.stop();
				}
			}
			else {
				// Pass command into commandque				
				
				CMDq.add(ip.direction_to_fly(), 10, 500);
				MC.logWrite(("Not centered. Moving: " + ip.direction_to_fly().toString()));
				
			}
			
			/**
			 * Tror ikke, at denne linje kode er afgørende for noget. While loopet burde vente på at
			 * take() returnerer noget, før at den kigger på om file != null
			 */
//			file = null;
		}
	}
}
