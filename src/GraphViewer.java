import java.awt.Color;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.LineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.PointMapObject;
//import mapViewer.SmallPointMapObject;

public class GraphViewer {
	
	public static void main(String[] args)
	{
		try {
			//displayGraph("GISData\\Solutions\\standorte_uni_bonn_utm0.9Greedy.graphml");
			displayGraph("SubInstancesPOIS\\Field3count50WithBeta0.0.graphml");
			//displayGraph("IpeData\\Solutions\\manhattan-subway+hotels+clinics0.9Greedy.graphml");
			//displayGraph("IpeData\\Solutions\\seattle-restaurants0.0Greedy.graphml");
			//displayGraph("IpeData\\seattle-restaurants0.5.graphml");
			//displayGraph("IpeData\\manhattan-subway+hotels+clinics.graphml");
			//displayGraph("GISData\\standorte_uni_bonn_utm.graphml");
			//displayGraph("SubInstancesPOIS\\Solutions\\Field3count50withBeta0.5Greedy.graphml");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void displayGraph(String location) throws IOException
	{
		Color[] colors = {
				new Color(0xfbb4ae), //red
				new Color(0xb3cde3), //blue
				new Color(0xccebc5), //green
				new Color(0xdecbe4), //purple
				new Color(0xfed9a6), //orange
				new Color(0xe5d8bd), //gray
				new Color(0xfddaec), //pink
				new Color(0xffffcc) //yellow
			};
		int colorindex = 0;
		
		Map<Color, Color> translation = new HashMap<>();
		
		Color[] ncolors = {
				new Color(0xFFB300), // Vivid Yellow
                new Color(0x803E75), // Strong Purple
                new Color(0xFF6800), // Vivid Orange
                new Color(0xA6BDD7), // Very Light Blue
                new Color(0xC10020), // Vivid Red
                new Color(0x00538A), // Grayish Yellow
                new Color(0x817066), // Medium Gray
                new Color(0x007D34) // Vivid Green
		};
		
		Color[] bgcolors = {
				new Color(0, 255, 255), //#1
				new Color(255, 175, 175), //#2
				new Color(255, 200, 0), //#3
				new Color(255, 0, 0), //#4
				new Color(0, 255, 0), //#5
				new Color(0, 0, 255), //#6
		};
		
		for(int i = 0; i < ncolors.length; i++)
		{
			translation.put(ncolors[i], colors[i]);
		}
		
		for (int i = 0; i < bgcolors.length; i++)
		{
			translation.put(bgcolors[i], colors[i]);
		}
		
		int stretch = 1;
		
		MapFrame myMapFrame = new MapFrame(location, true);
        myMapFrame.setPreferredSize(new Dimension(1000, 1000));
        myMapFrame.pack();
        GeometryFactory gf = new GeometryFactory();
        
        GraphMLIO.GraphWrapper wrapper = GraphMLIO.readGraph(location);
        
        Set<Vector2D> V = wrapper.V;
        Set<Edge2D> E = wrapper.E;
        Map<Vector2D, Color> colorMap = wrapper.colorMap;
        
        ListLayer l1 = new ListLayer(Color.DARK_GRAY);
        
        for (Edge2D e : E)
        {
           	Coordinate[] coords = new Coordinate[2];
           	coords[0] = new Coordinate(e.a.x*stretch, e.a.y*stretch);
           	coords[1] = new Coordinate(e.b.x*stretch, e.b.y*stretch);
           
    		
    		Coordinate[] lineCoords = new Coordinate[2]; 
			lineCoords[0] = coords[0];
			lineCoords[1] = coords[1];
			
			LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
			

			lmo.setStrokeWidth(2);
			l1.add(lmo);
        }
        
        myMapFrame.getMap().addLayer(l1, 0);
        
        ListLayer l2 = new ListLayer(Color.DARK_GRAY);
        
        for(Vector2D v : V)
        {
        	Color c = colorMap.get(v);
        	
        	if(!translation.keySet().contains(c))
			{
				translation.put(c, colors[colorindex]);
				colorindex++;
			}
			
			c = translation.get(c);
			
        	PointMapObject pmo = new PointMapObject(gf.createPoint(new Coordinate(v.x*stretch, v.y*stretch)));
        	pmo.myColor = c;
        	l2.add(pmo);
        }
        
        myMapFrame.getMap().addLayer(l2, 1);
        myMapFrame.setVisible(true);
        
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        myMapFrame.getMap().paint(svgGenerator);
        
        System.out.println(translation);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        Writer out;
		try {
			out = new FileWriter("output.svg");
			svgGenerator.stream(out, useCSS);   
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
}
