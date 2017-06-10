package image_processing.algorithms;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import image_processing.data.Pixel;
import image_processing.data_structures.DoubleLinkedList;

public class BFS {
	
	public void bfs(Pixel start, DoubleLinkedList dl_list, BufferedImage image) {
	 
	    Queue<Pixel> queue = new LinkedList<Pixel>();
	    
	    queue.offer(start);
	    // Insert pixel in double linked list
	    new Marked_Pixel().insertPixel(dl_list, start);
	    
	    while (queue.peek() != null) {
	        Pixel cur = queue.remove();

	        // Iterate through the pixels neighbours
	        for (int x = -1; x <= 1; x++) {
	        	
	        	for (int y = -1; y <= 1; y++) {
	        		
	        		Pixel temp = new Pixel(cur.getX() + x, cur.getY() + y);
	        		
	        		if (temp.getX() < 0 || temp.getX() >= image.getWidth()) { // If the pixel is outside of the image...
	        			// Do nothing
	        		} // if end
	        		else if (temp.getY() < 0 || temp.getY() >= image.getHeight()) { // If the pixel is outside of the image...
	        			// Do nothing
	        		} // else if end
	        		else if (temp.getX() == 0 && temp.getY() == 0) { // If the pixel is cur
	        			// Do nothing
	        		}
	        		else { // ... The pixel is inside the picture
	        			
	        			if (new Marked_Pixel().pixelSearch(dl_list, temp.getX(), temp.getY())) { // If the pixel is already registret (marked)
	        				// Do nothing
	        			} // inner if end
	        			else {
		        			int color = image.getRGB((int) temp.getX(), (int) temp.getY()); // What is the color of the pixel
		        			
		        			// Extract RGB of pixel
		        			int r = (color & 0x00ff0000) >> 16; // R (red)
		        			int g = (color & 0x0000ff00) >> 8;	// G (green)
		        			int b = color & 0x000000ff;			// B (blue)
		        			
		        			if (r == 255 && g == 255 && b == 255) { // If the pixel is white ...
		        				// Insert pixel into list
		        				new Marked_Pixel().insertPixel(dl_list, temp);
		        				queue.offer(temp);
		        			} // inner inner if end
	        			} // inner else end
	        		} // else end
	        	} // inner for end
	        } // outer for end
	    } // while end
	} // bfs end
} // class end
