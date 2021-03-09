import java.awt.Color;
import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.jgrapht.io.ExportException;

import com.vividsolutions.jts.geom.Point;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.MapObject;
import mapViewer.PointMapObject;

public class RandomFileGenerator {
	
	
	
	public static void main(String[] args)
	{
		
		RandomFileFactory rff = new RandomFileFactory();
		/*
		for(int i = 50; i<= 500; i = i + 50)
		{
			for(int j = 2; j<=6; j++)
				rff.generateRandomFile("RandomData\\"+ i + "nodes\\" + i + "nodes" + j + "colors.shp", i, 50000, 50000, j);
		}
		*/
		
		int i = 20;
		int j = 1;
		
		rff.generateRandomFile("RandomData\\"+ i + "nodes\\" + i + "nodes" + j + "colors.shp", i, 50000, 50000, j);
		
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
        
        double[] betas = new double[9];
        
        betas[0] = 0.5;
		betas[1] = 0.55;
		betas[2] = 0.6;
		betas[3] = 0.65;
		betas[4] = 0.7;
		betas[5] = 0.75;
		betas[6] = 0.8;
		betas[7] = 0.85;
		betas[8] = 0.9;
        
        for(double beta : betas)
        {
        	try {
        		generateBetaFile(datei, beta);
        	}
        	catch(IOException | ExportException e) {e.printStackTrace();}
        }
		
	}
	
	public static void generateBetaFile(String location, double beta) throws IOException, ExportException
	{
		GraphMLIO.GraphWrapper gw = GraphMLIO.readGraph(location + ".graphml");
		
		BetaSkeletonizer bs = new BetaSkeletonizer(gw.V, beta);
		bs.skeletonize();
		
		gw.E = new HashSet<>(bs.E);
		GraphMLIO.writeGraphToGraphML(gw, location + "beta" + beta + ".graphml");
	}
	
}
