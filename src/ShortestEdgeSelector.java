import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import delaunayTriangulation.Edge2D;

//returns the shortest edge of a set. intended for use with the greedy algorithm on 1-planar graphs.

public class ShortestEdgeSelector implements EdgeSelector {

	@Override
	public Set<Edge2D> selectUncrossedSet(Set<Edge2D> set, Map<Edge2D, List<Edge2D>> crossings) {
		if(set.isEmpty())
			return set;
		HashSet<Edge2D> ret = new HashSet<Edge2D>();
		Iterator<Edge2D> it = set.iterator();
		Edge2D shortest = it.next();
		Edge2D temp = shortest;
		while(it.hasNext())
		{
			temp = it.next();
			if(temp.length<shortest.length)
				shortest = temp;
		}
		ret.add(shortest);
		
		return ret;
	}

}
