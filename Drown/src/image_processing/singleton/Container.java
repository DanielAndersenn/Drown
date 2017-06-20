package image_processing.singleton;

import image_processing.data.Pixel;
import image_processing.data_structures.DoubleLinkedList;
import image_processing.data_structures.Node;

public class Container {

	public static Container instance = null;

	private Pixel top = null, bot, left, right;
	private Pixel a, b, c, d;
	private DoubleLinkedList list;
	private double mm_px;
	private double minDistance = getDistance(1500);
	private double maxDistance = getDistance(2000);
	private double prevPicDist = 0;

	public double getPrevPicDist() {
		return prevPicDist;
	}

	public void setPrevPicDist(double prevPicDist) {
		this.prevPicDist = prevPicDist;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	private Container() {

		list = new DoubleLinkedList();
	}

	public static Container getInstance() {

		if (instance == null)
			instance = new Container();

		return instance;
	}

	public void controlDimensions(Pixel p) {

		// To counter NullPoinerException
		if (top == null) {
			top = p;
			bot = p;
			right = p;
			left = p;
		}

		// Is p a new top?
		if (p.getY() > top.getY()) {
			top = p;
			return;
		}

		// Is p a new bot?
		if (p.getY() < bot.getY()) {
			bot = p;
			return;
		}

		// Is p a new right?
		if (p.getX() > right.getX()) {
			right = p;
			return;
		}

		// Is p a new left?
		if (p.getX() < left.getX()) {
			left = p;
			return;
		}
	}

	public void findABCD() {
		
		/**
		 * I changed this function to not take any parameters.
		 * BEFORE: It took the rings height (mm) and the min margin of our margin box (mm)
		 * 
		 * AFTER: Instead it fetches the rings height from our singleton class "Rings"
		 * The minimum margin of our margin box is pr. standard 750.
		 * I just hardcoded this into the calculations.
		 * Date: 9/6 '17
		 * @author haugaard
		 */
		
		double h_px = top.getX() - bot.getX();
		mm_px = Rings.getInstance().getActiveRingDimension()/h_px;
		double y_px = 750/mm_px;

		// Find A and B
		for (Node node = list.getSentinel().getNext();
				!node.equals(list.getSentinel());
				node = node.getNext()) {

			Pixel rowMin = node.getTable().getTable()[0];
			Pixel rowMax = node.getTable().getTable()[node.getTable().getElements() - 1];

			if (rowMax.getX() - rowMin.getX() >= y_px) {
				a = rowMin;
				b = rowMax;
			}
		}

		// Find C and D
		for (Node node = list.getSentinel().getPrev();
				!node.equals(list.getSentinel());
				node = node.getPrev()) {

			Pixel rowMin = node.getTable().getTable()[0];
			Pixel rowMax = node.getTable().getTable()[node.getTable().getElements() - 1];

			if (rowMax.getX() - rowMin.getX() >= y_px) {
				c = rowMin;
				d = rowMax;
			}
		}
	}

	public Pixel getTop() {
		return top;
	}

	public Pixel getBot() {
		return bot;
	}

	public Pixel getLeft() {
		return left;
	}

	public Pixel getRight() {
		return right;
	}

	public DoubleLinkedList getList() {
		return list;
	}

	public Pixel getA() {
		return a;
	}

	public Pixel getB() {
		return b;
	}

	public Pixel getC() {
		return c;
	}

	public Pixel getD() {
		return d;
	}
	
	public double getMM_PX() {
		
		return mm_px;
	}

	public void printData() {

		String str = "";

		str += "Top	: (" + top.getX() + ", " + top.getY() + ")\n";
		str += "Bot	: (" + bot.getX() + ", " + bot.getY() + ")\n";
		str += "Right	: (" + right.getX() + ", " + right.getY() + ")\n";
		str += "Left	: (" + left.getX() + ", " + left.getY() + ")\n";
		str += "A	: (" + a.getX() + ", " + a.getY() + ")\n";
		str += "B	: (" + b.getX() + ", " + b.getY() + ")\n";
		str += "C	: (" + c.getX() + ", " + c.getY() + ")\n";
		str += "D	: (" + d.getX() + ", " + d.getY() + ")\n";

		System.out.print(str);
	}

	public void resetList() {

		if(top != null) {
		prevPicDist = top.getY() - bot.getY();
		}
		list = new DoubleLinkedList();
		top = null;
		bot = null;
		left = null;
		right = null;
	}

	
	public double getDistance(double x) {
		
		return 2087.36015710060 * Math.exp((-0.302828015964803 * Math.pow(10, -2)) * x);
	}

}
