import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import GraphML.GraphMLIO;
import GraphML.GraphMLIO.ClusterContainer;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.LineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.PointMapObject;
import mapViewer.ColoredLineMapObject;
import mapViewer.KelpMapObject;
import mapViewer.PolygonMapObject;

import java.util.HashSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.vividsolutions.jts.awt.*;
import java.awt.Graphics2D;


public class GraphKelper {

	
	public static void main(String[] args)
	{
		try {
			//displayGraph("GISData\\Solutions\\standorte_uni_bonn_utm0.0Greedyfilled.graphml");
			displayGraph("SubInstancesPOIS\\Solutions\\Field3count50WithBeta0.0GreedyfilledNew.graphml");
			//displayGraph("IpeData\\Solutions\\manhattan-subway+hotels+clinics0.5Greedyfilled.graphml");
			//displayGraph("IpeData\\Solutions\\seattle-restaurants0.5Greedyfilled.graphml");
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
		/*
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
		*/
		
		Color[] bgcolors = {
				new Color(0, 255, 255), //#1
				new Color(255, 0, 0), //#4
				new Color(0, 255, 0), //#5
				new Color(255, 175, 175), //#2
				new Color(255, 200, 0), //#3
				new Color(0, 0, 255), //#6
		};
		
		for (int i = 0; i < bgcolors.length; i++)
		{
			translation.put(bgcolors[i], colors[i]);
		}
		
		
		//translation.put(ncolors[6], new Color(220,180,20));
		
		GraphMLIO.ComponentsWrapper wrapper = GraphMLIO.readConnectedComponents(location);
		
		List<GraphMLIO.ClusterContainer> clusters = wrapper.clusters;
		Map<Vector2D, Color> colorMap = wrapper.colorMap;
		
		int width = 20;
		
		MapFrame myMapFrame = new MapFrame(location, true);
        myMapFrame.setPreferredSize(new Dimension(1000, 1000));
        myMapFrame.pack();
        GeometryFactory gf = new GeometryFactory();
        
        ListLayer l0 = new ListLayer(Color.DARK_GRAY);
         
        int i = 1;
        
        List<ListLayer> polEdgeLayers = new ArrayList<>();
		
		for(GraphMLIO.ClusterContainer container : clusters)
		{
			List<Edge2D> enclosingEdges = new ArrayList<>();
			List<Vector2D> enclosingVertices = new ArrayList<>();
			
			Color c = colorMap.get(container.V.iterator().next());
			
			if(!translation.keySet().contains(c))
			{
				translation.put(c, colors[colorindex]);
				colorindex++;
			}
			
			c = translation.get(c);
			
			if(container.V.size() > 2)
			{
				
				PolygonContainer p = coveringPolygon(container, gf);
				
				Coordinate[] coords = new Coordinate[p.V.size()];
				//System.out.println();
				for(int j = 0; j < p.V.size(); j++)
				{
					coords[j] = new Coordinate(p.V.get(j).x, p.V.get(j).y);
					//if(!container.V.contains(p.V.get(j)))
						//System.out.println(coords[j]);
				}
				
				PolygonMapObject pmo = new PolygonMapObject(gf.createPolygon(coords));
				pmo.setFillColor(new Color(c.getRed(), c.getGreen(),c.getBlue(),122));
				//pmo.setStrokeWidth(10.f);
				
				l0.add(pmo);
				enclosingEdges.addAll(p.E);
				enclosingVertices.addAll(p.V);
			}
			else if(container.V.size() == 2)
			{
				enclosingEdges.addAll(container.E);
				enclosingVertices.addAll(container.V);
			}
			
			ListLayer l1 = new ListLayer(Color.DARK_GRAY);
			ListLayer l2 = new ListLayer(Color.DARK_GRAY);
			
			for(Edge2D e : enclosingEdges)
			{
				Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           
	    		
	    		Coordinate[] lineCoords = new Coordinate[2]; 
				lineCoords[0] = coords[0];
				lineCoords[1] = coords[1];
				
				ColoredLineMapObject clmo = new ColoredLineMapObject(gf.createLineString(lineCoords));
				
				if(enclosingEdges.contains(e))
				{
					clmo.setStrokeWidth(width);
					clmo.setColor(c);
					l2.add(clmo);
				}
				
				/*
				LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
				lmo.setStrokeWidth(4);
				lmo.setColor(Color.BLACK);
				l1.add(lmo);
				*/
				
			}
			
			for(Vector2D v : container.V)
			{
				if(enclosingVertices.contains(v))
				{
					KelpMapObject pmo = new KelpMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
					Color col = translation.get(colorMap.get(v));
					
		        	pmo.myColor = col;
		        	pmo.setStrokeWidth(width/2);
		        	l2.add(pmo);
				}
			}
			
			polEdgeLayers.add(l2);
			polEdgeLayers.add(l1);
		}
		
		myMapFrame.getMap().addLayer(l0,0);
		for(ListLayer l : polEdgeLayers)
		{
			myMapFrame.getMap().addLayer(l, i);
			i++;
		}
        
        ListLayer l1 = new ListLayer(Color.DARK_GRAY);
        ListLayer l2 = new ListLayer(Color.DARK_GRAY);
        ListLayer l3 = new ListLayer(Color.DARK_GRAY);
        for(GraphMLIO.ClusterContainer container : clusters)
		{
        	/*
        	for(Vector2D v : container.V)
			{
				
				boolean isolated = true;
				for(Edge2D e : container.E)
				{
					if(e.a.equals(v) || e.b.equals(v))
					{
						isolated = false;
						break;
					}
				}
				
				//if(!isolated) 
				{
					KelpMapObject pmo = new KelpMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
		        	pmo.myColor = translation.get(colorMap.get(v));
		        	pmo.setStrokeWidth(width/2);
		        	l.add(pmo);
				}
			}*/
        	for(Edge2D e : container.E)
			{
				Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           
	    		
	    		Coordinate[] lineCoords = new Coordinate[2]; 
				lineCoords[0] = coords[0];
				lineCoords[1] = coords[1];
				
				LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));

				lmo.setStrokeWidth(1);
				//lmo.setColor(translation.get(colorMap.get(e.a)));
				lmo.setColor(Color.BLACK);
				l1.add(lmo);
			}
			for(Vector2D v : container.V)
			{
				/*
				boolean isolated = true;
				for(Edge2D e : container.E)
				{
					if(e.a.equals(v) || e.b.equals(v))
					{
						isolated = false;
						break;
					}
				}
				
				//if(!isolated) 
				{
					KelpMapObject pmo = new KelpMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
		        	pmo.myColor = translation.get(colorMap.get(v));
		        	pmo.setStrokeWidth(width/2);
		        	l.add(pmo);
				}
				*/
				//else
				KelpMapObject kmo = new KelpMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
	        	kmo.myColor = translation.get(colorMap.get(v));
	        	kmo.setStrokeWidth(width/2);
	        	l3.add(kmo);
				{
					PointMapObject pmo = new PointMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
		        	pmo.myColor = translation.get(colorMap.get(v));
		        	pmo.setStrokeWidth(4);
		        	l2.add(pmo);
				}
			}
			
		}

