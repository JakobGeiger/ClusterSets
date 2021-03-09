import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;

import delaunayTriangulation.Edge2D;

//This selector iteratively selects the edge with the fewest crossings and removes all edges crossed by it
public class GreedySelector implements EdgeSelector {

	@Override
	public Set<Edge2D> selectUncrossedSet(Set<Edge2D> set, Map<Edge2D, List<Edge2D>> crossings) {
		Set<Edge2D> ret = new HashSet<Edge2D>();
		
		MapComparator mc = new MapComparator(crossings);
		
		while(!crossings.isEmpty())
		{
			Edge2D curr = Collections.min(crossings.keySet(),mc);
			Collection<Edge2D> toRemove = new LinkedList<Edge2D>(crossings.get(curr));
			ret.add(curr);
			crossings.remove(curr);
			
			for(Edge2D e : toRemove)
			{
				List<Edge2D> temp = crossings.get(e);
				if(!(temp==null))
				{
					for(Edge2D edge : temp)
					{
						List<Edge2D> l = crossings.get(edge);
						if(!(l==null))
							l.remove(e);
					}	
				}
				crossings.remove(e);
			}
			
		}
		
		return ret;
	}

	
	class MapComparator implements Comparator<Edge2D>
	{

		Map<Edge2D, List<Edge2D>> crossings;
		
		public MapComparator(Map<Edge2D, List<Edge2D>> crossings)
		{
			this.crossings = crossings;
		}
		
		@Override
		public int compare(Edge2D arg0, Edge2D arg1) {
			return Integer.compare(crossings.get(arg0).size(), crossings.get(arg1).size());
		}
		
	}

}
