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
				// Pass command into commandque
				MC.logWrite("GOGOGO");
				CMDq.add(Command.CommandType.LAND, 0, 1);
			}
			else {
				// Pass command into commandque
				
				
				CMDq.add(ip.direction_to_fly(), 5, 1000);
				MC.logWrite(("Cant fly though. Moving: " + ip.direction_to_fly().toString()));
				
			}
			
			file = null;
		}
	}
}
