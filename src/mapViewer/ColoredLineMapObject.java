package mapViewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;

public class ColoredLineMapObject implements MapObject{

	private LineString myLineString;
	private String myName; //name des Strasse
    private int strokeWidth;
    public Color myColor;
	
	public String getMyName() {
		return myName;
	}
	
	public LineString getMyLineString() {
		return myLineString;
	}

	public ColoredLineMapObject(LineString ls) {
		this.myLineString = ls;
		this.myName = "";
	}
	
	public ColoredLineMapObject(String name, LineString ls) {
		this.myName = name;
		this.myLineString = ls;
	}
		
	@Override
	public void draw(Graphics2D g, Transformation t) {
		
		g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		Coordinate[] coords = myLineString.getCoordinates();
		int n = coords.length;
		int[] x = new int[n];
		int[] y = new int[n];
		for(int i=0; i<n; i++) {
			x[i] = t.getColumn(coords[i].x);
			y[i] = t.getRow(coords[i].y);			
		}
		Color c = g.getColor();
		g.setColor(myColor);
		g.drawPolyline(x, y, n);
		g.setColor(c);
		
	}

	@Override
	public Envelope getBoundingBox() {

		return myLineString.getEnvelopeInternal();
	}

    public void setStrokeWidth(int i) {
        strokeWidth = i;
        
    }
    
    public void setColor (Color c) {
    	myColor = c;
    }
}
