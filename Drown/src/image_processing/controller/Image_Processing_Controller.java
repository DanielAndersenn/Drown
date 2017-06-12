package image_processing.controller;

import java.awt.image.BufferedImage;
import application.MainController;
import application.autonomy.CMDQueue;
import application.autonomy.Command;
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
					CMDq.add(Command.CommandType.LAND, 0, 1);
					MC.logWrite("Drone is centered PogChamp - GOGOGO");
				}
				else {
					// Fly backwards
					CMDq.add(Command.CommandType.MOVEBACKWARD, 15, 500);
					MC.logWrite("Distance NOT long enough. Moving back" );
				}
			}
			else {
				// Pass command into commandque				
				
				CMDq.add(ip.direction_to_fly(), 15, 1000);
				MC.logWrite(("Cant fly though. Moving: " + ip.direction_to_fly().toString()));
				
				// Pass command into commandque
				CMDq.add(ip.direction_to_fly(), 5, 1000);
				MC.logWrite(("Cant fly through. Moving: " + ip.direction_to_fly().toString()));
			}
			
			/**
			 * Tror ikke, at denne linje kode er afgørende for noget. While loopet burde vente på at
			 * take() returnerer noget, før at den kigger på om file != null
			 */
//			file = null;
		}
	}
}
