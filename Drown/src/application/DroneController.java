package application;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import application.autonomy.CMDQueue;
import application.autonomy.Command;
import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class DroneController extends Thread implements TagListener {
	
	private final static int SPEED = 5;
	private final static int SLEEP = 500;
	
	protected IARDrone drone;
	protected MainController mc;
	protected CMDQueue cmdq;
	protected boolean doStop = false;
	private Result result;
	
	//QRCounter
	private String[] ringsToFind = new String[] {"P.00","P.01","P.02","P.03","P.04","P.05"};
	private String nextQR; 
	private int count = 0;
	
	
	public DroneController(IARDrone drone, MainController mc, CMDQueue cmdq) {
		this.drone = drone;
		this.mc = mc;
		this.cmdq = cmdq;
	}

	
	public void run(){
		cmdq.add(Command.CommandType.TAKEOFF,0,0);
//		drone.getCommandManager().takeOff();
//		drone.getCommandManager().up(100).doFor(1000);
//		drone.getCommandManager().hover();
		mc.logWrite("Entered Run");
		while(!doStop){
			try {
				if ((result != null) && (System.currentTimeMillis() - result.getTimestamp() > 500)){
					result = null;
				}
				
//				if(result == null){
//					System.out.println("spin");
//					Thread.currentThread().sleep(500);
//				}
				
				if( (result != null) && (result.getText().equals("P.03")) ){
					mc.logWrite("Center");
					Thread.currentThread().sleep(500);
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
			System.out.println(
					);
		}

	}
	

	private String QRCounter(){
		nextQR = ringsToFind[count++];
		System.out.println("QRCounter| nextQR = "+ nextQR);
		return nextQR;
	}
	
	private void centerTag() throws InterruptedException {
		mc.logWrite("Entered CenterTag");
		ResultPoint[] points;
		
		synchronized(result)
		{
			points = result.getResultPoints();	
		}
		
		int imgCenterX = Main.IMAGE_WIDTH / 2;
		int imgCenterY = Main.IMAGE_HEIGHT / 2;
		
		float x = points[1].getX();
		float y = points[1].getY();
		mc.logWrite("Floats: x = "+x+"| y = "+y);
		if (x < (imgCenterX - Main.TOLERANCE)){
//			drone.getCommandManager().goLeft(SPEED).doFor(SLEEP);
			cmdq.add(Command.CommandType.MOVELEFT, SPEED, SLEEP);
//			mc.logWrite("going left.");
			Thread.currentThread().sleep(SLEEP);

		}else if (x > (imgCenterX + Main.TOLERANCE)){
//			drone.getCommandManager().goRight(SPEED).doFor(SLEEP);
			cmdq.add(Command.CommandType.MOVERIGHT, SPEED, SLEEP);
//			mc.logWrite("going right.");
			Thread.currentThread().sleep(SLEEP);
			
		}else if (y < (imgCenterY - Main.TOLERANCE)){
//			drone.getCommandManager().forward(SPEED).doFor(SLEEP);
			cmdq.add(Command.CommandType.MOVEUP, SPEED, SLEEP);
//			mc.logWrite("going forward.");
			Thread.currentThread().sleep(SLEEP);
			
		}else if (y > (imgCenterY + Main.TOLERANCE)){
//			drone.getCommandManager().backward(SPEED).doFor(SLEEP);
			cmdq.add(Command.CommandType.MOVEDOWN, SPEED, SLEEP);
//			mc.logWrite("going backwards.");
			Thread.currentThread().sleep(SLEEP);
			
		}else{
			//drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
			mc.logWrite("CENTERED!!!!");
		}
	
	}
	
}
