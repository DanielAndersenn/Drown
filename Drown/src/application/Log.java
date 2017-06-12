package application;

public class Log {
	
	public static Log instance = null;
	
	private Log() {
		
	}
	
	public static Log getInstance() {
		
		if (instance == null)
			instance = new Log();
		
		return instance;
	}

}
