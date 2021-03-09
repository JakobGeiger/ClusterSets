import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;


import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;

//this class takes a graph, given via a collection of vertices and a collection of edges 
//and runs the greedy algorithm for the minimum nomber of connected components and no edge crossings on it
public class Greedy {
	
	public Collection<Vector2D> V;
	public Collection<Edge2D> E;
    
    public Greedy(Collection<Vector2D> V, Collection<Edge2D> E) {
    	this.V = V;
    	this.E = E;
    }
    
    public void runGreedy() {
    	
    	//the edges in G
    	ArrayList<Edge2D> Eg = new ArrayList<Edge2D>();
    	
    	//P is represented by it's edges
    	ArrayList<Edge2D> Ep = new ArrayList<Edge2D>(E);
    	
    	//Collections that allow us to monitor the useless and uncrossed edges in P
    	Collection<Edge2D> useless;
    	Collection<Edge2D> uncrossed;
    	
    	while(!Ep.isEmpty()) 	//While P!= empty Set
    	{ 
    		//update the collections for the nested while loop
    		useless = findUselessEdges(Ep, Eg);
        	uncrossed = findUncrossedEdges(Ep);
        	
    		while((!uncrossed.isEmpty())||(!useless.isEmpty()))	//while P contains non-crossed or useless edges   															
    		{													//after the first iteration, useless will always be empty
    			
    			//move all non-crossed edges from P to G
    			Eg.addAll(uncrossed);
    			Ep.removeAll(uncrossed);
    			
    			//clear the collection of "used" edges
    			uncrossed.clear();
    			
    			//update the useless edges in P
        		useless = findUselessEdges(Ep, Eg);
    			
    			//delete all useless edges in P
    			Ep.removeAll(useless);
    			
    			//clear the collection, all of them have been deleted from P
    			useless.clear();
    			
    			//update the uncrossed edges in P
      			uncrossed = findUncrossedEdges(Ep);
    		}
    		if(!Ep.isEmpty()) {
    			Edge2D e = Ep.get(0);
    			Ep.remove(e);
    			Eg.add(e);
    			Ep.remove(findCrossingEdge(e, Ep));
    		}
    	}
    	
    	E.clear();
    	E.addAll(Eg);
    }
    
    public Collection<Edge2D> findUselessEdges(Collection<Edge2D> Ep, Collection<Edge2D> Eg) {
    	Collection<Edge2D> useless = new ArrayList<Edge2D>();
    	
    	for(Edge2D e : Ep) {
    		if(path(e, Eg))
    			useless.add(e);
    	}	
    	return useless;
    }
    
    public Collection<Edge2D> findUncrossedEdges(Collection<Edge2D> edges) {
    	Collection<Edge2D> uncrossed = new ArrayList<Edge2D>(edges);
    	
    	for(Edge2D e : edges) {
    		if(findCrossingEdge(e, edges)!=null)
    			uncrossed.remove(e);
    	}
    	
    	return uncrossed;
    }
    
    public Edge2D findCrossingEdge(Edge2D edge, Collection<Edge2D> edges) {
    	for(Edge2D e : edges) {
    		if(edge!=e && Edge2D.crosses(edge,e))
    			return e;
    	}
    	return null;
    }
    
    //checks for a path between the endpoints of an edge in a specified edge set
    public boolean path(Edge2D edge, Collection<Edge2D> edges)
    {
    	LinkedList<Vector2D> queue = new LinkedList<Vector2D>();
    	LinkedList<Vector2D> vertices = new LinkedList<Vector2D>();
    	LinkedList<Vector2D> visited = new LinkedList<Vector2D>();
    	HashMap<Vector2D, LinkedList<Vector2D>> neighbors = new HashMap<Vector2D, LinkedList<Vector2D>>();
    		
    	vertices.add(edge.a);
    	
    	for(Edge2D e : edges)
    	{
    		vertices.add(e.a);
    		vertices.add(e.b);
    	}
    	
    	for(Vector2D v : vertices)
    	{
    		neighbors.put(v, getNeighbors(v, edges));
    	}
    	
    	queue.add(edge.a);
    	visited.add(edge.a);
    	
    	while(queue.size() != 0)
    	{
    		Vector2D curr = queue.poll();
    		for(Vector2D v : neighbors.get(curr))
    		{
    			if(v == edge.b)
    				return true;
    			if(!visited.contains(v))
    			{
    				queue.add(v);
    				visited.add(v);
    			}
    				
    		}
    	}
    	return false;
    }
    
    public LinkedList<Vector2D> getNeighbors(Vector2D v, Collection<Edge2D> edges)
    {
    	LinkedList<Vector2D> temp = new LinkedList<Vector2D>();
    	for(Edge2D e : edges)
    	{
    		if(e.a == v)
    			temp.add(e.b);
    		if(e.b == v)
    			temp.add(e.a);
    	}
    	
    	return temp;
    }
    
}

