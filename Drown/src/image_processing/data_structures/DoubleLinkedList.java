package image_processing.data_structures;

import image_processing.data.Pixel;

/**
 * Magnus Haugaard Nielsen
 * 7/6 2017
 */

public class DoubleLinkedList {
	
	private Node sentinel = new Node();
	
	public DoubleLinkedList() {
		
	}

	// Basic search algorithm - Theta(n)
	// TODO Better run time is possible with better search algo, though
	// since this is a doubly linked list I dont know...
	public DynamicTable Search_and_return_list(double key) {
		
		Node temp = sentinel.getNext();
		
		while (!temp.equals(sentinel)) {
			
			if (temp.getKey() == key) {
				
				return temp.getTable();
			}
			
			temp = temp.getNext();
		} // while end
		
		return null;
	} // end method

	public void insert(Pixel p) {
		
		Node node = new Node();
		node.setKey(p.getY());
		node.getTable().insert(p);
		
		node.setNext(sentinel.getNext());
		sentinel.getNext().setPrev(node);
		sentinel.setNext(node);
		node.setPrev(sentinel);
		
		insertionSort();
		
	} // end method
	
	// TODO Working spaghetti code
	private void insertionSort() {
		
		Node node = sentinel.getNext();
		
		while (!node.equals(sentinel) && node.getNext().getKey() > node.getKey()) {
			
			Node p = node;
			Node pp = node.getPrev();
			Node n = node.getNext();
			Node nn = n.getNext();
			
			pp.setNext(n);
			n.setPrev(pp);
			n.setNext(p);
			p.setPrev(n);
			p.setNext(nn);
			nn.setPrev(p);
			
		}
	}
	
	public String print() {
			
		String str = "";
		Node temp = sentinel.getNext();
			
		while (!temp.equals(sentinel)) {
			
			str += "Node key : " + Double.toString(temp.getKey());
			str += "\nNode list elements : " + temp.getTable().getElements();
			str += "\n\n";
			temp = temp.getNext();
		} // while end
		
		return str;
			
	} // method end
	
	public Node getSentinel() {
		
		return sentinel;
	}
	
} // end class
