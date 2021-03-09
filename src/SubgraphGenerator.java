import java.awt.Color;
import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.io.ExportException;

import com.vividsolutions.jts.geom.Point;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.MapObject;
import mapViewer.PointMapObject;

public class SubgraphGenerator {

	public static void main (String[] args)
	{
		
		MapFrame myMapFrame = new MapFrame("Greedy Clustering", true);
        myMapFrame.setPreferredSize(new Dimension(1000, 600));
        myMapFrame.pack();
        
        //Define which classes of POIs to read and how to display them
        HashMap<String, Color> colorMap = new HashMap<String, Color>();
        colorMap.put("restaurant", Color.RED);
        colorMap.put("clothes", Color.PINK);
        colorMap.put("fast_food", Color.GREEN);
        colorMap.put("shoe_shop", Color.BLUE);
        colorMap.put("jeweller", Color.ORANGE);
        colorMap.put("hairdresser", Color.CYAN);
      
        String datei = "pois";
        
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
        
        Set<Vector2D> V = new HashSet<>(pointList);
        
        //calculate dimensions of "pois"
        double minX, minY = Double.MAX_VALUE;
        double maxX, maxY = Double.MIN_VALUE;
        minX = minY;
        maxX = maxY;
        
        for(Vector2D vec : V)
        {
        	if(vec.x > maxX)
        		maxX = vec.x;
        	if(vec.x < minX)
        		minX = vec.x;
        	if(vec.y > maxY)
        		maxY = vec.y;
        	if(vec.y < minY)
        		minY = vec.y;
        }
        
        double xDim = maxX - minX;
        double yDim = maxY - minY;
        
        //these values define a 3x3 grid
        double gridWidth = xDim/3;
        double gridHeight = yDim/3;
        
        //iterate over the 9 grid cells
        for(int i = 0; i < 9; i ++)
		{
        	//iterate over the sizes we want to achieve
			for(int count = 50; count <= 250; count = count + 50)
			{
				
				
				//generate the graph instance
				List<Vector2D> instance = new LinkedList<>(V);
				
				int col = i%3;
				int row = i/3;
				
				Vector2D referencePoint = new Vector2D(minX + gridWidth/2 + gridWidth + gridWidth*col,
						minY + gridHeight/2 + gridHeight*row);
				
				instance.sort(new SubgraphGenerator.ClosestComparator(referencePoint));
				
				String destination = "SubInstancesPOIS\\Field"+i+"count"+count+".graphml";
				
				
				try {
					FileOutputStream os = new FileOutputStream(destination);
					
					GraphMLIO.writeGraphToGraphML(new HashSet<>(instance.subList(0, count)), new HashSet<Edge2D>(), colors, os);
					os.close();
				} catch (IOException | ExportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
        System.exit(1);
	}
	
	static class ClosestComparator implements Comparator<Vector2D>
	{
		private Vector2D referencePoint;
		
		ClosestComparator(Vector2D referencePoint)
		{
			this.referencePoint = referencePoint;
		}

		@Override
		public int compare(Vector2D arg0, Vector2D arg1) {
			// TODO Auto-generated method stub
			return Double.compare(arg0.dist(referencePoint), arg1.dist(referencePoint));
		}
		
	}
	
}
