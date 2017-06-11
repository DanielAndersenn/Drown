package application.autonomy;

import application.MainController;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

public class CommandHandler {
	
	private final IARDrone drone;
	private final CommandManager droneCMDM;
	private final MainController mc;
	
	
	public CommandHandler(MainController mc) {
		this.mc = mc;
		drone = mc.getDrone();
		droneCMDM = drone.getCommandManager();
	}
	
	protected void takeOff() {
		mc.logWrite("SENT CMD ## TAKEOFF ##");
		droneCMDM.takeOff();
		droneCMDM.waitFor(2000);
		sleep(2000);
		droneCMDM.up(2000).doFor(2500);
		droneCMDM.hover();
	}
	
	protected void land() {
		mc.logWrite("SENT CMD ## LAND ##");
		
		droneCMDM.landing();
		droneCMDM.waitFor(2500);
		sleep(2500);
		
	}
	
	protected void hover(int hoverTime) {
		mc.logWrite("SENT CMD ## HOVER ##");
		
        if(hoverTime < 0){
        	droneCMDM.hover();
        }
        else{
        	droneCMDM.hover().doFor(hoverTime);
            sleep(hoverTime);
        }
	}
	
	protected void moveLeft(int speed, int duration) {
		mc.logWrite("SENT CMD ## moveLeft ##");
		mc.logWrite("Moving left for " + duration + "ms at " + speed + "mm/s");
		
		if(speed > 0 && duration > 0)
		{
			droneCMDM.goLeft(speed).doFor(duration);
		} else {
			droneCMDM.goLeft(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	
	protected void moveRight(int speed, int duration) {
		mc.logWrite("SENT CMD ## moveRight ##");
		mc.logWrite("Moving right for " + duration + "ms at " + speed + "mm/s");
		
		if(speed > 0 && duration > 0)
		{
			droneCMDM.goRight(speed).doFor(duration);
		} else {
			droneCMDM.goRight(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	

	protected void moveUp(int speed, int duration) {
		mc.logWrite("SENT CMD ## moveUp ##");
		if(speed > 0 && duration > 0)
		{
			droneCMDM.up(speed).doFor(duration);
		} else {
			droneCMDM.up(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	
	protected void moveDown(int speed, int duration) {
		mc.logWrite("SENT CMD ## moveDown ##");
		if(speed > 0 && duration > 0)
		{
			droneCMDM.down(speed).doFor(duration);
		} else {
			droneCMDM.down(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	
	protected void moveForward(int speed, int duration) {
		mc.logWrite("SENT CMD ## moveForward ##");
		if(speed > 0 && duration > 0) {
			droneCMDM.forward(speed).doFor(duration);
		} else {
			droneCMDM.forward(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	
	protected void moveBackward(int speed, int duration) {
		mc.logWrite("SENT CMD ## moveBackward ##");
		if(speed > 0 && duration > 0) {
			droneCMDM.backward(speed).doFor(duration);
		} else {
			droneCMDM.backward(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	
	protected void spinRight(int speed, int duration) {
		mc.logWrite("SEND CMD ## spinRight ##");
		
		if(speed > 0 && duration > 0) {
			droneCMDM.spinRight(speed).doFor(duration);
		} else {
			droneCMDM.spinRight(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	
	protected void spinLeft(int speed, int duration) {
		mc.logWrite("SEND CMD ## spinRight ##");
		
		if(speed > 0 && duration > 0) {
			droneCMDM.spinLeft(speed).doFor(duration);
		} else {
			droneCMDM.spinLeft(speed);
		}
		
		sleep(duration);
		droneCMDM.hover();
		
	}
	

    private void sleep(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
	
}