		myMapFrame.getMap().addLayer(l3,i);
		myMapFrame.getMap().addLayer(l1,i+1);
		myMapFrame.getMap().addLayer(l2,i+2);
        
        
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
	
	public static PolygonContainer coveringPolygon(GraphMLIO.ClusterContainer container, GeometryFactory gf)
	{
		double right = Double.MIN_VALUE;
		Vector2D rv = null;
		
		Set<Vector2D> V = new HashSet<>(container.V);
		Set<Edge2D> E = new HashSet<>(container.E);
		
		Map<Edge2D, List<Edge2D>> crossings = CrossingsFinder.findCrossings(E);
		
		while(true)
		{
			Edge2D edge1 = null, edge2 = null;
			//get an arbitrary edge
			for(Edge2D e : crossings.keySet())
			{
				if(crossings.get(e).size() > 0)
				{
					edge1 = e;
					edge2 = crossings.get(e).get(0);
					break;
				}
			}
			
			//if there are no crossings left, exit
			if(edge1 == null)
				break;
			
			Vector2D v = Edge2D.intersectLines(edge1, edge2);
			E.remove(edge1);
			E.remove(edge2);
			
			E.add(new Edge2D(edge1.a, v));
			E.add(new Edge2D(edge1.b, v));
			E.add(new Edge2D(edge2.a, v));
			E.add(new Edge2D(edge2.b, v));
			
			V.add(v);
			
			crossings = CrossingsFinder.findCrossings(E);
		}
		
		for(Vector2D v : V)
		{
			if(v.x > right)
			{
				right = v.x;
				rv = v;
			}
		}
		
		double minAngle = Double.MAX_VALUE;
		Edge2D edge = null;
		
		for(Edge2D e : E)
		{
			if(!(e.a.equals(rv) || e.b.equals(rv)))
				continue;
			Vector2D a;
			if(e.a.equals(rv))
				a = e.b;
			else
				a = e.a;
			
			double x = Math.atan2(a.y - rv.y, a.x - rv.x);
			
			if(x<0)
				x = 2*Math.PI - Math.abs(x);
			
			if(x < minAngle)
			{
				minAngle = x;
				edge = e;
			}
		}
		
		Vector2D start = rv;
		Vector2D curr;
		if(edge.a.equals(rv))
			curr = edge.b;
		else
			curr = edge.a;
		
		Edge2D currentEdge = edge;
		Edge2D startingEdge = edge;
		
		List<Vector2D> vertices = new ArrayList<>();
		List<Edge2D> edges = new ArrayList<>();
		vertices.add(start);
		edges.add(currentEdge);
		
		while(!(curr.equals(start) && nextCounterClockwiseEdge(curr, currentEdge, E).equals(startingEdge)))
		{
			vertices.add(curr);
			currentEdge = nextCounterClockwiseEdge(curr, currentEdge, E);
			edges.add(currentEdge);
			if(currentEdge.a.equals(curr))
				curr = currentEdge.b;
			else
				curr = currentEdge.a;
		}
		
			
		vertices.add(start);
		
		return new PolygonContainer(vertices, edges);
	}
	
