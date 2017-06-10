package image_processing.algorithms;

import java.util.ArrayList;

import image_processing.singleton.Container;
import image_processing.data.Pixel;
import image_processing.data_structures.Node;

public class Polygon {
	
	private ArrayList<Pixel> polygon = new ArrayList<>();
	private final double D_H_MM = 110;
	private final double D_B_MM = 720;
	
	/**
	 * 
	 * @param A
	 * @param B
	 * @param C
	 * @param D
	 * @param Left
	 * @param Right
	 * @param mm_px
	 * @return
	 */
	public ArrayList<Pixel> generatePolygon(Pixel A, Pixel B, Pixel C, Pixel D,
										 Pixel Left, Pixel Right, double mm_px) {
		
		double d_h_mm = D_H_MM/mm_px;
		double d_b_mm = D_B_MM/mm_px;
		
		Pixel poly_top = new Pixel(A.getX() + ((B.getX() - A.getX())/2), A.getY() + d_h_mm/2);
		Pixel poly_bot = new Pixel(C.getX() + ((D.getX() - C.getX())/2), C.getY() - d_h_mm/2);
		Pixel poly_left = new Pixel(Left.getX() + d_b_mm/2, Left.getY());
		Pixel poly_right = new Pixel(Right.getX() - d_b_mm/2, Right.getY());
		
		polygon.add(poly_top);
		polygon.add(poly_bot);
		polygon.add(poly_left);
		polygon.add(poly_right);
		
		for (Node node = Container.getInstance().getList().getSentinel().getPrev();
			 !node.equals(Container.getInstance().getList().getSentinel());
			 node = node.getPrev()) {
			
			// If pass scope - break for loop
			if (node.getTable().getTable()[0].getY() >= poly_bot.getY())
				break;

			// If before scope - skip iteration
			if (node.getTable().getTable()[0].getY() <= poly_top.getY())
				continue;
			
			// If we are at mid row - skip iteration
			if (node.getTable().getTable()[0].getY() == poly_left.getY())
				continue;
			
			polygon.add(new Pixel(node.getTable().getTable()[0].getX() + d_b_mm,
								  node.getTable().getTable()[0].getY()));
			
			polygon.add(new Pixel(node.getTable().getTable()[node.getTable().getElements() - 1].getX() - d_b_mm,
								  node.getTable().getTable()[node.getTable().getElements() - 1].getY()));
			
		}
		
		return polygon;
	}
	
	/**
	 * 
	 * @param top
	 * @param bot
	 * @param left
	 * @param right
	 * @param imageHeight
	 * @param imageWidth
	 */
	public void centerPolygon(Pixel top, Pixel bot, Pixel left, Pixel right, int imageHeight, int imageWidth) {
		
		// Calculate vector from center of circle to center of image
		double c_y = top.getY() - ((top.getY() - bot.getY())/2);
		double c_x = right.getX() - ((right.getX() - left.getX())/2);
		
		Pixel o = new Pixel((imageWidth/2) - c_x,
							(imageHeight/2) - c_y);
		
		// Move all polygon pixels
		//for... all pixels in polygon
		// simple vector
		for (Pixel p : polygon) {
			p.setX(p.getX() + o.getX());
			p.setY(p.getY() + o.getY());
		}
	}

	/**
	 * 
	 * @param inf Image width
	 * @param p Center pixel of image
	 * @return
	 */
	public boolean isInside(double inf, Pixel p) {
		
		// Create a point for line segment from p to infinite
		Pixel extreme = new Pixel(inf, p.getY());
		
		// Count intersections of the above line with sides of polygon
		int count = 0, i = 0;
		int n = polygon.size();
			    
		do {
			int next = (i + 1) % n;
		 
			// Check if the line segment from 'p' to 'extreme' intersects
			// with the line segment from 'polygon[i]' to 'polygon[next]'
			if (doIntersect(polygon.get(i), polygon.get(next), p, extreme)) {
				// If the point 'p' is colinear with line segment 'i-next',
				// then check if it lies on segment. If it lies, return true,
				// otherwise false
				if (orientation(polygon.get(i), p, polygon.get(next)) == 0)
					return onSegment(polygon.get(i), p, polygon.get(next));
		            
				if (orientation(polygon.get(next), p, extreme) != 0)
					count++;
			}
			i = next;
		} while (i != 0);
		 
		// Return true if count is odd, false otherwise
		return count%2 == 1;
	}
		
	private boolean doIntersect(Pixel p1, Pixel q1, Pixel p2, Pixel q2) {
			
		int o1 = orientation(p1, q1 , p2);
		int o2 = orientation(p1, q1 , q2);
		int o3 = orientation(p2, q2 , p1);
		int o4 = orientation(p2, q2 , q1);
			
		if (o1 != o2 && o3 != o4)
			return true;
			
		// Special Cases
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1
		if (o1 == 0 && onSegment(p1, p2, q1))
			return true;
		 
		// p1, q1 and p2 are colinear and q2 lies on segment p1q1
		if (o2 == 0 && onSegment(p1, q2, q1))
			return true;
		 
		// p2, q2 and p1 are colinear and p1 lies on segment p2q2
		if (o3 == 0 && onSegment(p2, p1, q2))
			return true;
		 
		// p2, q2 and q1 are colinear and q1 lies on segment p2q2
		if (o4 == 0 && onSegment(p2, q1, q2))
			return true;
		 
		return false; // Doesn't fall in any of the above cases
			
	}
		
	private int orientation(Pixel p, Pixel q, Pixel r) {
			
		double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
					 (q.getX() - p.getX()) * (r.getY() - q.getY());
			
		if (val == 0)
			return 0;
			
		return (val > 0) ? 1 : 2;
	}
		
	private boolean onSegment(Pixel p, Pixel q, Pixel r) {
		if (q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
			q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY())) {
			return true;
		}
		    
		return false;
	}
	
	@Override
	public String toString() {
		
		String str = "";
		str += "Pixels in polygon : " + polygon.size() + "\n";
		
		
		return str;
	}
	
}
