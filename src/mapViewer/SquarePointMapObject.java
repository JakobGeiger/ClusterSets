package mapViewer;

import java.awt.Color;
import java.awt.Graphics2D;
import com.vividsolutions.jts.geom.Point;

public class SquarePointMapObject extends PointMapObject implements MapObject{
	
	public SquarePointMapObject(Point p) {
		super(p);
		myPoint = p;
	}
	
	private Point myPoint;
	
	@Override
	public void draw(Graphics2D g, Transformation t) {
	    Color c = g.getColor();
	    g.fillRect(t.getColumn(myPoint.getX()) - 6, t.getRow(myPoint.getY()) - 6, 13, 13);
	    if (myColor != null) g.setColor(myColor);
		g.fillRect(t.getColumn(myPoint.getX()) - 4, t.getRow(myPoint.getY()) - 4, 9, 9);
		g.setColor(c);
	}

	
}
