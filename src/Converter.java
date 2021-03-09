

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import com.vividsolutions.jts.geom.Point;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.MapObject;
import mapViewer.PointMapObject;

public class Converter {
	public static void main (String[] args)
	{
		try
		{
			convertBirdData();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void convertBirdData() throws IOException
	{
		FileInputStream is = new FileInputStream("birds20gerNorth/DistinctBirdnames.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		//hopefully distinct colors taken from https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
		int[] kelly_colors_hex = {
		                    0xFFB300, // Vivid Yellow
		                    0x803E75, // Strong Purple
		                    0xFF6800, // Vivid Orange
		                    0xA6BDD7, // Very Light Blue
		                    0xC10020, // Vivid Red
		                    0xCEA262, // Grayish Yellow
		                    0x817066, // Medium Gray

		                    // The following don't work well for people with defective color vision
		                    0x007D34, // Vivid Green
		                    0xF6768E, // Strong Purplish Pink
		                    0x00538A, // Strong Blue
		                    0xFF7A5C, // Strong Yellowish Pink
		                    0x53377A, // Strong Violet
		                    0xFF8E00, // Vivid Orange Yellow
		                    0xB32851, // Strong Purplish Red
		                    0xF4C800, // Vivid Greenish Yellow
		                    0x7F180D, // Strong Reddish Brown
		                    0x93AA00, // Vivid Yellowish Green
		                    0x593315, // Deep Yellowish Brown
		                    0xF13A13, // Vivid Reddish Orange
		                    0x232C16, // Dark Olive Green
		};
		
		HashMap<String,Color> colorMap = new HashMap<>();
		
		for(int i = 0; i < 20; i++)
		{
			colorMap.put(reader.readLine(), new Color(kelly_colors_hex[i]));
		}
		
		MapFrame myMapFrame = new MapFrame("Greedy Clustering", true);
        myMapFrame.setPreferredSize(new Dimension(1000, 600));
        myMapFrame.pack();
        //GeometryFactory gf = new GeometryFactory();
        
        //Define which classes of POIs to read and how to display them

        
        //Create a new layer with the specified file of POIs            
        ListLayer l1 = ListLayer.readFromShapefile("birds20gerNorth/birds20gerNorth.shp", Color.DARK_GRAY, "common_nam", colorMap);
        
        System.out.println(l1.getMyObjects().size());
        
        myMapFrame.getMap().addLayer(l1, 1);
        myMapFrame.setVisible(true);
        
        Map<Vector2D, Color> colors = new HashMap<Vector2D, Color>();
        
        LinkedList<Vector2D> pointList = new LinkedList<Vector2D>();
        for (MapObject myMapObject : l1.getMyObjects()) {
          if (myMapObject instanceof PointMapObject) {
            PointMapObject pmo = (PointMapObject) myMapObject;
            Point p = pmo.getMyPoint();
            Vector2DWithInfo<PointMapObject> v2d = new Vector2DWithInfo<PointMapObject>(p.getX(), p.getY(), pmo);
            pointList.add(v2d);
            colors.put(v2d, pmo.myColor);
          }
        }
        
        
        try {
        	FileOutputStream os = new FileOutputStream("birds20gerNorth/birds20gerNorth.graphml");
        	GraphMLIO.writeGraphToGraphML(new HashSet<Vector2D>(pointList), new HashSet<Edge2D>(), colors, os);
        	os.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		
	}

	public static void convertRandomInstances()
	{
		for(int i = 50; i<=500; i = i + 50)
		{
			for(int j = 2; j<=6; j++)
			{
		        MapFrame myMapFrame = new MapFrame("Greedy Clustering", true);
		        myMapFrame.setPreferredSize(new Dimension(1000, 600));
		        myMapFrame.pack();
		        //GeometryFactory gf = new GeometryFactory();
		        
		        //Define which classes of POIs to read and how to display them
		        HashMap<String, Color> colorMap = new HashMap<String, Color>();
		        colorMap.put("restaurant", Color.RED);
		        colorMap.put("clothes", Color.PINK);
		        colorMap.put("fast_food", Color.GREEN);
		        colorMap.put("shoe_shop", Color.BLUE);
		        colorMap.put("jeweller", Color.ORANGE);
		        colorMap.put("hairdresser", Color.CYAN);
		      
		        //String datei = "pois";
		        String datei = "RandomData\\"+i+"nodes\\"+i+"nodes"+j+"colors";
		        
		        //Create a new layer with the specified file of POIs            
		        ListLayer l1 = ListLayer.readFromShapefile(datei+".shp", Color.DARK_GRAY, "fclass", colorMap);
		        Map<Vector2D, Color> colors = new HashMap<Vector2D, Color>();
		               
		        //Prepare a list of points for the triangulation algorithm
		        LinkedList<Vector2D> pointList = new LinkedList<Vector2D>();
		        for (MapObject myMapObject : l1.getMyObjects()) {
		          if (myMapObject instanceof PointMapObject) {
		            PointMapObject pmo = (PointMapObject) myMapObject;
		            Point p = pmo.getMyPoint();
		            Vector2DWithInfo<PointMapObject> v2d = new Vector2DWithInfo<PointMapObject>(p.getX(), p.getY(), pmo);
		            pointList.add(v2d);
		            colors.put(v2d, pmo.myColor);
		          }
		        }
		        
		        
		        try {
		        	FileOutputStream os = new FileOutputStream(datei + ".graphml");
		        	GraphMLIO.writeGraphToGraphML(new HashSet<Vector2D>(pointList), new HashSet<Edge2D>(), colors, os);
		        	os.close();
		        }
		        catch (Exception e)
		        {
		        	e.printStackTrace();
		        }
			}
		}
        System.exit(1);
        
        
	}
	
	
}
