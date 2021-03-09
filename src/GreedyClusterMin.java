import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.util.UnionFind;

import delaunayTriangulation.Edge2D;
import delaunayTriangulation.Vector2D;

import java.awt.Color;

public class GreedyClusterMin
{
	
	public Set<Edge2D> E;
	public Set<Vector2D> V;
	public Map<Edge2D, List<Edge2D>> crossings;
	public Set<Edge2D> uncrossed;
	public Map<Vector2D, Color> vColors;
	public Map<Edge2D, Color> eColors;
	public Set<Edge2D> _E;
	public UnionFind<Vector2D> UF;
	public CrossingsComparator cc;
	
	
	public GreedyClusterMin (Set<Vector2D> V, Set<Edge2D> E, Map<Vector2D, Color> vColors)
	{
		this.V = new HashSet<>(V);
		this.E = new HashSet<>(E);
		this.vColors = new HashMap<>(vColors);
		
		_E = new HashSet<>();
		
		eColors = new HashMap<>();
		
		for(Edge2D e : this.E)
		{
			Color a = this.vColors.get(e.a);
			Color b = this.vColors.get(e.b);
			if(a.equals(b) && !(a == null))
				eColors.put(e, a);
		}
		
		this.E.clear();
		this.E.addAll(eColors.keySet());
		
		computeCrossings();
		
		cc = new CrossingsComparator(crossings);
		
		
		UF = new UnionFind<>(V);
	}
	
	//thanks to submethods, the "main" algorithm is very short
	public void runGreedy()
	{
		//this step is technically unnecessary, since in the following while-loop, we would select the uncrossed edges first anyway
		selectUncrossed();
		
		//greedily select the edge with the least crossings and remove all crossing edges
		while(crossingsLeft())
		{
			selectEdge(getEdgeWithLeastCrossings());
		}
	}
	
	public void runReverseGreedy()
	{
		//select all relevant uncrossed edges
		selectUncrossed();
		
		//greedily remove the edge with the most crossings
		while(!E.isEmpty())
		{
			for(Edge2D e : new HashSet<>(E))
			{
				if(UF.find(e.a).equals(UF.find(e.b)))
				{
					remove(e);
				}
			}
			
			if(E.isEmpty())
				break;
			
			Edge2D e;
			
			e = getEdgeWithLeastCrossings();
			
			if(crossings.get(e).size() == 0)
				selectEdge(e);
			else 
			{
				remove(getEdgeWithMostCrossings());
			}
			
		}
	}
	
	//this is a variation of the ReverseGreedy algorithm, in which we greedily remove the most crossed edge until a 1-plane drawing remains
	//which we the solve optimally
	public void runReverse1PlaneGreedy()
	{
		selectUncrossed();
		
		//greedily remove the edge with the most crossings
			while(!E.isEmpty() && getHighestCrossingsNumber() > 1)
			{
				for(Edge2D e : new HashSet<>(E))
				{
					if(UF.find(e.a).equals(UF.find(e.b)))
					{
						remove(e);
					}
				}
				
				if(E.isEmpty())
					break;
				
				Edge2D e;
				
				e = getEdgeWithLeastCrossings();
				
				if(crossings.get(e).size() == 0)
					selectEdge(e);
				else 
				{
					remove(getEdgeWithMostCrossings());
				}
				
			}
			
			while(!E.isEmpty())
				selectEdge(getEdgeWithLeastCrossings());
	}
	
	public void selectUncrossed()
	{
		for(Edge2D e : uncrossed)
		{
			Vector2D ufa = UF.find(e.a);
			Vector2D ufb = UF.find(e.b);
			
			if(ufa != ufb)
			{
				UF.union(ufa, ufb);
				_E.add(e);
			}
			remove(e);
		}
		uncrossed.clear();
	}
	
	public Edge2D getEdgeWithLeastCrossings()
	{
		return Collections.min(E, cc);
	}
	
	public Edge2D getEdgeWithMostCrossings()
	{
		return Collections.max(E, cc);
	}
	
	public int getHighestCrossingsNumber()
	{
		List<Edge2D> temp = crossings.get(getEdgeWithMostCrossings());
		
		//this case only comes up if all edges are already either selected or removed
		if(temp == null)
			return 0;
		
		return temp.size();
	}
	
	public void remove(Edge2D e)
	{
		E.remove(e);
		if(!uncrossed.contains(e))
		{
			for(Edge2D _e : new HashSet<>(crossings.get(e)))
				crossings.get(_e).remove(e);
			crossings.remove(e);
		}
	}
	
	public void selectEdge(Edge2D e)
	{
		Vector2D ufa = UF.find(e.a);
		Vector2D ufb = UF.find(e.b);
		
		//if the edge would merge two clusters, we do it
		if(ufa != ufb)
		{
			for(Edge2D _e : new HashSet<>(crossings.get(e)))
			{
				remove(_e);
			}
			_E.add(e);
			UF.union(ufa, ufb);
		}
		//doesn't matter if we actually selected the edge or not, we'll always remove it
		remove(e);
	}
	
	//a set of edges has no crossings if the edge with the most crossings has 0 crossings or the PG is empty
	public boolean noMoreCrossings()
	{
		return E.isEmpty() || crossings.get(getEdgeWithMostCrossings()).isEmpty();
	}
	
	public boolean crossingsLeft()
	{
		return !noMoreCrossings();
	}
	
	public void computeCrossings()
	{
		//this is a naive brute force algorithm to compute all the crossings in a drawing
		crossings = new HashMap<>();
		uncrossed = new HashSet<>();
		
		for(Edge2D e : E)
		{
			boolean flag = true;
			List<Edge2D> temp = new ArrayList<>();
			
			for(Edge2D _e : E)
			{
				if(e!=_e && Edge2D.crosses(e, _e))
				{
					temp.add(_e);
					flag = false;
				}
			}
			
			if(flag)
				uncrossed.add(e);
			else
				crossings.put(e, temp);
		}
	}
	
	class CrossingsComparator implements Comparator<Edge2D>
	{
		public Map<Edge2D, List<Edge2D>> crossings;
		
		public CrossingsComparator(Map<Edge2D, List<Edge2D>> crossings)
		{
			this.crossings = crossings;
		}

		@Override
		public int compare(Edge2D o1, Edge2D o2) {
			return Integer.compare(crossings.get(o1).size(), crossings.get(o2).size());
		}
		
	}
	
	public Set<List<Vector2D>> getClusters()
	{
		Set<List<Vector2D>> ret = new HashSet<>();
		
		for(Vector2D v : V)
		{
			boolean flag = false;
			for(List<Vector2D> curr : ret)
			{
				if(UF.find(v) == UF.find(curr.get(0)))
				{
					curr.add(v);
					flag = true;
				}
			}
			if(!flag)
			{
				List<Vector2D> temp = new ArrayList<>();
				temp.add(v);
				ret.add(temp);
			}
		}
		
		return ret;
	}
	
}
