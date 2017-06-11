package application.autonomy;

import java.util.LinkedList;

import application.MainController;
import application.autonomy.Command.CommandType;

public class CMDQueue implements Runnable{


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

		return cmdQueue.add(new Command(cType, speed, duration));
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
		running = false;
	}


	@Override
	public void run() {
		System.out.println("Entered run()");
		running = true;

		while(running) {
			System.out.println(cmdQueue.isEmpty());
			if(!cmdQueue.isEmpty()) {
				busy = true;
				final Command cmd = cmdQueue.remove();

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
				}
			} 
			else
				try {
					Thread.sleep(timeOut);
				} catch (InterruptedException e) { }

			busy = false;
		}

	}




}
