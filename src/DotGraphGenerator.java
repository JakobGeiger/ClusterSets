import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import GraphML.GraphMLIO;
import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;

public class DotGraphGenerator {

	
	public static void main(String[] args)
	{
		try {
			//generateDotGraph("SubinstancesPOIS\\Field3count50.graphml");
			generateDotGraph("IpeData\\manhattan-subway+hotels+clinics.graphml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateDotGraph(String location) throws IOException
	{
		GraphMLIO.ComponentsWrapper cw = GraphMLIO.readConnectedComponents(location);
		
		FileOutputStream os = new FileOutputStream("dotgraph.txt");
		
		String output = "";
		
		output += "graph {\n";
		
		int id = 0;
		int cl = 1;
		
		Map<Vector2D, Integer> map = new HashMap<>();
		
		Map<Color, Integer> cmap = new HashMap<>();
		
		for(Color c : cw.colorMap.values())
		{
			if(!cmap.containsKey(c))
			{
				cmap.put(c, cl);
				cl++;
			}
		}
		
		double maxX = Double.MIN_VALUE;
    	double maxY = Double.MIN_VALUE;
    	double minX = Double.MAX_VALUE;
    	double minY = Double.MAX_VALUE;
    	
    	for(GraphMLIO.ClusterContainer container : cw.clusters)
    	{
	    	for(Vector2D v : container.V)
	    	{
	    		if(v.x > maxX)
	    			maxX = v.x;
	    		if(v.x < minX)
	    			minX = v.x;
	    		if(v.y > maxY)
	    			maxY = v.y;
	    		if(v.y < minY)
	    			minY = v.y;
	    	}
    	}
    	
    	
    	double ratio = 32.5/20.5;
    	double width = 1000.;
    	double height = ratio*width;
		
		for(GraphMLIO.ClusterContainer container : cw.clusters)
		{
			for(Vector2D v : container.V)
			{
				map.put(v, id);
				output += "\"" + id + "\" [cluster=\"" + cmap.get(cw.colorMap.get(v)) + "\", fontsize=\"8\", height=\"0.23611\", pos=\"" + width*(v.x-minX)/(maxX-minX) + "," + height*(v.y-minY)/(maxY-minY) + "\", width=\"0.98611\", label="+ id +"];\n";
				id++;
			}
			cl++;
		}
		
		for(GraphMLIO.ClusterContainer container : cw.clusters)
		{
			for(Edge2D e : container.E)
			{
				output += "\"" + map.get(e.a) + "\" -- \"" + map.get(e.b) + "\" [pos=\"" + width*(e.a.x-minX)/(maxX-minX) + "," + height*(e.a.y-minY)/(maxY-minY) + " " + width*(e.b.x-minX)/(maxX-minX) + "," + height*(e.b.y-minY)/(maxY-minY) +"\"];\n";
			}
		}
		
		
		output += "}";
		os.write(output.getBytes());
		os.close();
	}
}
