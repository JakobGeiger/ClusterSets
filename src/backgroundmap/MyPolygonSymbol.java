package backgroundmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Area;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import io.structures.Feature;
import viewer.base.Transformation;
import viewer.symbols.PolygonSymbol;

public class MyPolygonSymbol extends PolygonSymbol {

	public MyPolygonSymbol(Feature feature, Color strokeColor, Stroke stroke, Color fillColor) {
		super(feature, strokeColor, stroke, fillColor);
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		Color oldColor = g.getColor();

		Stroke oldStroke = g.getStroke();
		g.setStroke(stroke);

		for (int k = 0; k < this.myFeature.getGeometry().getNumGeometries(); k++) {
			Geometry geom = this.myFeature.getGeometry().getGeometryN(k);
			drawSingle(g, t, geom);
		}

		g.setColor(oldColor);
		g.setStroke(oldStroke);
	}

	/**
	 * draws a polygon considering holes if available into a specified Graphics
	 * object
	 * 
	 * @param g    Graphics object
	 * @param t    transformation
	 * @param geom geometry
	 */
	private void drawSingle(Graphics2D g, Transformation t, Geometry geom) {
		if (geom instanceof Polygon) {
			Polygon jtsPolygon = (Polygon) geom;

			java.awt.Polygon awtPolygon = lineStringToAWTPolygon(jtsPolygon.getExteriorRing(), t);

			Area polyArea = new Area(awtPolygon);

			Area inner;
			for (int i = 0; i < jtsPolygon.getNumInteriorRing(); i++) {
				inner = new Area(lineStringToAWTPolygon(jtsPolygon.getInteriorRingN(i), t));
				polyArea.subtract(inner);
			}

			if (strokeColor != null) {
				g.setColor(strokeColor);
				g.draw(polyArea);
	
			}
			
			if (fillColor != null) {
				g.setColor(fillColor);
				g.fill(polyArea);
			}
		}
	}

	/**
	 * transforms a lineString to an AWTPolygon
	 * 
	 * @param ring the lineString
	 * @param t    transformation
	 * @return resulted AWTPolygon object
	 */
	private java.awt.Polygon lineStringToAWTPolygon(LineString ring, Transformation t) {
		int npoints = ring.getNumPoints();
		int[] xpoints = new int[npoints];
		int[] ypoints = new int[npoints];
		int zaehler = 0;
		for (Coordinate c : ring.getCoordinates()) {
			xpoints[zaehler] = t.getColumn(c.x);
			ypoints[zaehler] = t.getRow(c.y);
			zaehler++;
		}
		return new java.awt.Polygon(xpoints, ypoints, npoints);
	}
}



