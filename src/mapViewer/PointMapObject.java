package mapViewer;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;

public class PointMapObject implements MapObject, Comparable<PointMapObject> {

	private Point myPoint;
    public String fclass;
    public Color myColor;
    private int strokeWidth = 1;
	
	/**
	 * @return the myPoint
	 */
	public Point getMyPoint() {
		return myPoint;
	}
	
	public PointMapObject(Point p) {
		myPoint = p;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		g.setStroke(new BasicStroke(strokeWidth));
	    Color c = g.getColor();
		g.fillOval(t.getColumn(myPoint.getX()) - 6, t.getRow(myPoint.getY()) - 6, 13, 13);
	    if (myColor != null) g.setColor(myColor);
		g.fillOval(t.getColumn(myPoint.getX()) - 4, t.getRow(myPoint.getY()) - 4, 9, 9);
		g.setColor(c);
	}

	@Override
	public Envelope getBoundingBox() {
		return new Envelope(myPoint.getX(),myPoint.getX(),myPoint.getY(),myPoint.getY());
	}

	public int compareTo(PointMapObject p2) {
		if (this.myPoint.getX() < p2.myPoint.getX()) {
			return -1;
		} else if (this.myPoint.getX() > p2.myPoint.getX()) {
			return 1;
		} else {
			if (this.myPoint.getY() < p2.myPoint.getY()) {
				return -1;
			} else if (this.myPoint.getY() > p2.myPoint.getY()) {
				return 1;
			} else {
				return 0;
			}	
		}
	}
	
	public void setStrokeWidth(int i) {
        strokeWidth = i;
        
    }
}
