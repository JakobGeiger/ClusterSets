package mapViewer;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;

public class KelpMapObject implements MapObject, Comparable<KelpMapObject> {

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
	
	public KelpMapObject(Point p) {
		myPoint = p;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		g.setStroke(new BasicStroke(strokeWidth));
	    Color c = g.getColor();
	    if (myColor != null) g.setColor(myColor);
		g.fillOval(t.getColumn(myPoint.getX()) - strokeWidth, t.getRow(myPoint.getY()) - strokeWidth, strokeWidth*2, strokeWidth*2);
		g.setColor(c);
		g.fillOval(t.getColumn(myPoint.getX()) - 6, t.getRow(myPoint.getY()) - 6, 12, 12);
	}

	@Override
	public Envelope getBoundingBox() {
		return new Envelope(myPoint.getX(),myPoint.getX(),myPoint.getY(),myPoint.getY());
	}

	public int compareTo(KelpMapObject p2) {
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
