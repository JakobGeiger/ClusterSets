import java.awt.Color;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import delaunayTriangulation.DelaunayTriangulator;
import delaunayTriangulation.NotEnoughPointsException;
import delaunayTriangulation.Vector2D;
import mapViewer.LineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.MapObject;
import mapViewer.PointMapObject;
import mapViewer.SquarePointMapObject;
import mapViewer.StringMapObject;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.FirstOrderDelaunayTriangulator;
import java.time.LocalDate;
    
public class GreedyClusterTest {
    
    public static void main(String[] args) {
    	
    	
    	long time = System.currentTimeMillis();
    	//long size = 1000;
    	//String datei = "random"+size;
    	
    	
    	String datei = "pois";
        MapFrame myMapFrame = new MapFrame("Greedy Clustering", true);
        myMapFrame.setPreferredSize(new Dimension(1000, 600));
        myMapFrame.pack();
        GeometryFactory gf = new GeometryFactory();
        
        //Define which classes of POIs to read and how to display them
        HashMap<String, Color> colorMap = new HashMap<String, Color>();
        colorMap.put("restaurant", Color.RED);
        colorMap.put("clothes", Color.PINK);
        colorMap.put("fast_food", Color.GREEN);
        colorMap.put("shoe_shop", Color.BLUE);
        colorMap.put("jeweller", Color.ORANGE);
        colorMap.put("hairdresser", Color.CYAN);
      
        
        //Create a new layer with the specified file of POIs            
        ListLayer l1 = ListLayer.readFromShapefile(datei+".shp", Color.DARK_GRAY, "fclass", colorMap);
               
        
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
        
    	
    	
        Map<Vector2D, Color> colors = new HashMap<Vector2D, Color>();
        //LinkedList<Vector2D> pointList = new LinkedList<Vector2D>();
        //ListLayer l1 = new ListLayer(Color.DARK_GRAY);
        //Vector2D v2dwi;
        //Random r = new Random();
        
        /*
    	for(long i = 0; i< size; i++)
    	{
    		double randomX = 350000 + 50000*r.nextDouble();
    		double randomY = 5600000 + 50000*r.nextDouble();
    		int randomColor = (int) (6*r.nextDouble());
    		PointMapObject pmo = new PointMapObject(gf.createPoint(new Coordinate(randomX, randomY)));
    		Color c;
    		switch(randomColor)
    		{
    		case 0: {c = Color.RED; break;}
    		case 1: {c = Color.PINK; break;}
    		case 2: {c = Color.GREEN; break;}
    		case 3: {c = Color.BLUE; break;}
    		case 4: {c = Color.ORANGE; break;}
    		case 5: {c = Color.CYAN; break;}
    		default: {c = Color.BLACK; break;}
    		}
    		pmo.myColor = c;
    		v2dwi = new Vector2DWithInfo<PointMapObject>(randomX, randomY, pmo);
    		pointList.add(v2dwi);
    		l1.add(pmo);
    	}*/
        
        //Triangulate        
        DelaunayTriangulator dt = new DelaunayTriangulator(pointList);
        FirstOrderDelaunayTriangulator fodt = new FirstOrderDelaunayTriangulator(dt);
        
        try {
			fodt.execute();
		} catch (NotEnoughPointsException e) {
			e.printStackTrace();
		}
        
        
        
        for(Vector2D v : fodt.V)
        {
        	Vector2DWithInfo<PointMapObject> v2d = (Vector2DWithInfo<PointMapObject>) v;
        	
        	colors.put(v, v2d.info.myColor);
        }
        
        Map<Edge2D, List<Edge2D>> crossings = new HashMap<Edge2D, List<Edge2D>>();
        for(Edge2D e : fodt.crossings.keySet())
        {
        	crossings.put(e, new LinkedList<Edge2D>());
        	crossings.get(e).add(fodt.crossings.get(e));
        }
        
        GreedyCluster1Planar gcm = new GreedyCluster1Planar(fodt.V, fodt.E, colors);
        gcm.executeAlgorithm();
        
        //create layers for display of edges 
        ListLayer l2 = new ListLayer(Color.LIGHT_GRAY);
        ListLayer l3 = new ListLayer(Color.DARK_GRAY);

        ListLayer l4 = new ListLayer(Color.DARK_GRAY);
        
        //iterate all triangles for drawing their edges
        for (Edge2D e : gcm.E){
        	
           	
           	Coordinate[] coords = new Coordinate[2];
           	coords[0] = new Coordinate(e.a.x, e.a.y);
           	coords[1] = new Coordinate(e.b.x, e.b.y);
           
    		
    		Coordinate[] lineCoords = new Coordinate[2]; 
			lineCoords[0] = coords[0];
			lineCoords[1] = coords[1];
			
			LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
			

			lmo.setStrokeWidth(2);
			l2.add(lmo);
           	
        	
        }
        
        
        
        for(Vector2D v : gcm.newVertices)
        {
        	//PointMapObject pmo = new PointMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
        	PointMapObject pmo = new SquarePointMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
        	pmo.myColor = colors.get(v);
        	l4.add(pmo);
        }
        
        

        ListLayer l5 = new ListLayer(Color.DARK_GRAY);
        int fontSize = 15;
        String content = "";
        
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;
        
        for(Vector2D v : fodt.V)
        {
        	if(v.x > xMax)
        		xMax = v.x;
        	if(v.y > yMax)
        		yMax = v.y;
        }
        
        
        
        content += "File: " + datei + ".shp" + "\n";
        content += "Date: " + LocalDate.now().toString() + "\n";
        content += "#Vertices: " + fodt.V.size() + "\n";
        content += "#Classes: " + colorMap.size() + "\n";
        //content += "#Clusters: " + gcm.numberOfClusters + "\n";
        //content += "Runtime Greedy: " + gcm.time + " ms\n";
        content += "Overall Runtime: " + (System.currentTimeMillis()-time) + " ms";
        
        l5.add(new StringMapObject(content, gf.createPoint(new Coordinate(xMax + 2000, yMax + 2000)), fontSize));
        
        ListLayer l6 = new ListLayer(Color.MAGENTA);
        
        for(Edge2D e : gcm.oldEdges)
        {
        	Coordinate[] coords = new Coordinate[2];
           	coords[0] = new Coordinate(e.a.x, e.a.y);
           	coords[1] = new Coordinate(e.b.x, e.b.y);
           
    		
    		Coordinate[] lineCoords = new Coordinate[2]; 
			lineCoords[0] = coords[0];
			lineCoords[1] = coords[1];
			
			LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
			

			lmo.setStrokeWidth(2);
			l6.add(lmo);
        }
        	 
        myMapFrame.getMap().addLayer(l2, 0);
        myMapFrame.getMap().addLayer(l3, 1);
        myMapFrame.getMap().addLayer(l4, 2);
	    myMapFrame.getMap().addLayer(l1, 3);
        myMapFrame.getMap().addLayer(l5, 4);
        myMapFrame.getMap().addLayer(l6, 5);
	    myMapFrame.setVisible(true);
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
			out = new FileWriter(datei+"Solved.svg");
			svgGenerator.stream(out, useCSS);   
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

}

