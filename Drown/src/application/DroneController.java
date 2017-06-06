package application;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.apps.paperchase.PaperChase;
import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class DroneController extends Thread implements TagListener {
	
	private final static int SPEED = 5;
	private final static int SLEEP = 1000;
	
	protected IARDrone drone;
	protected boolean doStop = false;
	private Result result;
	private float orientation;
	
	//QRCounter
	private String[] ringsToFind = new String[] {"P.00","P.01","P.02","P.03","P.04","P.05"};
	private String nextQR; 
	private int count = 0;
	
	
	public DroneController(IARDrone drone) {
		this.drone = drone;
	}

	
	public void run(){
		drone.getCommandManager().setOutdoor(false, false);
		drone.getCommandManager().takeOff();
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		QRCounter();
		while(!doStop){
			try {

				drone.getCommandManager().hover().doFor(500);
				
				
				if ((result != null) && (System.currentTimeMillis() - result.getTimestamp() > 500)){
					result = null;
				}
				
				
				if(result == null){
					System.out.println("spin");
					drone.getCommandManager().spinLeft(50).doFor(1000);
					Thread.currentThread().sleep(5000);
				}
				
				if( (result != null) && (result.getText().equals(nextQR)) ){
					System.out.println("center");
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
			//System.out.println("RingChaseController:onTag = result.getText = " + result.getText() + " | Orientation = "+orientation);
		}

	}
	

	private boolean findQR(){
			if(result != null) {
				if(result.getText().equals(nextQR)){
					//Center
					System.out.println("center");
					return true;
				} 
			}else {
				//Spin
				System.out.println("spin");
				return false;
			}
			return false;
		}

	private String QRCounter(){
		nextQR = ringsToFind[count++];
		System.out.println("QRCounter| nextQR = "+ nextQR);
		return nextQR;
	}
	
	private void centerTag() throws InterruptedException {

		String tagText;
		ResultPoint[] points;
		
		synchronized(result)
		{
			points = result.getResultPoints();	
			tagText = result.getText();
		}
		
		int imgCenterX = PaperChase.IMAGE_WIDTH / 2;
		int imgCenterY = PaperChase.IMAGE_HEIGHT / 2;
		
		float x = points[1].getX();
		float y = points[1].getY();
		
		if ((orientation > 10) && (orientation < 180))
		{
			System.out.println("PaperChaseAutoController: Spin left");
			drone.getCommandManager().spinLeft(SPEED * 2);
			Thread.currentThread().sleep(SLEEP);
		}
		else if ((orientation < 350) && (orientation > 180))
		{
			System.out.println("PaperChaseAutoController: Spin right");
			drone.getCommandManager().spinRight(SPEED * 2);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (x < (imgCenterX - PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go left");
			drone.getCommandManager().goLeft(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (x > (imgCenterX + PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go right");
			drone.getCommandManager().goRight(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (y < (imgCenterY - PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go forward");
			drone.getCommandManager().forward(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if (y > (imgCenterY + PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go backward");
			drone.getCommandManager().backward(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else
		{
			System.out.println("PaperChaseAutoController: Tag centered");
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
		}
	
	}
	
}
