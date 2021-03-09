package testGIS;

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
import util.GraphKelper;
import util.GraphKelper.PolygonContainer;
import viewer.symbols.BasicSymbolFactory;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class TestBonn {

	//set to true to approximate line voronoi diagram with parameter delta
	private static boolean computeLineVoronoiDiagram = true;
	private static double delta = 10.0;
	
	//set to true to display cluster edges
	private static boolean displayClusterEdges = true;
	
	//box containing relevant context
	private static double x1 = 363746;
	private static double x2 = 366470;
	private static double y1 = 5620840;
	private static double y2 = 5623282;
	
	//center of box (if tranlateBox = false, center is computed from coordinates above)
	private static boolean tranlateBox = true;
	private static double xMid = 365145;
	private static double yMid = 5620480;
	
	//scale factor applied to box before fitting box to display
	private static double s = 2;
	
	
	public static void main(String[] args)
	{
		try {
			displayGraph("GISData\\Solutions\\standorte_uni_bonn_utm0.5Greedyfilled.graphml");
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
         
        viewer.base.ListLayer linesLayer = new viewer.base.ListLayer(new BasicSymbolFactory(Color.BLACK, Color.BLACK, (float) 1.5));
        viewer.base.ListLayer pointsLayer = new viewer.base.ListLayer(new CategorizedSymbolFactory());
        viewer.base.ListLayer voronoiCellsLayer = new viewer.base.ListLayer(new CategorizedSymbolFactory());
        viewer.base.ListLayer clusterPolygonsLayer = new viewer.base.ListLayer(new CategorizedSymbolFactory());
        Map<String, Color> colorsByCoordinate = new HashMap<String, Color>();
                
        Set<Coordinate> sites = new HashSet<Coordinate>();
        
        double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
        
		com.vividsolutions.jts.geom.GeometryFactory gf2 = new com.vividsolutions.jts.geom.GeometryFactory();
		
        for(GraphMLIO.ClusterContainer container : clusters)
		{
        	
        	//create polygonal representation
        	if(container.V.size() > 2)
			{
				GraphKelper.PolygonContainer p = GraphKelper.coveringPolygon(container, gf2);
				
				Coordinate[] coords = new Coordinate[p.V.size()];
				for(int j = 0; j < p.V.size(); j++)
				{
					coords[j] = new Coordinate(p.V.get(j).x, p.V.get(j).y);
				}
			
				HashMap<String, Object> attributes = new HashMap<String, Object>();
				
				Color myColor = colorMap.get(container.V.iterator().next());
				
				if (myColor.getRed() == 129) myColor = new Color(220,180,20);
				
				attributes.put("fillColor", myColor);
    			attributes.put("width", 0);
				Feature f = new Feature(gf.createPolygon(coords), attributes);
				clusterPolygonsLayer.add(f);
			}
        	
        	
        	//iterate all points to create point symbols
        	for(Vector2D v : container.V)
			{	
        		 
        		org.locationtech.jts.geom.Coordinate c = new org.locationtech.jts.geom.Coordinate(v.x, v.y);
        		
        		
        		HashMap<String, Object> attributes = new HashMap<String, Object>();
        		HashMap<String, Object> attributes2 = new HashMap<String, Object>();
    			
        		Color myColor = colorMap.get(v);
        		if (myColor.getRed() == 129) myColor = new Color(220,180,20);
        		
        		attributes.put("fillColor", myColor);
    			attributes.put("width", (int) (9 / s));
    			attributes2.put("fillColor", Color.BLACK);
    			attributes2.put("width", (int) (13 / s));
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
        		if (myColor.getRed() == 129) myColor = new Color(220,180,20);
        		
        		Coordinate[] coords = new Coordinate[2];
	           	coords[0] = new Coordinate(e.a.x, e.a.y);
	           	coords[1] = new Coordinate(e.b.x, e.b.y);
	           	           	
	           	
				LineString lstring = gf.createLineString(coords);
				Feature f = new Feature(lstring);
				linesLayer.add(f);
				
				if (computeLineVoronoiDiagram) {
					lstring = refineLineString(lstring, delta);	
				}				
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
			voronoiCellsLayer.add(new Feature(p, attributes));
			
		}
		
		
		myMapPanel.getMap().addLayer(voronoiCellsLayer,3);
		
		myMapPanel.getMap().addLayer(clusterPolygonsLayer,5);
		
		if (displayClusterEdges) {
			myMapPanel.getMap().addLayer(linesLayer,6);	
		}
		
		myMapPanel.getMap().addLayer(pointsLayer,7);
		
		if (!tranlateBox) {
			xMid = 0.5 * (x1 + x2);
			yMid = 0.5 * (y1 + y2);
		}
		double dx_half = 0.5 * (x2 - x1);
		double dy_half = 0.5 * (y2 - y1);
			
		//myMapFrame.pack();
		myMapPanel.getMap().fitBoxToDisplay(xMid - s * dx_half, yMid - s * dy_half, xMid + s * dx_half, yMid + s * dy_half);
		
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

