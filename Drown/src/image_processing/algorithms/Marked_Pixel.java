package image_processing.algorithms;

import image_processing.singleton.Container;
import image_processing.data.Pixel;
import image_processing.data_structures.DoubleLinkedList;
import image_processing.data_structures.DynamicTable;

public class Marked_Pixel {
	
	public void insertPixel(DoubleLinkedList list, Pixel pixel) {
		
		DynamicTable temp;
		
		// If the list exists
		if ((temp = list.Search_and_return_list(pixel.getY())) != null) {
			
			// if pixel with (x, y) doesnt exists
			if (!pixelSearch(list, pixel.getX(), pixel.getY())) {
				
				temp.insert(pixel); //  insert it
				Container.getInstance().controlDimensions(pixel);
			}
		}
		// if the list doesnt exist yet
		else {
			list.insert(pixel); // create it
		}
	}
	
	public boolean pixelSearch(DoubleLinkedList list, double val_X, double val_Y) {
		
		DynamicTable temp;
		
		// Search through the linked list
		if ((temp = list.Search_and_return_list(val_Y)) != null) {
			return new Binary_Search().find(temp, val_X);
		}
		
		return false;
	}

}
