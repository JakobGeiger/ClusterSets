package backgroundmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.io.File;
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
import io.shp.FeatureReader;
import io.structures.Feature;
import mapViewer.LineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.KelpMapObject;
import mapViewer.PolygonMapObject;
import viewer.symbols.BasicSymbolFactory;

import java.util.HashSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.vividsolutions.jts.awt.*;
import java.awt.Graphics2D;


public class Test {

	
	public static void main(String[] args)
	{
		try {
			//displayGraph("ILPSolution.graphml");
			displayGraph("SubInstancesPOIS\\Solutions\\Field3count50WithBeta0.5ILP.graphml");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	

	
	public static void displayGraph(String location) throws IOException
	{	
		GraphMLIO.ComponentsWrapper wrapper = GraphMLIO.readConnectedComponents(location);
		
		List<GraphMLIO.ClusterContainer> clusters = wrapper.clusters;
		Map<Vector2D, Color> colorMap = wrapper.colorMap;
		
		int width = 20;
		
		MapFrameWithBackground myMapFrame = new MapFrameWithBackground(location, true);
        myMapFrame.setPreferredSize(new Dimension(1000, 1000));
        myMapFrame.pack();
        GeometryFactory gf = new GeometryFactory();
        
        /*
         * Layers for background map here
         */
                
        viewer.base.ListLayer roadLayer = new viewer.base.ListLayer(new BasicSymbolFactory(Color.BLACK, Color.GRAY));
        List<Feature> list1 = FeatureReader.readFeaturesFromShapefile(new File("GISData\\bonn-buildings.shp"));
        for (Feature f : list1) roadLayer.add(f);   
        myMapFrame.getMap().addBackgroundLayer(roadLayer,1 );
        
        viewer.base.ListLayer buildingLayer = new viewer.base.ListLayer(new BasicSymbolFactory(Color.GRAY, Color.GRAY));
        List<Feature> list2 = FeatureReader.readFeaturesFromShapefile(new File("GISData\\bonn-roads.shp"));
        for (Feature f : list2) buildingLayer.add(f);
        myMapFrame.getMap().addBackgroundLayer(buildingLayer, 2);
        
        
        /*
         * end of background map
         */
        
        
        ListLayer l0 = new ListLayer(Color.DARK_GRAY);
         
        int i = 1;
		
		for(GraphMLIO.ClusterContainer container : clusters)
		{
			List<Edge2D> enclosingEdges = new ArrayList<>();
			List<Vector2D> enclosingVertices = new ArrayList<>();
			
			if(container.V.size() > 2)
			{
				Color c = colorMap.get(container.V.iterator().next());
				
				PolygonContainer p = coveringPolygon(container, gf);
				
				Coordinate[] coords = new Coordinate[p.V.size()];
				
				for(int j = 0; j < p.V.size(); j++)
				{
					coords[j] = new Coordinate(p.V.get(j).x, p.V.get(j).y);
				}
				
				PolygonMapObject pmo = new PolygonMapObject(gf.createPolygon(coords));
				pmo.setFillColor(new Color(c.getRed(), c.getGreen(),c.getBlue(),122));
				
				l0.add(pmo);
				enclosingEdges.addAll(p.E);
				enclosingVertices.addAll(p.V);
			}
			else
			{
				enclosingEdges.addAll(container.E);
				enclosingVertices.addAll(container.V);
			}
			
			ListLayer l1 = new ListLayer(Color.DARK_GRAY);
			ListLayer l2 = new ListLayer(Color.DARK_GRAY);
			for(Edge2D e : container.E)
			{
				Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           
	    		
	    		Coordinate[] lineCoords = new Coordinate[2]; 
				lineCoords[0] = coords[0];
				lineCoords[1] = coords[1];
				
				LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));
				
				if(enclosingEdges.contains(e))
				{
					lmo.setStrokeWidth(width);
					lmo.setColor(colorMap.get(e.a));
					l2.add(lmo);
				}
				
				lmo = new LineMapObject(gf.createLineString(lineCoords));
				lmo.setStrokeWidth(2);
				lmo.setColor(Color.BLACK);
				l1.add(lmo);
			}
			for(Vector2D v : container.V)
			{
				if(enclosingVertices.contains(v))
				{
					KelpMapObject pmo = new KelpMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
		        	pmo.myColor = colorMap.get(v);
		        	pmo.setStrokeWidth(width);
		        	l2.add(pmo);
				}
			}
			myMapFrame.getMap().addLayer(l2, i);
			myMapFrame.getMap().addLayer(l1, i+1);
			i++;
			i++;
		}
		
		myMapFrame.getMap().addLayer(l0,0);

        
        for(GraphMLIO.ClusterContainer container : clusters)
		{
        	
		}
        
        ListLayer l = new ListLayer(Color.DARK_GRAY);
        for(GraphMLIO.ClusterContainer container : clusters)
		{
        	for(Edge2D e : container.E)
			{
				Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           
	    		
	    		Coordinate[] lineCoords = new Coordinate[2]; 
				lineCoords[0] = coords[0];
				lineCoords[1] = coords[1];
				
				LineMapObject lmo = new LineMapObject(gf.createLineString(lineCoords));

				lmo.setStrokeWidth(2);
				lmo.setColor(Color.BLACK);
				l.add(lmo);
			}
			for(Vector2D v : container.V)
			{
				KelpMapObject pmo = new KelpMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
	        	pmo.myColor = colorMap.get(v);
	        	pmo.setStrokeWidth(4);
	        	l.add(pmo);
			}
			
		}
		myMapFrame.getMap().addLayer(l,i);
		
		
        
        
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
		
		for(Vector2D v : container.V)
		{
			if(v.x > right)
			{
				right = v.x;
				rv = v;
			}
		}
		
		double minAngle = Double.MAX_VALUE;
		Edge2D edge = null;
		
		for(Edge2D e : container.E)
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
		
		List<Vector2D> vertices = new ArrayList<>();
		List<Edge2D> edges = new ArrayList<>();
		vertices.add(start);
		edges.add(currentEdge);
		
		while(!curr.equals(start))
		{
			vertices.add(curr);
			currentEdge = nextCounterClockwiseEdge(curr, currentEdge, container.E);
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

