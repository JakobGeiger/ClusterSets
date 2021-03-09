import delaunayTriangulation.Vector2D;
import delaunayTriangulation.Edge2D;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.util.UnionFind;

public class GreedyCluster1Planar {

	public Set<Vector2D> V;
	public Set<Edge2D> E;
	public Map<Vector2D, Color> vColors;
	public Map<Edge2D, Color> eColors;
	public Map<Edge2D, Edge2D> crossings;
	public Map<Edge2D, Edge2D> monochromaticCrossings;
	public Set<Edge2D> uncrossed;
	public UnionFind<Vector2D> UF;
	public Set<Vector2D> newVertices;
	public Set<Edge2D> oldEdges;
	
	public GreedyCluster1Planar(Set<Vector2D> V, Set<Edge2D> E, Map<Vector2D, Color> vColors)
	{
		this.V = V;
		this.E = E;
		this.vColors = vColors;
		
		eColors = new HashMap<>();
		for(Edge2D e : E)
		{
			Color a = vColors.get(e.a);
			Color b = vColors.get(e.b);
			if(a.equals(b) && !(a == null))
				eColors.put(e, a);
		}
	}
	
	public GreedyCluster1Planar(Set<Vector2D> V, Set<Edge2D> E, Map<Vector2D, Color> vColors, Map<Edge2D, Edge2D> crossings)
	{
		this(V, E, vColors);
		this.crossings = crossings;
	}
	
	public void executeAlgorithm()
	{
		Set<Edge2D> _E = new HashSet<>();
		
		//remove uncolored edges from E - or, in this case, remove everything and only add back edges with a color
		E.clear();
		E.addAll(eColors.keySet());
		
		//compute crossings if necessary
		if(crossings == null)
			computeCrossings();
		
		eliminateMonochromaticCrossings();
		
		//initialize UnionFind, with each vertex in its own set
		UF = new UnionFind<>(V);
		
		for(Edge2D e : uncrossed)
		{
			Vector2D ufa = UF.find(e.a);
			Vector2D ufb = UF.find(e.b);
			
			if(!(ufa==ufb))
			{
				UF.union(ufa, ufb);
				_E.add(e);
			}
			//it doesn't matter if we moved e to _E, we don't need it in E anymore
			E.remove(e);
		}
		
		Set<Edge2D> removedEdges = new HashSet<>();
		
		//E now contains no uncrossed edges
		for(Edge2D e : E)
		{
			if(removedEdges.contains(e))
				continue;
			
			Vector2D ufa = UF.find(e.a);
			Vector2D ufb = UF.find(e.b);
			
			if(!(ufa == ufb))
			{
				UF.union(ufa, ufb);
				removedEdges.add(crossings.get(e));
				_E.add(e);
			}
		}
		
		E = _E;
	}
	
	public void eliminateMonochromaticCrossings()
	{	
		newVertices = new HashSet<>();
		oldEdges = new HashSet<>();
		
		for(Edge2D e : monochromaticCrossings.keySet())
		{
			Edge2D _e = monochromaticCrossings.get(e);
			Color a = eColors.get(e);
			
			Vector2D v = Edge2D.intersectLines(e, _e);
			
			E.remove(e);
			E.remove(_e);
			oldEdges.add(e);
			oldEdges.add(_e);
			
			V.add(v);
			vColors.put(v, a);
			newVertices.add(v);
			
			Edge2D[] temp = {new Edge2D(e.a, v), new Edge2D(_e.a, v), new Edge2D(e.b, v), new Edge2D(_e.b, v)};
			
			for(Edge2D newEdge : temp)
			{
				E.add(newEdge);
				eColors.put(newEdge, a);
				uncrossed.add(newEdge);
			}
		}
		monochromaticCrossings.clear();
	}
	
	public void computeCrossings()
	{
		crossings = new HashMap<>();
		monochromaticCrossings = new HashMap<>();
		uncrossed = new HashSet<>();
		
		//this is the naive approach with quadratic runtime
		a: for(Edge2D e : E)
		{
			b: for(Edge2D _e : E)
			{
				//don't look at edges we already have in our structure
				if(crossings.containsKey(_e) || monochromaticCrossings.containsKey(_e))
					continue b;
				
				if(Edge2D.crosses(e, _e))
				{
					if(eColors.get(e).equals(eColors.get(_e)))
						monochromaticCrossings.put(e, _e);
					else
						crossings.put(e, _e);
					continue a;
				}
			}
			//if we reach this, we have not used continue a => e has no crossings
			uncrossed.add(e);
		}
	}
	
}
