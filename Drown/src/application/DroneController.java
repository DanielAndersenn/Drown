package application;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class DroneController extends Thread implements TagListener {
	
	private final static int SPEED = 5;
	private final static int SLEEP = 500;
	
	protected IARDrone drone;
	protected MainController mc;
	protected boolean doStop = false;
	private Result result;
	private float orientation;
	
	//QRCounter
	private String[] ringsToFind = new String[] {"P.00","P.01","P.02","P.03","P.04","P.05"};
	private String nextQR; 
	private int count = 0;
	
	
	public DroneController(IARDrone drone, MainController mc) {
		this.drone = drone;
		this.mc = mc;
	}

	
	public void run(){
		System.out.println("run entered");
//		QRCounter();
		drone.getCommandManager().takeOff();
//		drone.getCommandManager().up(75).doFor(1000);
//		drone.getCommandManager().hover();
		while(!doStop){
			try {
				if ((result != null) && (System.currentTimeMillis() - result.getTimestamp() > 500)){
					System.out.println("BITCH ASS NIGGER");
					result = null;
				}
				
//				if(result == null){
//					System.out.println("spin");
//					drone.getCommandManager().up(SPEED).doFor(SLEEP);
//					drone.getCommandManager().spinLeft(50).doFor(SLEEP);
//					drone.getCommandManager().hover();
//					this.currentThread().sleep(500);
//					Thread.currentThread().sleep(500);
//				}
//				System.out.println("run:this.Result = "+result);
				
				//&& (result.getText().equals("P.03"))
				if( (result != null)  ){
					System.out.println("center");
//					this.currentThread().sleep(500);
					centerTag();
				}
				
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		drone.stop();
	}
	
	
	public void stopController()
	{
		doStop = true;
	}

	
	@Override
	public void onTag(Result result, float orientation) {
		if(result !=null){
			this.result = result;
			this.orientation = orientation;
		}

	}

	private String QRCounter(){
		nextQR = ringsToFind[count++];
		System.out.println("QRCounter| nextQR = "+ nextQR);
		return nextQR;
	}
	
	private void centerTag() throws InterruptedException {
		System.out.println("HELLO =?!?!?!=?!?!?!?!?!s");
		mc.logWrite("Entered CenterTag");
		ResultPoint[] points;
		
		synchronized(result)
		{
			points = result.getResultPoints();	
		}
		
		int imgCenterX = Main.IMAGE_WIDTH / 2;
		int imgCenterY = Main.IMAGE_HEIGHT / 2;
		mc.logWrite("imgCenter: x = "+imgCenterX+"| y = "+imgCenterY);
		
		float x = points[1].getX();
		float y = points[1].getY();
		mc.logWrite("floats: x = "+x+"| y = "+y);
		
		/*
		if (x < (imgCenterX - Main.TOLERANCE)){
			drone.getCommandManager().goLeft(SPEED).doFor(250);
			mc.logWrite("left");
			Thread.currentThread().sleep(SLEEP);

		}else if (x > (imgCenterX + Main.TOLERANCE)){
			drone.getCommandManager().goRight(SPEED).doFor(250);
			mc.logWrite("right");
			Thread.currentThread().sleep(SLEEP);
			
		}else if (y < (imgCenterY - Main.TOLERANCE)){
			drone.getCommandManager().up(SPEED).doFor(250);
			mc.logWrite("up");
			Thread.currentThread().sleep(SLEEP);
			
		}else if (y > (imgCenterY + Main.TOLERANCE)){
			drone.getCommandManager().down(SPEED).doFor(250);
			mc.logWrite("down");
			Thread.currentThread().sleep(SLEEP);
			
		}else{
			mc.logWrite("CENTERED!!!!");
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
		}
		*/
		
	
	}
	
}
