package image_processing.data_structures;


/**
 * Magnus Haugaard Nielsen
 * 7/6/2016
 */

public class Node {
	
	private double key;
	private Node prev;
	private Node next;
	private DynamicTable list;
	
	public Node() {
		
		this.key = -1;
		this.prev = this; // To avoid NullPointerException
		this.next = this;
		list = new DynamicTable();
	} // end constructor

	public double getKey() {
		return key;
	}

	public void setKey(double key) {
		this.key = key;
	}

	public Node getPrev() {
		return prev;
	}

	public void setPrev(Node prev) {
		this.prev = prev;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}
	
	public DynamicTable getTable() {
		
		return list;
	}

} // class end
