import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;
import mapViewer.ColoredLineMapObject;
import mapViewer.ListLayer;
import mapViewer.MapFrame;
import mapViewer.PointMapObject;
import mapViewer.ColorfulStringMapObject;

public class SolutionVisualizer {
	
	public static void main(String[] args)
	{
		try {
			boolean[] sel = {false,false,true,true};
			showComparison("SubInstancesPOIS\\Solutions\\Field1count50WithBeta0.7", sel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void showComparison(String file, boolean[] selection) throws IOException
	{
		String[] algos = {"ReverseGreedy", "Reverse1PlaneGreedy","Greedy",  "ILP"};
		
		MapFrame myMapFrame = new MapFrame(file, true);
        myMapFrame.setPreferredSize(new Dimension(1000, 1000));
        myMapFrame.pack();
        GeometryFactory gf = new GeometryFactory();
        
        ListLayer l1 = new ListLayer(Color.DARK_GRAY);
        ListLayer l2 = new ListLayer(Color.DARK_GRAY);
        ListLayer l3 = new ListLayer(Color.DARK_GRAY);
        
        HashMap<Edge2D, Color> edgeColors = new HashMap<>();
        int[] algoColors = new int[4];
        
        for(int i = 0; i < 4; i++)
        {
        	algoColors[i] = 0b111111 << (6*i);
        }
        
        GraphMLIO.GraphWrapper wrap = GraphMLIO.readGraph(file + algos[0] + ".graphml");
		
		for(int i = 0; i < 4; i++)
		{
			if(!selection[i])
				continue;
			
			GraphMLIO.GraphWrapper curr = GraphMLIO.readGraph(file + algos[i] + ".graphml");
			final int temp = i;
			
			for(Edge2D edge : curr.E)
			{
				edgeColors.compute(edge, (k,v) -> (v == null) ? new Color(16777215 - algoColors[temp]) : new Color(v.getRGB() - algoColors[temp]));
			}
		}
		
		for(Edge2D e : edgeColors.keySet())
		{
			Coordinate[] coords = new Coordinate[2];
           	coords[0] = new Coordinate(e.a.x, e.a.y);
           	coords[1] = new Coordinate(e.b.x, e.b.y);
           
    		
    		Coordinate[] lineCoords = new Coordinate[2]; 
			lineCoords[0] = coords[0];
			lineCoords[1] = coords[1];
			
			ColoredLineMapObject lmo = new ColoredLineMapObject(gf.createLineString(lineCoords));
			lmo.myColor = edgeColors.get(e);

			lmo.setStrokeWidth(2);
			l1.add(lmo);
		}
		
		double xMax = Double.MIN_VALUE;
		double yMax = Double.MIN_VALUE;
		
		for(Vector2D v : wrap.V)
        {
			if(v.x > xMax)
				xMax = v.x;
			if(v.y > yMax)
				yMax = v.y;
        	PointMapObject pmo = new PointMapObject(gf.createPoint(new Coordinate(v.x, v.y)));
        	pmo.myColor = wrap.colorMap.get(v);
        	l2.add(pmo);
        }
		
		myMapFrame.getMap().addLayer(l1, 0);
		myMapFrame.getMap().addLayer(l2, 1);
		
		int num = 0;
		for(boolean b :selection)
		{
			if(b)
				num ++;
		}
		
		Color[] col = new Color[num];
		
		String legend = "";
		ColorfulStringMapObject cosmo = new ColorfulStringMapObject(legend, gf.createPoint(new Coordinate(xMax + 2000, yMax + 2000)), 15);
		int j = 0;
		for(int i = 0; i < 4; i++)
		{
			if(!selection[i])
				continue;
			
			legend += algos[i] + "\n";
			col[j] = new Color(16777215 - algoColors[i]);
			j++;
		}
		cosmo.myColors = col;
		cosmo.content = legend;
		
		l3.add(cosmo);
		myMapFrame.getMap().addLayer(l3, 2);

        myMapFrame.setVisible(true);
	}
}
