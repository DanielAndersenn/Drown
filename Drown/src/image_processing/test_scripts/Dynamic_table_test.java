package image_processing.test_scripts;

import image_processing.data.Pixel;
import image_processing.data_structures.DynamicTable;

public class Dynamic_table_test {

	public static void main(String[] args) {
		
		DynamicTable table = new DynamicTable();
		
		Pixel p = new Pixel(100,2);
		table.insert(p);
		p = new Pixel(2,3);
		table.insert(p);
		p = new Pixel(3,4);
		table.insert(p);
		p = new Pixel(4,5);
		table.insert(p);
		p = new Pixel(6,7);
		table.insert(p);
		p = new Pixel(0.5, 5);
		table.insert(p);
		p = new Pixel(0.2,5);
		table.insert(p);
		
		table.printTable();
		table.reportSize();
	}
}
