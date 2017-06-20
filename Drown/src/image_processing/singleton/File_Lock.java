package image_processing.singleton;

import java.awt.image.BufferedImage;

/**
 * 
 * Se eksemplet i linket til Java Concurrency (Guarded block)
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html
 * 
 * Da, det er et objekt fælles objekt som de 2 controllers skal deles om har jeg valgt, at lave
 * dette som en singletonklasse. 
 *
 */

public class File_Lock {

	private static File_Lock instance = null;
	private boolean empty = true;
	private BufferedImage file;
	
	private File_Lock() {
		
	}
	
	public static File_Lock getInstance() {
		
		if (instance == null)
			instance = new File_Lock();
		
		return instance;
	}
	
    public synchronized BufferedImage take() {
    	
    	System.out.println("File_Lock: take()");
    	
        // Wait until command is available
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        
        // Toggle status.
        empty = true;
        
        // Notify producer that
        // status has changed.
        notifyAll();
        return file;
    }

    public synchronized void put(BufferedImage file) {
    	System.out.println("File_Lock: put()");
    	
        // Wait until command has been read
        while (!empty) {
            try { 
                wait();
            } catch (InterruptedException e) {}
        }
        
        // Toggle status.
        empty = false;
        
        // Store file
        this.file = file;
        
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}
