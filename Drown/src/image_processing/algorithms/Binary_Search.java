package image_processing.algorithms;

import image_processing.data_structures.DynamicTable;

public class Binary_Search {
	
	public boolean find(DynamicTable numbers, double val_X) {
		
		return binarySearch(numbers, val_X, 0, numbers.getElements() - 1);
	}
	 
	private boolean binarySearch(DynamicTable numbers, double val_X, int left, int right) {
		
	    if (right < left) {
	        return false;
	    }
	 
	    int mid = (left + right) / 2;
	 
	    if (numbers.getTable()[mid].getX() == val_X) { // We found it
	        return true;
	    }
	    else if (numbers.getTable()[mid].getX() < val_X) { // Go right
	        return binarySearch(numbers, val_X, mid + 1, right);
	    }
	    else { // Go left
	        return binarySearch(numbers, val_X, left, mid - 1);
	    }
	    
	}
}