	public static Set<Edge2D> adjacentEdges(Vector2D v, Set<Edge2D> E)
	{
		Set<Edge2D> ret = new HashSet<>();
		
		for(Edge2D e : E)
		{
			if(e.a.equals(v) || e.b.equals(v))
				ret.add(e);
		}
		
		return ret;
	}
	
	public static Edge2D nextCounterClockwiseEdge(Vector2D v, Edge2D previous, Set<Edge2D> E)
	{
		Edge2D curr = previous;
		double angle = 2* Math.PI;
		Set<Edge2D> temp = new HashSet<>(E);
		
		temp.remove(previous);
		
		for(Edge2D e : temp)
		{
			if(!(e.a.equals(v) || e.b.equals(v)))
				continue;
			Vector2D a, b;
			if(previous.a.equals(v))
			{
				a = previous.b;
			}
			else
			{
				a = previous.a;
			}
			if(e.a.equals(v))
			{
				b = e.b;
			}
			else
			{
				b = e.a;
			}
			double x = Math.atan2(b.y - v.y, b.x - v.x);
			double y = Math.atan2(a.y - v.y, a.x - v.x);
			
			
			double ca = x - y;
			
			if(ca<0)
				ca = 2*Math.PI - Math.abs(ca);
			
			if(ca < angle)
			{
				angle = ca;
				curr = e;
			}
		}
		
		return curr;
	}
	
	public static Vector2D nextCounterClockwiseVertex(Vector2D v, Edge2D previous, Set<Edge2D> E)
	{
		Edge2D temp = nextCounterClockwiseEdge(v, previous, E);
		if(temp.a.equals(v))
			return temp.b;
		else
			return temp.a;
	}
	
	public static void displayKelpGraph(String location) throws IOException
	{

       	double distance = 10.0;
		
		GraphMLIO.ComponentsWrapper wrapper = GraphMLIO.readConnectedComponents(location);
		
		List<GraphMLIO.ClusterContainer> clusters = wrapper.clusters;
		Map<Vector2D, Color> colorMap = wrapper.colorMap;
		
		MapFrame myMapFrame = new MapFrame(location, true);
        myMapFrame.setPreferredSize(new Dimension(1000, 1000));
        myMapFrame.pack();
		GeometryFactory gf = new GeometryFactory();

		
		ListLayer l = new ListLayer(Color.DARK_GRAY);
		for(GraphMLIO.ClusterContainer container : clusters)
		{
			for(Edge2D e : container.E)
			{
				Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           	
	           	Geometry geom = gf.createLineString(coords);
				Polygon p = (Polygon) geom.buffer(distance);
				
				PolygonMapObject pmo = new PolygonMapObject(p);
				l.add(pmo);
			}
		}
		
		myMapFrame.getMap().addLayer(l,0);
        
        
        myMapFrame.setVisible(true);
		
	}
	
	static class PolygonContainer
	{
		List<Vector2D> V;
		List<Edge2D> E;
		
		public PolygonContainer(List<Vector2D> V, List<Edge2D> E)
		{
			this.V = V;
			this.E = E;
		}
	}
	
}
