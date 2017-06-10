package image_processing.data;

public class Pixel {
	
	/**
	 * Dummy class
	 */
	
	private double x, y;
	private double angle;
	
	public Pixel(double x, double y) {		
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		
		return x;
	}
	
	public double getY() {
		
		return y;
	}
	
	public void setX(double x) {
		
		this.x = x;
	}
	
	public void setY(double y) {
		
		this.y = y;
	}
	
	public void setAngle(double angle) {
		
		this.angle = angle;
	}
	
	public double getAngle() {
		
		return angle;
	}

}
