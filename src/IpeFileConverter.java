
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.io.ExportException;

import GraphML.GraphMLIO;

import java.awt.Color;

import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;

public class IpeFileConverter {

    public static void main(String args[])   
    {  
	    try   
	    {  
		    convertFile("seattle-restaurants");   
	    }   
	    catch (Exception e)   
	    {  
	    	e.printStackTrace();  
	    }  
    } 
    
    public static void convertFile(String location) throws IOException, ExportException
    {
    	Set<Vector2D> V = new HashSet<>();
    	Set<Edge2D> E = new HashSet<>();
    	Map<Vector2D, Color> colorMap = new HashMap<>();
    	Scanner sc = new Scanner(new FileInputStream(location + ".ipe"));
    	while(sc.hasNext())
    	{
    		String line = sc.nextLine();
    		if(!line.startsWith("<use name"))
    		{
    			continue;
    		}
    		String[] parts = line.split("\"");
    		String[] coordinates = parts[3].split(" ");
    		double x = Double.parseDouble(coordinates[0]);
    		double y = Double.parseDouble(coordinates[1]);
    		
    		Vector2D v = new Vector2D(x,y);
    		V.add(v);
    		
    		if(parts[1].contains("fdisk"))
    			colorMap.put(v, Color.RED);
    		else if(parts[1].contains("fsquare"))
    			colorMap.put(v, Color.BLUE);
    		else
    			colorMap.put(v, Color.GREEN);
    	}
    	GraphMLIO.writeGraphToGraphML(new GraphMLIO.GraphWrapper(V, E, colorMap), location + ".graphml");
    	
    	sc.close();
    }
}
