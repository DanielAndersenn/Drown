package application.autonomy;

public class Command {

	public enum CommandType {
		TAKEOFF,
		LAND,
		HOVER,
		MOVELEFT,
		MOVERIGHT,
		MOVEUP,
		MOVEDOWN,
		MOVEFORWARD,
		MOVEBACKWARD,
		SPINRIGHT,
		SPINLEFT
	}
	
	protected final CommandType cmd;
	protected final int speed;
	protected final int duration;
	
	
	public Command(CommandType cmd, int speed, int duration) {
		this.cmd = cmd;
		this.speed = speed;
		this.duration = duration;
	}
	

	
}