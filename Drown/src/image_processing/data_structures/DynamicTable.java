package image_processing.data_structures;
import image_processing.data.Pixel;

public class DynamicTable {
	
	private Pixel[] table = new Pixel[1];
	private Pixel[] backup;
	private int elements = 0;

	public void insert(Pixel pixel) {
		
		if (elements == 0) {
			table = new Pixel[1];
		}
		else if (elements == table.length) {
			backup = table.clone();
			table =  new Pixel[table.length * 2];
			
			for (int i = 0; i < backup.length; i++) {
				table[i] = backup[i];
			}
		}
		
		table[elements] = pixel;
		insertionSort();
		elements++;
	} // insert end
	
	private void insertionSort() {
		
		for (int j = 1; j <= elements; j++) {
			Pixel temp = table[j];
			double key = table[j].getX();
			int i = j - 1;
			
			while (i >= 0 && table[i].getX() > key) {
				table[i + 1] = table[i];
				i = i - 1;
			}
			
			table[i + 1] = temp;
		}
	}
		
	public void printTable() {

		for (int i = 0; i < elements; i++) {
			System.out.print("(" + table[i].getX() + ", " + table[i].getY() + ")\n");
		}

		System.out.print("\n");
	}
	
	public void reportSize() {
		
		System.out.println("Elements : " + elements + " " + "\nTable length : " + Integer.toString(table.length));
	}
	
	public Pixel[] getTable() {
		
		return table;
	}
	
	public void setTable(Pixel[] table) {
		
		this.table = table;
	}
	
	public int getElements() {
		
		return elements;
	}
}