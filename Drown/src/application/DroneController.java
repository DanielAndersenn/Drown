package application;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.apps.paperchase.PaperChase;
import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class DroneController extends Thread implements TagListener {
	
	private final static int SPEED = 5;
	private final static int SLEEP = 500;
	
	protected IARDrone drone;
	protected boolean doStop = false;
	private Result result;
	
	//QRCounter
	private String[] ringsToFind = new String[] {"P.00","P.01","P.02","P.03","P.04","P.05"};
	private String nextQR; 
	private int count = 0;
	
	
	public DroneController(IARDrone drone) {
		this.drone = drone;
	}

	
	public void run(){
		drone.getCommandManager().setOutdoor(false, false);
		//drone.getCommandManager().takeOff();
		QRCounter();
		while(!doStop){
			try {
				if ((result != null) && (System.currentTimeMillis() - result.getTimestamp() > 500)){
					result = null;
				}
				
				if(result == null){
					System.out.println("spin");
					//drone.getCommandManager().spinLeft(100).doFor(SLEEP);
					//Thread.currentThread().sleep(SLEEP);
				}
				
				if( (result != null) && (result.getText().equals("P.03")) ){
					System.out.println("center");
					//centerTag();
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
		}

	}
	

	private String QRCounter(){
		nextQR = ringsToFind[count++];
		System.out.println("QRCounter| nextQR = "+ nextQR);
		return nextQR;
	}
	
	private void centerTag() throws InterruptedException {

		ResultPoint[] points;
		
		synchronized(result)
		{
			points = result.getResultPoints();	
		}
		
		int imgCenterX = Main.IMAGE_WIDTH / 2;
		int imgCenterY = Main.IMAGE_HEIGHT / 2;
		
		float x = points[1].getX();
		float y = points[1].getY();
		
		if (x < (imgCenterX - Main.TOLERANCE)){
			drone.getCommandManager().goLeft(SPEED).doFor(SLEEP);
			Thread.currentThread().sleep(SLEEP);

		}else if (x > (imgCenterX + Main.TOLERANCE)){
			drone.getCommandManager().goRight(SPEED).doFor(SLEEP);
			Thread.currentThread().sleep(SLEEP);
			
		}else if (y < (imgCenterY - Main.TOLERANCE)){
			drone.getCommandManager().forward(SPEED).doFor(SLEEP);
			Thread.currentThread().sleep(SLEEP);
			
		}else if (y > (imgCenterY + Main.TOLERANCE)){
			drone.getCommandManager().backward(SPEED).doFor(SLEEP);
			Thread.currentThread().sleep(SLEEP);
			
		}else{
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
		}
	
	}
	
}
