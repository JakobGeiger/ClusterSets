import java.awt.Color;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import delaunayTriangulation.DelaunayTriangulator;
import delaunayTriangulation.NotEnoughPointsException;
import delaunayTriangulation.Triangle2D;
import delaunayTriangulation.Vector2D;
import mapViewer.LineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.MapObject;
import mapViewer.PointMapObject;
    
public class Clustering {
    
    public static void main(String[] args) {
       
        MapFrame myMapFrame = new MapFrame("IGGGIS - Clustering Project", true);
        myMapFrame.setPreferredSize(new Dimension(1000, 600));
        myMapFrame.pack();
       
        //Define which classes of POIs to read and how to display them
        HashMap<String, Color> colorMap = new HashMap<String, Color>();
        colorMap.put("restaurant", Color.RED);
        colorMap.put("clothes", Color.PINK);
        colorMap.put("fast_food", Color.GREEN);
        colorMap.put("shoe_shop", Color.BLUE);
        colorMap.put("jeweller", Color.ORANGE);
        colorMap.put("hairdresser", Color.CYAN);
      
        //Create a new layer with the specified file of POIs            
        ListLayer l1 = ListLayer.readFromShapefile("pois-badgodesbg.shp", Color.DARK_GRAY, "fclass", colorMap);            
        myMapFrame.getMap().addLayer(l1, 2);
        myMapFrame.setVisible(true);
               
        
        //Prepare a list of points for the triangulation algorithm
        LinkedList<Vector2D> pointList = new LinkedList<Vector2D>();
        for (MapObject myMapObject : l1.getMyObjects()) {
          if (myMapObject instanceof PointMapObject) {
            PointMapObject pmo = (PointMapObject) myMapObject;
            Point p = pmo.getMyPoint();
            Vector2DWithInfo<PointMapObject> v2d = new Vector2DWithInfo<PointMapObject>(p.getX(), p.getY(), pmo);
            pointList.add(v2d);
          }
        }
        
        //Triangulate        
        DelaunayTriangulator dt = new DelaunayTriangulator(pointList);
        try {
			dt.triangulate();
		} catch (NotEnoughPointsException e) {
			e.printStackTrace();
		}
        
        //create layers for display of edges 
        GeometryFactory gf = new GeometryFactory();
        ListLayer l2 = new ListLayer(Color.DARK_GRAY);
        ListLayer l3 = new ListLayer(Color.LIGHT_GRAY);
        
        //iterate all triangles for drawing their edges
        for (Triangle2D t : dt.getTriangles()){
        	
        	Vector2DWithInfo<PointMapObject> a = (Vector2DWithInfo<PointMapObject>) t.a;	
        	Vector2DWithInfo<PointMapObject> b = (Vector2DWithInfo<PointMapObject>) t.b;
           	Vector2DWithInfo<PointMapObject> c = (Vector2DWithInfo<PointMapObject>) t.c;
           	
           	Coordinate[] coords = new Coordinate[3];
           	coords[0] = new Coordinate(a.x, a.y);
           	coords[1] = new Coordinate(b.x, b.y);
           	coords[2] = new Coordinate(c.x, c.y);
           	
           	PointMapObject[] pmos = new PointMapObject[3];
           	pmos[0] = a.info;
           	pmos[1] = b.info;
           	pmos[2] = c.info;
           	
        	//iterate all edges of the current triangle
        	for (int i = 0; i < 3; i++) {
        		PointMapObject p1 = pmos[i];
        		PointMapObject p2 = pmos[(i + 1) % 3];
        		//if (p1.compareTo(p2) < 0) {
        			Coordinate[] lineCoords = new Coordinate[2]; 
        			lineCoords[0] = coords[i];
        			lineCoords[1] = coords[(i + 1) % 3];
        			LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
        			if (p1.myColor == p2.myColor) {
        				lmo.setStrokeWidth(4);
        				l2.add(lmo);
                	} else {
                		lmo.setStrokeWidth(2);
                		l3.add(lmo);
                	}
        		//}
        	}
        }
        	 
        myMapFrame.getMap().addLayer(l3, 0);
        myMapFrame.getMap().addLayer(l2, 1);
        
        //write svg - example from https://xmlgraphics.apache.org/batik/using/svg-generator.html
        
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        myMapFrame.getMap().paint(svgGenerator);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        Writer out;
		try {
			out = new FileWriter("test.svg");
			svgGenerator.stream(out, useCSS);   
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
}

