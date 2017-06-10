package image_processing.test_scripts;

import image_processing.algorithms.Marked_Pixel;
import image_processing.data.Pixel;
import image_processing.data_structures.DoubleLinkedList;

public class Double_Linked_List_test {
	
	public static void main(String[] args) {
		
		DoubleLinkedList list = new DoubleLinkedList();
		
		list.insert(new Pixel(4,5));
		list.insert(new Pixel(5,6));
		list.insert(new Pixel(1,1));
		list.insert(new Pixel(2,3));
		list.insert(new Pixel(3,4));
		list.insert(new Pixel(8,8));
		
		System.out.println("Print the list - expects Y max --> Y min");
		System.out.println(list.print());
		
		System.out.println("Search for 1 - expects not null");
		System.out.println(list.Search_and_return_list(1));
		System.out.println("Search for 5 - expects not null");
		System.out.println(list.Search_and_return_list(5));
		System.out.println("Search for 7 - expects false");
		System.out.println(list.Search_and_return_list(7));
		System.out.println();
		
		System.out.println("Search and print dynamic table for 6 - expects no duplicates");
		new Marked_Pixel().insertPixel(list, new Pixel(4,6));
		new Marked_Pixel().insertPixel(list, new Pixel(4,6));
		new Marked_Pixel().insertPixel(list, new Pixel(3,6));
		new Marked_Pixel().insertPixel(list, new Pixel(2,6));
		new Marked_Pixel().insertPixel(list, new Pixel(1,6));
		
		list.Search_and_return_list(6).printTable();
		
		
	}
}
