import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import delaunayTriangulation.Vector2D;

import java.util.HashMap;

import GraphML.GraphMLIO;
import setvis.*;
import setvis.bubbleset.*;
import setvis.gui.Canvas;
import setvis.gui.MainWindow;
import setvis.shape.*;

public class BubbleSetVisualizer {

	public static void main(String[] args)
	{
		// a list of groups of rectangles --
	    // bubble set will try to create an outline with
	    // as little overlap between the groups as possible
	    
	    // using bubble set outlines
	    SetOutline setOutline = new BubbleSet();
	    
	    final AbstractShapeGenerator shaper = new BSplineShapeGenerator(setOutline);
	    final MainWindow mw = new MainWindow(shaper);
	    final Canvas canvas = mw.getCanvas();
	    // a simple example item set
	    final double w = canvas.getCurrentItemWidth();
	    final double h = canvas.getCurrentItemHeight();
	    
	    try
	    {
	    	GraphMLIO.GraphWrapper gw = GraphMLIO.readGraph("SubInstancesPOIS\\Solutions\\Field3count50WithBeta0.5ILP.graphml");
	    	
	    	Map<Color, ArrayList<Vector2D>> map = new HashMap<>();
	    	
	    	double maxX = Double.MIN_VALUE;
	    	double maxY = Double.MIN_VALUE;
	    	double minX = Double.MAX_VALUE;
	    	double minY = Double.MAX_VALUE;
	    	
	    	for(Vector2D v : gw.V)
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
	    	
	    	
	    	for(Color c : gw.colorMap.values())
	    	{
	    		if(!map.containsKey(c))
	    			map.put(c, new ArrayList<>());
	    	}
	    	for(Vector2D v : gw.V)
	    	{
	    		map.get(gw.colorMap.get(v)).add(v);
	    	}
	    	int i = 0;
	    	for(ArrayList<Vector2D> al : map.values())
	    	{
	    		canvas.addGroup();
	    		for(Vector2D v : al)
	    		{
	    			canvas.addItem(i, mw.getWidth()*(v.x-minX)/(maxX-minX), mw.getHeight()*(v.y-minY)/(maxY-minY), 10, 10);
	    		}
	    		i++;
	    	}
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }

	    

	    // make the outlines smooth
	    AbstractShapeGenerator shapeGenerator = new BezierShapeGenerator(setOutline);

	    // generate shapes for each group
	    // the shapes can be drawn by a Graphics object
	    // as passed by a component's paint method
	    //Shape[] shapes = shapeGenerator.createShapesFor(items);

	    canvas.translateScene(112.0, 106.0);
	    canvas.setDefaultView();
	    mw.setVisible(true);
	}
}
