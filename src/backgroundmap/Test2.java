package backgroundmap;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import io.shp.FeatureReader;
import io.structures.Feature;
import viewer.symbols.BasicSymbolFactory;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class Test2 {

	
	public static void main(String[] args)
	{
		try {
			//displayGraph("ILPSolution.graphml");
			//displayGraph("SubInstancesPOIS\\Solutions\\Field3count50WithBeta0.5ILP.graphml");
			displayGraph("GISData\\Solutions\\standorte_uni_bonn_utm0.5Greedy.graphml");
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
		
		viewer.base.MapPanel myMapPanel = new viewer.base.MapPanel();
				
		JFrame myMapFrame = new JFrame();
		
		myMapFrame.add(myMapPanel);
        myMapFrame.setPreferredSize(new Dimension(1000, 1000));
        myMapFrame.setVisible(true);
        myMapFrame.pack();
        
        
        
        
        GeometryFactory gf = new GeometryFactory();
                
        /*
         * Layers for background map here
         */

        
        Color transp_gray1 = new Color(0,0,0);
        Color transp_gray2 = new Color(0,0,0);
        
        viewer.base.ListLayer roadLayer = new viewer.base.ListLayer(new BasicSymbolFactory(transp_gray2, transp_gray1,1));
        List<Feature> list1 = FeatureReader.readFeaturesFromShapefile(new File("GISData\\bonn-roads.shp"));
        for (Feature f : list1) roadLayer.add(f);   
        myMapPanel.getMap().addLayer(roadLayer,0);
        
        viewer.base.ListLayer buildingLayer = new viewer.base.ListLayer(new BasicSymbolFactory(transp_gray2, transp_gray1));
        List<Feature> list2 = FeatureReader.readFeaturesFromShapefile(new File("GISData\\bonn-buildings.shp"));
        for (Feature f : list2) buildingLayer.add(f);
        myMapPanel.getMap().addLayer(buildingLayer, 1);
                
        /*
         * end of background map
         */
         
        viewer.base.ListLayer linesLayer = new viewer.base.ListLayer(new BasicSymbolFactory(Color.BLACK, Color.BLACK, 3));
        viewer.base.ListLayer pointsLayer = new viewer.base.ListLayer(new CategorizedSymbolFactory());
        viewer.base.ListLayer polygonsLayer = new viewer.base.ListLayer(new CategorizedSymbolFactory());
        Map<String, Color> colorsByCoordinate = new HashMap<String, Color>();
                
        Set<Coordinate> sites = new HashSet<Coordinate>();
        
        double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
        
        
        for(GraphMLIO.ClusterContainer container : clusters)
		{
        	
        	
        	
        	//iterate all points to create point symbols
        	for(Vector2D v : container.V)
			{	
        		 
        		org.locationtech.jts.geom.Coordinate c = new org.locationtech.jts.geom.Coordinate(v.x, v.y);
        		
        		
        		HashMap<String, Object> attributes = new HashMap<String, Object>();
        		HashMap<String, Object> attributes2 = new HashMap<String, Object>();
    			
        		Color myColor = colorMap.get(v);
        		attributes.put("fillColor", myColor);
    			attributes.put("width", 7);
    			attributes2.put("fillColor", Color.BLACK);
    			attributes2.put("width", 13);
    			Feature f = new Feature(gf.createPoint(c), attributes);
    			Feature f2 = new Feature(gf.createPoint(c), attributes2);
       
    			sites.add(c);
    			colorsByCoordinate.put(c.x + " " + c.y, myColor);
    			
    			pointsLayer.add(f2);
        		pointsLayer.add(f);
        		
				
				xMin = Math.min(xMin, v.x);
				yMin = Math.min(yMin, v.y);
				xMax = Math.max(xMax, v.x);
				yMax = Math.max(yMax, v.y);
				
			}
        	
        	
        	//iterate all edges to create voronoi diagram
        	for(Edge2D e : container.E)
			{
        		Color myColor = colorMap.get(e.a);
				
        		Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           	           	
	           	
				LineString lstring = gf.createLineString(coords);
				Feature f = new Feature(lstring);
				linesLayer.add(f);
				
				
				/*
				 * refinement to approximate line voronoi diagram
				 * uncomment to generate standard voronoi diagram 
				 */
				//lstring = refineLineString(lstring, 10.0);
				
				//add line vertices as sites of voronoi diagram
				for (Coordinate c : lstring.getCoordinates()) {
					sites.add(c);
					
					//memorize colors for sites
					colorsByCoordinate.put(c.x + " " + c.y, myColor);
				} 
			}
		}
        
        //compute voronoi diagram
		VoronoiDiagramBuilder vdb = new VoronoiDiagramBuilder();
		vdb.setSites(sites);
		GeometryCollection geos = (GeometryCollection) vdb.getDiagram(gf);
		
		//iterate cells of voronoi diagram
		for (int i = 0; i < geos.getNumGeometries(); i++) {
			Polygon p = (Polygon) geos.getGeometryN(i);
			
			//fetch center of voronoi cell
			Coordinate center = (Coordinate) p.getUserData();
			
			//add voronoi cell to map using color memorized for site

			Color myColor = colorsByCoordinate.get(center.x + " " + center.y);
			myColor = new Color(myColor.getRed(), myColor.getGreen(), myColor.getBlue(),220);
				
			HashMap<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("fillColor", myColor);
			attributes.put("strokeColor", null);
			attributes.put("width", 0.0);
			polygonsLayer.add(new Feature(p, attributes));
			
		}
		
		
		myMapPanel.getMap().addLayer(polygonsLayer,3);
		//myMapPanel.getMap().addLayer(linesLayer,4);
		myMapPanel.getMap().addLayer(pointsLayer,6);
		//myMapPanel.getMap().fitBoxToDisplay(xMin, yMin,xMax,  yMax);
		myMapPanel.getMap().fitBoxToDisplay(363746,5620840,366470,5623282);
		
		myMapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myMapFrame.setVisible(true);
        
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        myMapPanel.getMap().paint(svgGenerator);

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
	


	public static LineString refineLineString(LineString ls, double maximalDistance){
	    // list to store coordinates for resulting line string
	    ArrayList<Coordinate> resultLineStringCoordinates = new ArrayList();
	    // list of LineSegments from input LineString
	    ArrayList<LineSegment> segmentList = new ArrayList();
	    for(int i = 1; i < ls.getCoordinates().length; i++){
	        // create LineSegments from input LineString and add them to list
	        segmentList.add(new LineSegment(ls.getCoordinates()[i-1], ls.getCoordinates()[i]));
	    }
	    boolean isFirstSegment = true;
	    // refine each LineSegment if necessary
	    for(LineSegment currentSegment : segmentList){
	        // add startpoint from first segment only
	        if(isFirstSegment){
	            resultLineStringCoordinates.add(new Coordinate(currentSegment.p0));
	            isFirstSegment = false;
	        }
	        // refinement necessary
	        if(currentSegment.getLength() > maximalDistance){
	            // calculate distance between coordinates as fraction (0-1)
	            double distanceFraction = 1 / (currentSegment.getLength()/maximalDistance);
	            for(double currentFraction = distanceFraction; currentFraction < 1; currentFraction += distanceFraction){
	                // add coordinates from calculated fraction
	                resultLineStringCoordinates.add(new Coordinate(currentSegment.pointAlong(currentFraction)));
	            }
	        }
	        // add segment endpoint coordinate to result list
	        resultLineStringCoordinates.add(new Coordinate(currentSegment.p1));
	    }
	    return new GeometryFactory().createLineString(resultLineStringCoordinates.toArray(new Coordinate[resultLineStringCoordinates.size()]));
	}
	
}

