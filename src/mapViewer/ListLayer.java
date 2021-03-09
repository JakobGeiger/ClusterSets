package mapViewer;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.ShapefileReader;

/**
 * This class represents a layer that can be displayed in a Map.
 * 
 * @author Jan-Henrik Haunert
 */
public class ListLayer extends Layer {

	/**
	 * The MapObjects of this Layer.
	 */
	private LinkedList<MapObject> myObjects;

	/**
	 * Returns the map objects of this layer
	 * 
	 * @return the map objects of this layer
	 */
	public LinkedList<MapObject> getMyObjects() {
		return myObjects;
	}

	/**
	 * Constructs a new empty Layer with a specified ID.
	 * 
	 * @param myID
	 *            the ID
	 */
	public ListLayer(Color c) {
		super(c);
		extent = null;
		myObjects = new LinkedList<MapObject>();
	}

	/**
	 * Adds a MapObject to this Layer.
	 * 
	 * @param m
	 *            the MapObject to be added
	 */
	public void add(MapObject m) {
		myObjects.add(m);
		if (extent == null) {
			extent = m.getBoundingBox();
		} else {
			extent.expandToInclude(m.getBoundingBox());
		}
	}

	/**
	 * Returns the map objects of this layer that intersect a specified
	 * envelope.
	 * 
	 * @param searchEnv
	 *            the query envelope
	 * @return the map objects of this layer that intersect the envelope
	 */
	public List<MapObject> query(Envelope searchEnv) {
		List<MapObject> result = new LinkedList<MapObject>();
		for (MapObject m : myObjects) {
			if (searchEnv.intersects(m.getBoundingBox())) {
				result.add(m);
			}
		}
		return result;
	}

	/**
	 * creates a layer from an ESRI shapefile
	 * 
	 * @param path
	 *            the path of the file
	 * @param c
	 *            the color used when drawing the layer
	 * @param columnname 
	 * @param filter 
	 * @return a new layer
	 */
	public static ListLayer readFromShapefile(String path, Color c, String columnname, HashMap<String, Color> filter) {
		ListLayer ll = new ListLayer(c); // a new layer with color c
		ShapefileReader shpRead = new ShapefileReader();
		DriverProperties dp = new DriverProperties(path);
		try { // "read" may throw exception that needs to be caught
			FeatureCollection fc = shpRead.read(dp);
			Iterator<Feature> it = fc.iterator();
			while (it.hasNext()) { // iteration over all features
				Feature f = it.next();
				Geometry geom = f.getGeometry();
				GeometryFactory gf = new GeometryFactory();
				if (geom.getGeometryType().equals("Point")) {
					Coordinate[] coords = geom.getCoordinates();
					Point p = gf.createPoint(coords[0]);
					PointMapObject pmo = new PointMapObject(p);
					pmo.fclass = (String) f.getAttribute(f.getSchema().getAttributeIndex(columnname));
					pmo.fclass = pmo.fclass.replaceAll(" ", "");
					
					if (filter.containsKey(pmo.fclass)) {
					    pmo.myColor = filter.get(pmo.fclass);
					    ll.add(pmo);
					} 
				} else if (geom.getGeometryType().equals("LineString")) {
					Coordinate[] coords = geom.getCoordinates();
					LineString ls = gf.createLineString(coords);
					ll.add(new LineMapObject(ls));
				} else if (geom.getGeometryType().equals("MultiLineString")) {
					MultiLineString ml = (MultiLineString) geom;
					for (int i = 0; i < ml.getLength(); i++) {
						Coordinate[] coords = ml.getGeometryN(i).getCoordinates();
						LineString ls = gf.createLineString(coords);
						ll.add(new LineMapObject(ls));
					}
				} else if (geom.getGeometryType().equals("Polygon")) {
					Coordinate[] coords = geom.getCoordinates();
					Polygon p = gf.createPolygon(coords);
					ll.add(new PolygonMapObject(p));
				}
				else if (geom.getGeometryType().equals("MultiPoint")) {
					Coordinate[] coords = geom.getCoordinates();
					for(int i = 0; i < coords.length; i++)
					{
						Point p = gf.createPoint(coords[i]);
						PointMapObject pmo = new PointMapObject(p);
						pmo.fclass = (String) f.getAttribute(f.getSchema().getAttributeIndex(columnname));
						pmo.fclass = pmo.fclass.replaceAll(" ", "");
						
						if (filter.containsKey(pmo.fclass)) {
						    pmo.myColor = filter.get(pmo.fclass);
						    ll.add(pmo);
						} 
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ll;
	}
}
