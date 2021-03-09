package testGIS;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import io.structures.Feature;
import viewer.symbols.PointSymbol;
import viewer.symbols.PolygonSymbol;
import viewer.symbols.Symbol;
import viewer.symbols.SymbolFactory;

public class CategorizedSymbolFactory implements SymbolFactory {

	@Override
	public Symbol createSymbol(Feature feature) {
		
		if (feature.getGeometry() instanceof Polygon) {
			Stroke s = new BasicStroke();
			Color fillColor = (Color) feature.getAttribute("fillColor");
			Color strokeColor = (Color) feature.getAttribute("strokeColor");				
			return new PolygonSymbol(feature, strokeColor, s, fillColor);
	
		} else if (feature.getGeometry() instanceof Point) {
			Color fillColor = (Color) feature.getAttribute("fillColor");
			int width = (Integer) feature.getAttribute("width");
			return new PointSymbol(feature, fillColor, width);
		} else {
			return null;
		}
	}
}
