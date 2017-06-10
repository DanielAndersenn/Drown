package image_processing.singleton;

public class Rings {
	
	/**
	 * THIS IS A CLASS REFERENCED BY MORE THAN JUST 1 CONTROLLER - NEW PACKAGE
	 * 
	 * Class to keep track of active ring and its dimension (height)
	 */
	
	public static Rings instance = null;
	
	private int ringsPassed;
	private int activeRing;
	private RingSpecs[] rings;
	
	private Rings() {
		this.ringsPassed = 0;
		this.activeRing = 0;
		rings = new RingSpecs[] {new RingSpecs(1000), 
				 				 new RingSpecs(900),
				 				 new RingSpecs(800)};
		
	}
	
	public static Rings getInstance() {
		
		if (instance == null)
			instance = new Rings();
		
		return instance;
	}
	
	
	public void ringPassed() {
		
		ringsPassed++;
		
		// If this is the second ring we pass
		if (ringsPassed % 2 == 0)
			activeRing++; // Update pointer in array
	}
	
	public int getActiveRingDimension() {
		
		return rings[activeRing].getHeight();
	}
	
	public int getRingsPassed() {
		
		return ringsPassed;
	}
	
	/**
	 * Private inner class to contain ring specs
	 */
	private class RingSpecs {
		
		private int object_height;
		
		public RingSpecs(int obj_h) {
			
			this.object_height = obj_h;
		}
		
		public int getHeight() {
			
			return object_height;
		}
	}

}
