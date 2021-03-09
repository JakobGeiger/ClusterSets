package mapViewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class PolygonMapObject implements MapObject {
	
	private Polygon myPolygon;
	private String myName;
	private Color strokeColor;
	private Color fillColor;
	
	public String getMyName() {
		return myName;
	}

	public Polygon getMyPolygon() {
		return myPolygon;
	}

	public PolygonMapObject(Polygon p) {
		myPolygon = p;
		myName = "";
		strokeColor = Color.BLACK; 
		fillColor = null;
	}
		
	public PolygonMapObject(String name, Polygon p) {
		myPolygon = p;
		myName = name;
		strokeColor = Color.BLACK;
		fillColor = null;
	}
	
	public void setStrokeColor(Color c) {
	    strokeColor = c;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
	    JTSPointTransformation jtsTrans = new JTSPointTransformation(t);
        ShapeWriter sw = new ShapeWriter(jtsTrans);
        Shape a = sw.toShape(myPolygon);
        
		Color oldColor = g.getColor();
		if (fillColor != null) {
			g.setColor(fillColor);
			g.fill(a);	
		}
		if (strokeColor != null) {
			g.setColor(strokeColor);		
			g.draw(a);	
		}
		Point centr = getMyPolygon().getCentroid();
		Font font = new Font("Arial", Font.PLAIN, 10);
	    g.setFont(font);
	    String name = getMyName();
	    if (name.length() > 0)
	    	g.drawString(name, t.getColumn(centr.getX()), t.getRow(centr.getY()));
	    g.setColor(oldColor);
	}

	@Override
	public Envelope getBoundingBox() {
		return myPolygon.getEnvelopeInternal();
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	
}