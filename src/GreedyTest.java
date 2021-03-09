import java.util.ArrayList;
import java.util.Collection;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.LineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.PointMapObject;

public class GreedyTest {

	public static void main(String[] args)
	{
		Collection<Vector2D> V = new ArrayList<Vector2D>();
		Collection<Edge2D> E = new ArrayList<Edge2D>();
		
		double x = 100000;
		
		Vector2D p1 = new Vector2D(1*x,0*x);
		Vector2D p2 = new Vector2D(0*x,1*x);
		Vector2D p3 = new Vector2D(1*x,1*x);
		Vector2D p4 = new Vector2D(2*x,1*x);
		Vector2D p5 = new Vector2D(1.25*x,1.75*x);
		Vector2D p6 = new Vector2D(0*x,2*x);
		Vector2D p7 = new Vector2D(2*x,2*x);
		Vector2D p8 = new Vector2D(0*x,3*x);
		Vector2D p9 = new Vector2D(1*x,3*x);
		Vector2D p10 = new Vector2D(0.5*x,3.25*x);
		Vector2D p11 = new Vector2D(1*x,3.75*x);
		Vector2D p12 = new Vector2D(0*x,4*x);
		Vector2D p13 = new Vector2D(2*x,4*x);
		Vector2D p14 = new Vector2D(1*x,5*x);
		
		V.add(p1);
		V.add(p2);
		V.add(p3);
		V.add(p4);
		V.add(p5);
		V.add(p6);
		V.add(p7);
		V.add(p8);
		V.add(p9);
		V.add(p10);
		V.add(p11);
		V.add(p12);
		V.add(p13);
		V.add(p14);
		
		E.add(new Edge2D(p1,p2));
		E.add(new Edge2D(p1,p4));
		
		E.add(new Edge2D(p2,p3));
		E.add(new Edge2D(p2,p6));
		
		E.add(new Edge2D(p3,p6));
		E.add(new Edge2D(p3,p7));
		E.add(new Edge2D(p3,p4));
		
		E.add(new Edge2D(p4,p6));
		E.add(new Edge2D(p4,p7));
		
		E.add(new Edge2D(p5,p9));
		
		E.add(new Edge2D(p6,p7));
		E.add(new Edge2D(p6,p10));
		
		E.add(new Edge2D(p7,p11));
		
		E.add(new Edge2D(p8,p9));
		E.add(new Edge2D(p8,p12));
		
		E.add(new Edge2D(p9,p12));
		E.add(new Edge2D(p9,p13));
		
		E.add(new Edge2D(p10,p11));
		
		E.add(new Edge2D(p12,p13));
		E.add(new Edge2D(p12,p14));
		
		E.add(new Edge2D(p13,p14));
		
		Greedy greedy = new Greedy(V, E);
		drawIt(V,E, "beforeGreedyTest.svg");
		
		
		greedy.runGreedy();

		drawIt(greedy.V,greedy.E, "afterGreedyTest.svg");
		
		
	}
	
	public static void drawIt(Collection<Vector2D> V, Collection<Edge2D> E, String name) {
		MapFrame myMapFrame = new MapFrame("GreedyTest", true);
        myMapFrame.setPreferredSize(new Dimension(1000, 600));
        myMapFrame.pack();
       
        
      
        //Create a new layer with the specified file of POIs            
        ListLayer l1 = new ListLayer(Color.BLACK);
        

        GeometryFactory gf = new GeometryFactory();
        
        for(Vector2D v : V)
        {
        	Coordinate[] coords = new Coordinate[1];
        	coords[0] = new Coordinate(v.x,v.y);
        	
        	
            l1.add(new PointMapObject(new Point(new CoordinateArraySequence(coords), gf)));
        }
        
        myMapFrame.getMap().addLayer(l1, 2);
        myMapFrame.setVisible(true);
               
        
        
        
        //create layers for display of edges 
        ListLayer l2 = new ListLayer(Color.RED);
        
        //iterate all triangles for drawing their edges
        for (Edge2D e : E){
        	
        	Vector2DWithInfo<PointMapObject> a = new Vector2DWithInfo<PointMapObject>(e.a.x, e.a.y, null);	
        	Vector2DWithInfo<PointMapObject> b = new Vector2DWithInfo<PointMapObject>(e.b.x, e.b.y, null);
           	
           	Coordinate[] coords = new Coordinate[2];
           	coords[0] = new Coordinate(e.a.x, e.a.y);
           	coords[1] = new Coordinate(e.b.x, e.b.y);
           	
           	PointMapObject[] pmos = new PointMapObject[2];
           	pmos[0] = a.info;
           	pmos[1] = b.info;
           	
        	//iterate all edges of the current triangle
        	
        		//if (p1.compareTo(p2) < 0) {
        			Coordinate[] lineCoords = new Coordinate[2]; 
        			lineCoords[0] = coords[0];
        			lineCoords[1] = coords[1];
        			LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
        				lmo.setStrokeWidth(4);
        				l2.add(lmo);
        		//}
        	
        }
        	 
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
			out = new FileWriter(name);
			svgGenerator.stream(out, useCSS);   
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
