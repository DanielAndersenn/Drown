package application.autonomy;

import java.util.LinkedList;

import application.MainController;
import application.autonomy.Command.CommandType;

public class CMDQueue implements Runnable {

	private final MainController mc;

	private final CommandHandler cmdHandler;

	private boolean running = false;

	private int timeOut = 0;

	private Thread cmdThread;

	private boolean busy = false;

	private final LinkedList<Command> cmdQueue = new LinkedList();

	public CMDQueue(MainController mc, CommandHandler cmdHandler) {
		this.mc = mc;
		this.cmdHandler = cmdHandler;
	}


	public boolean add(CommandType cType, int speed, int duration) {
		System.out.println("## Added new command " +  cType + " ##");
		//mc.logWrite("");
		return getList().add(new Command(cType, speed, duration));
	}

	public void printQueuedCmds(){

		mc.logWrite("## Printing queued commands ##");
		for(int i = 0; i < cmdQueue.size(); i++) {
			mc.logWrite(i + cmdQueue.get(i).cmd.toString());
		}
	}

	public int getQueueSize() {
		return cmdQueue.size();
	}

	public void start(int timeoutDuration) {
		System.out.println("Entered CMDQueue.start()");
		timeOut = timeoutDuration;

		cmdThread = new Thread(this);
		cmdThread.setDaemon(true);
		cmdThread.start();
	}

	public void stop() {

		setRunning(false);
	}


	@Override
	public void run() {
		System.out.println("Entered run()");

		setRunning(true);

		//Thread t1 = new Thread(new Runnable() {
			//public void run() {

				while(getRunning()) {
					if(!getList().isEmpty()) {
						busy = true;
						final Command cmd = getList().remove();

						switch(cmd.cmd) {
							case TAKEOFF: {
								cmdHandler.takeOff();
								break;
							}
							case LAND: {
								cmdHandler.land();
								break;
							}
							case HOVER: {
								cmdHandler.hover(cmd.duration);
								break;
							}
							case MOVELEFT: {
								cmdHandler.moveLeft(cmd.speed, cmd.duration);
								break;
							}
							case MOVERIGHT: {
								cmdHandler.moveRight(cmd.speed, cmd.duration);
								break;
							}
							case MOVEUP: {
								cmdHandler.moveUp(cmd.speed, cmd.duration);
								break;
							}
							case MOVEDOWN: {
								cmdHandler.moveDown(cmd.speed, cmd.duration);
								break;
							}
							case MOVEFORWARD: {
								cmdHandler.moveForward(cmd.speed, cmd.duration);
								break;
							}
							case MOVEBACKWARD: {
								cmdHandler.moveBackward(cmd.speed, cmd.duration);
								break;
							}
							case SPINRIGHT: {
								cmdHandler.spinRight(cmd.speed, cmd.duration);
								break;
							}
							case SPINLEFT: {
								cmdHandler.spinLeft(cmd.speed, cmd.duration);
								break;
							}
							default: {
								mc.logWrite("DEFAULT CASE WHAT UP");
								break;
							}
							
						} // switch end
						
						//mc.logWrite("EXECUTED ##" + cmd.cmd +  " ##");
						System.out.println("EXECUTED ##" + cmd.cmd +  " ##");
					} else
						try {
							Thread.sleep(timeOut);
						} catch (InterruptedException e) { }

					busy = false;
				} // while end
			//} // run end
		//}); // Thread end
		//t1.start();

	}

	private synchronized LinkedList<Command> getList() {

		return cmdQueue;
	}
	
	private synchronized boolean getRunning() {
		
		return running;
	}
	
	private synchronized void setRunning(boolean r) {
		
		running = r;
	}


}
