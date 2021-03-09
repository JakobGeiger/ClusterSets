import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import GraphML.GraphMLIO;
import delaunayTriangulation.Vector2D;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.PointMapObject;
import mapViewer.PolygonMapObject;

//code for this taken and adapted from https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
public class ConvexHullCalculator {

	public static int orientation(Vector2D p, Vector2D q, Vector2D r)
	{
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y); 
     
     	if (val == 0) return 0;  // collinear 
     	return (val > 0)? 1: 2; // clock or counterclock wise 
	}
	
	public static List<Vector2D> convexHull(Vector2D points[]) throws IllegalArgumentException
    { 
		
		int n = points.length;
		
        // There must be at least 3 points 
        if (n < 3) throw new IllegalArgumentException("There must be at least 3 points!"); 
       
        // Initialize Result 
        List<Vector2D> hull = new ArrayList<Vector2D>(); 
       
        // Find the leftmost point 
        int l = 0; 
        for (int i = 1; i < n; i++) 
            if (points[i].x < points[l].x) 
                l = i; 
       
        // Start from leftmost point, keep moving  
        // counterclockwise until reach the start point 
        // again. This loop runs O(h) times where h is 
        // number of points in result or output. 
        int p = l, q; 
        do
        { 
            // Add current point to result 
            hull.add(points[p]); 
       
            // Search for a point 'q' such that  
            // orientation(p, x, q) is counterclockwise  
            // for all points 'x'. The idea is to keep  
            // track of last visited most counterclock- 
            // wise point in q. If any point 'i' is more  
            // counterclock-wise than q, then update q. 
            q = (p + 1) % n; 
              
            for (int i = 0; i < n; i++) 
            { 
               // If i is more counterclockwise than  
               // current q, then update q 
               if (orientation(points[p], points[i], points[q]) 
                                                   == 2) 
                   q = i; 
            } 
       
            // Now q is the most counterclockwise with 
            // respect to p. Set p as q for next iteration,  
            // so that q is added to result 'hull' 
            p = q; 
       
        } while (p != l);  // While we don't come to first  
                           // point 
       
        // Print Result 
        return hull;
    } 
	
	public static int howManyPoints(Set<Vector2D> cluster, Set<Vector2D> points)
	{
		GeometryFactory gf = new GeometryFactory();
		
		Point[] p = new Point[cluster.size()];
		
		int i = 0;
		
		for(Vector2D temp : cluster)
		{
			p[i] = gf.createPoint(new Coordinate(temp.x, temp.y));
			i++;
		}
		
		MultiPoint mp = gf.createMultiPoint(p);
		
		Geometry hull = mp.convexHull();
		
		int count = 0;
		
//		ListLayer l2 = new ListLayer(Color.RED);
//		ListLayer l3 = new ListLayer(Color.GREEN);
		
		for(Vector2D temp : points)
		{
			if(hull.contains(gf.createPoint(new Coordinate(temp.x, temp.y))))
			{
				count++;
//				l2.add(new PointMapObject(gf.createPoint(new Coordinate(temp.x, temp.y))));
			}
			else
			{
//				l3.add(new PointMapObject(gf.createPoint(new Coordinate(temp.x, temp.y))));
			}
		}
		
//		MapFrame myMapFrame = new MapFrame("Greedy Clustering", true);
//        myMapFrame.setPreferredSize(new Dimension(1000, 600));
//        myMapFrame.pack();
//        
//        ListLayer l0 = new ListLayer(Color.LIGHT_GRAY);
//        
//        Polygon poly = (Polygon) hull;
//        
//        l0.add(new PolygonMapObject(poly));
//        
//        ListLayer l1 = new ListLayer(Color.DARK_GRAY);
//        
//        for(Point temp : p)
//        {
//        	l1.add(new PointMapObject(temp));
//        }
//        
//        
//        
//        myMapFrame.getMap().addLayer(l0, 1);
//        myMapFrame.getMap().addLayer(l1, 2);
//        myMapFrame.getMap().addLayer(l2, 3);
//        myMapFrame.getMap().addLayer(l3, 4);
//        
//        myMapFrame.setVisible(true);
		
		return count;
	}
	
	public static void main(String[] args)
	{
		Set<Vector2D> testCluster = new HashSet<>();
//		
//		testCluster.add(new Vector2D(0.,0.));
//		testCluster.add(new Vector2D(1.,0.));
//		testCluster.add(new Vector2D(0.,1.));
//		testCluster.add(new Vector2D(1.,1.));
//		
		Set<Vector2D> testPoints = new HashSet<>();
//		
//		testPoints.add(new Vector2D(1.5, 1.5));
		
		try {
			GraphMLIO.GraphWrapper g = GraphMLIO.readGraph("C:\\Users\\Jakob Geiger\\Documents\\geometric-clusters\\code\\SubInstancesPOIS\\Field4count100.graphml");
			
			Random r = new Random();
			for(Vector2D v : g.V)
			{
				boolean b = r.nextBoolean();
				if(b)
					testCluster.add(v);
				else
					testPoints.add(v);
			}
			
			System.out.println(howManyPoints(testCluster, testPoints));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.exit(0);
		
	}
	
	
}
